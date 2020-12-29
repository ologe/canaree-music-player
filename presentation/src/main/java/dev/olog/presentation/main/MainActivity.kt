package dev.olog.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.appshortcuts.Shortcuts
import dev.olog.core.MediaId
import dev.olog.feature.base.CanHandleOnBackPressed
import dev.olog.feature.base.DrawsOnTop
import dev.olog.feature.base.HasBottomNavigation
import dev.olog.feature.base.RestorableScrollHelper
import dev.olog.intents.AppConstants
import dev.olog.intents.Classes
import dev.olog.intents.FloatingWindowsConstants
import dev.olog.intents.MusicServiceAction
import dev.olog.navigation.Navigator
import dev.olog.navigation.destination.FragmentScreen
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.navigation.BottomNavigationPage
import dev.olog.presentation.navigator.NavigatorLegacy
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
    lateinit var navigatorLegacy: NavigatorLegacy
    @Inject
    lateinit var navigator: Navigator

    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Suppress("unused")
    @Inject
    lateinit var rateAppDialog: RateAppDialog

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
            FloatingWindowsConstants.ACTION_START_SERVICE -> {
                FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
            }
            Shortcuts.SEARCH -> bottomNavigation.navigate(BottomNavigationPage.SEARCH)
            AppConstants.ACTION_CONTENT_VIEW -> slidingPanel.expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, Class.forName(Classes.SERVICE_MUSIC))
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            Shortcuts.DETAIL -> {
                launch {
                    delay(250)
                    val string = intent.getStringExtra(Shortcuts.DETAIL_EXTRA_ID)!!
                    val mediaId = MediaId.fromString(string)
                    navigator.toDetailFragment(mediaId)
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
        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION) {
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
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