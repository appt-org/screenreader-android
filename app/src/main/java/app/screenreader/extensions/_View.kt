package app.screenreader.extensions

import android.view.View

/**
 * Created by Jan Jaap de Groot on 29/10/2020
 * Copyright 2020 Stichting Appt
 */
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}