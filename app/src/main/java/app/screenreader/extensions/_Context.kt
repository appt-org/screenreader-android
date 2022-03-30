package app.screenreader.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import app.screenreader.R
import java.util.*
import kotlin.concurrent.schedule

/** Toast **/
fun toast(
    context: Context?,
    message: String,
    duration: Long = 3000,
    callback: (() -> Unit)? = null
) {
    if (context == null) {
        return
    }

    val builder = AlertDialog.Builder(context, R.style.Toast)
    builder.setCancelable(false)
    builder.setMessage(message)

    builder.setOnDismissListener {
        callback?.let {
            it()
        }
    }

    val dialog = builder.create()
    dialog.show()

    Timer().schedule(duration) {
        dialog.dismiss()
    }
}

fun toast(context: Context?, message: Int, duration: Long = 3000, callback: (() -> Unit)? = null) {
    toast(context, context?.getString(message) ?: "", duration, callback)
}

/** String **/

fun Context.getIdentifier(resourceType: String, resourceName: String): Int {
    return resources.getIdentifier(resourceName, resourceType, "app.screenreader")
}

fun Context.getString(name: String): String {
    val identifier = getIdentifier("string", name)
    return if (identifier > 0) {
        getString(identifier)
    } else {
        name
    }
}