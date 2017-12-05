package dev.olog.presentation.activity_main

import android.content.Intent
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
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_floating_info.FloatingInfoServiceBinder
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import dev.olog.presentation.service_music.MediaControllerProvider
import dev.olog.presentation.service_music.MusicServiceBinder
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.rx.RxSlidingUpPanel
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_player_drag_area.*
import kotlinx.android.synthetic.main.layout_tab_view_pager.*
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class MainActivity: BaseActivity(), MediaControllerProvider, HasSlidingPanel {

    companion object {
        val REQUEST_CODE_HOVER_PERMISSION = 1000
    }

    @Inject lateinit var adapter: TabViewPagerAdapter
    @Inject lateinit var musicServiceBinder: MusicServiceBinder
    @Inject lateinit var floatingInfoClass: FloatingInfoServiceBinder
    @Inject lateinit var navigator: Navigator
    private val innerPanelSlideListener by lazy(NONE) { InnerPanelSlideListener(this) }

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
                RxSlidingUpPanel.panelStateEvents(slidingPanel).map { it.newState == SlidingUpPanelLayout.PanelState.EXPANDED },
                RxSlidingUpPanel.panelStateEvents(innerPanel).map { it.newState == SlidingUpPanelLayout.PanelState.COLLAPSED },
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
        search.setOnClickListener { navigator.toSearchFragment() }
    }

    override fun onPause() {
        super.onPause()
        innerPanel.removePanelSlideListener(innerPanelSlideListener)
        search.setOnClickListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaControllerCompat.setMediaController(this, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_HOVER_PERMISSION){
            FloatingInfoServiceHelper.startService(this, floatingInfoClass)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
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