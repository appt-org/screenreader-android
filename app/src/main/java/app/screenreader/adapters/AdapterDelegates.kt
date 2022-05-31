package app.screenreader.adapters

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.setPadding
import app.screenreader.R
import app.screenreader.extensions.getSpannable
import app.screenreader.model.Item
import app.screenreader.model.Training
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import app.screenreader.extensions.setVisible
import app.screenreader.helpers.Accessibility
import app.screenreader.model.Header

fun headerAdapterDelegate() = adapterDelegate<Header, Any>(R.layout.view_header) {
    val header: TextView = findViewById(R.id.headerView)

    bind {
        header.text = item.title(context)
        setAccessibilityHeading(header)
    }
}

fun textAdapterDelegate() = adapterDelegate<String, Any>(R.layout.view_text) {
    val textView: TextView = findViewById(R.id.textView)

    bind {
        textView.text = item
    }
}

fun textResourceAdapterDelegate() = adapterDelegate<Int, Any>(R.layout.view_text) {
    val textView: TextView = findViewById(R.id.textView)
    val marginMedium = context.resources.getDimension(R.dimen.margin_medium).toInt()
    val marginSmall = context.resources.getDimension(R.dimen.margin_small).toInt()

    bind {
        textView.text = context.getSpannable(item)

        when (adapterPosition) {
            0 -> textView.setPadding(marginMedium)
            -1 -> textView.setPadding(marginMedium) // TODO: Replace -1 with last index
            else -> textView.setPadding(marginMedium, 0, marginMedium, marginSmall)
        }
    }
}

inline fun <reified T : Item> itemAdapterDelegate(crossinline callback: (T) -> Unit) =
    adapterDelegate<T, Any>(R.layout.view_item) {
        val textView: TextView = findViewById(R.id.textView)

        itemView.setOnClickListener {
            callback(item)
        }

        bind {
            textView.text = item.title(context)
        }
    }


inline fun <reified T : Training> trainingAdapterDelegate(crossinline callback: (T) -> Unit) =
    adapterDelegate<T, Any>(R.layout.view_training) {
        val textView: TextView = findViewById(R.id.textView)
        val imageView: ImageView = findViewById(R.id.imageView)

        itemView.setOnClickListener {
            callback(item)
        }

        bind {
            val title = item.title(context)
            val completed = item.completed(context)

            textView.text = title

            if (completed) {
                imageView.setVisible(true)
                Accessibility.label(itemView, context.getSpannable(R.string.training_accessibility_label, title))
            } else {
                imageView.setVisible(false)
                Accessibility.label(itemView, title)
            }
        }
    }

private fun setAccessibilityButtonDelegate(view: View) {
    ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.className = Button::class.java.name
        }
    })
}

private fun setAccessibilityHeading(header: View){
    ViewCompat.setAccessibilityDelegate(header, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.isHeading = true
        }
    })
}