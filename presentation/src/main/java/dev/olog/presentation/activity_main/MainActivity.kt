package dev.olog.presentation.activity_main

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.layout_tab_view_pager.*
import javax.inject.Inject

class MainActivity: BaseActivity(), MediaControllerProvider, HasSlidingPanel {

    @Inject lateinit var adapter: TabViewPagerAdapter

    @Inject lateinit var musicServiceBinder: MusicServiceBinder

    lateinit var title: TextView
    lateinit var artist: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = findViewById<ViewGroup>(R.id.titleWrapper).findViewById(R.id.title)
        artist = findViewById<ViewGroup>(R.id.artistWrapper).findViewById(R.id.artist)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
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
        slidingPanel
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
        val playingQueueList = playingQueue?.view as RecyclerView?
        when {
            playingQueueList?.canScrollVertically(-1) == true -> {
                playingQueueList.stopScroll()
                playingQueueList.smoothScrollToPosition(0)
            }
            innerPanel.isExpanded() -> innerPanel.collapse()
            slidingPanel.isExpanded() -> slidingPanel.collapse()
            else -> super.onBackPressed()
        }

    }

    private val innerPanelSlideListener = object : SlidingUpPanelLayout.PanelSlideListener {

        override fun onPanelSlide(panel: View?, slideOffset: Float) {
            // translate player layout on inner sliding panel translation
            playerLayout.translationY = - Math.abs(playingQueueLayout.top - playerLayout.bottom).toFloat()
        }

        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            // disable outer panel touch if inner is expanded
            slidingPanel.isTouchEnabled = (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
        }
    }

    override fun getSupportMediaController(): MediaControllerCompat? {
        return MediaControllerCompat.getMediaController(this)
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel
}