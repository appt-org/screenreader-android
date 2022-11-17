package app.screenreader.helpers

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.graphics.Region
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityEventCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.allViews
import app.screenreader.extensions.toSpannable

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
object Accessibility {

    /**
     * Returns the AccessibilityManager if available and enabled.
     *
     * @param context Context reference
     *
     * @return AccessibilityManager object, or null
     */
    fun accessibilityManager(context: Context?): AccessibilityManager? {
        if (context != null) {
            val service = ContextCompat.getSystemService(context, AccessibilityManager::class.java)
            if (service is AccessibilityManager && service.isEnabled) {
                return service
            }
        }
        return null
    }

    /**
     * Interrupts the assistive technology
     *
     * @param context Context reference
     */
    fun interrupt(context: Context?) {
        accessibilityManager(context)?.interrupt()
    }

    /**
     * Announces the given message using the assistive technology
     *
     * @param context Context reference
     * @param message The message to announce
     */
    fun announce(context: Context?, message: String) {
        accessibilityManager(context)?.let { accessibilityManager ->
            val event = AccessibilityEvent.obtain(AccessibilityEventCompat.TYPE_ANNOUNCEMENT)
            event.text.add(message)

            accessibilityManager.sendAccessibilityEvent(event)
        }
    }

    /**
     * Checks whether any kind of screen reader is active
     *
     * @param context Context reference
     */
    fun screenReader(context: Context?): Boolean {
        return accessibilityManager(context)?.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_SPOKEN
        )?.isNotEmpty() ?: false
    }

    /**
     * Checks whether touch exploration is active
     *
     * @param context Context reference
     */
    fun touchExploration(context: Context?): Boolean {
        return accessibilityManager(context)?.isTouchExplorationEnabled ?: false
    }

    /**
     * Moves the accessibility focus to the given view
     *
     * @param view View to move accessibility focus to
     */
    fun focus(view: View): View {
        view.isFocusable = true
        view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        return view
    }

    /**
     * Extension to move the accessibility focus to the given view
     */
    fun View.setAccessibilityFocus(): View {
        return focus(this)
    }

    /**
     * Helper method to set accessibility delegate with callback
     *
     * @param view View to set the delegate of
     * @param callback Callback used to set properties of AccessibilityNodeInfoCompat
     */
    fun accessibilityDelegate(view: View, callback: (host: View, info: AccessibilityNodeInfoCompat) -> Unit) {
        ViewCompat.setAccessibilityDelegate(
            view,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    callback(host, info)
                }
            }
        )
    }

    /**
     * Helper method to mark a view as accessibility heading
     *
     * @param view View to mark
     * @param isHeading Value to apply
     */
    fun heading(view: View, isHeading: Boolean = true): View {
        ViewCompat.setAccessibilityHeading(view, isHeading)
        return view
    }

    /**
     * Extension to mark the given view as accessibility heading
     *
     * @param isHeading Value to apply
     */
    fun View.setAsAccessibilityHeading(isHeading: Boolean = true): View {
        return heading(this, isHeading)
    }

    /**
     * Helper method to mark a view as accessibility button
     *
     * @param view View to mark
     * @param isButton Value to apply
     */
    fun button(view: View, isButton: Boolean = true): View {
        accessibilityDelegate(view) { _, info ->
            info.className = if (isButton) {
                Button::class.java.name
            } else {
                this::class.java.name
            }
        }
        return view
    }

    /**
     * Extension to mark the given view as accessibility button
     *
     * @param isButton Value to apply
     */
    fun View.setAccessibilityButton(isButton: Boolean = true): View {
        return button(this, isButton)
    }

    /**
     * Helper method to set the state description for a view
     */
    fun state(view: View, state: CharSequence?): View {
        ViewCompat.setStateDescription(view, state)
        return view
    }

    /**
     * Helper method to set the state description for a view
     */
    fun View.setAccessibilityState(state: String): View {
        return state(this, state)
    }

    /**
     * Adds an AccessibilityAction of the given type to the given view
     *
     * @param view The view to set the action to
     * @param type Type of the action, listed in AccessibilityNodeInfoCompat
     * @param description Short description of the action
     *
     * @see androidx.core.view.accessibility.AccessibilityNodeInfoCompat
     */
    fun action(view: View, type: Int, description: CharSequence): View {
        accessibilityDelegate(view) { _, info ->
            val action = AccessibilityNodeInfoCompat.AccessibilityActionCompat(type, description)
            info.addAction(action)
        }
        return view
    }

    /**
     * Extension to add an accessibility action to the the given view
     *
     * @param type Type of the action, listed in AccessibilityNodeInfoCompat
     * @param description Short description of the action
     */
    fun View.addAccessibilityAction(type: Int, description: CharSequence): View {
        return action(this, type, description)
    }

    /**
     * Helper method to mark a view as accessibility heading
     *
     * @param view View to label
     * @param label The label to set
     */
    fun label(view: View, label: CharSequence): View {
        view.contentDescription = view.context.toSpannable(label)
        return view
    }

    /**
     * Extension to add an accessibility label to the the given view
     *
     * @param label The label to set
     */
    fun View.setAccessibilityLabel(label: CharSequence): View {
        return label(this, label)
    }

    /**
     * Sets traversal information for the given view.
     *
     * @param view View to apply traversal information to
     * @param before View to traverse beforehand
     * @param after View to traverse afterwards
     */
    fun setTraversal(view: View, before: View? = null, after: View? = null) {
        ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                before?.let { before ->
                    info.setTraversalBefore(before)
                }
                after?.let { after ->
                    info.setTraversalAfter(after)
                }
                super.onInitializeAccessibilityNodeInfo(host, info)
            }
        })
    }

    /**
     * Sets the given `view` to be traversed before the given `before` view.
     *
     * @param view View to apply traversal information to
     * @param before View to traverse beforehand
     */
    fun setTraversalBefore(view: View, before: View) {
        setTraversal(view, before = before)
    }

    /**
     * Sets the given `view` to be traversed after the given `after` view.
     *
     * @param view The view to apply traversal information to
     * @param after View to traverse afterwards
     */
    fun setTraversalAfter(view: View, after: View) {
        setTraversal(view, after = after)
    }

    /**
     * Sets the given `views` to be traversed in chronological order.
     *
     * @param views The view order to apply
     */
    fun setTraversalOrder(vararg views: View) {
        if (views.size > 1) {
            for (i in 0..views.size-2) {
                setTraversalBefore(views[i], views[i+1])
            }
        }
    }

    /**
     * Applies the current app language to all descendants of a view
     *
     * @param view The view
     */
    fun languages(view: View) {
        if (view is ViewGroup) {
            view.allViews.forEach { child ->
                language(child)
            }
        } else {
            language(view)
        }
    }

    /**
     * Applies the current app language to string resources
     *
     * @param view The view
     */
    fun language(view: View) {
        val contentDescription = view.contentDescription
        if (contentDescription != null && contentDescription.isNotEmpty()) {
            view.contentDescription = view.context.toSpannable(contentDescription)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val tooltip = view.tooltipText
            if (tooltip != null && tooltip.isNotEmpty()) {
                view.tooltipText = view.context.toSpannable(tooltip)
            }
        }

        if (view is TextView) {
            val text = view.text
            if (text != null && text.isNotEmpty()) {
                view.text = view.context.toSpannable(text)
            }
        }
    }
}