package dev.olog.msc

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.AppInitializer
import javax.inject.Inject

class FlipperInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkFlipperPlugin: NetworkFlipperPlugin,
) : AppInitializer {

    override fun init() {
        SoLoader.init(context, false)
        if (FlipperUtils.shouldEnableFlipper(context)) {
            AndroidFlipperClient.getInstance(context).apply {
                addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
                // TODO add also post migration prefs
                addPlugin(SharedPreferencesFlipperPlugin(context, "${context.packageName}_preferences"))
                addPlugin(DatabasesFlipperPlugin(context))
                addPlugin(networkFlipperPlugin)
            }.start()
        }
    }
}