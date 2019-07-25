package dev.olog.presentation.debug

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.configuration
import kotlinx.android.synthetic.main.activity_debug_configuration.*

class DebugConfigurationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug_configuration)

        dpi.append(configuration.densityDpi.toString())
        fontScale.append(configuration.fontScale.toString())
        mcc.append(configuration.mcc.toString())
        mnc.append(configuration.mnc.toString())
        orientation.append(configuration.orientation.toString())
        screenHeight.append(configuration.screenHeightDp.toString())
        screenWidth.append(configuration.screenWidthDp.toString())
        smallestWidth.append(configuration.smallestScreenWidthDp.toString())
    }

}