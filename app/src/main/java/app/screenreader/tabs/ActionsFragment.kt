package app.screenreader.tabs

import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.trainingAdapterDelegate
import app.screenreader.model.Action
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class ActionsFragment: ListFragment() {

    override fun getItems(): List<Any> {
        return listOf(
            "Navigeren",
            Action.HEADINGS,
            Action.LINKS,
            "Bewerken",
            Action.SELECTION,
            Action.COPY,
            Action.PASTE
        )
    }

    override fun getAdapter(): ListDelegationAdapter<List<Any>> {
        return ListDelegationAdapter(
            headerAdapterDelegate(),
            trainingAdapterDelegate<Action> { action ->
                onActionClicked(action)
            }
        )
    }

    private fun onActionClicked(action: Action) {

    }

    companion object {
        private val REQUEST_CODE = 1337
    }
}