package dev.olog.presentation.activity_main

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.widget.TextView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.HasSlidingPanel
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import dev.olog.presentation.collapse
import dev.olog.presentation.fragment_queue.PlayingQueueFragment
import dev.olog.presentation.isExpanded
import dev.olog.presentation.music_service.MediaControllerProvider
import dev.olog.presentation.music_service.MusicServiceBinder
import dev.olog.presentation.utils.asLiveData
import dev.olog.presentation.utils.rx.RxSlidingUpPanel
import dev.olog.presentation.utils.subscribe
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_player_drag_area.*
import kotlinx.android.synthetic.main.layout_tab_view_pager.*
import javax.inject.Inject

class MainActivity: BaseActivity(), MediaControllerProvider, HasSlidingPanel {

    @Inject lateinit var adapter: TabViewPagerAdapter

    @Inject lateinit var musicServiceBinder: MusicServiceBinder
    @Inject lateinit var innerPanelSlideListener : InnerPanelSlideListener

    lateinit var title: TextView
    lateinit var artist: TextView

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
                RxSlidingUpPanel.panelStateEvents(slidingPanel).map { it.newState() == SlidingUpPanelLayout.PanelState.EXPANDED },
                RxSlidingUpPanel.panelStateEvents(innerPanel).map { it.newState() == SlidingUpPanelLayout.PanelState.COLLAPSED },
                { outerIsExpanded, innerIsCollapsed -> outerIsExpanded && innerIsCollapsed }
        ).distinctUntilChanged()
                .asLiveData()
                .subscribe(this, {
                    title.isSelected = it
                    artist.isSelected = it
                })
    }

    override fun onResume() {
        super.onResume()
        innerPanel.addPanelSlideListener(innerPanelSlideListener)
    }

    override fun onPause() {
        super.onPause()
        innerPanel.removePanelSlideListener(innerPanelSlideListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaControllerCompat.setMediaController(this, null)
    }

    override fun onBackPressed() {
        val playingQueue = findFragmentByTag<PlayingQueueFragment>(getString(R.string.player_queue_fragment_tag))
        when {
            playingQueue?.cannotScrollUp() ?: false -> {
                playingQueue?.smoothScrollToTop()
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