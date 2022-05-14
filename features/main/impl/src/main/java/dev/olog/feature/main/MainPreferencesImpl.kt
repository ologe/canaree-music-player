package dev.olog.feature.main

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.feature.main.api.BottomNavigationPage
import dev.olog.feature.main.api.MainPreferences
import javax.inject.Inject

class MainPreferencesImpl @Inject constructor(
    private val preferences: SharedPreferences
) : MainPreferences {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val FIRST_ACCESS = "$TAG.FIRST_ACCESS"
        private const val BOTTOM_VIEW_LAST_PAGE = "$TAG.BOTTOM_VIEW_3"
    }

    override fun isFirstAccess(): Boolean {
        val isFirstAccess = preferences.getBoolean(FIRST_ACCESS, true)

        if (isFirstAccess) {
            preferences.edit { putBoolean(FIRST_ACCESS, false) }
        }

        return isFirstAccess
    }



    override fun getLastBottomViewPage(): BottomNavigationPage {
        val page =
            preferences.getString(BOTTOM_VIEW_LAST_PAGE, BottomNavigationPage.LIBRARY.toString())!!
        return BottomNavigationPage.valueOf(page)
    }

    override fun setLastBottomViewPage(page: BottomNavigationPage) {
        preferences.edit { putString(BOTTOM_VIEW_LAST_PAGE, page.toString()) }
    }

}