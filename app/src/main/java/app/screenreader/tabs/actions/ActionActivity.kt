package app.screenreader.tabs.actions

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.text.SpannableString
import android.widget.ScrollView
import app.screenreader.R
import app.screenreader.extensions.doGetAction
import app.screenreader.extensions.identifier
import app.screenreader.extensions.showDialog
import app.screenreader.helpers.Accessibility
import app.screenreader.helpers.Events
import app.screenreader.model.Action
import app.screenreader.views.actions.ActionViewCallback
import app.screenreader.widgets.ToolbarActivity

/**
 * Created by Jan Jaap de Groot on 16/11/2020
 * Copyright 2020 Stichting Appt
 */
class ActionActivity: ToolbarActivity(), ActionViewCallback {

    private val startTime = System.currentTimeMillis()

    private val scrollView get() = findViewById<ScrollView>(R.id.scrollView)
    private val action: Action by lazy {
        intent.doGetAction() ?: Action.SELECT
    }

    override fun getLayoutId() = R.layout.activity_action

    override fun getToolbarTitle() = action.title(this)

    override fun onViewCreated() {
        super.onViewCreated()

        val view = action.view(this)
        view.callback = this
        scrollView.addView(view)

        // Listen to accessibility state changes
        Accessibility.accessibilityManager(this)?.let { manager ->
            manager.addAccessibilityStateChangeListener {
                if (manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN).isEmpty()) {
                    showDialog(R.string.actions_talkback_disabled_title, R.string.actions_talkback_disabled_message) {
                        finish()
                    }
                }
            }
        }
    }

    override fun correct(action: Action) {
        val elapsedTime = (System.currentTimeMillis() - startTime).toInt() / 1000
        events.log(Events.Category.action_completed, action.identifier, elapsedTime)

        action.completed(this, true)
        setResult(RESULT_OK)

        toast(R.string.action_completed_message) {
            finish()
        }
    }

    override fun incorrect(action: Action, feedback: SpannableString) {
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