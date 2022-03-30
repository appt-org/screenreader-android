package app.screenreader.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * Created by Jan Jaap de Groot on 23/11/2020
 * Copyright 2020 Stichting Appt
 */
class TrainingField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatEditText(context, attrs, defStyle) {

    init {
        showSoftInputOnFocus = false
        setTextIsSelectable(true)
    }

    interface OnSelectionChangedListener {
        fun onSelectionChanged(start: Int, end: Int)
    }

    var callback: OnSelectionChangedListener? = null

    override fun onSelectionChanged(start: Int, end: Int) {
        super.onSelectionChanged(start, end)
        callback?.onSelectionChanged(start, end)
    }
}