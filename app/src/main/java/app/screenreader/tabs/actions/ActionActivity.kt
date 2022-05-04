package app.screenreader.tabs.actions

import android.content.Intent
import app.screenreader.R
import app.screenreader.extensions.doGetAction
import app.screenreader.extensions.showDialog
import app.screenreader.helpers.Accessibility
import app.screenreader.model.Action
import app.screenreader.views.actions.ActionViewCallback
import app.screenreader.widgets.ToolbarActivity
import kotlinx.android.synthetic.main.activity_action.*

/**
 * Created by Jan Jaap de Groot on 16/11/2020
 * Copyright 2020 Stichting Appt
 */
class ActionActivity: ToolbarActivity(), ActionViewCallback {

    private val startTime = System.currentTimeMillis()

    private val action: Action by lazy {
        intent.doGetAction() ?: Action.SELECTION
    }

    override fun getLayoutId() = R.layout.activity_action

    override fun getToolbarTitle() = action.title(this)

    override fun onViewCreated() {
        super.onViewCreated()

        val view = action.view(this)
        view.callback = this
        scrollView.addView(view)

        if (!Accessibility.screenReader(this)) {
            showDialog(R.string.talkback_disabled_title, R.string.talkback_disabled_explanation) {
                finish()
            }
        }
    }

    override fun correct(action: Action) {
        val elapsedTime = (System.currentTimeMillis() - startTime).toInt()
        //events.log(Events.Category.actionCompleted, action.identifier, elapsedTime)

        action.completed(this, true)
        setResult(RESULT_OK)

        toast("Training afgerond!") {
            finish()
        }
    }

    override fun incorrect(action: Action, feedback: String) {
        toast(feedback)
    }

    override fun startActivity(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            // Ignore view actions
            return
        }
        super.startActivity(intent)
    }
}