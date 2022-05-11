package app.screenreader.model

import android.content.Context
import app.screenreader.R
import java.io.Serializable

/**
 * Created by Jan Jaap de Groot on 04/05/2022
 * Copyright 2022 Stichting Appt
 */
enum class Topic : Item, Serializable {

    RATING,
    SHARE,
    WEBSITE,
    SOURCE,
    STICHTING_APPT,
    ABRA,
    SIDN_FONDS;

    override fun title(context: Context): String {
        return context.getString(title)
    }

    val title: Int
        get() {
            return when (this) {
                RATING -> R.string.topic_rating_title
                SHARE -> R.string.topic_share_title
                WEBSITE -> R.string.topic_website_title
                SOURCE -> R.string.topic_source_title
                STICHTING_APPT -> R.string.topic_stichting_appt_title
                ABRA -> R.string.topic_abra_title
                SIDN_FONDS -> R.string.topic_sidn_fonds_title
            }
        }

    val url: Int
        get() {
            return when (this) {
                RATING -> R.string.topic_rating_url
                SHARE -> R.string.topic_share_url
                WEBSITE -> R.string.topic_website_url
                SOURCE -> R.string.topic_source_url
                STICHTING_APPT -> R.string.topic_stichting_appt_url
                ABRA -> R.string.topic_abra_url
                SIDN_FONDS -> R.string.topic_sidn_fonds_url
            }
        }

    fun url(context: Context): String {
        return context.getString(url)
    }
}