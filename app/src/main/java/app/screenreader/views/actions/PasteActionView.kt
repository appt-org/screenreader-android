package app.screenreader.views.actions

import android.content.Context
import androidx.core.widget.addTextChangedListener
import app.screenreader.R
import app.screenreader.model.Action
import app.screenreader.views.TrainingField

/**
 * Created by Jan Jaap de Groot on 23/11/2020
 * Copyright 2020 Stichting Appt
 */
class PasteActionView(context: Context) : ActionView(
    context,
    Action.PASTE,
    R.layout.action_paste
) {

    init {
        findViewById<TrainingField>(R.id.trainingField).addTextChangedListener(beforeTextChanged = { _, _, _, after ->
            if (after > 1) {
                correct()
            }
        })
    }
}