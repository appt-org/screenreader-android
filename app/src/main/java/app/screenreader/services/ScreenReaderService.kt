package app.screenreader.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.TouchInteractionController
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Region
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.hardware.display.DisplayManagerCompat
import app.screenreader.MainActivity
import app.screenreader.R
import app.screenreader.tabs.actions.ActionActivity
import app.screenreader.tabs.gestures.GestureActivity
import app.screenreader.extensions.getSpannable
import app.screenreader.model.Constants
import app.screenreader.model.Gesture
import java.io.Serializable

/**
 * This is where the magic happens.
 * Running an AccessibilityService makes gestures "bypass" other services such as TalkBack and Voice Assistant.
 * The ScreenReaderService hooks into accessibility events and gestures.
 *
 * Created by Jan Jaap de Groot on 09/10/2020
 * Copyright 2020 Stichting Appt
 */
class ScreenReaderService: AccessibilityService() {

    private val TAG = "ScreenReaderService"
    private val MAIN_ACTIVITY_CLASS_NAME = MainActivity::class.java.name
    private val GESTURE_ACTIVITY_CLASS_NAME = GestureActivity::class.java.name
    private val ACTION_ACTIVITY_CLASS_NAME = ActionActivity::class.java.name

    private var touchController: TouchInteractionController? = null
    private var touchControllerCallback: TouchInteractionController.Callback? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")

        setPassthroughRegions()
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

        // Setup TouchInteractionController (API 32+) to intercept touch events
        // and pass them through to the app via requestDelegating(), bypassing TalkBack's gesture handling
        setupTouchInteractionController()
    }

    private fun setupTouchInteractionController() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            try {
                // Use default display (ID = 0)
                val displayId = android.view.Display.DEFAULT_DISPLAY

                touchController = getTouchInteractionController(displayId)
                Log.i(TAG, "Got TouchInteractionController for display $displayId")

                touchControllerCallback = object : TouchInteractionController.Callback {
                    override fun onMotionEvent(event: MotionEvent) {
                        Log.d(TAG, "TouchController onMotionEvent: action=${event.action}, pointerCount=${event.pointerCount}")

                        // When gesture training is active, request delegating to pass ALL events
                        // through to the app without TalkBack processing them.
                        // This allows the app's gesture recognizers to handle both swipes and taps.
                        if (isGestureTraining()) {
                            touchController?.let { controller ->
                                if (controller.state == TouchInteractionController.STATE_TOUCH_INTERACTING) {
                                    Log.d(TAG, "Requesting delegating mode to bypass TalkBack")
                                    controller.requestDelegating()
                                }
                            }
                        }
                    }

                    override fun onStateChanged(state: Int) {
                        val stateName = when (state) {
                            TouchInteractionController.STATE_CLEAR -> "CLEAR"
                            TouchInteractionController.STATE_TOUCH_INTERACTING -> "TOUCH_INTERACTING"
                            TouchInteractionController.STATE_TOUCH_EXPLORING -> "TOUCH_EXPLORING"
                            TouchInteractionController.STATE_DRAGGING -> "DRAGGING"
                            TouchInteractionController.STATE_DELEGATING -> "DELEGATING"
                            else -> "UNKNOWN($state)"
                        }
                        Log.d(TAG, "TouchController state changed to: $stateName")
                    }
                }

                touchController?.registerCallback(mainExecutor, touchControllerCallback!!)
                Log.i(TAG, "Registered TouchInteractionController callback")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to setup TouchInteractionController", e)
            }
        }
    }

    /**
     * Called when raw motion events are received from the configured motion event sources.
     * On API 32+, we use TouchInteractionController with requestDelegating() instead,
     * which passes events directly to the app's normal touch pipeline.
     */
    override fun onMotionEvent(event: MotionEvent) {
        Log.d(TAG, "onMotionEvent: action=${event.action}, pointerCount=${event.pointerCount}, x=${event.x}, y=${event.y}")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
            if (isGestureTraining()) {
                broadcastMotionEvent(event)
            }
        }
    }

    private fun broadcastMotionEvent(event: MotionEvent) {
        val intent = Intent(Constants.SERVICE_ACTION)
        intent.setPackage(packageName)
        // MotionEvent must be copied because the original may be recycled
        intent.putExtra(Constants.SERVICE_MOTION_EVENT, MotionEvent.obtain(event))
        sendBroadcast(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind")

        // Cleanup TouchInteractionController
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            touchControllerCallback?.let { callback ->
                touchController?.unregisterCallback(callback)
            }
            touchController = null
            touchControllerCallback = null
        }

        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.i(TAG, "onInterrupt")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i(TAG, "onAccessibilityEvent: $event")

        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        if (event.packageName == null || event.packageName.isEmpty()) {
            return
        }

    
        if (event.packageName == this.packageName) {
            return
        }
        if (event.packageName.contains("accessibility")) {
            return
        }

        val serviceName = getString(R.string.service_label)
        if (event.text.contains(serviceName)) {
            return
        }

        if (isInApp()) {
            return
        }

        // Kill the service
        kill()
    }

    override fun onGesture(gestureId: Int): Boolean {
        Log.i(TAG, "onGesture called with gestureId: $gestureId")

        // Broadcast gesture to GestureActivity
        val gesture = Gesture.from(gestureId)
        Log.i(TAG, "Mapped gestureId $gestureId to gesture: $gesture")

        if (gesture != null) {
            broadcast(Constants.SERVICE_GESTURE, gesture)
        } else {
            Log.w(TAG, "Unknown gestureId: $gestureId - not mapped to any Gesture")
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

            if (flags and capability == capability) {
                count++
            }
        }

        // Touch Exploration capability should be granted to at least two services: ScreenReaderService and TalkBack/VoiceAssistant/other.
        return count >= 2
    }

    private fun isGestureTraining(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningTasks(1).firstOrNull()?.topActivity?.let { activity ->
            // Check if the user is in the GestureActivity (where gesture training happens)
            return activity.className == GESTURE_ACTIVITY_CLASS_NAME
        }
        return false
    }

    private fun isInApp(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningTasks(1).firstOrNull()?.topActivity?.let { activity ->
            // Check if user is in any of the app's activities
            return activity.className == MAIN_ACTIVITY_CLASS_NAME ||
                   activity.className == GESTURE_ACTIVITY_CLASS_NAME ||
                   activity.className == ACTION_ACTIVITY_CLASS_NAME
        }
        return false
    }

    private fun startGestureTraining() {
        val gestures = Gesture.randomized()
        gestures.forEach { gesture ->
            gesture.completed(this, false)
        }
    }

    companion object {
        const val MIN_API_FOR_TALKBACK_COMPATIBILITY = Build.VERSION_CODES.TIRAMISU

        fun supportsTalkBackCompatibility(): Boolean {
            return Build.VERSION.SDK_INT >= MIN_API_FOR_TALKBACK_COMPATIBILITY
        }

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
                .setTitle(context.getSpannable(R.string.service_enable_title))
                .setMessage(context.getSpannable(R.string.service_enable_message))
                .setPositiveButton(context.getSpannable(R.string.action_activate)) { _, _ ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
                .setNegativeButton(context.getSpannable(R.string.action_cancel)) { _, _ ->
                    // Dismiss
                }
                .setCancelable(false)
                .show()
        }

        var instructions = true
    }
}