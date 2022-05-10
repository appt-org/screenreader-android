package app.screenreader.widgets

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import app.screenreader.R

/**
 * Created by Jan Jaap de Groot on 19/10/2020
 * Copyright 2020 Stichting Appt
 */
abstract class ToolbarActivity : BaseActivity() {

    var toolbar: Toolbar? = null

    abstract fun getToolbarTitle(): String?

    override fun onViewCreated() {
        super.onViewCreated()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeActionContentDescription(R.string.action_cancel)

        title = getToolbarTitle()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    open fun onBackSelected() {
        // Can be overridden
    }
}