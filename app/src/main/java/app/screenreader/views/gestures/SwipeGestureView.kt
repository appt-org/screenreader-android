package app.screenreader.views.gestures

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import app.screenreader.R
import app.screenreader.extensions.isEnd
import app.screenreader.extensions.isStart
import app.screenreader.model.Direction
import app.screenreader.model.Gesture

/**
 * Created by Jan Jaap de Groot on 15/10/2020
 * Copyright 2020 Stichting Appt
 */
open class SwipeGestureView(
    context: Context,
    gesture: Gesture
): GestureView(gesture, context) {

    private val TAG = "SwipeGestureView"
    var swiped = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        if (event != null) {
            Log.d(TAG, "onTouchEvent: action=${event.action}, pointerCount=${event.pointerCount}")
            gestureDetector.onTouchEvent(event)

            if (event.isStart()) {
                swiped = false
            } else if (event.isEnd()) {
                if (!swiped) {
                    incorrect(R.string.gestures_feedback_swipe_larger)
                }
            }
        }

        return true
    }

    override fun onAccessibilityGesture(gesture: Gesture) {
        Log.d(TAG, "onAccessibilityGesture received: $gesture (expected: ${this.gesture})")
        when {
            this.gesture == gesture -> {
                Log.d(TAG, "Gesture matches! Marking correct.")
                correct()
            }
            gesture.directions.isNotEmpty() -> {
                Log.d(TAG, "Gesture has directions but doesn't match, calling onSwipe")
                onSwipe(gesture.directions)
            }
            else -> {
                Log.d(TAG, "Unknown gesture, marking incorrect")
                incorrect(R.string.gestures_feedback_swipe)
            }
        }
    }

    fun onSwipe(directions: Array<Direction>) {
        swiped = true

        val fingers = directions.map { it.fingers }.average().toInt()
        Log.d(TAG, "onSwipe (touch-based): directions=${directions.joinToString { it.toString() }}, fingers=$fingers, expected=${gesture.fingers}")

        when {
            fingers != gesture.fingers -> {
                incorrect(R.string.gestures_feedback_fingers, gesture.fingers, fingers)
            }
            directions.contentEquals(gesture.directions) -> {
                correct()
            }
            else -> {
                incorrect(R.string.gestures_feedback_swipe_directions, Direction.feedback(context, directions))
            }
        }
    }

    /**
     * Custom implementation of GestureDetector to detect swipe directions
     */
    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        private val THRESHOLD = 15
        private var path = arrayListOf<Direction>()

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            Log.d(TAG, "onScroll, distanceX: $distanceX, distanceY: $distanceY")

            // Determine direction based on the DOMINANT axis (larger absolute movement)
            var direction = Direction.UNKNOWN
            val absX = kotlin.math.abs(distanceX)
            val absY = kotlin.math.abs(distanceY)

            // Only detect direction if movement exceeds threshold
            // Prioritize the axis with larger movement to avoid detecting
            // slight horizontal drift during vertical swipes (and vice versa)
            if (absX > THRESHOLD || absY > THRESHOLD) {
                if (absY >= absX) {
                    // Vertical movement is dominant
                    direction = if (distanceY > 0) Direction.UP else Direction.DOWN
                } else {
                    // Horizontal movement is dominant
                    direction = if (distanceX > 0) Direction.LEFT else Direction.RIGHT
                }
            }

            if (direction != Direction.UNKNOWN) {
                // Determine amount of fingers
                direction.fingers = e2.pointerCount ?: 1

                if (path.isEmpty()) {
                    // Add first direction
                    path.add(direction)
                    Log.d(TAG, "Direction: $direction, fingers: ${direction.fingers}")
                } else {
                    // Only add if direction is different than last direction
                    path.lastOrNull()?.let { lastDirection ->
                        if (direction != lastDirection) {
                            path.add(direction)
                            Log.d(TAG, "Direction: $direction, fingers: ${direction.fingers}")
                        }
                    }
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            Log.d(TAG, "onFling, velocityX: $velocityX, velocityY: $velocityY")

            if (path.isNotEmpty()) {
                onSwipe(path.toTypedArray())
            }
            path.clear()

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    })
}