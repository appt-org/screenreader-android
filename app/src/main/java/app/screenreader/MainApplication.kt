package app.screenreader

import android.app.Application
import app.screenreader.helpers.Preferences
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Preferences
        Preferences.init(this)

        // Firebase
        //FirebaseApp.initializeApp(this)

        // Calligraphy
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath(getString(R.string.font_regular))
                            .setFontAttrId(io.github.inflationx.calligraphy3.R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
    }
}