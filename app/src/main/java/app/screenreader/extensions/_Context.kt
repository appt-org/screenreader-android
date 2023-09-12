package app.screenreader.extensions

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.LocaleList
import android.text.SpannableString
import android.text.Spanned
import android.text.style.LocaleSpan
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import app.screenreader.R
import java.util.*
import kotlin.concurrent.schedule

/** String **/

fun Context.getIdentifier(resourceType: String, resourceName: String): Int {
    return resources.getIdentifier(resourceName, resourceType, packageName)
}

fun Context.getSpannable(name: String): SpannableString {
    val identifier = getIdentifier("string", name)
    return if (identifier > 0) {
        getSpannable(identifier)
    } else {
        SpannableString(name)
    }
}

fun Context.getSpannable(id: Int): SpannableString {
    val string = getString(id)
    return toSpannable(string)
}

fun Context.getSpannable(id: Int, vararg args: Any): SpannableString {
    val string = getString(id, *args)
    return toSpannable(string)
}

fun Context.toSpannable(string: CharSequence): SpannableString {
    val language = getString(R.string.app_language)

    val spannable = SpannableString(string)

    val locales = LocaleList.forLanguageTags(language)
    if (!locales.isEmpty) {
        val locale = locales.get(0)
        val localeSpan = LocaleSpan(locale)
        spannable.setSpan(localeSpan, 0, string.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    return spannable
}

/** Dialog **/

fun Context.showDialog(title: CharSequence, message: CharSequence?, callback: (() -> Unit)? = null) {
    if (this is Activity && this.isFinishing) {
        return
    }

    val builder = AlertDialog.Builder(this, R.style.Dialog)

    builder.setTitle(title)
    builder.setMessage(message)

    builder.setPositiveButton(getSpannable(R.string.action_ok)) { _, _ ->
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
    val title = getSpannable(titleId)
    val message = if (messageId != null) getSpannable(messageId) else null
    showDialog(title, message, callback)
}

fun Context.showError(messageId: Int, callback: (() -> Unit)? = null) {
    showDialog(R.string.error, messageId, callback)
}

fun Context.showError(message: CharSequence, callback: (() -> Unit)? = null) {
    showDialog(getSpannable(R.string.error), message, callback)
}

/** Toast **/
fun toast(
    context: Context?,
    message: CharSequence?,
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
    toast(context, context?.getSpannable(message) ?: null, duration, callback)
}

/** Browser **/

fun Context.openWebsite(url: Int) {
    openWebsite(getString(url))
}

fun Context.openWebsite(url: String) {
    openWebsite(Uri.parse(url))
}

fun Context.openWebsite(uri: Uri) {
    val darkBackground = resources.getColor(R.color.background_dark, null)
    val lightBackground = resources.getColor(R.color.background_light, null)

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