package app.screenreader.views.actions

import android.content.ClipboardManager
import android.content.Context
import app.screenreader.R
import app.screenreader.model.Action
import kotlinx.android.synthetic.main.action_copy.view.*

/**
 * Created by Jan Jaap de Groot on 23/11/2020
 * Copyright 2020 Stichting Appt
 */
class CopyActionView(context: Context): ActionView(
    context,
    Action.COPY,
    R.layout.action_copy
) {

    init {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.addPrimaryClipChangedListener {
            clipboard.primaryClip?.let { clip ->
                if (clip.itemCount > 0) {
                    val text = clip.getItemAt(0).text

                    if (trainingField.text.toString().contains(text, false)) {
                        correct()
                    } else {
                        incorrect("Kopieer tekst uit het tekstveld.")
                    }
                }
            }
        }
    }
}