package com.tsquaredapplications.waterquality

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class WarningNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage?.let { message ->
            if(message.data.isNotEmpty()){
                if(message.notification != null){
                    remoteMessage.notification?.let {
                        sendNotification(it)
                    }
                }
            }

        }
    }

    fun sendNotification(notification: RemoteMessage.Notification) {

        if (notification.body != null && notification.title != null) {

            val title = notification.title!!
            val message = if(notification.body!!.length > 100) notification.body!!.substring(0 until 97) + "..."
            else notification.body!!


            val intent = Intent(this, MainActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

            val channelId = getString(R.string.message_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notifcationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_drop)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Since Oreo notification channel is needed
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId,
                    getString(R.string.message_channel_name),
                    NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(0, notifcationBuilder.build())
        }
    }
}
