package dev.olog.msc.presentation.main.widget

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.olog.msc.R
import dev.olog.msc.domain.gateway.prefs.PresentationPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CustomBottomNavigator(
        context: Context, attrs: AttributeSet
) : BottomNavigationView(context, attrs), CoroutineScope by MainScope() {

    internal lateinit var presentationPrefs: PresentationPreferences

    private val navigator = BottomNavigator()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnNavigationItemSelectedListener { menu ->
            val navigationPage = menu.itemId.toBottomNavigationPage()
            saveLastPage(navigationPage)
            navigate(navigationPage)
            true
        }
        setOnNavigationItemReselectedListener {  }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnNavigationItemSelectedListener(null)
        setOnNavigationItemReselectedListener(null)
    }

    fun navigate(page: Page) {
        navigator.navigate(context as FragmentActivity, page)
    }

    fun navigateToLastPage(){
        val bottomNavigationPage = presentationPrefs.getLastBottomViewPage()
        var navigateTo = bottomNavigationPage.toMenuId()
        if (!presentationPrefs.canShowPodcastCategory()) {
            menu.removeItem(R.id.navigation_podcasts)
            if (navigateTo == R.id.navigation_podcasts) {
                navigateTo = R.id.navigation_songs
                saveLastPage(navigateTo.toBottomNavigationPage())
            }
        }
        selectedItemId = navigateTo
        navigate(navigateTo.toBottomNavigationPage())
    }

    private fun saveLastPage(page: Page){
        launch(Dispatchers.Default) { presentationPrefs.setLastBottomViewPage(page) }
    }

    private fun Int.toBottomNavigationPage(): Page = when (this){
        R.id.navigation_songs -> Page.SONGS
        R.id.navigation_podcasts -> Page.PODCASTS
        R.id.navigation_search -> Page.SEARCH
        R.id.navigation_queue -> Page.QUEUE
        else -> throw IllegalArgumentException("invalid menu id")
    }

    private fun Page.toMenuId(): Int = when (this){
        Page.SONGS -> R.id.navigation_songs
        Page.PODCASTS -> R.id.navigation_podcasts
        Page.SEARCH -> R.id.navigation_search
        Page.QUEUE -> R.id.navigation_queue
    }

    enum class Page {
        SONGS,
        PODCASTS,
        SEARCH,
        QUEUE
    }

}

