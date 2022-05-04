package app.screenreader.tabs.home

import app.screenreader.R
import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.textAdapterDelegate
import app.screenreader.adapters.textResourceAdapterDelegate
import app.screenreader.model.Header
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class HomeFragment: ListFragment() {

    override var decoration: Boolean = false

    override val adapter = ListDelegationAdapter(
        headerAdapterDelegate(),
        textResourceAdapterDelegate(),
    )

    override val items = listOf(
        R.string.home_description,
        Header(R.string.home_section_1),
        R.string.home_section_1_paragraph_1,
        Header(R.string.home_section_2),
        R.string.home_section_2_paragraph_1,
        Header(R.string.home_section_3),
        R.string.home_section_3_paragraph_1,
        R.string.home_section_3_paragraph_2,
        R.string.home_section_3_paragraph_4,
        R.string.home_section_3_paragraph_3,
        Header(R.string.home_section_4),
        R.string.home_section_4_paragraph_1,
        Header(R.string.home_section_5),
        R.string.home_section_5_paragraph_1,
        R.string.home_section_5_paragraph_2,
        Header(R.string.home_section_6),
        R.string.home_section_6_paragraph_1,
        Header(R.string.home_section_7),
        R.string.home_section_7_paragraph_1
    )
}