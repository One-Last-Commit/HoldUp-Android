package com.one.last.commit.holdup

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateToPermission: () -> Unit,
    navigateToAppSelection: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(1000)
        if (isAccessibilityServiceEnabled(context, "com.one.last.commit.holdup/.AppMonitorService")) {
            navigateToAppSelection()
        } else {
            navigateToPermission()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hold Up",
            color = Color.White,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold
            ),
        )
    }
}

private fun isAccessibilityServiceEnabled(context: Context, serviceId: String): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    return am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        .any { it.id == serviceId }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(
        navigateToPermission = {},
        navigateToAppSelection = {}
    )
}