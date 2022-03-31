package app.screenreader.tabs.more

import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.textAdapterDelegate
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class MoreFragment: ListFragment() {

    override fun getAdapter(): ListDelegationAdapter<List<Any>> {
        return ListDelegationAdapter(
            textAdapterDelegate(),
            headerAdapterDelegate()
        )
    }

    override fun getItems(): List<Any> {
        return listOf("Informatie over het Meer-tabblad")
    }
}