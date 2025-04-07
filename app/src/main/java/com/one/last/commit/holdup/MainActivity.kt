package com.one.last.commit.holdup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.one.last.commit.holdup.ui.theme.HoldUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoldUpTheme {
                HoldUpNavHost(
                    navController = rememberNavController()
                )
            }
        }
    }
}
