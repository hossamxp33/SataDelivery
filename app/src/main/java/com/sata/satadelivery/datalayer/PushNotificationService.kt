package com.codesroots.satavendor.datalayer

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import javax.inject.Inject

class PushNotificationService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("TAG", "onMessageReceived: $remoteMessage")
        if (remoteMessage.notification != null) {
            setNotification(
                remoteMessage.notification!!.title, remoteMessage.notification!!
                    .body
            )
        }
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        if (intent != null) {
            val title = intent.getStringExtra("gcm.notification.title")
            val body = intent.getStringExtra("gcm.notification.body")
            setNotification(title, body)
        }
    }

    private fun setNotification(title: String?, body: String?) {
        var notifyPendingIntent: PendingIntent? = null
//        if (Utils.get(Utils.PREF_TOKEN) != null) {
//            val notifyIntent = Intent(this, HomeActivity::class.java)
//            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            notifyPendingIntent = PendingIntent.getActivity(
//                this, 0,
//                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        }
        NotificationUtils.getInstance(this)?.notify(1, title, body, notifyPendingIntent)
    }


    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("TAG", "onMessageReceived onNewToken: $s")

        sendNewTokenToServer(s)

    }

    private fun sendNewTokenToServer(token: String) {


    }
}