package app.screenreader.model

import android.content.Context
import android.text.SpannableString
import app.screenreader.extensions.getSpannable
import app.screenreader.extensions.identifier
import app.screenreader.helpers.Preferences
import app.screenreader.views.actions.*
import java.io.Serializable

/**
 * Created by Jan Jaap de Groot on 06/11/2020
 * Copyright 2020 Stichting Appt
 */
enum class Action: Training, Serializable {

    HEADINGS,
    LINKS,
    SELECT,
    COPY,
    PASTE;

    private fun spannable(context: Context, property: String): SpannableString {
        return context.getSpannable("action_${identifier}_${property}")
    }

    override fun title(context: Context): SpannableString {
        return spannable(context, "title")
    }

    fun view(context: Context): ActionView {
        return when (this) {
            HEADINGS -> HeadingsActionView(context)
            LINKS -> LinksActionView(context)
            SELECT -> SelectionActionView(context)
            COPY -> CopyActionView(context)
            PASTE -> PasteActionView(context)
        }
    }

    override fun completed(context: Context): Boolean {
        return Preferences.isGestureCompleted(this)
    }
    override fun completed(context: Context, completed: Boolean) {
        Preferences.setGestureCompleted(this, true)
    }
}