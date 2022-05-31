package app.screenreader.model

import android.content.Context
import android.text.SpannableString
import app.screenreader.extensions.getSpannable
import java.io.Serializable

/**
 * Created by Jan Jaap de Groot on 20/10/2020
 * Copyright 2020 Stichting Appt
 */
data class Header(
    val textId: Int
): Item, Serializable {

    override fun title(context: Context): SpannableString {
        return context.getSpannable(textId)
    }
}