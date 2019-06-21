package dev.olog.msc.presentation.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import com.crashlytics.android.Crashlytics
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.firebase.analytics.FirebaseAnalytics
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.shared.Permissions
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
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.presentation.utils.animation.HasSafeTransition
import dev.olog.msc.pro.IBilling
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.*
import dev.olog.shared.clamp
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : MusicGlueActivity(), HasSlidingPanel, HasBilling {

    companion object {
        private const val SPLASH_REQUEST_CODE = 0
        const val INVITE_FRIEND_CODE = 12198
    }

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
                    presenter.setLastBottomViewPage(navigateTo)
                }
            }
            bottomNavigation.selectedItemId = navigateTo
            bottomNavigate(navigateTo, false)
        } else {
            if (!presenter.canShowPodcastCategory()){
                val currentId = presenter.getLastBottomViewPage()
                bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
                if (currentId == R.id.navigation_podcasts){
                    bottomNavigation.selectedItemId = R.id.navigation_songs
                    presenter.setLastBottomViewPage(R.id.navigation_songs)
                    bottomNavigate(bottomNavigation.selectedItemId, true)
                }
            }
        }

        if (AppTheme.isMiniTheme()){
            slidingPanel.setParallaxOffset(0)
            playerLayout.layoutParams = SlidingUpPanelLayout.LayoutParams(
                    SlidingUpPanelLayout.LayoutParams.MATCH_PARENT, SlidingUpPanelLayout.LayoutParams.WRAP_CONTENT
            )
        }

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
        handleFakeView(slidingPanel.panelState)
    }

    private fun handleFakeView(state: SlidingUpPanelLayout.PanelState){
        when (state){
            SlidingUpPanelLayout.PanelState.EXPANDED,
            SlidingUpPanelLayout.PanelState.ANCHORED -> {
                fakeView.isClickable = true
                fakeView.isFocusable = true
                fakeView.setOnClickListener { slidingPanel.collapse() }
            }
            else -> {
                fakeView.setOnClickListener(null)
                fakeView.isClickable = false
                fakeView.isFocusable = false
            }
        }
    }

    private fun bottomNavigate(itemId: Int, forceRecreate: Boolean){
        when (itemId){
            R.id.navigation_songs -> navigator.toLibraryCategories(forceRecreate)
            R.id.navigation_search -> navigator.toSearchFragment()
            R.id.navigation_podcasts -> navigator.toPodcastCategories(forceRecreate)
            R.id.navigation_queue -> navigator.toPlayingQueueFragment()
            else -> bottomNavigate(R.id.navigation_songs, forceRecreate)
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

        override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
            handleFakeView(newState)
        }
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
                INVITE_FRIEND_CODE -> handleOnFriendsInvited(resultCode, data)
            }
        }

        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION){
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleOnFriendsInvited(resultCode: Int, data: Intent?){
        try {
            val invitedIds = AppInviteInvitation.getInvitationIds(resultCode, data!!)
            val analytics = FirebaseAnalytics.getInstance(this)
            analytics.logEvent("invited_friends", bundleOf(
                    "friends_number_invited" to invitedIds.size
            ))
            analytics.setUserProperty("invited_friends", "true")
        } catch (ex: Exception){
            ex.printStackTrace()
            Crashlytics.logException(ex)
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
        categories?.view?.findViewById<androidx.viewpager.widget.ViewPager>(R.id.viewPager)?.let { pager ->
            val currentItem = pager.adapter?.instantiateItem(pager, pager.currentItem) as androidx.fragment.app.Fragment
            return if (currentItem is FolderTreeFragment){
                currentItem.pop()
            } else false

        } ?: return false
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel
}