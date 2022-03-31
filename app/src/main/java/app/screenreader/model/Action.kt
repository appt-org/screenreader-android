package app.screenreader.model

import android.content.Context
import app.screenreader.extensions.getString
import app.screenreader.extensions.identifier
import app.screenreader.helpers.Preferences
import app.screenreader.model.Training
import app.screenreader.views.actions.*
import java.io.Serializable

/**
 * Created by Jan Jaap de Groot on 06/11/2020
 * Copyright 2020 Stichting Appt
 */
enum class Action: Training, Serializable {

    HEADINGS,
    LINKS,
    SELECTION,
    COPY,
    PASTE;

    private fun getString(context: Context, property: String): String {
        return context.getString("action_${identifier}_${property}")
    }

    override fun title(context: Context): String {
        return getString(context, "title")
    }

    fun view(context: Context): ActionView {
        return when (this) {
            HEADINGS -> HeadingsActionView(context)
            LINKS -> LinksActionView(context)
            SELECTION -> SelectionActionView(context)
            COPY -> CopyActionView(context)
            PASTE -> PasteActionView(context)
        }
    }

    override fun completed(context: Context): Boolean {
        return Preferences.isCompleted(this)
    }
    override fun completed(context: Context, completed: Boolean) {
        Preferences.setCompleted(this, true)
    }
}