package com.android.go.sopt.winey.presentation.main.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyPageBinding
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.presentation.main.MainViewModel
import com.android.go.sopt.winey.presentation.main.mypage.myfeed.MyFeedFragment
import com.android.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.fragment.viewLifeCycle
import com.android.go.sopt.winey.util.fragment.viewLifeCycleScope
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val myPageViewModel by viewModels<MyPageViewModel>()

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init1On1ButtonClickListener()
        initLevelHelpButtonClickListener()
        initToMyFeedButtonClickListener()
        initLogoutButtonClickListener()
        initWithdrawButtonClickListener()

        setupGetUserState()
        setupDeleteUserState()
        mainViewModel.getUser()
    }

    private fun initToMyFeedButtonClickListener() {
        binding.clMypageToMyfeed.setOnSingleClickListener {
            navigateTo<MyFeedFragment>()
        }
    }

    private fun init1On1ButtonClickListener() {
        binding.clMypageTo1on1.setOnClickListener {
            val url = ONE_ON_ONE_URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun initLevelHelpButtonClickListener() {
        binding.btnMypageLevelHelp.setOnClickListener {
            val intent = Intent(context, MypageHelpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initLogoutButtonClickListener() {
        binding.clMypageLogout.setOnClickListener {
            mainViewModel.postLogout()
        }
    }

    private fun initWithdrawButtonClickListener() {
        binding.ivMypageWithdraw.setOnClickListener {
            myPageViewModel.deleteUser()
        }
    }

    private fun setupDeleteUserState() {
        myPageViewModel.deleteUserState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        // todo: 데이터 스토어에 저장된 값들도 모두 지워야 한다! (액세스 토큰 남아있으면 메인 피드로 넘어감)
                        navigateToGuideScreen()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {
                    }
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun navigateToGuideScreen() {
        Intent(requireContext(), GuideActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
        }
    }

    private fun setupGetUserState() {
        mainViewModel.getUserState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    val data = dataStoreRepository.getUserInfo().firstOrNull()
                    handleSuccessState(data)
                    handleTargetModifyButtonState(data)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                is UiState.Empty -> {
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleTargetModifyButtonState(data: User?) {
        binding.btnMypageTargetModify.setOnSingleClickListener {
            val bottomSheet = TargetAmountBottomSheetFragment()
            bottomSheet.show(this.childFragmentManager, bottomSheet.tag)
            /*when (data.isOver) {
            true -> {
                val bottomSheet = TargetAmountBottomSheetFragment()
                bottomSheet.show(this.childFragmentManager, bottomSheet.tag)
            }

            false -> {
                val dialog = MyPageDialogFragment()
                dialog.show(this.childFragmentManager, dialog.tag)
            }
        }*/
        }
    }

    private fun handleSuccessState(data: User?) {
        binding.data = data

        when (data?.isOver) {
            true -> {
                binding.tvMypageTargetAmount.text = getString(R.string.mypage_not_yet_set)
                binding.tvMypagePeriodValue.text = getString(R.string.mypage_not_yet_set)
            }

            false -> {
                if (data.dday == 0) {
                    binding.tvMypagePeriodValue.text = getString(R.string.mypage_d_day)
                    binding.targetMoney = data
                } else {
                    binding.targetMoney = data
                    binding.dday = data
                }
            }

            null -> {
            }
        }
        when (data?.userLevel) {
            LEVEL_COMMON -> {
                binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv1_progressbar)
                binding.ivMypageProfile.setImageResource(R.drawable.ic_mypage_lv1_profile)
            }

            LEVEL_KNIGHT -> {
                binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv2_progressbar)
                binding.ivMypageProfile.setImageResource(R.drawable.ic_mypage_lv2_profile)
            }

            LEVEL_NOBLESS -> {
                binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv3_progressbar)
                binding.ivMypageProfile.setImageResource(R.drawable.ic_mypage_lv3_profile)
            }

            LEVEL_KING -> {
                binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv4_progressbar)
                binding.ivMypageProfile.setImageResource(R.drawable.ic_mypage_lv4_profile)
            }
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.simpleName)
            addToBackStack(null)
        }
    }

    companion object {
        private const val LEVEL_COMMON = "평민"
        private const val LEVEL_KNIGHT = "기사"
        private const val LEVEL_NOBLESS = "귀족"
        private const val LEVEL_KING = "황제"
        private const val ONE_ON_ONE_URL = "https://open.kakao.com/o/s751Susf"
    }
}
