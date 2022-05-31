package app.screenreader.model

import android.content.Context
import android.text.SpannableString

/**
 * Created by Jan Jaap de Groot on 20/10/2020
 * Copyright 2020 Stichting Appt
 */
interface Item {
    fun title(context: Context): SpannableString
}