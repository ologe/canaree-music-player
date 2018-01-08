package dev.olog.presentation.activity_main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.HIDDEN
import dev.olog.presentation.HasSlidingPanel
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import dev.olog.presentation.activity_preferences.PreferencesActivity
import dev.olog.presentation.collapse
import dev.olog.presentation.fragment_playing_queue.PlayingQueueFragment
import dev.olog.presentation.isExpanded
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import dev.olog.presentation.service_music.MediaControllerProvider
import dev.olog.presentation.service_music.MusicServiceBinderViewModel
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.toggleVisibility
import dev.olog.presentation.utils.rx.RxSlidingUpPanel
import dev.olog.shared.constants.FloatingInfoConstants
import dev.olog.shared_android.extension.asLiveData
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tab_view_pager.*
import javax.inject.Inject

class MainActivity: BaseActivity(), MediaControllerProvider, HasSlidingPanel {

    @Inject lateinit var musicServiceBinder: MusicServiceBinderViewModel
    private val innerPanelSlideListener by lazy(LazyThreadSafetyMode.NONE) { InnerPanelSlideListener(this) }

    @Inject lateinit var presenter: MainActivityPresenter
    @Inject lateinit var adapter: TabViewPagerAdapter
    @Inject lateinit var navigator: Navigator

    private lateinit var title: TextView
    private lateinit var artist: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = presenter.getViewPagerLastPage()

        title = wrapper.findViewById(R.id.title)
        artist = wrapper.findViewById(R.id.artist)

        musicServiceBinder.getMediaControllerLiveData()
                .subscribe(this, { MediaControllerCompat.setMediaController(this, it) })

        Observables.combineLatest(
                RxSlidingUpPanel.panelStateEvents(slidingPanel).map { it.newState == SlidingUpPanelLayout.PanelState.EXPANDED },
                RxSlidingUpPanel.panelStateEvents(innerPanel).map { it.newState == SlidingUpPanelLayout.PanelState.COLLAPSED },
                { outerIsExpanded, innerIsCollapsed -> outerIsExpanded && innerIsCollapsed }
        ).distinctUntilChanged()
                .asLiveData()
                .subscribe(this, { canScroll ->
                    title.isSelected = canScroll
                    artist.isSelected = canScroll
                })

        presenter.isRepositoryEmptyUseCase.execute()
                .asLiveData()
                .subscribe(this, this::handleEmptyRepository)
    }

    override fun handleIntent(intent: Intent) {
        if (intent.action == FloatingInfoConstants.ACTION_START_SERVICE){
            musicServiceBinder.getMediaControllerLiveData().value?.let {
                val title = it.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                presenter.startFloatingService(this, title)
            }
        }
    }

    private fun handleEmptyRepository(isEmpty: Boolean){
        if (isEmpty){
            slidingPanel.panelState = HIDDEN
        } else if (slidingPanel.panelState == HIDDEN){
            slidingPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }

    override fun onResume() {
        super.onResume()
        innerPanel.addPanelSlideListener(innerPanelSlideListener)
        innerPanel.addPanelSlideListener(panelSlideListener)
        search.setOnClickListener { navigator.toSearchFragment() }
        settings.setOnClickListener { navigator.toMainPopup(it) }
        viewPager.addOnPageChangeListener(onAdapterPageChangeListener)
    }

    override fun onPause() {
        super.onPause()
        innerPanel.removePanelSlideListener(innerPanelSlideListener)
        innerPanel.removePanelSlideListener(panelSlideListener)
        search.setOnClickListener(null)
        settings.setOnClickListener(null)
        viewPager.removeOnPageChangeListener(onAdapterPageChangeListener)
    }

    override fun onDestroy() {
        MediaControllerCompat.setMediaController(this, null)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode){
            PreferencesActivity.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK){
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
            innerPanel.isExpanded() -> innerPanel.collapse()
            slidingPanel.isExpanded() -> slidingPanel.collapse()
            else -> super.onBackPressed()
        }
    }

    override fun getSupportMediaController(): MediaControllerCompat? {
        return MediaControllerCompat.getMediaController(this)
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel

    private val panelSlideListener = object : SlidingUpPanelLayout.SimplePanelSlideListener(){
        override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
            drag_area.toggleVisibility(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
        }
    }

    private val onAdapterPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            presenter.setViewPagerLastPage(position)
        }
    }
}