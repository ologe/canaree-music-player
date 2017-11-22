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
        innerPanel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                println(first.bottom)
                println(second.top)
                first.translationY = - Math.abs(second.top - first.bottom).toFloat()
            }

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                slidingPanel.isTouchEnabled = (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    innerPanel.setDragView(R.id.second)
                } else {
                    innerPanel.setDragView(R.id.drag_area)
                }
            }
        })
    }

}