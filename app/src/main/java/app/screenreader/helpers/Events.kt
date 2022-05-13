package app.screenreader.helpers

import android.os.Bundle
import app.screenreader.extensions.identifier
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by Jan Jaap de Groot on 13/05/2022
 * Copyright 2020 Stichting Appt
 */
class Events(private val firebaseAnalytics: FirebaseAnalytics) {

    enum class Property {
        screenreader
    }

    fun property(property: Property, value: String) {
        firebaseAnalytics.setUserProperty(property.identifier, value)
    }

    fun property(property: Property, value: Boolean) {
        property(property, if (value) "1" else "0")
    }

    enum class Category {
        action_completed,
        gesture_completed
    }

    fun log(category: Category, identifier: String, value: Int? = null) {
        print("Log event, category: $category, identifier: $identifier, value: $value")

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, identifier)

        if (value != null) {
            bundle.putInt(FirebaseAnalytics.Param.VALUE, value)
        }

        firebaseAnalytics.logEvent(category.identifier, bundle)
    }
}