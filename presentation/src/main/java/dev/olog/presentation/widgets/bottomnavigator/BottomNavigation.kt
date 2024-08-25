package dev.olog.presentation.widgets.bottomnavigator

import android.content.Context
import android.util.AttributeSet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.component.BottomNavigation
import dev.olog.shared.compose.component.BottomNavigationItem
import dev.olog.shared.compose.component.CustomAbstractComposeView
import javax.inject.Inject

@AndroidEntryPoint
internal class BottomNavigation : CustomAbstractComposeView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Inject
    internal lateinit var presentationPrefs: PresentationPreferencesGateway

    private val navigator = BottomNavigator()

    private var selected by mutableStateOf(presentationPrefs.getLastBottomViewPage())

    @Composable
    override fun Content() {
        CanareeTheme {
            BottomNavigation {
                for (page in BottomNavigationPage.entries) {
                    val icon = when (page) {
                        BottomNavigationPage.LIBRARY -> Icons.Rounded.Home
                        BottomNavigationPage.SEARCH -> Icons.Rounded.Search
                        BottomNavigationPage.QUEUE -> Icons.AutoMirrored.Rounded.PlaylistPlay
                    }
                    BottomNavigationItem(
                        selected = selected == page,
                        onClick = { navigate(page) },
                        icon = icon,
                    )
                }
            }
        }
    }

    fun navigate(page: BottomNavigationPage) {
        selected = page
        saveLastPage(page)
        navigateInternal(page)
    }

    fun navigateToLastPage(){
        val navigationPage = presentationPrefs.getLastBottomViewPage()
        navigate(navigationPage)
    }

    private fun navigateInternal(page: BottomNavigationPage) {
        val libraryPage = presentationPrefs.getLastLibraryPage()
        navigator.navigate(context.findInContext<FragmentActivity>(), page, libraryPage)
    }

    private fun saveLastPage(page: BottomNavigationPage){
        presentationPrefs.setLastBottomViewPage(page)
    }

}

