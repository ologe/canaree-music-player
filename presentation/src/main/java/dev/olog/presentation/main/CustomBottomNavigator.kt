package dev.olog.presentation.main

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.navigation.BottomNavigationPage
import dev.olog.navigation.Navigator
import dev.olog.presentation.R
import dev.olog.presentation.model.PresentationPreferencesGateway
import javax.inject.Inject

@AndroidEntryPoint
internal class CustomBottomNavigator(
    context: Context,
    attrs: AttributeSet
) : BottomNavigationView(context, attrs) {

    @Inject
    internal lateinit var prefs: PresentationPreferencesGateway

    @Inject
    internal lateinit var navigator: Navigator

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lastLibraryPage = prefs.bottomNavigationPage
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
        val navigationPage = prefs.bottomNavigationPage
        navigator.bottomNavigate(navigationPage)
    }

    private fun saveLastPage(page: BottomNavigationPage){
        prefs.bottomNavigationPage = page
    }

    private fun Int.toBottomNavigationPage(): BottomNavigationPage = when (this){
        R.id.navigation_library -> {
            val isTracks = prefs.bottomNavigationPage == BottomNavigationPage.LIBRARY_TRACKS
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

