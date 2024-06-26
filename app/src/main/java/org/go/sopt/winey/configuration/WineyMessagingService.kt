package org.go.sopt.winey.configuration

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.go.sopt.winey.util.activity.ActivityLifecycleHandler
import org.go.sopt.winey.R
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
        if (remoteMessage.data.isNotEmpty() && !ActivityLifecycleHandler.isAppInForeground) {
            sendNotification(remoteMessage)
        }
    }

    private fun createNotificationIntent(remoteMessage: RemoteMessage): Intent {
        return Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(KEY_NOTI_TYPE, remoteMessage.data[KEY_NOTI_TYPE])
            putExtra(KEY_FEED_ID, remoteMessage.data[KEY_FEED_ID])
        }
    }

    private fun createPendingIntent(intent: Intent, uniqueIdentifier: Int): PendingIntent {
        return PendingIntent.getActivity(
            this,
            uniqueIdentifier,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getSoundUri(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    private fun generateUniqueIdentifier(): Int {
        return (System.currentTimeMillis() / 7).toInt()
    }

    private fun createNotificationBuilder(remoteMessage: RemoteMessage, pendingIntent: PendingIntent): NotificationCompat.Builder {
        val soundUri = getSoundUri()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.data[KEY_TITLE])
            .setContentText(remoteMessage.data[KEY_MESSAGE])
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
    }

    private fun showNotification(notificationBuilder: NotificationCompat.Builder, uniqueIdentifier: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(uniqueIdentifier, notificationBuilder.build())
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val uniqueIdentifier = generateUniqueIdentifier()
        val intent = createNotificationIntent(remoteMessage)
        val pendingIntent = createPendingIntent(intent, uniqueIdentifier)
        val notification = createNotificationBuilder(remoteMessage, pendingIntent)

        showNotification(notification, uniqueIdentifier)
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_NOTI_TYPE = "notiType"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val CHANNEL_NAME = "Notice"
        private const val CHANNEL_ID = "channel"
    }
}
