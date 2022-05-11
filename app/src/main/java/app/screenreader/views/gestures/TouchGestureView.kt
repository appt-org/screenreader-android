package app.screenreader.views.gestures

import android.content.Context
import android.view.MotionEvent
import app.screenreader.R
import app.screenreader.extensions.isStart
import app.screenreader.model.Gesture

/**
 * Created by Jan Jaap de Groot on 22/10/2020
 * Copyright 2020 Stichting Appt
 */
class TouchGestureView(
    context: Context,
    gesture: Gesture
): GestureView(gesture, context) {

    private var touched = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        if (!touched && event?.isStart() == true) {
            touched = true
            correct()
        }

        return true
    }

    override fun onAccessibilityGesture(gesture: Gesture) {
        if (gesture == gesture) {
            correct()
        } else {
            incorrect(R.string.gestures_feedback_touch)
        }
    }
}