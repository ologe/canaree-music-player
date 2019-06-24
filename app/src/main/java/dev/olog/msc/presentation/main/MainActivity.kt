package dev.olog.msc.presentation.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.crashlytics.android.Crashlytics
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import dev.olog.core.MediaId
import dev.olog.msc.R
import dev.olog.presentation.AppConstants
import dev.olog.msc.constants.FloatingWindowsConstants
import dev.olog.media.MusicConstants
import dev.olog.msc.app.shortcuts.Shortcuts
import dev.olog.msc.domain.gateway.prefs.PresentationPreferences
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.DrawsOnTop
import dev.olog.msc.presentation.base.HasBilling
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.msc.presentation.base.bottom.sheet.DimBottomSheetDialogFragment
import dev.olog.msc.presentation.base.music.service.MusicGlueActivity
import dev.olog.msc.presentation.dialog.rate.request.RateAppDialog
import dev.olog.msc.presentation.main.di.inject
import dev.olog.presentation.main.BottomNavigationPage
import dev.olog.presentation.navigator.Navigator
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.k.extension.*
import dev.olog.presentation.interfaces.HasBottomNavigation
import dev.olog.scrollhelper.Input
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.shared.extensions.dimen
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.extensions.subscribe
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : MusicGlueActivity(), HasSlidingPanel, HasBilling, HasBottomNavigation {

    companion object {
        const val INVITE_FRIEND_CODE = 12198
    }

    @Inject lateinit var presenter: MainActivityPresenter
    @Inject lateinit var navigator: Navigator
    // handles lifecycle itself
    @Inject override lateinit var billing: IBilling

    @Inject lateinit var presentationPrefs: PresentationPreferences

    @Suppress("unused") @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Suppress("unused") @Inject
    lateinit var rateAppDialog : RateAppDialog

    private lateinit var scrollHelper: SuperCerealScrollHelper

    override fun injectComponent() = inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollHelper = SuperCerealScrollHelper(this, Input.Full(
            slidingPanel = getSlidingPanel() to dimen(R.dimen.sliding_panel_peek),
            bottomNavigation = bottomWrapper to dimen(R.dimen.bottom_navigation_height),
            toolbarHeight = dimen(R.dimen.toolbar),
            tabLayoutHeight = dimen(R.dimen.tab)
        ))

        bottomNavigation.presentationPrefs = presentationPrefs

        presenter.isRepositoryEmptyUseCase.execute()
                .asLiveData()
                .subscribe(this, this::handleEmptyRepository)

        when {
            presenter.isFirstAccess() -> {
                navigator.toFirstAccess()
                return
            }
            savedInstanceState == null -> {
                getSlidingPanel().peekHeight = dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)
                bottomNavigation.navigateToLastPage()
            }
        }

        intent?.let { handleIntent(it) }
    }

    override fun onResume() {
        super.onResume()
        scrollHelper.onAttach()
    }

    override fun onPause() {
        super.onPause()
        scrollHelper.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        scrollHelper.dispose()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action){
            FloatingWindowsConstants.ACTION_START_SERVICE -> {
                FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
            }
            Shortcuts.SEARCH -> bottomNavigation.navigate(BottomNavigationPage.SEARCH)
            AppConstants.ACTION_CONTENT_VIEW -> getSlidingPanel().expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            Shortcuts.DETAIL -> {
                val string = intent.getStringExtra(Shortcuts.DETAIL_EXTRA_ID)
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
//        if (isEmpty){ TODO
//            getSlidingPanel().peekHeight = dimen(R.dimen.bottom_navigation_height)
//        } else {
//            getSlidingPanel().peekHeight = dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            when (requestCode){
                PreferencesActivity.REQUEST_CODE -> {
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
        // TODO update
        try {
            if (tryPopFolderBack()){
                return
            }

            val topFragment = getTopFragment()

            when {
                topFragment is DrawsOnTop -> super.onBackPressed()
                topFragment is DimBottomSheetDialogFragment -> supportFragmentManager.popBackStack()
                getSlidingPanel().isExpanded() -> getSlidingPanel().collapse()
                else -> super.onBackPressed()
            }
        } catch (ex: IllegalStateException){ /*random fragment manager crashes */}

    }

    private fun tryPopFolderBack(): Boolean {
        return false
//        val categories = findFragmentByTag<CategoriesFragment>(CategoriesFragment.TAG)
//        categories?.view?.findViewById<androidx.viewpager.widget.ViewPager>(R.id.viewPager)?.let { pager ->
//            val currentItem = pager.adapter?.instantiateItem(pager, pager.currentItem) as androidx.fragment.app.Fragment
//            return if (currentItem is FolderTreeFragment){
//                currentItem.pop()
//            } else false
//
//        } ?: return false
    }

    override fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*> {
        return BottomSheetBehavior.from(slidingPanel) as MultiListenerBottomSheetBehavior<*>
    }

    override fun navigate(page: BottomNavigationPage) {
        bottomNavigation.navigate(page)
    }
}