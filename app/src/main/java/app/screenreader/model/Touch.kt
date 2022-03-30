package app.screenreader.model

/**
 * Created by Jan Jaap de Groot on 06/08/2021
 * Copyright 2021 Stichting Appt
 */
data class Touch(
    val x: Int,
    val y: Int,
    val taps: Int = 1,
    val longPress: Boolean = false
)