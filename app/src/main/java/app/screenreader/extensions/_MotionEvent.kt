package app.screenreader.extensions

import android.view.MotionEvent

/**
 * Created by Jan Jaap de Groot on 27/11/2020
 * Copyright 2020 Stichting Appt
 */

fun MotionEvent.isStart(): Boolean {
    return actionMasked == MotionEvent.ACTION_DOWN || actionMasked == MotionEvent.ACTION_HOVER_ENTER
}

fun MotionEvent.isEnd(): Boolean {
    return actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_HOVER_EXIT
}