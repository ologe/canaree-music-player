package dev.olog.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.core.extensions.getTopFragment
import dev.olog.domain.MediaId
import dev.olog.feature.app.shortcuts.Shortcuts
import dev.olog.feature.presentation.base.CanHandleOnBackPressed
import dev.olog.feature.presentation.base.DrawsOnTop
import dev.olog.feature.presentation.base.activity.HasScrollingInterface
import dev.olog.feature.presentation.base.activity.HasSlidingPanel
import dev.olog.feature.presentation.base.activity.SharedViewModel
import dev.olog.feature.presentation.base.extensions.*
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toPresentation
import dev.olog.intents.AppConstants
import dev.olog.intents.Classes
import dev.olog.intents.MusicServiceAction
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.activity.HasBottomNavigation
import dev.olog.feature.presentation.base.activity.OnPermissionChanged
import dev.olog.feature.presentation.base.activity.Permission
import dev.olog.navigation.screens.BottomNavigationPage
import dev.olog.navigation.Navigator
import dev.olog.presentation.rateapp.RateAppDialog
import dev.olog.scrollhelper.ScrollType
import dev.olog.shared.android.theme.BottomSheetType
import dev.olog.shared.android.theme.themeManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject


class MainActivity : MusicGlueActivity(),
    HasSlidingPanel,
    HasBottomNavigation,
    HasScrollingInterface,
    OnPermissionChanged {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by viewModels<MainActivityViewModel> {
        factory
    }
    private val sharedViewModel by viewModels<SharedViewModel> {
        factory
    }

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
        setContentView(R.layout.activity_main)

        observeMetadata()
            .map { it.mediaId.toPresentation() }
            .filterIsInstance<PresentationId.Track>()
            .onEach { sharedViewModel.setCurrentPlaying(it) }
            .launchIn(lifecycleScope)

        if (themeManager.isImmersive){
            // workaround, on some device on immersive mode bottom navigation disappears
            rootView.fitsSystemWindows = true
            slidingPanel.fitsSystemWindows = true
            bottomWrapper.fitsSystemWindows = true
        }

        if (themeManager.playerAppearance.isMini){
            // TODO made a resource value
            slidingPanelFade.parallax = 0
            slidingPanel.setHeight(dip(300))
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
        getSlidingPanel().peekHeight = when (themeManager.bottomSheetType) {
            BottomSheetType.DEFAULT -> dimen(R.dimen.sliding_panel_peek_plus_navigation)
            BottomSheetType.FLOATING -> dimen(R.dimen.sliding_panel_peek_plus_navigation) + dip(16)
        }
        val peekHeight = when (themeManager.bottomSheetType) {
            BottomSheetType.DEFAULT -> dimen(R.dimen.sliding_panel_peek)
            BottomSheetType.FLOATING -> dimen(R.dimen.sliding_panel_peek) + dip(16)
        }
        if (themeManager.bottomSheetType == BottomSheetType.FLOATING) {
            separator.isVisible = false
        }

        val scrollHelper = SuperCerealScrollHelper(
            this, ScrollType.Full(
                slidingPanel = slidingPanel,
                bottomNavigation = bottomWrapper,
                toolbarHeight = dimen(R.dimen.toolbar),
                tabLayoutHeight = dimen(R.dimen.tab),
                realSlidingPanelPeek = peekHeight
            )
        )
        lifecycle.addObserver(scrollHelper)
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
            Shortcuts.SEARCH -> bottomNavigation.navigate(BottomNavigationPage.SEARCH)
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
                    navigator.toDetailFragment(mediaId as MediaId.Category)
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
            navigator.toFloating(this)
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
                getSlidingPanelView().findViewById<RecyclerView>(R.id.list).canScrollVertically(-1) -> {
                    getSlidingPanelView().findViewById<RecyclerView>(R.id.list).smoothScrollToPosition(0)
                    return
                }
                getSlidingPanel().isExpanded() -> {
                    getSlidingPanel().collapse()
                    return
                }
            }
            if (tryPopFolderBack()) {
                return
            }

            super.onBackPressed()
        } catch (ex: IllegalStateException) {
            /*random fragment manager crashes */
            Timber.e(ex)
        }

    }

    private fun tryPopFolderBack(): Boolean {
//        TODO
        return false
//        val categoriesFragment = supportFragmentManager
//            .findFragmentByTag(LibraryFragment.TAG_TRACK) as? LibraryFragment
//            ?: return false
//
//        if (categoriesFragment.isVisible && categoriesFragment.isCurrentFragmentFolderTree()){
//            val folderTree = categoriesFragment.childFragmentManager.fragments
//                .filter { it.isVisible }
//                .find { it is FolderTreeFragment } as? CanHandleOnBackPressed
//            return folderTree?.handleOnBackPressed() == true
//        }
//        return false
    }

    override fun getSlidingPanel(): BottomSheetBehavior<*> {
        return BottomSheetBehavior.from(slidingPanel)
    }

    override fun getSlidingPanelView(): View {
        return slidingPanel
    }

    override fun navigate(page: BottomNavigationPage) {
        bottomNavigation.navigate(page)
    }

    override fun restoreUpperWidgetsTranslation(){
        findViewById<View>(R.id.toolbar)?.animate()?.translationY(0f)
        findViewById<View>(R.id.tabLayout)?.animate()?.translationY(0f)
    }
}