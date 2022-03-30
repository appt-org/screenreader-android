package app.screenreader.views.actions

import android.content.Context
import app.screenreader.R
import app.screenreader.extensions.className
import app.screenreader.model.Action

/**
 * Created by Jan Jaap de Groot on 12/10/2020
 * Copyright 2020 Stichting Appt
 */
abstract class FocusActionView(
    context: Context,
    action: Action,
    layoutId: Int
) : ActionView(context, action, layoutId) {

    private val COUNT = 3
    private val CLASS_NAME = getClassName()

    override fun onFocusChanged(elements: List<String>) {
        if (elements.size >= COUNT) {
            if (elements.takeLast(COUNT).all { className ->
                className == CLASS_NAME
            }) {
                correct()
            }
        }
    }

    abstract fun getClassName(): String
}

class HeadingsActionView(context: Context): FocusActionView(context, Action.HEADINGS, R.layout.action_headings) {
    override fun getClassName() = className<HeadingTextView>()
}

class LinksActionView(context: Context): FocusActionView(context, Action.LINKS, R.layout.action_links) {
    override fun getClassName() = className<LinkTextView>()
}