package app.screenreader.views.actions

import android.content.Context
import android.util.AttributeSet
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat

/**
 * Created by Jan Jaap de Groot on 18/11/2020
 * Copyright 2020 Stichting Appt
 */
class HeadingTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    init {
        ViewCompat.setAccessibilityHeading(this, true)
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        event?.className = javaClass.name
        return super.dispatchPopulateAccessibilityEvent(event)
    }
}