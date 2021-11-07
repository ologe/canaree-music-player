package dev.olog.msc.main

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.appshortcuts.Shortcuts
import dev.olog.core.MediaId
import dev.olog.feature.base.CanHandleOnBackPressed
import dev.olog.feature.base.DrawsOnTop
import dev.olog.feature.base.HasSlidingPanel
import dev.olog.feature.base.RestorableScroll
import dev.olog.feature.main.BottomNavigationPage
import dev.olog.feature.main.HasBottomNavigation
import dev.olog.feature.base.permission.OnPermissionChanged
import dev.olog.feature.base.permission.Permission
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.dialogs.rate.RateAppDialog
import dev.olog.feature.floating.FeatureFloatingNavigator
import dev.olog.feature.floating.FloatingWindowsConstants
import dev.olog.feature.library.folder.tree.FolderTreeFragment
import dev.olog.feature.library.library.LibraryFragment
import dev.olog.feature.splash.FeatureSplashNavigator
import dev.olog.intents.AppConstants
import dev.olog.intents.Classes
import dev.olog.intents.MusicServiceAction
import dev.olog.msc.R
import dev.olog.scrollhelper.ScrollType
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.theme.isImmersiveMode
import dev.olog.shared.widgets.extension.collapse
import dev.olog.shared.widgets.extension.expand
import dev.olog.shared.widgets.extension.isExpanded
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MusicGlueActivity(),
    HasSlidingPanel,
    HasBottomNavigation,
    OnPermissionChanged,
    RestorableScroll {

    private val viewModel by viewModels<MainActivityViewModel>()

    @Inject
    lateinit var splashNavigator: FeatureSplashNavigator
    @Inject
    lateinit var floatingNavigator: FeatureFloatingNavigator
    @Inject
    lateinit var detailNavigator: FeatureDetailNavigator

    // handles lifecycle itself
    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Suppress("unused")
    @Inject
    lateinit var rateAppDialog: RateAppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isImmersiveMode()){
            // workaround, on some device on immersive mode bottom navigation disappears
            rootView.fitsSystemWindows = true
            slidingPanel.fitsSystemWindows = true
            bottomWrapper.fitsSystemWindows = true
        }

        if (hasPlayerAppearance().isMini()){
            // TODO made a resource value
            slidingPanelFade.parallax = 0
            slidingPanel.setHeight(dip(300))
        }

        setupSlidingPanel()

        when {
            viewModel.isFirstAccess() -> {
                splashNavigator.toFirstAccess(this)
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
                    slidingPanel = slidingPanel,
                    bottomNavigation = bottomWrapper,
                    toolbarHeight = dimen(dev.olog.shared.android.R.dimen.toolbar),
                    tabLayoutHeight = dimen(dev.olog.shared.android.R.dimen.tab),
                    realSlidingPanelPeek = dimen(dev.olog.shared.widgets.R.dimen.sliding_panel_peek)
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
                floatingNavigator.startServiceIfHasPermission(this)
            }
            Shortcuts.SEARCH -> bottomNavigation.navigate(BottomNavigationPage.SEARCH)
            AppConstants.ACTION_CONTENT_VIEW -> getPanel().expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, Class.forName(Classes.SERVICE_MUSIC))
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            Shortcuts.DETAIL -> {
                lifecycleScope.launch {
                    delay(250)
                    val string = intent.getStringExtra(Shortcuts.DETAIL_EXTRA_ID)!!
                    val mediaId = MediaId.fromString(string)
                    detailNavigator.toDetailFragment(this@MainActivity, mediaId)
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
        if (floatingNavigator.handleOnActivityResult(this, requestCode, resultCode, data)) {
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
                getPanel().isExpanded() -> {
                    getPanel().collapse()
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
        val categoriesFragment =
            supportFragmentManager.findFragmentByTag(LibraryFragment.TAG_TRACK) as? LibraryFragment ?: return false

        if (categoriesFragment.isCurrentFragmentFolderTree()){
            val folderTree = categoriesFragment.childFragmentManager.fragments
                .find { it is FolderTreeFragment } as? CanHandleOnBackPressed
            return folderTree?.handleOnBackPressed() == true
        }
        return false
    }

    override fun getPanel(): BottomSheetBehavior<*> {
        return BottomSheetBehavior.from(slidingPanel)
    }

    override fun navigate(page: BottomNavigationPage) {
        bottomNavigation.navigate(page)
    }

    override fun restoreUpperWidgets(){
        findViewById<View>(R.id.toolbar)?.animate()?.translationY(0f)
        findViewById<View>(R.id.tabLayout)?.animate()?.translationY(0f)
    }
}