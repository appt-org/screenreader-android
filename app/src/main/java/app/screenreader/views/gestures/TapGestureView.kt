package app.screenreader.views.gestures

import android.content.Context
import android.view.MotionEvent
import it.sephiroth.android.library.uigestures.*
import app.screenreader.extensions.isEnd
import app.screenreader.extensions.isStart
import app.screenreader.model.Gesture
import app.screenreader.model.Touch
import app.screenreader.R

/**
 * Created by Jan Jaap de Groot on 22/10/2020
 * Copyright 2020 Stichting Appt
 */
class TapGestureView(
    context: Context,
    gesture: Gesture
): GestureView(gesture, context) {

    private val TAG = "TapGestureView"
    private var tapped = false

    private val gestureListener = { recognizer: UIGestureRecognizer ->
        var fingers = 1
        var taps = 1
        var hold = false

        if (recognizer is UITapGestureRecognizer) {
            fingers = recognizer.touchesRequired
            taps = recognizer.tapsRequired
        } else if (recognizer is UILongPressGestureRecognizer) {
            fingers = recognizer.touchesRequired
            taps = recognizer.tapsRequired
            hold = true
        }

        when {
            fingers != gesture.fingers -> {

                incorrect(R.string.gestures_feedback_tap_fingers, gesture.fingers, fingers)
            }
            taps != gesture.taps -> {
                incorrect(R.string.gestures_feedback_tap_amount, gesture.taps, taps)
            }
            hold != gesture.hold -> {
                if (hold) {
                    incorrect(R.string.gestures_feedback_hold_shorter)
                } else {
                    incorrect(R.string.gestures_feedback_hold_longer)
                }
            }
            else -> {
                correct()
            }
        }

        showTap(taps, hold)
    }

    private fun tapGestureRecognizer(fingers: Int, taps: Int, requiresFailureOf: UIGestureRecognizer? = null): UITapGestureRecognizer {
        val recognizer = object : UITapGestureRecognizer(context) {
            override fun onTouchEvent(event: MotionEvent): Boolean {
                val result = super.onTouchEvent(event)
                if (requiresFailureOf == null) {
                    this@TapGestureView.onTouchEvent(event)
                }
                return result
            }
        }
        recognizer.tag = "tap-$fingers-fingers-$taps-taps"
        recognizer.touchesRequired = fingers
        recognizer.tapsRequired = taps
        recognizer.requireFailureOf = requiresFailureOf
        recognizer.actionListener = gestureListener
        return recognizer
    }

    private fun longPressRecognizer(fingers: Int, taps: Int, requiresFailureOf: UIGestureRecognizer? = null): UILongPressGestureRecognizer {
        val recognizer = object : UILongPressGestureRecognizer(context) {
            override fun onTouchEvent(event: MotionEvent): Boolean {
                val result = super.onTouchEvent(event)
                if (requiresFailureOf == null) {
                    this@TapGestureView.onTouchEvent(event)
                }
                return result
            }
        }
        recognizer.tag = "long-press-$fingers-fingers-$taps-taps"
        recognizer.touchesRequired = fingers
        recognizer.tapsRequired = taps
        recognizer.requireFailureOf = requiresFailureOf
        recognizer.actionListener = gestureListener
        return recognizer
    }

    init {
        val delegate = UIGestureRecognizerDelegate()

        val fourFingerDoubleTapLongPressRecognizer = longPressRecognizer(4, 2)
        val threeFingerDoubleTapLongPressRecognizer = longPressRecognizer(3, 2, fourFingerDoubleTapLongPressRecognizer)
        val twoFingerDoubleTapLongPressRecognizer = longPressRecognizer(2, 2, threeFingerDoubleTapLongPressRecognizer)
        val oneFingerDoubleTapLongPressRecognizer = longPressRecognizer(1, 2, twoFingerDoubleTapLongPressRecognizer)

        val fourFingerOneTapLongPressRecognizer = longPressRecognizer(4, 1, fourFingerDoubleTapLongPressRecognizer)
        val threeFingerOneTapLongPressRecognizer = longPressRecognizer(3, 1, threeFingerDoubleTapLongPressRecognizer)
        val twoFingerOneTapLongPressRecognizer = longPressRecognizer(2, 1, twoFingerDoubleTapLongPressRecognizer)
        val oneFingerOneTapLongPressRecognizer = longPressRecognizer(1, 1, oneFingerDoubleTapLongPressRecognizer)

        val fourFingerTripleTapRecognizer = tapGestureRecognizer(4, 3, threeFingerDoubleTapLongPressRecognizer)
        val fourFingerTwoTapRecognizer = tapGestureRecognizer(4, 2, fourFingerTripleTapRecognizer)
        val fourFingerOneTapRecognizer = tapGestureRecognizer(4, 1, fourFingerTwoTapRecognizer)

        val threeFingerTripleTapRecognizer = tapGestureRecognizer(3, 3, fourFingerTripleTapRecognizer)
        val threeFingerTwoTapRecognizer = tapGestureRecognizer(3, 2, threeFingerTripleTapRecognizer)
        val threeFingerOneTapRecognizer = tapGestureRecognizer(3, 1, threeFingerTwoTapRecognizer)

        val twoFingerTripleTapRecognizer = tapGestureRecognizer(2, 3, threeFingerTripleTapRecognizer)
        val twoFingerTwoTapRecognizer = tapGestureRecognizer(2, 2, twoFingerTripleTapRecognizer)
        val twoFingerOneTapRecognizer = tapGestureRecognizer(2, 1, twoFingerTwoTapRecognizer)

        val oneFingerTripleTapRecognizer = tapGestureRecognizer(1, 3, twoFingerTripleTapRecognizer)
        val oneFingerTwoTapRecognizer = tapGestureRecognizer(1, 2, oneFingerTripleTapRecognizer)
        val oneFingerOneTapRecognizer = tapGestureRecognizer(1, 1, oneFingerTwoTapRecognizer)

        delegate.addGestureRecognizer(fourFingerDoubleTapLongPressRecognizer)
        delegate.addGestureRecognizer(threeFingerDoubleTapLongPressRecognizer)
        delegate.addGestureRecognizer(twoFingerDoubleTapLongPressRecognizer)
        delegate.addGestureRecognizer(oneFingerDoubleTapLongPressRecognizer)

        delegate.addGestureRecognizer(fourFingerOneTapLongPressRecognizer)
        delegate.addGestureRecognizer(threeFingerOneTapLongPressRecognizer)
        delegate.addGestureRecognizer(twoFingerOneTapLongPressRecognizer)
        delegate.addGestureRecognizer(oneFingerOneTapLongPressRecognizer)

        delegate.addGestureRecognizer(fourFingerTripleTapRecognizer)
        delegate.addGestureRecognizer(fourFingerTwoTapRecognizer)
        delegate.addGestureRecognizer(fourFingerOneTapRecognizer)

        delegate.addGestureRecognizer(threeFingerTripleTapRecognizer)
        delegate.addGestureRecognizer(threeFingerTwoTapRecognizer)
        delegate.addGestureRecognizer(threeFingerOneTapRecognizer)

        delegate.addGestureRecognizer(twoFingerTripleTapRecognizer)
        delegate.addGestureRecognizer(twoFingerTwoTapRecognizer)
        delegate.addGestureRecognizer(twoFingerOneTapRecognizer)

        delegate.addGestureRecognizer(oneFingerTripleTapRecognizer)
        delegate.addGestureRecognizer(oneFingerTwoTapRecognizer)
        delegate.addGestureRecognizer(oneFingerOneTapRecognizer)

        setGestureDelegate(delegate)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.isStart() == true) {
            tapped = false
        } else if (event?.isEnd() == true && !tapped) {
            postDelayed({
                if (!tapped) {
                    incorrect(R.string.gestures_feedback_tap)
                }
            }, 500)
        }
        return super.onTouchEvent(event)
    }

    override fun onAccessibilityGesture(gesture: Gesture) {
        if (this.gesture == gesture) {
            correct()
        } else {
            incorrect(R.string.gestures_feedback_tap)
        }
    }

    private fun showTap(taps: Int, longPress: Boolean) {
        if (tapped) {
            return
        }

        tapped = true

        for (key in touches.keys) {
            touches[key]?.lastOrNull()?.let { lastTouch ->
                val touch = Touch(lastTouch.x, lastTouch.y, taps, longPress)
                touches[key] = arrayListOf(touch)
            }
        }
        invalidate()
    }
}