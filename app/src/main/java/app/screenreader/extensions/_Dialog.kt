package app.screenreader.extensions

import androidx.appcompat.app.AlertDialog
import app.screenreader.model.Gesture

/**
 * Created by Jan Jaap de Groot on 07/12/2020
 * Copyright 2020 Stichting Appt
 */
fun AlertDialog.onAccessibilityGesture(gesture: Gesture) {
    when (gesture) {
        Gesture.ONE_FINGER_SWIPE_LEFT -> AlertDialog.BUTTON_NEGATIVE
        Gesture.ONE_FINGER_SWIPE_RIGHT -> AlertDialog.BUTTON_POSITIVE
        Gesture.ONE_FINGER_SWIPE_DOWN -> AlertDialog.BUTTON_NEUTRAL
        else -> null
    }?.let { button ->
        getButton(button)?.performClick()
    }
}