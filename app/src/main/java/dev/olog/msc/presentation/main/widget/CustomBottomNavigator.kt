package dev.olog.msc.presentation.main.widget

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.olog.msc.R
import dev.olog.msc.domain.gateway.prefs.PresentationPreferences
import dev.olog.presentation.main.BottomNavigationPage
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

    fun navigate(page: BottomNavigationPage) {
        selectedItemId = page.toMenuId()
        navigator.navigate(context as FragmentActivity, page)
    }

    fun navigateToLastPage(){
        val bottomNavigationPage = presentationPrefs.getLastBottomViewPage()
        val navigateTo = bottomNavigationPage.toMenuId()
        selectedItemId = navigateTo
        navigate(navigateTo.toBottomNavigationPage())
    }

    private fun saveLastPage(page: BottomNavigationPage){
        launch(Dispatchers.Default) { presentationPrefs.setLastBottomViewPage(page) }
    }

    private fun Int.toBottomNavigationPage(): BottomNavigationPage = when (this){
        R.id.navigation_songs -> BottomNavigationPage.SONGS
        R.id.navigation_search -> BottomNavigationPage.SEARCH
        R.id.navigation_queue -> BottomNavigationPage.QUEUE
        else -> throw IllegalArgumentException("invalid menu id")
    }

    private fun BottomNavigationPage.toMenuId(): Int = when (this){
        BottomNavigationPage.SONGS -> R.id.navigation_songs
        BottomNavigationPage.SEARCH -> R.id.navigation_search
        BottomNavigationPage.QUEUE -> R.id.navigation_queue
        BottomNavigationPage.PODCASTS -> throw IllegalArgumentException("podcast has not a id")
    }

}

