package dev.olog.presentation.debug

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dev.olog.presentation.R
import kotlinx.android.synthetic.main.activity_debug_configuration.*
import org.jetbrains.anko.configuration

class DebugConfigurationActivity: AppCompatActivity() {


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