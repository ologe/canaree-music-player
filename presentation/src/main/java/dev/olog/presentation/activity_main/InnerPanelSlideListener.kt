package dev.olog.presentation.activity_main

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.R
import dev.olog.presentation.utils.delegates.weakRef
import javax.inject.Inject

class InnerPanelSlideListener @Inject constructor(
        activity: AppCompatActivity
) : SlidingUpPanelLayout.PanelSlideListener {

    private val playerLayout by weakRef(activity.findViewById<View>(R.id.playerLayout))
    private val playingQueueLayout by weakRef(activity.findViewById<View>(R.id.playingQueueLayout))
    private val slidingPanel by weakRef(activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel))

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        // translate player layout on inner sliding panel translation
        playerLayout?.let {
            it.translationY = - Math.abs((playingQueueLayout?.top ?: 0) - it.bottom).toFloat()
        }
    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
        // disable outer panel touch if inner is expanded
        slidingPanel?.isTouchEnabled = (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
    }
}