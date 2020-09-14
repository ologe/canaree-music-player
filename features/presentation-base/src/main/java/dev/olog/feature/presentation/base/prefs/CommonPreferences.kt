package dev.olog.feature.presentation.base.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.presentation.base.R
import dev.olog.navigation.screens.BottomNavigationPage
import dev.olog.navigation.screens.LibraryPage
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "prefs" // TODO merged tags

private const val BOTTOM_VIEW_LAST_PAGE = "$TAG.BOTTOM_VIEW"
private const val LIBRARY_LAST_PAGE = "$TAG.LIBRARY_PAGE"

class CommonPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
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

    val libraryPageFlow: Flow<LibraryPage>
        get() {
            return preferences.observeKey(LIBRARY_LAST_PAGE, LibraryPage.TRACKS.toString())
                .map(LibraryPage::valueOf)
        }

    var libraryPage: LibraryPage
        get() = LibraryPage.valueOf(preferences.getString(LIBRARY_LAST_PAGE, LibraryPage.TRACKS.toString())!!)
        set(value) {
            preferences.edit {
                putString(LIBRARY_LAST_PAGE, value.toString())
            }
        }

}