package com.one.last.commit.holdup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.one.last.commit.holdup.ui.theme.HoldUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoldUpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppSelectionScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
