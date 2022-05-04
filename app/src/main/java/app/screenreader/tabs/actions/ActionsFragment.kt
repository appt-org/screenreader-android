package app.screenreader.tabs.actions

import android.app.Activity
import android.content.Intent
import app.screenreader.R
import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.textResourceAdapterDelegate
import app.screenreader.adapters.trainingAdapterDelegate
import app.screenreader.extensions.setAction2
import app.screenreader.model.Action
import app.screenreader.model.Header
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class ActionsFragment: ListFragment() {

    override val items = listOf(
        R.string.actions_description,
        Header(R.string.actions_navigation),
        Action.HEADINGS,
        Action.LINKS,
        Header(R.string.actions_text),
        Action.SELECTION,
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
        startActivity<ActionActivity>(REQUEST_CODE) {
            setAction2(action)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        private val REQUEST_CODE = 1337
    }
}