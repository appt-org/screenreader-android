package app.screenreader.views.actions

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.text.util.Linkify
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import android.widget.Toast


/**
 * Created by Jan Jaap de Groot on 18/11/2020
 * Copyright 2020 Stichting Appt
 */
class LinkTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TextView(context, attrs, defStyle) {

    init {
        autoLinkMask = Linkify.ALL
        movementMethod = LinkMovementMethod.getInstance()
        linksClickable = false
        isClickable = false

//        val spannableStringBuilder = SpannableStringBuilder("Please go to \"Settings\" in")
//        val spannableString = SpannableString("mywebsite.com")
//
//        val clickableSpan: ClickableSpan = object : ClickableSpan() {
//            override fun onClick(view: View) {
//                // Ignored
//            }
//        }
//
//        spannableString.setSpan(
//            clickableSpan,
//            0,
//            spannableString.length,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
//
//        spannableStringBuilder.append(spannableString)
//        spannableStringBuilder.append("to change this status")
//
//        text = spannableStringBuilder
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        event?.className = javaClass.name
        return super.dispatchPopulateAccessibilityEvent(event)
    }
}