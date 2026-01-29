package app.screenreader.extensions

import java.util.*

/**
 * Created by Jan Jaap de Groot on 01/12/2020
 * Copyright 2020 Stichting Appt
 */
inline val <reified T : Enum<T>> T.identifier
    get() = toString().lowercase(Locale.ROOT)