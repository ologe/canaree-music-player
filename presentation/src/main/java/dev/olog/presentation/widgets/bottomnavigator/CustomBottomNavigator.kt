package dev.olog.presentation.widgets.bottomnavigator

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.platform.extension.findActivity
import javax.inject.Inject

@AndroidEntryPoint
internal class CustomBottomNavigator(
        context: Context,
        attrs: AttributeSet
) : BottomNavigationView(context, attrs) {

    @Inject
    internal lateinit var presentationPrefs: PresentationPreferencesGateway

    private val navigator = BottomNavigator()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lastLibraryPage = presentationPrefs.getLastBottomViewPage()
        selectedItemId = lastLibraryPage.toMenuId()

        setOnNavigationItemSelectedListener { menu ->
            val navigationPage = menu.itemId.toBottomNavigationPage()
            val libraryPage = presentationPrefs.getLastLibraryPage()
            saveLastPage(navigationPage)
            navigator.navigate(findActivity(), navigationPage, libraryPage)
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
        val navigationPage = presentationPrefs.getLastBottomViewPage()
        val libraryPage = presentationPrefs.getLastLibraryPage()
        navigator.navigate(findActivity(), navigationPage, libraryPage)
    }

    private fun saveLastPage(page: BottomNavigationPage){
        presentationPrefs.setLastBottomViewPage(page)
    }

    private fun Int.toBottomNavigationPage(): BottomNavigationPage = when (this){
        R.id.navigation_library -> BottomNavigationPage.LIBRARY
        R.id.navigation_search -> BottomNavigationPage.SEARCH
        R.id.navigation_queue -> BottomNavigationPage.QUEUE
        else -> throw IllegalArgumentException("invalid menu id")
    }

    private fun BottomNavigationPage.toMenuId(): Int = when (this){
        BottomNavigationPage.LIBRARY -> R.id.navigation_library
        BottomNavigationPage.SEARCH -> R.id.navigation_search
        BottomNavigationPage.QUEUE -> R.id.navigation_queue
    }

}

