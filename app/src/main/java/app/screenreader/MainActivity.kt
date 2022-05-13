package app.screenreader

import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.screenreader.helpers.Accessibility
import app.screenreader.helpers.Events
import app.screenreader.widgets.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onViewCreated() {
        super.onViewCreated()

        // Find nav controller
        val hostFragment = supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment
        val navController = hostFragment.navController

        // Setup BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.tab_talkback,
                R.id.tab_gestures,
                R.id.tab_actions,
                R.id.tab_more
            )
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)

        // Set title on destination change
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val titleId = when (destination.id) {
                R.id.tab_talkback -> R.string.talkback_title
                R.id.tab_gestures -> R.string.gestures_title
                R.id.tab_actions -> R.string.actions_title
                R.id.tab_more -> R.string.more_title
                else -> R.string.app_name
            }
            setTitle(titleId)
        }

        // Log screen reader status
        events.property(Events.Property.screenreader, Accessibility.screenReader(this))
    }
}