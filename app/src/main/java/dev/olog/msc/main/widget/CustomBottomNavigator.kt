package dev.olog.msc.main.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.LibraryPrefs
import dev.olog.feature.main.BottomNavigationPage
import dev.olog.feature.main.MainPrefs
import dev.olog.msc.R
import dev.olog.shared.android.extensions.findInContext
import javax.inject.Inject

@AndroidEntryPoint
internal class CustomBottomNavigator(
        context: Context,
        attrs: AttributeSet
) : BottomNavigationView(context, attrs) {

    @Inject
    internal lateinit var mainPrefs: MainPrefs
    @Inject
    lateinit var libraryPrefs: LibraryPrefs

    private val navigator = BottomNavigator()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lastLibraryPage = mainPrefs.lastBottomNavigationPage.get()
        selectedItemId = lastLibraryPage.toMenuId()

        setOnNavigationItemSelectedListener { menu ->
            val navigationPage = menu.itemId.toBottomNavigationPage()
            val libraryPage = libraryPrefs.getLastLibraryPage()
            saveLastPage(navigationPage)
            navigator.navigate(context.findInContext(), navigationPage, libraryPage)
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
        val navigationPage = mainPrefs.lastBottomNavigationPage.get()
        val libraryPage = libraryPrefs.getLastLibraryPage()
        navigator.navigate(context.findInContext(), navigationPage, libraryPage)
    }

    private fun saveLastPage(page: BottomNavigationPage){
        mainPrefs.lastBottomNavigationPage.set(page)
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

