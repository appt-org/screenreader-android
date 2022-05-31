package app.screenreader.views.actions

import android.content.Context
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.AttributeSet
import android.view.accessibility.AccessibilityEvent
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by Jan Jaap de Groot on 18/11/2020
 * Copyright 2020 Stichting Appt
 */
class LinkTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    init {
        autoLinkMask = Linkify.ALL
        movementMethod = LinkMovementMethod.getInstance()
        linksClickable = false
        isClickable = false
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        event?.className = javaClass.name
        return super.dispatchPopulateAccessibilityEvent(event)
    }
}