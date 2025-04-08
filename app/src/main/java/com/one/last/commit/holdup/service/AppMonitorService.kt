package com.one.last.commit.holdup.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.one.last.commit.holdup.data.repository.DataStoreRepository
import com.one.last.commit.holdup.ui.AlertActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppMonitorService : AccessibilityService() {
    private var selectedApps: Set<String> = emptySet()
    private var ignoredApps: MutableSet<String> = mutableSetOf()

    private var lastDetectedPackage: String? = null
    private var lastDetectionTime: Long = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val currentPkg = event.packageName?.toString() ?: return

        val now = System.currentTimeMillis()

        // 앱 종료 후 다른 앱으로 전환되었을 때 ignoredApps에서 제거
        if (lastDetectedPackage != null && lastDetectedPackage != currentPkg) {
            ignoredApps.remove(lastDetectedPackage)
        }

        if (lastDetectedPackage == currentPkg && now - lastDetectionTime < 3000) {
            return
        }

        lastDetectedPackage = currentPkg
        lastDetectionTime = now

        if (ignoredApps.contains(currentPkg)) {
            Log.d("AppMonitorService", "App $currentPkg is currently ignored.")
            return
        }

        if (selectedApps.contains(currentPkg)) {
            CoroutineScope(Dispatchers.IO).launch {
                DataStoreRepository.incrementUsage(this@AppMonitorService, currentPkg)
            }

            val intent = Intent(this, AlertActivity::class.java).apply {
                putExtra("packageName", currentPkg)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    override fun onInterrupt() { }

    companion object {
        private var instance: AppMonitorService? = null

        const val SERVICE_ID = "com.one.last.commit.holdup/.service.AppMonitorService"

        fun ignoreAppUntilClosed(packageName: String) {
            instance?.ignoredApps?.add(packageName)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        CoroutineScope(Dispatchers.Default).launch {
            DataStoreRepository.getSelectedApps(this@AppMonitorService).collect { apps ->
                selectedApps = apps
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }
}