package com.one.last.commit.holdup

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.launch

@Composable
fun AppSelectionScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pm = context.packageManager

    val apps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { appInfo ->
            pm.getLaunchIntentForPackage(appInfo.packageName) != null }
    }

    val selectedAppsFlow = remember { DataStoreRepository.getSelectedApps(context) }
    val selectedApps by selectedAppsFlow.collectAsState(initial = emptySet())

    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Black)
                .padding(20.dp)
        ) {
            Text(
                text = "사용을 제한할 앱 목록",
                color = Color.White,
            )
        }
        LazyColumn {
            items(apps) { app ->
                val appName = pm.getApplicationLabel(app).toString()
                val appPackageName = app.packageName
                val appIcon = remember(app) {
                    pm.getApplicationIcon(app).toBitmap().asImageBitmap()
                }
                val isSelected = selectedApps.contains(appPackageName)

                AppItem(
                    appName = appName,
                    appIcon = appIcon,
                    isSelected = isSelected,
                    onClick = {
                        val newSelectedApps = if (isSelected) {
                            selectedApps - appPackageName
                        } else {
                            selectedApps + appPackageName
                        }
                        scope.launch {
                            DataStoreRepository.setSelectedApps(context, newSelectedApps)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AppItem(
    appName: String,
    appIcon: ImageBitmap,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onClick() }
        )

        Image(
            bitmap = appIcon,
            contentDescription = "$appName icon",
            modifier = Modifier
                .size(40.dp)
                .padding(end = 12.dp)
        )
        Text(
            text = appName,
            modifier = Modifier.weight(1f)
        )
    }
}