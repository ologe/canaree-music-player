package dev.olog.presentation.widgets.bottomnavigator

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.HasAndroidInjector
import dev.olog.core.extensions.findActivity
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import dev.olog.navigation.Navigator
import dev.olog.navigation.screens.BottomNavigationPage
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.presentation.R
import dev.olog.shared.throwNotHandled
import javax.inject.Inject

internal class CanareeBottomNavigationView(
    context: Context,
    attrs: AttributeSet
) : BottomNavigationView(context, attrs) {

    @Inject
    internal lateinit var preferences: CommonPreferences

    @Inject
    lateinit var navigator: Navigator

    init {
        (findActivity() as HasAndroidInjector)
            .androidInjector()
            .inject(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lastLibraryPage = preferences.getLastBottomViewPage()
        selectedItemId = lastLibraryPage.toMenuId()

        setOnNavigationItemSelectedListener { menu ->
            val navigationPage = menu.itemId.toBottomNavigationPage()
            saveLastPage(navigationPage)
            navigator.bottomNavigate(
                findActivity(),
                navigationPage.toScreen()
            )
            true
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnNavigationItemSelectedListener(null)
    }

    private fun BottomNavigationPage.toScreen(): FragmentScreen {
        return when (this){
            BottomNavigationPage.HOME -> FragmentScreen.HOME
            BottomNavigationPage.LIBRARY -> FragmentScreen.TRACKS
            BottomNavigationPage.PLAYLISTS -> FragmentScreen.PLAYLISTS
            BottomNavigationPage.SEARCH -> FragmentScreen.SEARCH
            BottomNavigationPage.QUEUE -> FragmentScreen.QUEUE
            else -> throwNotHandled(this)
        }
    }

    fun navigate(page: BottomNavigationPage) {
        selectedItemId = page.toMenuId()
    }

    fun navigateToLastPage() {
        val navigationPage = preferences.getLastBottomViewPage()
        navigator.bottomNavigate(findActivity(), navigationPage.toScreen())
    }

    private fun saveLastPage(page: BottomNavigationPage) {
        preferences.setLastBottomViewPage(page)
    }

    private fun Int.toBottomNavigationPage(): BottomNavigationPage = when (this) {
        R.id.navigation_home -> BottomNavigationPage.HOME
        R.id.navigation_songs -> BottomNavigationPage.LIBRARY
        R.id.navigation_playlists -> BottomNavigationPage.PLAYLISTS
        R.id.navigation_search -> BottomNavigationPage.SEARCH
        R.id.navigation_queue -> BottomNavigationPage.QUEUE
        else -> throw IllegalArgumentException("invalid menu id")
    }

    private fun BottomNavigationPage.toMenuId(): Int = when (this) {
        BottomNavigationPage.HOME -> R.id.navigation_home
        BottomNavigationPage.LIBRARY -> R.id.navigation_songs
        BottomNavigationPage.PLAYLISTS -> R.id.navigation_playlists
        BottomNavigationPage.SEARCH -> R.id.navigation_search
        BottomNavigationPage.QUEUE -> R.id.navigation_queue
    }

}

