package app.screenreader.widgets

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.children
import app.screenreader.R
import io.github.inflationx.viewpump.ViewPumpContextWrapper

/**
 * Created by Jan Jaap de Groot on 19/10/2020
 * Copyright 2020 Stichting Appt
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Improve accessibility of Toolbar
        findViewById<Toolbar>(R.id.toolbar).children.firstOrNull {
            it is TextView
        }?.let { titleView ->
            ViewCompat.setAccessibilityHeading(titleView, true)
        }
    }
}