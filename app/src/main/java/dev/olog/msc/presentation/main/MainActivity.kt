package dev.olog.msc.presentation.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.HIDDEN
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.FloatingWindowsConstants
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.base.HasSlidingPanel
import dev.olog.msc.presentation.base.music.service.MusicGlueActivity
import dev.olog.msc.presentation.library.categories.CategoriesFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private const val SPLASH_REQUEST_CODE = 0

class MainActivity : MusicGlueActivity(), HasSlidingPanel {

    @Inject lateinit var presenter: MainActivityPresenter
    @Inject lateinit var navigator: Navigator
    @Suppress("unused") // false alarm
    @Inject lateinit var statusBarColorBehavior: StatusBarColorBehavior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            val hasStoragePermission = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (presenter.isFirstAccess(hasStoragePermission)){
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
                }
                PreferencesActivity.REQUEST_CODE -> recreateActivity()
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION){
                FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun recreateActivity(){
        val fragment = findFragmentByTag<CategoriesFragment>(CategoriesFragment.TAG)
        fragment?.pagerAdapter?.clearFragments()
        recreate()
    }

    override fun onBackPressed() {
        val playingQueue = findFragmentByTag<PlayingQueueFragment>(PlayingQueueFragment.TAG)
        when {
            playingQueue != null -> super.onBackPressed()
            slidingPanel.isExpanded() -> slidingPanel.collapse()
            else -> super.onBackPressed()
        }
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel


}