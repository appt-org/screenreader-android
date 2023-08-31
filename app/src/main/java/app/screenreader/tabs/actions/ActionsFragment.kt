package app.screenreader.tabs.actions

import android.app.Activity
import android.content.Intent
import app.screenreader.R
import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.textResourceAdapterDelegate
import app.screenreader.adapters.trainingAdapterDelegate
import app.screenreader.extensions.doSetAction
import app.screenreader.extensions.requestReview
import app.screenreader.extensions.showDialog
import app.screenreader.helpers.Accessibility
import app.screenreader.helpers.Preferences
import app.screenreader.model.Action
import app.screenreader.model.Header
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class ActionsFragment: ListFragment() {

    override val items = listOf(
        R.string.actions_description,
        Header(R.string.actions_header_navigation),
        Action.HEADINGS,
        Action.LINKS,
        Header(R.string.actions_header_text),
        Action.SELECT,
        Action.COPY,
        Action.PASTE
    )

    override val adapter = ListDelegationAdapter(
        textResourceAdapterDelegate(),
        headerAdapterDelegate(),
        trainingAdapterDelegate<Action> { action ->
            onActionClicked(action)
        }
    )

    private fun onActionClicked(action: Action) {
        context?.let { context ->
            if (!Accessibility.screenReader(context)) {
                context.showDialog(R.string.actions_talkback_disabled_title, R.string.actions_talkback_disabled_message)
                return
            }

            startActivity<ActionActivity>(REQUEST_CODE) {
                doSetAction(action)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            adapter.notifyDataSetChanged()

            if (Preferences.getActionsCompleted() >= 1) {
                activity?.requestReview()
            }
        }
    }

    companion object {
        private val REQUEST_CODE = 1337
    }
}