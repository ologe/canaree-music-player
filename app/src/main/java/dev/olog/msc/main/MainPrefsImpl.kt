package dev.olog.msc.main

import android.content.Context
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.Preference
import dev.olog.core.PreferenceManager
import dev.olog.feature.main.BottomNavigationPage
import dev.olog.feature.main.MainPrefs
import dev.olog.shared.android.theme.DarkMode
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.android.theme.QuickAction
import javax.inject.Inject

class MainPrefsImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferenceManager: PreferenceManager,
) : MainPrefs {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val FIRST_ACCESS = "$TAG.FIRST_ACCESS"
        private const val BOTTOM_VIEW_LAST_PAGE = "$TAG.BOTTOM_VIEW_3"
    }

    override val firstAccess: Preference<Boolean>
        get() = preferenceManager.create(FIRST_ACCESS, true)

    override val lastBottomNavigationPage: Preference<BottomNavigationPage>
        get() = preferenceManager.createWithMapper(
            key = BOTTOM_VIEW_LAST_PAGE,
            default = BottomNavigationPage.LIBRARY,
            serialize = BottomNavigationPage::toString,
            deserialize = BottomNavigationPage::valueOf,
        )

    override val darkMode: Preference<DarkMode>
        get() = preferenceManager.createWithMapper(
            key = context.getString(prefs.R.string.prefs_dark_mode_key),
            default = DarkMode.FollowSystem,
            serialize = { context.getString(it.prefValue) },
            deserialize = { DarkMode.fromPref(context, it) },
        )

    override val imageShape: Preference<ImageShape>
        get() = preferenceManager.createWithMapper(
            key = context.getString(prefs.R.string.prefs_icon_shape_key),
            default = ImageShape.ROUND,
            serialize = { context.getString(it.prefValue) },
            deserialize = { ImageShape.fromPref(context, it) }
        )

    override val immersiveMode: Preference<Boolean>
        get() = preferenceManager.create(prefs.R.string.prefs_immersive_key, false)

    override val adaptiveColorEnabled: Preference<Boolean>
        get() =  preferenceManager.create(prefs.R.string.prefs_adaptive_colors_key, false)

    override val quickAction: Preference<QuickAction>
        get() = preferenceManager.createWithMapper(
            key = context.getString(prefs.R.string.prefs_quick_action_key),
            default = QuickAction.NONE,
            serialize = { context.getString(it.prefValue) },
            deserialize = { QuickAction.fromPref(context, it) }
        )

    override val accentColor: Preference<Int>
        get() = preferenceManager.create(
            prefs.R.string.prefs_color_accent_key,
            ContextCompat.getColor(context, dev.olog.shared.android.R.color.defaultColorAccent)
        )
}