package dev.olog.feature.main

import dev.olog.core.Preference
import dev.olog.shared.android.theme.DarkMode
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.android.theme.QuickAction

interface MainPrefs {

    val firstAccess: Preference<Boolean>

    val lastBottomNavigationPage: Preference<BottomNavigationPage>

    val darkMode: Preference<DarkMode>

    val imageShape: Preference<ImageShape>

    val immersiveMode: Preference<Boolean>

    val adaptiveColorEnabled: Preference<Boolean>

    val quickAction: Preference<QuickAction>

    val accentColor: Preference<Int>

}