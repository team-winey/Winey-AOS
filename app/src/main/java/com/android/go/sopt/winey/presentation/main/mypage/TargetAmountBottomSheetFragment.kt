package com.android.go.sopt.winey.presentation.main.mypage

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentTargetAmountBottomSheetBinding
import com.android.go.sopt.winey.util.binding.BindingBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior


class TargetAmountBottomSheetFragment :
    BindingBottomSheetDialogFragment<FragmentTargetAmountBottomSheetBinding>(R.layout.fragment_target_amount_bottom_sheet) {
    private val viewModel by viewModels<TargetAmountViewModel>()

    override fun onStart() {
        super.onStart()

        initScreenHeight()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = viewModel
        initCancelButtonClickListener()
        initAmountEditTextWatcher()
        initDayEditTextWatcher()
        initAmountCheckObserver()
        initDayCheckObserver()
    }


    fun initCancelButtonClickListener() {
        binding.btnTargetAmountCancel.setOnClickListener {
            this.dismiss()
        }
    }

    private fun initAmountEditTextWatcher() {
        var temp = ""
        binding.etTargetAmountSetAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val amount: String = if (input.isNotBlank()) {
                    input.replace(",", "")
                } else {
                    "0"
                }
                viewModel.checkAmount(amount)
                viewModel.checkButtonState()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()

                // 문자열이 비어있거나 이전과 변함 없으면 그대로 둔다.
                if (input.isBlank() || input == temp) return

                temp = makeCommaString(input.replace(",", "").toLong())
                binding.etTargetAmountSetAmount.apply {
                    setText(temp)
                    setSelection(temp.length) // 커서를 오른쪽 끝으로 보낸다.
                }
            }
        })
    }

    private fun initDayEditTextWatcher() {
        var temp = ""
        binding.etTargetAmountSetDay.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val day: String = if (input.isNotBlank()) {
                    input.replace(",", "")
                } else {
                    "0"
                }
                viewModel.checkDay(day)
                viewModel.checkButtonState()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()

                // 문자열이 비어있거나 이전과 변함 없으면 그대로 둔다.
                if (input.isBlank() || input == temp) return

                temp = makeCommaString(input.replace(",", "").toLong())
                binding.etTargetAmountSetDay.apply {
                    setText(temp)
                    setSelection(temp.length) // 커서를 오른쪽 끝으로 보낸다.
                }
            }
        })
    }

    private fun initAmountCheckObserver() {
        viewModel.amountCheck.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.tilTargetAmountSetAmount.error = " "
                    binding.etTargetAmountSetAmount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_500
                        )
                    )
                    binding.tvTargetAmountWarningAmount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_500
                        )
                    )
                }

                false -> {
                    binding.tilTargetAmountSetAmount.error = null
                    binding.etTargetAmountSetAmount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.purple_400
                        )
                    )
                    binding.tvTargetAmountWarningAmount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_400
                        )
                    )
                }
            }
        }
    }

    private fun initDayCheckObserver() {
        viewModel.dayCheck.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.tilTargetAmountSetDay.error = " "
                    binding.etTargetAmountSetDay.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_500
                        )
                    )
                    binding.tvTargetAmountWarningDay.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_500
                        )
                    )
                }

                false -> {
                    binding.tilTargetAmountSetDay.error = null
                    binding.etTargetAmountSetDay.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.purple_400
                        )
                    )
                    binding.tvTargetAmountWarningDay.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_400
                        )
                    )
                }
            }
        }
    }
    fun initScreenHeight() {
        if (dialog != null) {
            val bottomSheet: View =
                dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        val view = view
        view?.post {
            val parent = view.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior!!.peekHeight = view.measuredHeight
            val maxHeight = resources.displayMetrics.heightPixels * 3 / 4
            bottomSheetBehavior.maxHeight = maxHeight
            parent.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}