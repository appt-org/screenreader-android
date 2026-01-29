package app.screenreader.model

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Build
import android.text.SpannableString
import app.screenreader.extensions.getSpannable
import app.screenreader.extensions.identifier
import app.screenreader.helpers.Preferences
import app.screenreader.views.gestures.*
import java.io.Serializable
import kotlin.collections.ArrayList

/**
 * Created by Jan Jaap de Groot on 12/10/2020
 * Copyright 2020 Stichting Appt
 */
enum class Gesture(
    val fingers: Int = 1,
    val taps: Int = 1,
    val hold: Boolean = false,
    val directions: Array<Direction> = arrayOf()
): Training, Serializable {

    ONE_FINGER_TOUCH(fingers = 1, taps = 1),

    ONE_FINGER_SWIPE_UP(fingers = 1, directions = arrayOf(Direction.UP)),
    ONE_FINGER_SWIPE_RIGHT(fingers = 1, directions = arrayOf(Direction.RIGHT)),
    ONE_FINGER_SWIPE_DOWN(fingers = 1, directions = arrayOf(Direction.DOWN)),
    ONE_FINGER_SWIPE_LEFT(fingers = 1, directions = arrayOf(Direction.LEFT)),

    TWO_FINGER_SWIPE_UP(fingers = 2, directions = arrayOf(Direction.UP)),
    TWO_FINGER_SWIPE_RIGHT(fingers = 2, directions = arrayOf(Direction.RIGHT)),
    TWO_FINGER_SWIPE_DOWN(fingers = 2, directions = arrayOf(Direction.DOWN)),
    TWO_FINGER_SWIPE_LEFT(fingers = 2, directions = arrayOf(Direction.LEFT)),

    THREE_FINGER_SWIPE_UP(fingers = 3, directions = arrayOf(Direction.UP)),
    THREE_FINGER_SWIPE_DOWN(fingers = 3, directions = arrayOf(Direction.DOWN)),

    //ONE_FINGER_SWIPE_UP_THEN_RIGHT(fingers = 1, directions = arrayOf(Direction.UP, Direction.RIGHT)),
    ONE_FINGER_SWIPE_UP_THEN_DOWN(fingers = 1, directions = arrayOf(Direction.UP, Direction.DOWN)),
    ONE_FINGER_SWIPE_UP_THEN_LEFT(fingers = 1, directions = arrayOf(Direction.UP, Direction.LEFT)),

    ONE_FINGER_SWIPE_RIGHT_THEN_DOWN(fingers = 1, directions = arrayOf(Direction.RIGHT, Direction.DOWN)),
    ONE_FINGER_SWIPE_RIGHT_THEN_LEFT(fingers = 1, directions = arrayOf(Direction.RIGHT, Direction.LEFT)),

    ONE_FINGER_SWIPE_DOWN_THEN_UP(fingers = 1, directions = arrayOf(Direction.DOWN, Direction.UP)),
    ONE_FINGER_SWIPE_DOWN_THEN_RIGHT(fingers = 1, directions = arrayOf(Direction.DOWN, Direction.RIGHT)),
    ONE_FINGER_SWIPE_DOWN_THEN_LEFT(fingers = 1, directions = arrayOf(Direction.DOWN, Direction.LEFT)),

    ONE_FINGER_SWIPE_LEFT_THEN_UP(fingers = 1, directions = arrayOf(Direction.LEFT, Direction.UP)),
    ONE_FINGER_SWIPE_LEFT_THEN_RIGHT(fingers = 1, directions = arrayOf(Direction.LEFT, Direction.RIGHT)),
    ONE_FINGER_SWIPE_LEFT_THEN_DOWN(fingers = 1, directions = arrayOf(Direction.LEFT, Direction.DOWN)),

    ONE_FINGER_DOUBLE_TAP(fingers = 1, taps = 2),
    ONE_FINGER_DOUBLE_TAP_HOLD(fingers = 1, taps = 2, hold = true),
    ONE_FINGER_TRIPLE_TAP(fingers = 1, taps = 3),

    TWO_FINGER_TAP(fingers = 2, taps = 1),
    TWO_FINGER_DOUBLE_TAP(fingers = 2, taps = 2),
    TWO_FINGER_DOUBLE_TAP_HOLD(fingers = 2, taps = 2, hold = true),
    TWO_FINGER_TRIPLE_TAP(fingers = 2, taps = 3),

    THREE_FINGER_TAP(fingers = 3, taps = 1),
    THREE_FINGER_DOUBLE_TAP(fingers = 3, taps = 2),
    THREE_FINGER_DOUBLE_TAP_HOLD(fingers = 3, taps = 2, hold = true),
    THREE_FINGER_TRIPLE_TAP(fingers = 3, taps = 3),

    FOUR_FINGER_TAP(fingers = 4, taps = 1),
    FOUR_FINGER_DOUBLE_TAP(fingers = 4, taps = 2),
    FOUR_FINGER_DOUBLE_TAP_HOLD(fingers = 4, taps = 2, hold = true);

    private fun spannable(context: Context, property: String): SpannableString {
        return context.getSpannable("gesture_${identifier}_${property}")
    }

    override fun title(context: Context) = spannable(context, "title")
    fun description(context: Context) = spannable(context, "description")
    fun explanation(context: Context) = spannable(context, "explanation")

    fun image(context: Context): Int {
        return context.resources.getIdentifier("gesture_${identifier}", "drawable", context.packageName)
    }

    fun view(context: Context): GestureView {
        return when (this) {
            ONE_FINGER_TOUCH -> {
                TouchGestureView(context, this)
            }

            ONE_FINGER_SWIPE_UP,
            ONE_FINGER_SWIPE_RIGHT,
            ONE_FINGER_SWIPE_DOWN,
            ONE_FINGER_SWIPE_LEFT,

            TWO_FINGER_SWIPE_UP,
            TWO_FINGER_SWIPE_RIGHT,
            TWO_FINGER_SWIPE_DOWN,
            TWO_FINGER_SWIPE_LEFT,

            THREE_FINGER_SWIPE_UP,
            THREE_FINGER_SWIPE_DOWN,

            //ONE_FINGER_SWIPE_UP_THEN_RIGHT,
            ONE_FINGER_SWIPE_UP_THEN_DOWN,
            ONE_FINGER_SWIPE_UP_THEN_LEFT,

            ONE_FINGER_SWIPE_RIGHT_THEN_DOWN,
            ONE_FINGER_SWIPE_RIGHT_THEN_LEFT,

            ONE_FINGER_SWIPE_DOWN_THEN_UP,
            ONE_FINGER_SWIPE_DOWN_THEN_RIGHT,
            ONE_FINGER_SWIPE_DOWN_THEN_LEFT,

            ONE_FINGER_SWIPE_LEFT_THEN_UP,
            ONE_FINGER_SWIPE_LEFT_THEN_RIGHT,
            ONE_FINGER_SWIPE_LEFT_THEN_DOWN -> {
                SwipeGestureView(context, this)
            }

            ONE_FINGER_DOUBLE_TAP,
            ONE_FINGER_DOUBLE_TAP_HOLD,
            ONE_FINGER_TRIPLE_TAP,

            TWO_FINGER_TAP,
            TWO_FINGER_DOUBLE_TAP,
            TWO_FINGER_TRIPLE_TAP,
            TWO_FINGER_DOUBLE_TAP_HOLD,

            THREE_FINGER_TAP,
            THREE_FINGER_DOUBLE_TAP,
            THREE_FINGER_DOUBLE_TAP_HOLD,
            THREE_FINGER_TRIPLE_TAP,

            FOUR_FINGER_TAP,
            FOUR_FINGER_DOUBLE_TAP,
            FOUR_FINGER_DOUBLE_TAP_HOLD -> {
                TapGestureView(context, this)
            }
        }
    }

    override fun completed(context: Context): Boolean {
        return Preferences.isActionCompleted(this)
    }
    override fun completed(context: Context, completed: Boolean) {
        Preferences.isActionCompleted(this, completed)
    }

    companion object {
        fun all(): ArrayList<Gesture> {
            return values().toCollection(arrayListOf())
        }

        fun randomized(): ArrayList<Gesture> {
            val gestures = all()
            gestures.shuffle()
            return gestures
        }

        fun from(gestureId: Int): Gesture? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (gestureId) {
                    // ONE FINGER TAP
                    AccessibilityService.GESTURE_DOUBLE_TAP -> return ONE_FINGER_DOUBLE_TAP
                    AccessibilityService.GESTURE_DOUBLE_TAP_AND_HOLD -> return ONE_FINGER_DOUBLE_TAP_HOLD

                    // TWO FINGER TAP
                    AccessibilityService.GESTURE_2_FINGER_SINGLE_TAP -> return TWO_FINGER_TAP
                    AccessibilityService.GESTURE_2_FINGER_DOUBLE_TAP -> return TWO_FINGER_DOUBLE_TAP
                    AccessibilityService.GESTURE_2_FINGER_DOUBLE_TAP_AND_HOLD -> return TWO_FINGER_DOUBLE_TAP_HOLD
                    AccessibilityService.GESTURE_2_FINGER_TRIPLE_TAP -> return TWO_FINGER_TRIPLE_TAP

                    // THREE FINGER TAP
                    AccessibilityService.GESTURE_3_FINGER_SINGLE_TAP -> return THREE_FINGER_TAP
                    AccessibilityService.GESTURE_3_FINGER_DOUBLE_TAP -> return THREE_FINGER_DOUBLE_TAP
                    AccessibilityService.GESTURE_3_FINGER_DOUBLE_TAP_AND_HOLD -> return THREE_FINGER_DOUBLE_TAP_HOLD
                    AccessibilityService.GESTURE_3_FINGER_TRIPLE_TAP -> return THREE_FINGER_TRIPLE_TAP

                    // FOUR FINGER TAP
                    AccessibilityService.GESTURE_4_FINGER_SINGLE_TAP -> return FOUR_FINGER_TAP
                    AccessibilityService.GESTURE_4_FINGER_DOUBLE_TAP -> return FOUR_FINGER_DOUBLE_TAP
                    AccessibilityService.GESTURE_4_FINGER_DOUBLE_TAP_AND_HOLD -> return FOUR_FINGER_DOUBLE_TAP_HOLD

                    // TWO FINGER SWIPE
                    AccessibilityService.GESTURE_2_FINGER_SWIPE_UP -> return TWO_FINGER_SWIPE_UP
                    AccessibilityService.GESTURE_2_FINGER_SWIPE_RIGHT -> return TWO_FINGER_SWIPE_RIGHT
                    AccessibilityService.GESTURE_2_FINGER_SWIPE_DOWN -> return TWO_FINGER_SWIPE_DOWN
                    AccessibilityService.GESTURE_2_FINGER_SWIPE_LEFT -> return TWO_FINGER_SWIPE_LEFT

                    // THREE FINGER SWIPE
                    AccessibilityService.GESTURE_3_FINGER_SWIPE_UP -> return THREE_FINGER_SWIPE_UP
                    AccessibilityService.GESTURE_3_FINGER_SWIPE_DOWN -> return THREE_FINGER_SWIPE_DOWN

                    // UNUSED
                    AccessibilityService.GESTURE_3_FINGER_SWIPE_RIGHT,
                    AccessibilityService.GESTURE_3_FINGER_SWIPE_LEFT,
                    AccessibilityService.GESTURE_4_FINGER_SWIPE_UP,
                    AccessibilityService.GESTURE_4_FINGER_SWIPE_RIGHT,
                    AccessibilityService.GESTURE_4_FINGER_SWIPE_DOWN,
                    AccessibilityService.GESTURE_4_FINGER_SWIPE_LEFT -> null
                }
            }

            return when (gestureId) {
                // ONE FINGER SWIPE
                AccessibilityService.GESTURE_SWIPE_UP -> return ONE_FINGER_SWIPE_UP
                AccessibilityService.GESTURE_SWIPE_RIGHT -> return ONE_FINGER_SWIPE_RIGHT
                AccessibilityService.GESTURE_SWIPE_DOWN -> return ONE_FINGER_SWIPE_DOWN
                AccessibilityService.GESTURE_SWIPE_LEFT -> return ONE_FINGER_SWIPE_LEFT

                // ONE FINGER SWIPE UP
                //AccessibilityService.GESTURE_SWIPE_UP_AND_RIGHT -> return ONE_FINGER_SWIPE_UP_THEN_RIGHT
                AccessibilityService.GESTURE_SWIPE_UP_AND_DOWN -> return ONE_FINGER_SWIPE_UP_THEN_DOWN
                AccessibilityService.GESTURE_SWIPE_UP_AND_LEFT -> return ONE_FINGER_SWIPE_UP_THEN_LEFT

                // ONE FINGER SWIPE RIGHT
                AccessibilityService.GESTURE_SWIPE_RIGHT_AND_UP -> return null
                AccessibilityService.GESTURE_SWIPE_RIGHT_AND_DOWN -> return ONE_FINGER_SWIPE_RIGHT_THEN_DOWN
                AccessibilityService.GESTURE_SWIPE_RIGHT_AND_LEFT -> return ONE_FINGER_SWIPE_RIGHT_THEN_LEFT

                // ONE FINGER SWIPE DOWN
                AccessibilityService.GESTURE_SWIPE_DOWN_AND_UP -> return ONE_FINGER_SWIPE_DOWN_THEN_UP
                AccessibilityService.GESTURE_SWIPE_DOWN_AND_RIGHT -> return ONE_FINGER_SWIPE_DOWN_THEN_RIGHT
                AccessibilityService.GESTURE_SWIPE_DOWN_AND_LEFT -> return ONE_FINGER_SWIPE_DOWN_THEN_LEFT

                // ONE FINGER SWIPE LEFT
                AccessibilityService.GESTURE_SWIPE_LEFT_AND_UP -> return ONE_FINGER_SWIPE_LEFT_THEN_UP
                AccessibilityService.GESTURE_SWIPE_LEFT_AND_RIGHT -> return ONE_FINGER_SWIPE_LEFT_THEN_RIGHT
                AccessibilityService.GESTURE_SWIPE_LEFT_AND_DOWN -> return ONE_FINGER_SWIPE_LEFT_THEN_DOWN

                else -> null
            }
        }
    }
}