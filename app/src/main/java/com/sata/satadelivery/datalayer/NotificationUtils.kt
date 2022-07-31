package com.codesroots.satavendor.datalayer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sata.satadelivery.R


class NotificationUtils private constructor(val context: Context){

    private var builder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null

    init {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {

        private var notificationUtils: NotificationUtils? = null

        @Synchronized
        fun getInstance(context: Context): NotificationUtils? {
            if (notificationUtils == null) {
                notificationUtils = NotificationUtils(context)
            }
            return notificationUtils
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun notify(idNotify: Int, title: String?, body: String?, pendingIntent: PendingIntent?) {
        val notificationChannelId = "OT-Attendance_Notify"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val notificationChannel = NotificationChannel(
                notificationChannelId,
                "OT-Attendance Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "OT-Attendance channel for app"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = context.getColor(R.color.purple_700)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
        builder = NotificationCompat.Builder(context, notificationChannelId)
            .setDefaults(Notification.DEFAULT_ALL)
            .setColorized(true)
            .setColor(context.getColor(R.color.purple_700))
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri)
            .setAutoCancel(false)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
                    .setBigContentTitle(title)
            )
        if (pendingIntent != null) {
            builder!!.setContentIntent(pendingIntent)
        }
        val notification: Notification
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder!!.build()
        } else {
            builder!!.notification
        }
        val manager = NotificationManagerCompat.from(context.applicationContext)
        manager.notify(idNotify, notification)
        val intent = Intent("NEW_NOTIFICATION")
        intent.putExtra("newNotification", true)
        context.sendBroadcast(intent)
    }

    fun cancelNotification(id: Int) {
        notificationManager!!.cancel(id)
    }

    fun cancelAllNotification() {
        notificationManager!!.cancelAll()
    }
}