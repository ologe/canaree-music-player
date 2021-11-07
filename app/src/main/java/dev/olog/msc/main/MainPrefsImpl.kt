package dev.olog.msc.main

import dev.olog.core.Preference
import dev.olog.core.PreferenceManager
import dev.olog.feature.main.BottomNavigationPage
import dev.olog.feature.main.MainPrefs
import javax.inject.Inject

class MainPrefsImpl @Inject constructor(
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
        get() = preferenceManager.createEnum(
            key = BOTTOM_VIEW_LAST_PAGE,
            default = BottomNavigationPage.LIBRARY,
            serialize = BottomNavigationPage::toString,
            deserialize = BottomNavigationPage::valueOf,
        )
}