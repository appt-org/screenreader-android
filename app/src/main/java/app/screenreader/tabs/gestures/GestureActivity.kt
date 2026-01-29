package app.screenreader.tabs.gestures

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.*
import android.os.Build
import android.text.SpannableString
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import app.screenreader.R
import app.screenreader.extensions.*
import app.screenreader.helpers.Accessibility
import app.screenreader.helpers.Events
import app.screenreader.model.Constants
import app.screenreader.model.Gesture
import app.screenreader.services.ScreenReaderService
import app.screenreader.views.gestures.GestureView
import app.screenreader.views.gestures.GestureViewCallback
import app.screenreader.widgets.ToolbarActivity
import java.util.*
import kotlin.concurrent.schedule

/**
 * Created by Jan Jaap de Groot on 12/10/2020
 * Copyright 2020 Stichting Appt
 */
class GestureActivity: ToolbarActivity(), GestureViewCallback {

    private val TAG = "GestureActivity"

    private val gestures: ArrayList<Gesture> by lazy {
        intent.getGestures() ?: arrayListOf()
    }

    private val gesture: Gesture by lazy {
        gestures.firstOrNull() ?: intent.getGesture() ?: Gesture.ONE_FINGER_TOUCH
    }

    private val instructions: Boolean by lazy {
        intent.getInstructions()
    }

    private lateinit var gestureView: GestureView

    private var dialog: AlertDialog? = null
    private var errorLimit = 5
    private var errorCount = 0
    private var finished = false

    private val container get() = findViewById<FrameLayout>(R.id.container)
    private val titleTextView get() = findViewById<android.widget.TextView>(R.id.titleTextView)
    private val descriptionTextView get() = findViewById<android.widget.TextView>(R.id.descriptionTextView)
    private val gestureImageView get() = findViewById<android.widget.ImageView>(R.id.gestureImageView)
    private val feedbackTextView get() = findViewById<android.widget.TextView>(R.id.feedbackTextView)

    private val isPracticing: Boolean
        get() = gestures.isNotEmpty()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Received kill event
            intent?.getBooleanExtra(Constants.SERVICE_KILLED, false)?.let { killed ->
                Log.d(TAG, "ScreenReaderService has been killed")
            }

            // Received gesture event
            (intent?.getSerializableExtra(Constants.SERVICE_GESTURE) as? Gesture)?.let { gesture ->
                if (dialog != null) {
                    dialog?.onAccessibilityGesture(gesture) // Pass gesture to AlertDialog if shown.
                } else {
                    gestureView.onAccessibilityGesture(gesture) // Pass gesture to GestureView.
                }
            }

            // Received motion event (from ScreenReaderService intercepting raw touch events)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (intent?.getParcelableExtra(Constants.SERVICE_MOTION_EVENT, MotionEvent::class.java))?.let { event ->
                    Log.d(TAG, "Received motion event from service: action=${event.action}, pointerCount=${event.pointerCount}")
                    gestureView.dispatchTouchEvent(event)
                    event.recycle()
                }
            }
        }
    }

    override fun getLayoutId() = R.layout.activity_gesture

    override fun getToolbarTitle() = null

    override fun onViewCreated() {
        super.onViewCreated()

        // Setup GestureView
        gestureView = gesture.view(this)
        gestureView.callback = this
        container.addView(gestureView)
        Accessibility.setTraversalOrder(gestureView)

        // Setup other views
        titleTextView.text = gesture.title(this)
        descriptionTextView.text = gesture.description(this)
        gestureImageView.setImageResource(gesture.image(this))

        // Setup instructions
        if (instructions) {
            Accessibility.label(container, String.format("%s. %s", titleTextView.text, descriptionTextView.text))
        } else {
            descriptionTextView.setVisible(false)
            gestureImageView.setVisible(false)
            Accessibility.label(container, titleTextView.text)
        }

        // Listen to events from ScreenReaderService
        val filter = IntentFilter()
        filter.addAction(Constants.SERVICE_ACTION)
        registerBroadcastReceiver(receiver, filter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (instructions) {
            menuInflater.inflate(R.menu.explanation, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_explanation) {
            showDialog(title = gesture.title(this), message = gesture.explanation(this))
            return true
        }
        return false
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        dialog?.dismiss()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        if (Accessibility.screenReader(this)) {
            if (ScreenReaderService.isEnabled(this)) {
                Timer().schedule(500) {
                    runOnUiThread {
                        Accessibility.focus(gestureView)
                    }
                }
            } else {
                showError(R.string.service_disabled_error) {
                    finish()
                }
            }
        }
    }

    override fun correct(gesture: Gesture) {
        finished = true
        feedbackTextView.visibility = View.GONE

        events.log(Events.Category.gesture_completed, gesture.identifier, errorCount)
        gesture.completed(baseContext, true)
        setResult(RESULT_OK)

        toast(R.string.gesture_completed_message) {
            if (isPracticing) {
                next()
            }
            finish()
        }
    }

    private fun next() {
        gestures.removeAt(0)

        startActivity<GestureActivity> {
            setGestures(gestures)
            setInstructions(instructions)
        }
    }

    override fun incorrect(gesture: Gesture, feedback: SpannableString) {
        if (finished) {
            return
        }

        val feedback = if (instructions) {
            feedback
        } else {
            getSpannable(R.string.gestures_feedback_incorrect)
        }

        // Show feedback
        feedbackTextView.animate()
            .alpha(0.0f)
            .setDuration(250)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator) {
                    if (finished) {
                        return
                    }
                    feedbackTextView.visibility = View.VISIBLE
                    feedbackTextView.text = feedback
                    feedbackTextView.animate().alpha(1.0f).setDuration(250)
                }
            })

        // Check if error count exceeds limit
        errorCount++
        if (errorCount < errorLimit) {
            return
        }

        // Show option dialog
        val incorrectMessage = getSpannable(R.string.gestures_feedback_incorrect_amount, errorCount)

        val actionMessage = if (Accessibility.screenReader(this)) {
            if (isPracticing) {
                getSpannable(R.string.gestures_feedback_wrong_action_talkback_practice)
            } else {
                getSpannable(R.string.gestures_feedback_wrong_action_talkback)
            }
        } else {
            if (isPracticing) {
                getSpannable(R.string.gestures_feedback_wrong_action_practice)
            } else {
                getSpannable(R.string.gestures_feedback_wrong_action)
            }
        }

        val message = TextUtils.concat(incorrectMessage, actionMessage)

        val builder = AlertDialog.Builder(this)
                        .setMessage(message)
                        .setPositiveButton(getSpannable(R.string.action_continue)) { _, _ ->
                            errorLimit *= 2
                        }
                        .setNegativeButton(getSpannable(R.string.action_stop)) { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .setOnDismissListener {
                            dialog = null
                        }

        if (isPracticing) {
            builder.setNeutralButton(getSpannable(R.string.action_skip)) { _, _ ->
                next()
                finish()
            }
        }

        if (dialog == null) {
            dialog = builder.show()
        }
    }
}