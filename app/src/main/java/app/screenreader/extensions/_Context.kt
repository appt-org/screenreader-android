package app.screenreader.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import app.screenreader.R
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule

/** Dialog **/

fun Context.showDialog(title: String, message: String?, callback: (() -> Unit)? = null) {
    val builder = AlertDialog.Builder(this, R.style.Dialog)

    builder.setTitle(title)
    builder.setMessage(message)

    builder.setPositiveButton(R.string.action_ok) { _, _ ->
        // Ignored, handled by on dismiss listener.
    }

    builder.setOnDismissListener {
        callback?.let {
            it()
        }
    }

    builder.create().show()
}

fun Context.showDialog(titleId: Int, messageId: Int?, callback: (() -> Unit)? = null) {
    val title = getString(titleId)
    val message = if (messageId != null) getString(messageId) else null
    showDialog(title, message, callback)
}

fun Context.showError(messageId: Int, callback: (() -> Unit)? = null) {
    showDialog(R.string.error, messageId, callback)
}

fun Context.showError(message: String, callback: (() -> Unit)? = null) {
    showDialog(getString(R.string.error), message, callback)
}

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

/** Browser **/

fun Context.openWebsite(url: Int) {
    openWebsite(getString(url))
}

fun Context.openWebsite(url: String) {
    openWebsite(Uri.parse(url))
}

fun Context.openWebsite(uri: Uri) {
    val darkBackground = resources.getColor(R.color.black, null)
    val lightBackground = resources.getColor(R.color.white, null)

    val dark = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(darkBackground)
        .setNavigationBarColor(darkBackground)
        .setNavigationBarDividerColor(darkBackground)
        .setSecondaryToolbarColor(darkBackground)
        .build()

    val light = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(lightBackground)
        .setNavigationBarColor(lightBackground)
        .setNavigationBarDividerColor(lightBackground)
        .setSecondaryToolbarColor(lightBackground)
        .build()

    val intent = CustomTabsIntent.Builder()
        .setShareState(CustomTabsIntent.SHARE_STATE_ON)
        .setUrlBarHidingEnabled(false)
        .setInstantAppsEnabled(false)
        .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
        .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, dark)
        .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, light)
        .build()

    intent.launchUrl(this, uri)
}