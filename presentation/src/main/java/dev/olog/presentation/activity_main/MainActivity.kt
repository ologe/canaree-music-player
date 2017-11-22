package dev.olog.presentation.activity_main

import android.os.Bundle
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_tab_view_pager.*
import javax.inject.Inject

class MainActivity: BaseActivity() {

    @Inject lateinit var adapter: TabViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        innerPanel.addPanelSlideListener(innerPanelSlideListener)
    }

    override fun onPause() {
        super.onPause()
        innerPanel.removePanelSlideListener(innerPanelSlideListener)
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