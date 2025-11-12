package com.example.projektbptb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.rememberNavController
import com.example.projektbptb.navigation.NavigationGraph
import com.example.projektbptb.ui.theme.ProjektBptbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjektBptbApp()
        }
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
