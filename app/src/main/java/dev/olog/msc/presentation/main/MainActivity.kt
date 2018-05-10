package dev.olog.msc.presentation.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.HIDDEN
import dev.olog.msc.Permissions
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.FloatingWindowsConstants
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.DrawsOnTop
import dev.olog.msc.presentation.base.HasBilling
import dev.olog.msc.presentation.base.HasSlidingPanel
import dev.olog.msc.presentation.base.music.service.MusicGlueActivity
import dev.olog.msc.presentation.dialog.rate.request.RateAppDialog
import dev.olog.msc.presentation.library.categories.CategoriesFragment
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.presentation.utils.animation.HasSafeTransition
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.getTopFragment
import dev.olog.msc.utils.k.extension.subscribe
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

//    @Suppress("unused") @Inject
//    lateinit var workManagerLiveData: WorkManagerLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            val canReadStorage = Permissions.canReadStorage(this)
            val toFirstAccess = !canReadStorage || presenter.isFirstAccess()
            if (toFirstAccess){
                navigator.toFirstAccess(SPLASH_REQUEST_CODE)
            } else {
                navigator.toLibraryCategories()
            }
        }

        presenter.isRepositoryEmptyUseCase.execute()
                .asLiveData()
                .subscribe(this, this::handleEmptyRepository)

        intent?.let { handleIntent(it) }
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
            AppConstants.SHORTCUT_SEARCH -> { navigator.toSearchFragment(null) }
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
        }
    }

    private fun handleEmptyRepository(isEmpty: Boolean){
        if (isEmpty && slidingPanel.panelState != HIDDEN){
            slidingPanel.panelState = HIDDEN
        } else if (!isEmpty && slidingPanel.panelState == HIDDEN){
            slidingPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            when (requestCode){
                SPLASH_REQUEST_CODE -> {
                    navigator.toLibraryCategories()
                    return
                }
                PreferencesActivity.REQUEST_CODE -> {
                    recreateActivity()
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

    private fun recreateActivity(){
        val fragment = findFragmentByTag<CategoriesFragment>(CategoriesFragment.TAG)
        fragment?.pagerAdapter?.clearFragments()
        recreate()
    }

    override fun onBackPressed() {
        if (tryPopFolderBack()){
            return
        }

        val topFragment = getTopFragment()

        when {
            topFragment is HasSafeTransition && topFragment.isAnimating() -> {
//              prevents circular reveal crash
            }
            topFragment is DrawsOnTop -> super.onBackPressed()
            slidingPanel.isExpanded() -> slidingPanel.collapse()
            else -> super.onBackPressed()
        }
    }

    private fun tryPopFolderBack(): Boolean {
        val categories = findFragmentByTag<CategoriesFragment>(CategoriesFragment.TAG)
        val pager = categories?.view?.findViewById<ViewPager>(R.id.viewPager)
        if (pager != null){
            val currentItem = pager.adapter!!.instantiateItem(pager, pager.currentItem) as Fragment
            if (currentItem is FolderTreeFragment){
                return currentItem.pop()
            }
        }
        return false
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel
}