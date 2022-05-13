package app.screenreader.widgets

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.screenreader.extensions.toast
import app.screenreader.helpers.Events
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import io.github.inflationx.viewpump.ViewPumpContextWrapper

/**
 * Created by Jan Jaap de Groot on 19/10/2020
 * Copyright 2020 Stichting Appt
 */
abstract class BaseActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var events: Events

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        events = Events(firebaseAnalytics)

        setContentView(getLayoutId())
        onViewCreated()
    }

    open fun onViewCreated() {
        // Can be overridden
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onSupportNavigateUp(): Boolean {
        this.onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun toast(message: String, duration: Long = 3000, callback: (() -> Unit)? = null) {
        toast(this, message, duration, callback)
    }

    fun toast(message: Int, duration: Long = 3000, callback: (() -> Unit)? = null) {
        toast(this, message, duration, callback)
    }

    inline fun <reified T : Activity> startActivity(requestCode: Int = -1, options: Bundle? = null, noinline init: Intent.() -> Unit = {}) {
        val intent = Intent(this, T::class.java)
        intent.init()
        startActivityForResult(intent, requestCode, options)
    }
}