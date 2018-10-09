package dev.olog.msc.presentation.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import androidx.core.view.doOnPreDraw
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.Permissions
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.FloatingWindowsConstants
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.DrawsOnTop
import dev.olog.msc.presentation.base.HasBilling
import dev.olog.msc.presentation.base.HasSlidingPanel
import dev.olog.msc.presentation.base.bottom.sheet.DimBottomSheetDialogFragment
import dev.olog.msc.presentation.base.music.service.MusicGlueActivity
import dev.olog.msc.presentation.dialog.rate.request.RateAppDialog
import dev.olog.msc.presentation.library.categories.track.CategoriesFragment
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.presentation.utils.animation.HasSafeTransition
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private const val SPLASH_REQUEST_CODE = 0

class MainActivity : MusicGlueActivity(), HasSlidingPanel, HasBilling {

    @Inject lateinit var presenter: MainActivityPresenter
    @Inject lateinit var navigator: Navigator
    // handles lifecycle itself
    @Inject override lateinit var billing: IBilling

    @Suppress("unused") @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Suppress("unused") @Inject
    lateinit var rateAppDialog : RateAppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slidingPanel.panelHeight = dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)

        presenter.isRepositoryEmptyUseCase.execute()
                .asLiveData()
                .subscribe(this, this::handleEmptyRepository)

        val canReadStorage = Permissions.canReadStorage(this)
        val isFirstAccess = presenter.isFirstAccess()
        val toFirstAccess = !canReadStorage || isFirstAccess
        if (toFirstAccess){
            navigator.toFirstAccess(SPLASH_REQUEST_CODE)
            return
        } else if (savedInstanceState == null) {
            var navigateTo = presenter.getLastBottomViewPage()
            if (!presenter.canShowPodcastCategory()){
                bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
                if (navigateTo == R.id.navigation_podcasts) {
                    navigateTo = R.id.navigation_songs
                }
            }
            bottomNavigation.selectedItemId = navigateTo
            bottomNavigate(navigateTo, false)
        } else if (savedInstanceState != null) {
            if (!presenter.canShowPodcastCategory()){
                val currentId = bottomNavigation.selectedItemId
                bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
                if (currentId == R.id.navigation_podcasts){
                    bottomNavigation.selectedItemId = R.id.navigation_songs
                    bottomNavigate(bottomNavigation.selectedItemId, true)
                }
            }
        }

        var navigateTo = bottomNavigation.selectedItemId
        if (!presenter.canShowPodcastCategory()){
            bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
            navigateTo = R.id.navigation_songs
        }
        bottomNavigate(navigateTo, false)

        bottomWrapper.doOnPreDraw {
            if (slidingPanel.isExpanded()){
                bottomWrapper.translationY = bottomWrapper.height.toFloat()
            }
        }

        intent?.let { handleIntent(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.setOnNavigationItemSelectedListener {
            presenter.setLastBottomViewPage(it.itemId)
            bottomNavigate(it.itemId, false)
            true
        }
        bottomNavigation.setOnNavigationItemReselectedListener { bottomNavigate(it.itemId, true) }
        slidingPanel.addPanelSlideListener(onPanelSlide)
    }

    private fun bottomNavigate(itemId: Int, forceRecreate: Boolean){
        when (itemId){
            R.id.navigation_songs -> navigator.toLibraryCategories(forceRecreate)
            R.id.navigation_search -> navigator.toSearchFragment()
            R.id.navigation_podcasts -> navigator.toPodcastCategories(forceRecreate)
            R.id.navigation_queue -> navigator.toPlayingQueueFragment()
            else -> throw IllegalArgumentException("invalid item")
        }
    }

    override fun onPause() {
        super.onPause()
        bottomNavigation.setOnNavigationItemSelectedListener(null)
        bottomNavigation.setOnNavigationItemReselectedListener(null)
        slidingPanel.removePanelSlideListener(onPanelSlide)
    }

    private val onPanelSlide = object : SlidingUpPanelLayout.PanelSlideListener {

        override fun onPanelSlide(panel: View, slideOffset: Float) {
            bottomWrapper.translationY = bottomWrapper.height * clamp(slideOffset, 0f, 1f)
        }

        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {}
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action){
            FloatingWindowsConstants.ACTION_START_SERVICE -> {
                FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
            }
            AppConstants.SHORTCUT_SEARCH -> {
                bottomNavigation.selectedItemId = R.id.navigation_search
                navigator.toSearchFragment()
            }
            AppConstants.ACTION_CONTENT_VIEW -> slidingPanel.expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            AppConstants.SHORTCUT_DETAIL -> {
                val string = intent.getStringExtra(AppConstants.SHORTCUT_DETAIL_MEDIA_ID)
                val mediaId = MediaId.fromString(string)
                navigator.toDetailFragment(mediaId)
            }
            Intent.ACTION_VIEW -> {
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MusicConstants.ACTION_PLAY_FROM_URI
                serviceIntent.data = intent.data
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
        setIntent(null)
    }

    private fun handleEmptyRepository(isEmpty: Boolean){
        if (isEmpty){
            slidingPanel.panelHeight = dimen(R.dimen.bottom_navigation_height)
        } else {
            slidingPanel.panelHeight = dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            when (requestCode){
                SPLASH_REQUEST_CODE -> {
                    bottomNavigate(bottomNavigation.selectedItemId, false)
                    slidingPanel.collapse()
                    return
                }
                PreferencesActivity.REQUEST_CODE -> {
                    bottomNavigate(bottomNavigation.selectedItemId, true)
                    recreate()
                    return
                }
            }
        }

        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION){
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        try {
            if (tryPopFolderBack()){
                return
            }

            val topFragment = getTopFragment()

            when {
                topFragment is HasSafeTransition && topFragment.isAnimating() -> {
//                  prevents circular reveal crash
                }
                topFragment is DrawsOnTop -> super.onBackPressed()
                topFragment is DimBottomSheetDialogFragment -> supportFragmentManager.popBackStack()
                slidingPanel.isExpanded() -> slidingPanel.collapse()
                else -> super.onBackPressed()
            }
        } catch (ex: IllegalStateException){ /*random fragment manager crashes */}

    }

    private fun tryPopFolderBack(): Boolean {
        val categories = findFragmentByTag<CategoriesFragment>(CategoriesFragment.TAG)
        categories?.view?.findViewById<ViewPager>(R.id.viewPager)?.let { pager ->
            val currentItem = pager.adapter?.instantiateItem(pager, pager.currentItem) as Fragment
            return if (currentItem is FolderTreeFragment){
                currentItem.pop()
            } else false

        } ?: return false
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel
}