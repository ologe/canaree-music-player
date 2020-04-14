package dev.olog.feature.presentation.base.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.feature.presentation.base.R
import dev.olog.navigation.screens.BottomNavigationPage
import javax.inject.Inject

private const val TAG = "AppPreferencesDataStoreImpl"

private const val BOTTOM_VIEW_LAST_PAGE = "$TAG.BOTTOM_VIEW_3"

class CommonPreferences @Inject constructor(
    private val context: Context,
    private val preferences: SharedPreferences
) {


    fun canShowPodcasts(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_show_podcasts_key), true)
    }

    fun getLastBottomViewPage(): BottomNavigationPage {
        val page = preferences.getString(BOTTOM_VIEW_LAST_PAGE, BottomNavigationPage.LIBRARY.toString())!!
        return BottomNavigationPage.valueOf(page)
    }

    fun setLastBottomViewPage(page: BottomNavigationPage) {
        preferences.edit { putString(BOTTOM_VIEW_LAST_PAGE, page.toString()) }
    }

}