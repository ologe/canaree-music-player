package dev.olog.presentation.activity_main

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import dev.olog.presentation.music_service.MusicServiceBinder
import dev.olog.presentation.utils.subscribe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_tab_view_pager.*
import javax.inject.Inject

class MainActivity: BaseActivity() {

    @Inject lateinit var adapter: TabViewPagerAdapter

    @Inject lateinit var musicServiceBinder: MusicServiceBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        tabLayout.setupWithViewPager(viewPager)

        musicServiceBinder.getMediaControllerLiveData()
                .subscribe(this, { MediaControllerCompat.setMediaController(this, it) })
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

    private val innerPanelSlideListener = object : SlidingUpPanelLayout.PanelSlideListener {

        override fun onPanelSlide(panel: View?, slideOffset: Float) {
            // translate player layout on inner sliding panel translation
            playerLayout.translationY = - Math.abs(playingQueueLayout.top - playerLayout.bottom).toFloat()
        }

        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            // disable outer panel touch if inner is expanded
            slidingPanel.isTouchEnabled = (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
            if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                innerPanel.setDragView(R.id.playingQueueLayout)
            } else {
                innerPanel.setDragView(R.id.drag_area)
            }
        }
    }

}