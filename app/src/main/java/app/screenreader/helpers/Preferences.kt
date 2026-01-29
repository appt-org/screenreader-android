package app.screenreader.helpers

import android.content.Context
import android.content.SharedPreferences
import app.screenreader.model.Action
import app.screenreader.model.Gesture

/**
 * Created by Jan Jaap de Groot on 26/11/2020
 * Copyright 2020 Stichting Appt
 */
object Preferences {

    private lateinit var preferences: SharedPreferences
    private val REVIEW = "review"

    fun init(context: Context) {
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    private fun getBoolean(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }

    private fun setBoolean(key: String, value: Boolean) {
        preferences.edit()?.putBoolean(key, value)?.apply()
    }

    fun getInt(key: String): Int {
        return preferences.getInt(key, 0)
    }

    fun setInt(key: String, value: Int) {
        preferences.edit()?.putInt(key, value)?.apply()
    }

    fun getString(key: String): String? {
        return preferences.getString(key, "")
    }

    fun setString(key: String, value: String) {
        preferences.edit()?.putString(key, value)?.apply()
    }

    /* Gesture */

    fun isActionCompleted(gesture: Gesture): Boolean {
        return getBoolean(gesture.toString())
    }

    fun isActionCompleted(gesture: Gesture, completed: Boolean) {
        setBoolean(gesture.toString(), completed)
    }

    fun getGesturesCompleted(): Int {
        var count = 0
        Gesture.values().forEach { gesture ->
            if (isActionCompleted(gesture)) {
                count++
            }
        }
        return count
    }

    /* Action */

    fun isActionCompleted(action: Action): Boolean {
        return getBoolean(action.toString())
    }

    fun isActionCompleted(action: Action, completed: Boolean) {
        setBoolean(action.toString(), completed)
    }

    fun getActionsCompleted(): Int {
        var count = 0
        Action.values().forEach { action ->
            if (isActionCompleted(action)) {
                count++
            }
        }
        return count
    }

    /* Review */

    fun isReviewPrompted(): Boolean {
        return getBoolean(REVIEW)
    }

    fun setReviewPrompted(value: Boolean) {
        setBoolean(REVIEW, value)
    }
}