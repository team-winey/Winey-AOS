package org.go.sopt.winey.presentation.splash

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivitySplashBinding
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.colorOf
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BindingActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdateResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                checkAutoLogin()
            } else {
                Timber.e("Update flow failed! Result code: " + result.resultCode)
                checkAppUpdateInfo()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setUpStatusBar()
        lifecycleScope.launch {
            delay(DELAY_TIME)
            checkAppUpdateInfo()
        }
    }

    override fun onResume() {
        super.onResume()

        // 백그라운드에서 포그라운드로 돌아왔는데 아직 설치하고 있는 경우
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                // 즉시 업데이트 다시 요청하기
                requestImmediateUpdate(appUpdateInfo)
            }
        }
    }

    private fun setUpStatusBar() {
        // 상태바 색상 변경
        window.statusBarColor = colorOf(R.color.black)

        // 상태바에 있는 아이콘 밝게 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
            windowInsetController?.isAppearanceLightStatusBars = false
        }
    }

    private fun checkAppUpdateInfo() {
        // 설치 가능한 업데이트 버전이 있고, 즉시 업데이트가 허용된 경우
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                requestImmediateUpdate(appUpdateInfo)
            } else {
                checkAutoLogin()
            }
        }
    }

    private fun requestImmediateUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            appUpdateResultLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        )
    }

    private fun checkAutoLogin() {
        lifecycleScope.launch {
            val accessToken = dataStoreRepository.getAccessToken().firstOrNull()
            if (accessToken.isNullOrBlank()) {
                navigateToGuideScreen()
            } else {
                navigateToMainScreen()
            }
        }
    }

    private fun navigateToGuideScreen() {
        Intent(this, GuideActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    private fun navigateToMainScreen() {
        Intent(this, MainActivity::class.java).apply {
            if (intent.extras != null) {
                putExtra(KEY_NOTI_TYPE, intent.getStringExtra(KEY_NOTI_TYPE))
                putExtra(KEY_FEED_ID, intent.getStringExtra(KEY_FEED_ID))
            }
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    companion object {
        private const val KEY_NOTI_TYPE = "notiType"
        private const val KEY_FEED_ID = "feedId"
        private const val DELAY_TIME = 1500L
    }
}
