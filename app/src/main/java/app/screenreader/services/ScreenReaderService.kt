package app.screenreader.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Region
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.hardware.display.DisplayManagerCompat
import app.screenreader.MainActivity
import app.screenreader.R
import app.screenreader.model.Constants
import app.screenreader.model.Gesture
import java.io.Serializable

/**
 * This is where the magic happens.
 * Running an AccessibilityService makes gestures "bypass" other services such as TalkBack and Voice Assistant.
 * The ApptService hooks into accessibility events and gestures.
 *
 * Created by Jan Jaap de Groot on 09/10/2020
 * Copyright 2020 Stichting Appt
 */
class ScreenReaderService: AccessibilityService() {

    private val TAG = "ApptService"
    private val GESTURE_TRAINING_CLASS_NAME = MainActivity::class.java.name

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")

        // Set passthrough regions
        setPassthroughRegions()

        // Start GestureActivity
        startGestureTraining()
    }

    private fun setPassthroughRegions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContextCompat.getSystemService(this, WindowManager::class.java)?.let { windowManager ->
                val bounds = windowManager.currentWindowMetrics.bounds
                val region = Region(bounds.left, bounds.top, bounds.right, bounds.bottom)

                val displays = DisplayManagerCompat.getInstance(this).displays
                displays.forEach { display ->
                    Log.d(TAG, "Setting passthrough for display ${display.displayId} to: $region")
                    setTouchExplorationPassthroughRegion(display.displayId, region)
                    setGestureDetectionPassthroughRegion(display.displayId, region)
                }
            }
        }
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        Log.d(TAG, "onKeyEvent: $event")
        return super.onKeyEvent(event)
    }

    override fun onServiceConnected() {
        Log.i(TAG, "Service connected")
        super.onServiceConnected()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.i(TAG, "onInterrupt")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i(TAG, "onAccessibilityEvent: $event")

        // Continue if eventType = TYPE_WINDOW_STATE_CHANGED
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        // Continue if packageName is empty
        if (event.packageName == null || event.packageName.isEmpty()) {
            return
        }

        // Continue if event does not come from own package
        if (event.packageName == this.packageName) {
            return
        }

        // Continue if event does not come from accessibility package
        if (event.packageName.contains("accessibility")) {
            return
        }

        // Continue if text does not contain the service label
        val serviceName = getString(R.string.service_label)
        if (event.text.contains(serviceName)) {
            return
        }

        // Continue if the gesture training is not active
        if (isGestureTraining()) {
            return
        }

        // Kill the service
        kill()
    }

    override fun onGesture(gestureId: Int): Boolean {
        Log.i(TAG, "onGesture: $gestureId")

        // Broadcast gesture to GestureActivity
        Gesture.from(gestureId)?.let { gesture ->
            broadcast(Constants.SERVICE_GESTURE, gesture)
        }

        // Kill service if touch exploration is disabled
        if (!isTouchExploring()) {
            kill()
            return false
        }

        return true
    }

    private fun broadcast(key: String, value: Serializable) {
        val intent = Intent(Constants.SERVICE_ACTION)
        intent.setPackage(packageName)
        intent.putExtra(key, value)
        sendBroadcast(intent)
    }

    private fun kill() {
        broadcast(Constants.SERVICE_KILLED, true)
        disableSelf()
    }

    private fun isTouchExploring(): Boolean {
        var count = 0

        val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val services = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in services) {
            val flags = service.capabilities
            val capability = AccessibilityServiceInfo.CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION

            // Check if Touch Exploration capability is granted
            if (flags and capability == capability) {
                count++
            }
        }

        // Touch Exploration capability should be granted to at least two services: ApptService and TalkBack/VoiceAssistant/other.
        return count >= 2
    }

    private fun isGestureTraining(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningTasks(1).firstOrNull()?.topActivity?.let { activity ->
            return activity.className == GESTURE_TRAINING_CLASS_NAME
        }
        return false
    }

    private fun startGestureTraining() {
        val gestures = Gesture.randomized()
        gestures.forEach { gesture ->
            gesture.completed(this, false)
        }

//        val intent = Intent(this, GestureActivity::class.java)
//        intent.setGestures(gestures)
//        intent.setInstructions(instructions)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
    }

    companion object {
        fun isEnabled(context: Context): Boolean {
            (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).let { manager ->
                val services = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
                for (service in services) {
                    if (service.resolveInfo.serviceInfo.name == ScreenReaderService::class.java.name) {
                        return true
                    }
                }
            }
            return false
        }

        fun enable(context: Context, instructions: Boolean = false) {
            this.instructions = instructions

            AlertDialog.Builder(context)
                .setTitle(R.string.service_enable_title)
                .setMessage(R.string.service_enable_message)
                .setPositiveButton(R.string.action_activate) { _, _ ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
                .setNegativeButton(R.string.action_cancel) { _, _ ->
                    // Dismiss
                }
                .setCancelable(false)
                .show()
        }

        var instructions = true
    }
}