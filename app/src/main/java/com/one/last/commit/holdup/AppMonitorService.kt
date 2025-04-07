package com.one.last.commit.holdup

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppMonitorService : AccessibilityService() {
    private var selectedApps: Set<String> = emptySet()
    private var ignoreMap: MutableMap<String, Long> = mutableMapOf()

    private var lastDetectedPackage: String? = null
    private var lastDetectionTime: Long = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val currentPkg = event.packageName?.toString() ?: return

        val now = System.currentTimeMillis()
        val ignoreUntil = ignoreMap[currentPkg] ?: 0L

        if (lastDetectedPackage == currentPkg && now - lastDetectionTime < 1000) {
            Log.d("AppMonitorService", "Duplicate event ignored for $currentPkg")
            return
        }

        lastDetectedPackage = currentPkg
        lastDetectionTime = now

        if (now < ignoreUntil) {
            Log.d("AppMonitorService", "Ignoring $currentPkg until $ignoreUntil")
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

    override fun onInterrupt() {}

    companion object {
        private var instance: AppMonitorService? = null

        fun ignoreAppFor(packageName: String, millis: Long) {
            instance?.ignoreMap?.put(packageName, System.currentTimeMillis() + millis)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("AppMonitorService", "Service connected")

        CoroutineScope(Dispatchers.Default).launch {
            DataStoreRepository.getSelectedApps(this@AppMonitorService).collect { apps ->
                Log.d("AppMonitorService", "Selected apps: $apps")
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