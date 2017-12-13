package dev.olog.presentation.activity_main

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.widget.TextView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.Lazy
import dev.olog.presentation.HasSlidingPanel
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import dev.olog.presentation.collapse
import dev.olog.presentation.fragment_mini_queue.MiniQueueFragment
import dev.olog.presentation.fragment_playing_queue.PlayingQueueFragment
import dev.olog.presentation.isExpanded
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import dev.olog.presentation.service_music.MediaControllerProvider
import dev.olog.presentation.service_music.MusicServiceBinderViewModel
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.rx.RxSlidingUpPanel
import dev.olog.shared.constants.FloatingInfoConstants
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_player_drag_area.*
import kotlinx.android.synthetic.main.layout_tab_view_pager.*
import javax.inject.Inject

class MainActivity: BaseActivity(), MediaControllerProvider, HasSlidingPanel {

    @Inject lateinit var adapter: TabViewPagerAdapter
    @Inject lateinit var musicServiceBinder: MusicServiceBinderViewModel
    @Inject lateinit var navigator: Navigator
    private val innerPanelSlideListener by lazy (LazyThreadSafetyMode.NONE) { InnerPanelSlideListener(this) }

    @Inject lateinit var presenter: Lazy<MainActivityPresenter>

    private lateinit var title: TextView
    private lateinit var artist: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = titleWrapper.findViewById(R.id.title)
        artist = artistWrapper.findViewById(R.id.artist)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4
        tabLayout.setupWithViewPager(viewPager)

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
    }


    override fun handleIntent(intent: Intent) {
        if (intent.action == FloatingInfoConstants.ACTION_START_SERVICE){
            musicServiceBinder.getMediaControllerLiveData().value?.let {
                val title = it.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                presenter.get().startFloatingService(this, title)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        innerPanel.addPanelSlideListener(innerPanelSlideListener)
        search.setOnClickListener { navigator.toSearchFragment() }
    }

    override fun onPause() {
        super.onPause()
        innerPanel.removePanelSlideListener(innerPanelSlideListener)
        search.setOnClickListener(null)
    }

    override fun onDestroy() {
        MediaControllerCompat.setMediaController(this, null)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode){
            FloatingInfoServiceHelper.REQUEST_CODE_HOVER_PERMISSION -> {
                presenter.get().startFloatingService(this, null)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        val playingQueue = findFragmentByTag<PlayingQueueFragment>(PlayingQueueFragment.TAG)
        val miniQueue = findFragmentByTag<MiniQueueFragment>(getString(R.string.player_queue_fragment_tag))
        when {
            playingQueue != null -> super.onBackPressed()
            miniQueue?.cannotScrollUp() ?: false -> {
                miniQueue?.smoothScrollToTop()
            }
            innerPanel.isExpanded() -> innerPanel.collapse()
            slidingPanel.isExpanded() -> slidingPanel.collapse()
            else -> super.onBackPressed()
        }
    }

    override fun getSupportMediaController(): MediaControllerCompat? {
        return MediaControllerCompat.getMediaController(this)
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel
}