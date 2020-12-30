package dev.olog.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.base.CanHandleOnBackPressed
import dev.olog.feature.base.DrawsOnTop
import dev.olog.feature.base.HasBottomNavigation
import dev.olog.feature.base.RestorableScrollHelper
import dev.olog.navigation.BottomNavigationPage
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.navigation.destination.FragmentScreen
import dev.olog.presentation.R
import dev.olog.presentation.rateapp.RateAppDialog
import dev.olog.scrollhelper.ScrollType
import dev.olog.shared.android.*
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.theme.immersiveAmbient
import dev.olog.shared.android.theme.playerAppearanceAmbient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MusicGlueActivity(),
    SlidingPanelAmbient,
    HasBottomNavigation,
    OnPermissionChanged,
    RestorableScrollHelper {

    private val viewModel by viewModels<MainActivityViewModel>()

    @Inject
    lateinit var navigator: Navigator

    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Suppress("unused")
    @Inject
    internal lateinit var rateAppDialog: RateAppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator.toPlayer(R.id.playerFragment)
        navigator.toMiniPlayer(R.id.miniPlayerFragment)

        if (immersiveAmbient.isEnabled) {
            // workaround, on some device on immersive mode bottom navigation disappears
            rootView.fitsSystemWindows = true
            bottomSheet.fitsSystemWindows = true
            bottomWrapper.fitsSystemWindows = true
        }

        if (playerAppearanceAmbient.isMini()){
            // TODO made a resource value
            slidingPanelFade.parallax = 0
            bottomSheet.setHeight(dip(300))
        }

        setupSlidingPanel()

        when {
            viewModel.isFirstAccess() -> {
                navigator.toFirstAccess()
                return
            }
            savedInstanceState == null -> navigateToLastPage()
        }

        intent?.let { handleIntent(it) }
    }

    override fun onPermissionGranted(permission: Permission) = when (permission){
        Permission.STORAGE -> {
            navigateToLastPage()
            connect()
        }
    }

    private fun setupSlidingPanel(){
        if (!isTablet) {
            val scrollHelper = SuperCerealScrollHelper(
                this, ScrollType.Full(
                    slidingPanel = bottomSheet,
                    bottomNavigation = bottomWrapper,
                    toolbarHeight = dimen(R.dimen.toolbar),
                    tabLayoutHeight = dimen(R.dimen.tab),
                    realSlidingPanelPeek = dimen(R.dimen.sliding_panel_peek)
                )
            )
            lifecycle.addObserver(scrollHelper)
        }
    }

    private fun navigateToLastPage(){
        bottomNavigation.navigateToLastPage()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Navigator.START_SERVICE_ACTION -> navigator.toFloatingWindow()
            Navigator.INTENT_ACTION_SEARCH -> bottomNavigation.navigate(BottomNavigationPage.SEARCH)
            Navigator.INTENT_ACTION_CONTENT_VIEW -> slidingPanel.expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> navigator.toMusicPlayFromSearch()
            Navigator.INTENT_ACTION_DETAIL -> handleDetailIntent(intent)
            Intent.ACTION_VIEW -> navigator.toMusicPlayFromUri(intent.data)
        }
        intent.action = null
        setIntent(null)
    }

    private fun handleDetailIntent(intent: Intent) {
        launch {
            delay(250)
            val string = intent.getStringExtra(Params.MEDIA_ID) ?: return@launch
            val mediaId = MediaId.fromStringOrNull(string) ?: return@launch
            navigator.toDetailFragment(mediaId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Navigator.HOVER_CODE) {
            navigator.toFloatingWindow()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        try {
            val topFragment = supportFragmentManager.getTopFragment()

            when {
                topFragment is CanHandleOnBackPressed && topFragment.handleOnBackPressed()-> {
                    return
                }
                topFragment is DrawsOnTop -> {
                    super.onBackPressed()
                    return
                }
                slidingPanel.isExpanded() -> {
                    slidingPanel.collapse()
                    return
                }
            }
            if (tryPopFolderBack()) {
                return
            }

            super.onBackPressed()
        } catch (ex: IllegalStateException) {
            /*random fragment manager crashes */
            ex.printStackTrace()
        }

    }

    private fun tryPopFolderBack(): Boolean {
        val categoriesFragment = supportFragmentManager.findFragmentByTag(FragmentScreen.LIBRARY_TRACKS.tag) ?: return false

        if (!categoriesFragment.isVisible || categoriesFragment.view == null) {
            return false
        }

//        if (categoriesFragment.isCurrentFragmentFolderTree()){ TODO
//            val folderTree = categoriesFragment.childFragmentManager.fragments
//                .find { it is FolderTreeFragment } as? CanHandleOnBackPressed
//            return folderTree?.handleOnBackPressed() == true
//        }
        return false
    }

    override fun getSlidingPanel(): BottomSheetBehavior<*> {
        return BottomSheetBehavior.from(bottomSheet)
    }

    override fun navigate(page: BottomNavigationPage) {
        bottomNavigation.navigate(page)
    }

    override fun restoreUpperWidgetsTranslation(){
        findViewById<View>(R.id.toolbar)?.animate()?.translationY(0f)
        findViewById<View>(R.id.tabLayout)?.animate()?.translationY(0f)
    }
}