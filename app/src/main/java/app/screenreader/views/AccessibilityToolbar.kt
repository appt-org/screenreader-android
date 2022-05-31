package app.screenreader.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.toSpannable
import app.screenreader.extensions.toSpannable
import app.screenreader.extensions.toast
import app.screenreader.helpers.Accessibility

/**
 * Adds accessibility markup to the Toolbar
 *
 * Created by Jan Jaap de Groot on 10/05/2022
 * Copyright 2022 Stichting Appt
 */
class AccessibilityToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.toolbarStyle
) : androidx.appcompat.widget.Toolbar(context, attrs, defStyleAttr) {

    init {
        // Listen to hierarchy changes to improve accessibility
        setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                // Title view?
                if (child is AppCompatTextView) {
                    // Mark title as heading
                    Accessibility.heading(child)

                    // Show full title on long press
                    child.setOnLongClickListener {
                        val title = context.toSpannable(child.text)
                        toast(context, title)
                        true
                    }
                }

                // Menu view?
                if (child is ActionMenuView) {
                    child.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
                        override fun onChildViewAdded(parent: View?, child: View?) {
                            // Mark menu items as button
                            if (child is ActionMenuItemView) {
                                Accessibility.button(child)
                            }
                        }

                        override fun onChildViewRemoved(parent: View?, child: View?) {
                            // Ignored
                        }
                    })
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
                // Ignored
            }
        })
    }
}