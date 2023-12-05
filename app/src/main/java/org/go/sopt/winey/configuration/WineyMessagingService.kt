package org.go.sopt.winey.configuration

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.WineyApplication
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.splash.SplashActivity
import javax.inject.Inject

@AndroidEntryPoint
class WineyMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch { dataStoreRepository.saveDeviceToken(token) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty() && !WineyApplication.isAppInForeground) {
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(KEY_NOTI_TYPE, remoteMessage.data[KEY_NOTI_TYPE])
        intent.putExtra(KEY_FEED_ID, remoteMessage.data[KEY_FEED_ID])
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            uniId,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = CHANNEL_ID

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.data[KEY_TITLE])
            .setContentText(remoteMessage.data[KEY_MESSAGE])
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, NOTICE, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(uniId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "FirebaseService"
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_NOTI_TYPE = "notiType"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val NOTICE = "Notice"
        private const val CHANNEL_ID = "channel"
    }
}
