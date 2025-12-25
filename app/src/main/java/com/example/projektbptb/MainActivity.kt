package com.example.projektbptb

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.rememberNavController
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.navigation.NavigationGraph
import com.example.projektbptb.ui.theme.ProjektBptbTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AuthRepository
        authRepository = AuthRepository(this)
        
        // Get FCM token and send to server if user is logged in
        getFcmTokenAndSendToServer()
        
        setContent {
            ProjektBptbApp()
        }
    }
    
    private fun getFcmTokenAndSendToServer() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("MainActivity", "FCM Registration Token: $token")

            // Send token to server if user is logged in
            if (authRepository.isLoggedIn()) {
                activityScope.launch {
                    authRepository.saveFcmToken(token)
                        .onSuccess {
                            Log.d("MainActivity", "FCM token berhasil dikirim ke server")
                        }
                        .onFailure { exception ->
                            Log.e("MainActivity", "Gagal mengirim FCM token ke server: ${exception.message}")
                        }
                }
            } else {
                Log.d("MainActivity", "User belum login, FCM token akan dikirim setelah login")
            }
        }
    }

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, arrayOf(permission), 101)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requestNotificationPermission()
    }
}

@Composable
fun ProjektBptbApp() {
    ProjektBptbTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val navController = rememberNavController()
            NavigationGraph(navController = navController)
        }
    }
}
