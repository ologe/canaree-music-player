package dev.olog.msc.presentation.main.di

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.crashlytics.android.Crashlytics
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import dev.olog.appshortcuts.Shortcuts
import dev.olog.core.MediaId
import dev.olog.msc.constants.FloatingWindowsConstants
import dev.olog.msc.presentation.base.bottom.sheet.DimBottomSheetDialogFragment
import dev.olog.msc.presentation.dialog.rate.request.RateAppDialog
import dev.olog.presentation.prefs.PreferencesActivity
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.CanHandleOnBackPressed
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.interfaces.HasBottomNavigation
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.library.LibraryFragment
import dev.olog.presentation.main.MainActivityViewModel
import dev.olog.presentation.main.MusicGlueActivity
import dev.olog.presentation.main.StatusBarColorBehavior
import dev.olog.presentation.main.SuperCerealScrollHelper
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.pro.HasBilling
import dev.olog.presentation.pro.IBilling
import dev.olog.presentation.utils.collapse
import dev.olog.presentation.utils.expand
import dev.olog.presentation.utils.isExpanded
import dev.olog.scrollhelper.Input
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.service.music.MusicService
import dev.olog.shared.AppConstants
import dev.olog.shared.MusicConstants
import dev.olog.shared.extensions.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : MusicGlueActivity(), HasSlidingPanel, HasBilling, HasBottomNavigation {

    companion object {
        const val INVITE_FRIEND_CODE = 12198
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazyFast { viewModelProvider<MainActivityViewModel>(factory) }
    @Inject
    lateinit var navigator: Navigator
    // handles lifecycle itself
    @Inject
    override lateinit var billing: IBilling

    @Inject
    lateinit var presentationPrefs: PresentationPreferencesGateway

    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Suppress("unused")
    @Inject
    lateinit var rateAppDialog: RateAppDialog

    private lateinit var scrollHelper: SuperCerealScrollHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollHelper = SuperCerealScrollHelper(
            this, Input.Full(
                slidingPanel = getSlidingPanel() to dimen(R.dimen.sliding_panel_peek),
                bottomNavigation = bottomWrapper to dimen(R.dimen.bottom_navigation_height),
                toolbarHeight = dimen(R.dimen.toolbar),
                tabLayoutHeight = dimen(R.dimen.tab)
            )
        )

        bottomNavigation.presentationPrefs = presentationPrefs

        viewModel.observeIsRepositoryEmpty()
            .subscribe(this, this::handleEmptyRepository)

        when {
            viewModel.isFirstAccess() -> {
                navigator.toFirstAccess()
                return
            }
            savedInstanceState == null -> {
                getSlidingPanel().peekHeight =
                    dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)
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
        when (intent.action) {
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

    private fun handleEmptyRepository(isEmpty: Boolean) {
//        if (isEmpty){ TODO
//            getSlidingPanel().peekHeight = dimen(R.dimen.bottom_navigation_height)
//        } else {
//            getSlidingPanel().peekHeight = dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PreferencesActivity.REQUEST_CODE -> {
                    recreate()
                    return
                }
                INVITE_FRIEND_CODE -> handleOnFriendsInvited(resultCode, data)
            }
        }

        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION) {
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleOnFriendsInvited(resultCode: Int, data: Intent?) {
        try {
            val invitedIds = AppInviteInvitation.getInvitationIds(resultCode, data!!)
            val analytics = FirebaseAnalytics.getInstance(this)
            analytics.logEvent(
                "invited_friends", bundleOf(
                    "friends_number_invited" to invitedIds.size
                )
            )
            analytics.setUserProperty("invited_friends", "true")
        } catch (ex: Exception) {
            ex.printStackTrace()
            Crashlytics.logException(ex)
        }
    }

    override fun onBackPressed() {
        try {
            val topFragment = supportFragmentManager.getTopFragment()

            when {
                topFragment is DrawsOnTop || topFragment is DimBottomSheetDialogFragment -> {
                    super.onBackPressed()
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
            ex.printStackTrace()
        }

    }

    private fun tryPopFolderBack(): Boolean {
        // TODO wrong implementation, i think it pop folder back even if not seeing the fragment
        val categoriesFragment =
            supportFragmentManager.findFragmentByTag(LibraryFragment.TAG_TRACK) ?: return false
        val fragments = categoriesFragment.childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is CanHandleOnBackPressed &&
                fragment.viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED && // ensure fragment is visible
                fragment.handleOnBackPressed()
            ) {
                return true
            }
        }
        return false
    }

    override fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*> {
        return BottomSheetBehavior.from(slidingPanel) as MultiListenerBottomSheetBehavior<*>
    }

    override fun navigate(page: BottomNavigationPage) {
        bottomNavigation.navigate(page)
    }
}