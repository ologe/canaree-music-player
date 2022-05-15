package dev.olog.feature.main

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import dev.olog.feature.bubble.api.FloatingWindowsConstants
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.library.api.FeatureLibraryNavigator
import dev.olog.feature.main.api.BottomNavigationPage
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.HasBottomNavigation
import dev.olog.feature.main.rate.RateAppDialog
import dev.olog.feature.media.api.MusicServiceAction
import dev.olog.feature.player.api.FeaturePlayerNavigator
import dev.olog.feature.shortcuts.api.ShortcutsConstants
import dev.olog.feature.splash.api.FeatureSplashNavigator
import dev.olog.platform.CanHandleOnBackPressed
import dev.olog.platform.DrawsOnTop
import dev.olog.platform.HasScrollableContent
import dev.olog.platform.HasSlidingPanel
import dev.olog.platform.permission.OnPermissionChanged
import dev.olog.platform.permission.Permission
import dev.olog.platform.theme.hasPlayerAppearance
import dev.olog.platform.theme.isImmersiveMode
import dev.olog.scrollhelper.ScrollType
import dev.olog.shared.extension.dimen
import dev.olog.shared.extension.dip
import dev.olog.shared.extension.getTopFragment
import dev.olog.shared.extension.isTablet
import dev.olog.shared.extension.setHeight
import dev.olog.ui.ScrollHelperFactory
import dev.olog.ui.extension.collapse
import dev.olog.ui.extension.expand
import dev.olog.ui.extension.isExpanded
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MusicGlueActivity(),
    HasSlidingPanel,
    HasBottomNavigation,
    HasScrollableContent,
    OnPermissionChanged {

    private val viewModel by viewModels<MainActivityViewModel>()

    // handles lifecycle itself
    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior

    @Suppress("unused")
    @Inject
    lateinit var rateAppDialog: RateAppDialog

    @Inject
    lateinit var featureSplashNavigator: FeatureSplashNavigator
    @Inject
    lateinit var featureBubbleNavigator: FeatureBubbleNavigator
    @Inject
    lateinit var featureDetailNavigator: FeatureDetailNavigator
    @Inject
    lateinit var featureLibraryNavigator: FeatureLibraryNavigator
    @Inject
    lateinit var featurePlayerNavigator: FeaturePlayerNavigator

    @Inject
    lateinit var scrollHelperFactory: ScrollHelperFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            featurePlayerNavigator.show(
                activity = this,
                playerContainer = R.id.playerContainer,
                miniPlayerContainer = R.id.miniPlayerContainer,
            )
        }

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
                featureSplashNavigator.toSplash(this)
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
            val scrollHelper = scrollHelperFactory.create(
                this, ScrollType.Full(
                    slidingPanel = slidingPanel,
                    bottomNavigation = bottomWrapper,
                    toolbarHeight = dimen(dev.olog.ui.R.dimen.toolbar),
                    tabLayoutHeight = dimen(dev.olog.ui.R.dimen.tab),
                    realSlidingPanelPeek = dimen(dev.olog.ui.R.dimen.sliding_panel_peek)
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
                featureBubbleNavigator.startServiceIfHasOverlayPermission(this)
            }
            ShortcutsConstants.SEARCH -> bottomNavigation.navigate(BottomNavigationPage.SEARCH)
            FeatureMainNavigator.ACTION_CONTENT_VIEW -> getSlidingPanel().expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                featureMediaNavigator.startService(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH, null)
            }
            ShortcutsConstants.DETAIL -> {
                lifecycleScope.launchWhenResumed {
                    delay(250)
                    val string = intent.getStringExtra(ShortcutsConstants.DETAIL_EXTRA_ID)!!
                    val mediaId = MediaId.fromString(string)
                    featureDetailNavigator.toDetail(this@MainActivity, mediaId)
                }
            }
            Intent.ACTION_VIEW -> {
                featureMediaNavigator.startService(MusicServiceAction.PLAY_URI, intent.data)
            }
        }
        intent.action = null
        setIntent(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FeatureBubbleNavigator.REQUEST_CODE_HOVER_PERMISSION) {
            featureBubbleNavigator.startServiceIfHasOverlayPermission(this)
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
                getSlidingPanel().isExpanded() -> {
                    getSlidingPanel().collapse()
                    return
                }
            }
            if (featureLibraryNavigator.tryPopFolderBack(this)) {
                return
            }

            super.onBackPressed()
        } catch (ex: IllegalStateException) {
            /*random fragment manager crashes */
            ex.printStackTrace()
        }

    }

    override fun getSlidingPanel(): BottomSheetBehavior<*> {
        return BottomSheetBehavior.from(slidingPanel)
    }

    override fun navigate(page: BottomNavigationPage) {
        bottomNavigation.navigate(page)
    }

    override fun restoreToInitialTranslation(){
        findViewById<View>(R.id.toolbar)?.animate()?.translationY(0f)
        findViewById<View>(R.id.tabLayout)?.animate()?.translationY(0f)
    }
}