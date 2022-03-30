package app.screenreader.extensions

/**
 * Created by Jan Jaap de Groot on 18/11/2020
 * Copyright 2020 Stichting Appt
 */
inline fun <reified T> className(): String {
    return T::class.java.name
}