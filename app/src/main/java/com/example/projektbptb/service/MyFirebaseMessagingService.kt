package com.example.projektbptb.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.projektbptb.MainActivity
import com.example.projektbptb.R
import com.example.projektbptb.data.network.AuthRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var authRepository: AuthRepository

    override fun onCreate() {
        super.onCreate()
        authRepository = AuthRepository(applicationContext)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed FCM token: $token")
        // Kirim token ke backend untuk disimpan
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Message ID: ${remoteMessage.messageId}")
        Log.d(TAG, "Message Type: ${if (remoteMessage.notification != null) "Notification" else "Data"}")

        // Handle both notification and data payload
        val title = remoteMessage.notification?.title 
            ?: remoteMessage.data["title"] 
            ?: "Postingan Anda Dikomentari"
        val message = remoteMessage.notification?.body 
            ?: remoteMessage.data["message"] 
            ?: ""
        val productId = remoteMessage.data["product_id"]?.toIntOrNull()

        Log.d(TAG, "Notification - Title: $title, Message: $message, Product ID: $productId")

        // Always show notification (even if app is in foreground)
        sendNotification(title, message, productId)

        // Also handle data payload if exists
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Postingan Anda Dikomentari"
        val message = data["message"] ?: ""
        val productId = data["product_id"]?.toIntOrNull()

        sendNotification(title, message, productId)
    }

    private fun sendNotification(title: String, messageBody: String, productId: Int? = null) {
        Log.d(TAG, "Creating notification - Title: $title, Message: $messageBody")
        
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            productId?.let {
                putExtra("product_id", it)
                putExtra("navigate_to", "product_detail")
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 
            (productId ?: System.currentTimeMillis()).toInt(), 
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifikasi Produk",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk komentar produk"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = (productId ?: System.currentTimeMillis()).toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d(TAG, "Notification displayed with ID: $notificationId")
    }

    private fun sendTokenToServer(token: String) {
        serviceScope.launch {
            try {
                if (authRepository.isLoggedIn()) {
                    val result = authRepository.saveFcmToken(token)
                    result.onSuccess {
                        Log.d(TAG, "FCM token berhasil dikirim ke server")
                    }.onFailure { exception ->
                        Log.e(TAG, "Gagal mengirim FCM token ke server: ${exception.message}")
                    }
                } else {
                    Log.d(TAG, "User belum login, FCM token akan dikirim setelah login")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending FCM token: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}

