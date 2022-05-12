package app.screenreader.views.actions

import android.content.Context
import kotlinx.android.synthetic.main.action_selection.view.*
import app.screenreader.R
import app.screenreader.model.Action
import app.screenreader.views.TrainingField

/**
 * Created by Jan Jaap de Groot on 23/11/2020
 * Copyright 2020 Stichting Appt
 */
class SelectionActionView(context: Context) : ActionView(
    context,
    Action.SELECT,
    R.layout.action_selection
), TrainingField.OnSelectionChangedListener {

    init {
        trainingField.callback = this
    }

    override fun onSelectionChanged(start: Int, end: Int) {
        if ((end - start) > 1) {
            correct()
        }
    }
}