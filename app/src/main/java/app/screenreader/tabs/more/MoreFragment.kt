package app.screenreader.tabs.more

import androidx.core.app.ShareCompat
import app.screenreader.R
import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.itemAdapterDelegate
import app.screenreader.adapters.textResourceAdapterDelegate
import app.screenreader.extensions.openWebsite
import app.screenreader.model.Header
import app.screenreader.model.Topic
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class MoreFragment: ListFragment() {

    override val adapter = ListDelegationAdapter(
        textResourceAdapterDelegate(),
        headerAdapterDelegate(),
        itemAdapterDelegate<Topic> { topic ->
            onTopicClicked(topic)
        }
    )

    override val items = listOf(
        R.string.more_description,
        Topic.RATING,
        Topic.SHARE,
        Topic.WEBSITE,
        Header(R.string.more_partners),
        Topic.STICHTING_APPT,
        Topic.ABRA,
        Topic.SIDN_FONDS
    )

    private fun onTopicClicked(topic: Topic) {
        activity?.let { activity ->
            if (topic == Topic.SHARE) {
                ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setChooserTitle(topic.title(activity))
                    .setText(topic.url(activity))
                    .startChooser()
            } else {
                activity.openWebsite(topic.url)
            }
        }
    }
}