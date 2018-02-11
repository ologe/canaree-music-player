package dev.olog.msc.presentation.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.view.ViewPager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.HIDDEN
import dev.olog.msc.R
import dev.olog.msc.constants.Constants
import dev.olog.msc.constants.FloatingInfoConstants
import dev.olog.msc.presentation.FloatingInfoServiceHelper
import dev.olog.msc.presentation.MediaControllerProvider
import dev.olog.msc.presentation.MusicServiceBinderViewModel
import dev.olog.msc.presentation.base.*
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.shared_android.interfaces.FloatingInfoServiceClass
import dev.olog.shared_android.interfaces.MusicServiceClass
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tab_view_pager.*
import javax.inject.Inject

class MainActivity: BaseActivity(), MediaControllerProvider, HasSlidingPanel {

    @Inject lateinit var musicServiceBinder: MusicServiceBinderViewModel

    @Inject lateinit var presenter: MainActivityPresenter
    @Inject lateinit var adapter: TabViewPagerAdapter
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var floatingInfoServiceBinder: FloatingInfoServiceClass
    @Inject lateinit var musicServiceClass: MusicServiceClass
//    @Inject lateinit var billing : IBilling

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = presenter.getViewPagerLastPage(adapter.count)

        musicServiceBinder.getMediaControllerLiveData()
                .subscribe(this, { MediaControllerCompat.setMediaController(this, it) })

        presenter.isRepositoryEmptyUseCase.execute()
                .asLiveData()
                .subscribe(this, this::handleEmptyRepository)

        slidingPanel.setScrollableViewHelper(NestedScrollHelper())

        pagerEmptyState.toggleVisibility(adapter.isEmpty())
    }

    override fun handleIntent(intent: Intent) {
        when (intent.action){
            FloatingInfoConstants.ACTION_START_SERVICE -> {
                musicServiceBinder.getMediaControllerLiveData().value?.let {
                    val title = it.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                    presenter.startFloatingService(this, title)
                }
            }
            Constants.SHORTCUT_SEARCH -> { navigator.toSearchFragment(true) }
            Constants.ACTION_CONTENT_VIEW -> {
                slidingPanel.expand()
            }
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, musicServiceClass.get())
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
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

    override fun onResume() {
        super.onResume()
        search.setOnClickListener { navigator.toSearchFragment(false) }
        settings.setOnClickListener { navigator.toMainPopup(it) }
        viewPager.addOnPageChangeListener(onAdapterPageChangeListener)
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        search.setOnClickListener(null)
        settings.setOnClickListener(null)
        viewPager.removeOnPageChangeListener(onAdapterPageChangeListener)
        floatingWindow.setOnClickListener(null)
    }

    override fun onDestroy() {
        MediaControllerCompat.setMediaController(this, null)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode){
            PreferencesActivity.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    adapter.removeAll()
                    recreate()
                }
            }
            FloatingInfoServiceHelper.REQUEST_CODE_HOVER_PERMISSION -> {
                presenter.startFloatingService(this, null)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        val playingQueue = findFragmentByTag<PlayingQueueFragment>(PlayingQueueFragment.TAG)
        when {
            playingQueue != null -> super.onBackPressed()
            slidingPanel.isExpanded() -> slidingPanel.collapse()
            else -> super.onBackPressed()
        }
    }

    override fun getSupportMediaController(): MediaControllerCompat? {
        return MediaControllerCompat.getMediaController(this)
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel

    fun startServiceOrRequestOverlayPermission(){
//        if (billing.isPremium()){
            FloatingInfoServiceHelper.startServiceOrRequestOverlayPermission(this, floatingInfoServiceBinder)
//        } else {
//            toast("floating window is a premium feature")
//             todo open purchase activity
//        }
    }

    private val onAdapterPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            presenter.setViewPagerLastPage(position)
        }
    }
}