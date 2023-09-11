package app.screenreader.tabs.talkback

import app.screenreader.R
import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.textResourceAdapterDelegate
import app.screenreader.model.Header
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class TalkBackFragment: ListFragment() {

    override var decoration: Boolean = false

    override val adapter = ListDelegationAdapter(
        headerAdapterDelegate(),
        textResourceAdapterDelegate(),
    )

    override val items = listOf(
        R.string.talkback_description,
        Header(R.string.talkback_section_1),
        R.string.talkback_section_1_paragraph_1,
        R.string.talkback_section_1_paragraph_2,
        R.string.talkback_section_1_paragraph_3,
        Header(R.string.talkback_section_2),
        R.string.talkback_section_2_paragraph_1,
        R.string.talkback_section_2_paragraph_2,
        R.string.talkback_section_2_paragraph_3,
        Header(R.string.talkback_section_3),
        R.string.talkback_section_3_paragraph_1,
        R.string.talkback_section_3_paragraph_2,
        R.string.talkback_section_3_paragraph_3,
        Header(R.string.talkback_section_4),
        R.string.talkback_section_4_paragraph_1,
        Header(R.string.talkback_section_5),
        R.string.talkback_section_5_paragraph_1,
        R.string.talkback_section_5_paragraph_2,
        R.string.talkback_section_5_paragraph_3,
        R.string.talkback_section_5_paragraph_4,
        Header(R.string.talkback_section_6),
        R.string.talkback_section_6_paragraph_1,
        R.string.talkback_section_6_paragraph_2,
        R.string.talkback_section_6_paragraph_3,
    )
}