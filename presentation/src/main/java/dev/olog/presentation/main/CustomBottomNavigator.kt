package dev.olog.presentation.main

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import androidx.core.content.edit
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.navigation.BottomNavigationPage
import dev.olog.navigation.Navigator
import dev.olog.presentation.R
import javax.inject.Inject

@AndroidEntryPoint
internal class CustomBottomNavigator(
    context: Context,
    attrs: AttributeSet
) : BottomNavigationView(context, attrs) {

    companion object {
        private const val BOTTOM_NAVIGATION_KEY = "AppPreferencesDataStoreImpl.BOTTOM_VIEW_4"
    }

    @Inject
    internal lateinit var prefs: SharedPreferences

    @Deprecated("use LibraryPreferencesGateway, this is a temp solution")
    private var bottomNavigationPage: BottomNavigationPage
        get() = BottomNavigationPage.valueOf(prefs.getString(BOTTOM_NAVIGATION_KEY, BottomNavigationPage.LIBRARY_TRACKS.toString())!!)
        set(value) {
            prefs.edit {
                putString(BOTTOM_NAVIGATION_KEY, value.toString())
            }
        }

    @Inject
    internal lateinit var navigator: Navigator

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lastLibraryPage = bottomNavigationPage
        selectedItemId = lastLibraryPage.toMenuId()

        setOnNavigationItemSelectedListener { menu ->
            val navigationPage = menu.itemId.toBottomNavigationPage()
            saveLastPage(navigationPage)
            navigator.bottomNavigate(navigationPage)
            true
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnNavigationItemSelectedListener(null)
    }

    fun navigate(page: BottomNavigationPage) {
        selectedItemId = page.toMenuId()
    }

    fun navigateToLastPage(){
        val navigationPage = bottomNavigationPage
        navigator.bottomNavigate(navigationPage)
    }

    private fun saveLastPage(page: BottomNavigationPage){
        bottomNavigationPage = page
    }

    private fun Int.toBottomNavigationPage(): BottomNavigationPage = when (this){
        R.id.navigation_library -> {
            val isTracks = bottomNavigationPage == BottomNavigationPage.LIBRARY_TRACKS
            if (isTracks) BottomNavigationPage.LIBRARY_TRACKS else BottomNavigationPage.LIBRARY_PODCASTS
        }
        R.id.navigation_search -> BottomNavigationPage.SEARCH
        R.id.navigation_queue -> BottomNavigationPage.QUEUE
        else -> throw IllegalArgumentException("invalid menu id")
    }

    private fun BottomNavigationPage.toMenuId(): Int = when (this){
        BottomNavigationPage.LIBRARY_TRACKS -> R.id.navigation_library
        BottomNavigationPage.LIBRARY_PODCASTS -> R.id.navigation_library
        BottomNavigationPage.SEARCH -> R.id.navigation_search
        BottomNavigationPage.QUEUE -> R.id.navigation_queue
    }

}

