package dev.olog.msc.presentation.preferences

import android.os.Bundle
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.HasSupportFragmentInjector
import dev.olog.msc.R
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.k.extension.setLightStatusBar
import kotlinx.android.synthetic.main.activity_preferences.*

class PreferencesActivity : DaggerAppCompatActivity(), HasSupportFragmentInjector {

    companion object {
        const val REQUEST_CODE = 1221
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        if (isMarshmallow()){
            window.setLightStatusBar()
        }
        setContentView(R.layout.activity_preferences)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }


}