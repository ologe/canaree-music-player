package dev.olog.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.ui.tooling.preview.Preview
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.extensions.getTopFragment
import dev.olog.core.extensions.setMatchParent
import dev.olog.domain.MediaId
import dev.olog.feature.app.shortcuts.Shortcuts
import dev.olog.feature.presentation.base.CanHandleOnBackPressed
import dev.olog.feature.presentation.base.FloatingWindow
import dev.olog.feature.presentation.base.activity.*
import dev.olog.feature.presentation.base.extensions.*
import dev.olog.intents.AppConstants
import dev.olog.intents.Classes
import dev.olog.intents.MusicServiceAction
import dev.olog.navigation.Navigator
import dev.olog.navigation.screens.BottomNavigationPage
import dev.olog.presentation.R
import dev.olog.presentation.rateapp.RateAppDialog
import dev.olog.shared.components.sliding.panel.SlidingPanel
import dev.olog.shared.components.sliding.panel.SlidingPanelState
import dev.olog.shared.components.sliding.panel.rememberSlidingPanelState
import dev.olog.shared.components.theme.CanareeTheme
import timber.log.Timber
import javax.inject.Inject

private val SlidingPanelHeight = 60.dp
private val BottomNavigationHeight = 50.dp

// TODO restore landscape??
// TODO test immersive on different devices
@AndroidEntryPoint
class MainActivity : MusicGlueActivity(),
    HasSlidingPanel,
    OnPermissionChanged {

    private val viewModel by viewModels<MainActivityViewModel2>()

    @Inject
    internal lateinit var navigator: Navigator

    // handles lifecycle itself
    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior

    @Suppress("unused")
    @Inject
    lateinit var rateAppDialog: RateAppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CanareeTheme {
                MainActivityContent(
                    SlidingPanelHeight,
                    BottomNavigationHeight,
                    viewModel.lastBottomNavigationPage,
                    viewModel::onPageChanged
                )
            }
        }

//        when { TODO handle first access in `setContent`
//            viewModel.isFirstAccess() -> {
//                navigator.toFirstAccess()
//                return
//            }
//            savedInstanceState == null -> navigateToLastPage()
//        }

        intent?.let { handleIntent(it) }
    }

    override fun onPermissionGranted(permission: Permission) = when (permission){
        Permission.STORAGE -> {
            navigateToLastPage()
            connect()
        }
    }
    private fun navigateToLastPage(){
        viewModel.onPageChanged()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Shortcuts.SEARCH -> {}// TODO bottomNavigation.navigate(BottomNavigationPage.SEARCH)
            AppConstants.ACTION_CONTENT_VIEW -> getSlidingPanel().expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, Class.forName(Classes.SERVICE_MUSIC))
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            Shortcuts.DETAIL -> {
                lifecycleScope.launchWhenResumed {
                    // TODO check
                    val string = intent.getStringExtra(Shortcuts.DETAIL_EXTRA_ID)!!
                    val mediaId = MediaId.fromString(string)
                    navigator.toDetailFragment(mediaId as MediaId.Category, null)
                }
            }
            Intent.ACTION_VIEW -> {
                val serviceIntent = Intent(this, Class.forName(Classes.SERVICE_MUSIC))
                serviceIntent.action = MusicServiceAction.PLAY_URI.name
                serviceIntent.data = intent.data
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
        intent.action = null
        setIntent(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Navigator.REQUEST_CODE_HOVER_PERMISSION) {
            navigator.toFloating()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        try {
            val topFragment = supportFragmentManager.getTopFragment()

            when {
                topFragment is CanHandleOnBackPressed && topFragment.handleOnBackPressed()-> {
                    return
                }
                topFragment is FloatingWindow -> {
                    super.onBackPressed()
                    return
                }
                getSlidingPanelView()?.findViewById<RecyclerView>(R.id.list)?.canScrollVertically(-1) == true -> {
                    getSlidingPanelView()?.findViewById<RecyclerView>(R.id.list)?.smoothScrollToPosition(0)
                    return
                }
                getSlidingPanel().isExpanded() -> {
                    getSlidingPanel().collapse()
                    return
                }
            }

            super.onBackPressed()
        } catch (ex: IllegalStateException) {
            /*random fragment manager crashes */
            Timber.e(ex)
        }

    }

    override fun getSlidingPanel(): BottomSheetBehavior<*>? {
        return null
    }

    override fun getSlidingPanelView(): View? {
        return null
    }

}

@Preview
@Composable
private fun MainActivityContentPreview() {
    CanareeTheme {
        MainActivityContent(
            slidingPanelHeight = SlidingPanelHeight,
            bottomNavigationHeight = BottomNavigationHeight,
            initialPage = BottomNavigationPage.LIBRARY
        )
    }
}

@Composable
private fun MainActivityContent(
    slidingPanelHeight: Dp,
    bottomNavigationHeight: Dp,
    initialPage: BottomNavigationPage,
    onPageChanged: (BottomNavigationPage) -> Unit = {}
) {
    val context = ContextAmbient.current
    val fragmentContainer = remember {
        FragmentContainerView(context).apply {
            id = R.id.fragmentContainer
            setMatchParent()
            updatePadding(bottom = context.dip(
                slidingPanelHeight.value.toInt() + bottomNavigationHeight.value.toInt()
            ))
        }
    }

    Stack(Modifier.fillMaxSize()) {
        val slidingPanelState = rememberSlidingPanelState()

        AndroidView({ fragmentContainer }) {
            // do nothing
        }

        MainActivityBottomViews(
            slidingPanelState = slidingPanelState,
            slidingPanelHeight = slidingPanelHeight,
            bottomNavigationHeight = bottomNavigationHeight,
            initialPage = initialPage,
            onPageChanged = onPageChanged
        )
    }
}

@Composable
private fun StackScope.MainActivityBottomViews(
    slidingPanelState: SlidingPanelState,
    slidingPanelHeight: Dp,
    bottomNavigationHeight: Dp,
    initialPage: BottomNavigationPage,
    onPageChanged: (BottomNavigationPage) -> Unit
) {
    SlidingPanel(
        slidingPanelState = slidingPanelState,
        modifier = Modifier.align(Alignment.BottomCenter),
        peek = slidingPanelHeight + bottomNavigationHeight
    ) {
        // TODO mini player, inject with dagger using interface?
        // TODO player
        // TODO merge player with miniplayer??
    }

    var lastPage by savedInstanceState { initialPage }
    BottomNavigation(
        modifier = Modifier.align(Alignment.BottomCenter)
            .height(bottomNavigationHeight)
            .offset(y = bottomNavigationHeight * slidingPanelState.fraction),
    ) {
        for (page in BottomNavigationPage.values()) {
            BottomNavigationItem(
                icon = { Icon(page.toIcon()) },
                selected = page == lastPage,
                onClick = {
                    lastPage = page
                    onPageChanged(lastPage)
                }
            )
        }
    }
}

@Composable
private fun BottomNavigationPage.toIcon(): VectorAsset = when (this) {
    BottomNavigationPage.HOME -> Icons.Rounded.Home
    BottomNavigationPage.LIBRARY -> Icons.Rounded.Audiotrack
    BottomNavigationPage.SEARCH -> vectorResource(id = R.drawable.vd_search_alt)
    BottomNavigationPage.PLAYLIST -> Icons.Rounded.PlaylistPlay
    BottomNavigationPage.QUEUE -> vectorResource(id = R.drawable.vd_queue_alt)
}