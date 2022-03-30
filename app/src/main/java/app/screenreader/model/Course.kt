package app.screenreader.model

import android.content.Context
import app.screenreader.extensions.getString
import app.screenreader.extensions.identifier
import app.screenreader.model.Item
import java.io.Serializable

/**
 * Created by Jan Jaap de Groot on 06/11/2020
 * Copyright 2020 Stichting Appt
 */
enum class Course: Item, Serializable {

    TALKBACK_GESTURES,
    TALKBACK_ENABLE,
    TALKBACK_ACTIONS;

    private fun getString(context: Context, property: String): String {
        return context.getString("course_${identifier}_${property}")
    }

    override fun title(context: Context): String {
        return getString(context, "title")
    }
}