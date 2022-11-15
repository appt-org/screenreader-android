package app.screenreader.views.actions

/**
 * Created by Jan Jaap de Groot on 16/11/2020
 * Copyright 2020 Stichting Appt
 */

import android.content.Context
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.LinearLayout
import app.screenreader.extensions.getSpannable
import app.screenreader.helpers.Accessibility
import app.screenreader.model.Action

interface ActionViewCallback {
    fun correct(action: Action)
    fun incorrect(action: Action, feedback: SpannableString)
}

/**
 * Created by Jan Jaap de Groot on 12/10/2020
 * Copyright 2020 Stichting Appt
 */
abstract class ActionView(context: Context, private val action: Action, layoutId: Int) : LinearLayout(context) {

    init {
        val view = inflate(context, layoutId, this)
        view.accessibilityDelegate = FocusDelegate()
        Accessibility.languages(view) // Set language of children
    }

    /** Accessibility */

    open fun onFocused(host: ViewGroup?, child: View?, className: CharSequence?) {
        // Can be overridden
    }

    open fun onFocusChanged(elements: List<String>) {
        // Can be overridden
    }

    private inner class FocusDelegate: View.AccessibilityDelegate() {

        private val elements = arrayListOf<String>()

        override fun onRequestSendAccessibilityEvent(
            host: ViewGroup?,
            child: View?,
            event: AccessibilityEvent?
        ): Boolean {
            if (event != null && event.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                onFocused(host, child, event.className)

                elements.add(event.className.toString())
                onFocusChanged(elements)
            }
            return super.onRequestSendAccessibilityEvent(host, child, event)
        }
    }

    /** Callback **/

    var callback: ActionViewCallback? = null

    open fun correct() {
        callback?.correct(action)
    }

    open fun incorrect(feedback: Int) {
        callback?.incorrect(action, context.getSpannable(feedback))
    }
}