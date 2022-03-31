package app.screenreader.tabs.home

import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.textAdapterDelegate
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class HomeFragment: ListFragment() {

    override val adapter = ListDelegationAdapter(
        textAdapterDelegate(),
        headerAdapterDelegate()
    )

    override val items = listOf("Informatie over het Home-tabblad")
}