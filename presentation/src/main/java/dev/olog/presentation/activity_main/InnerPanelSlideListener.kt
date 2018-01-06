package dev.olog.presentation.activity_main

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.R
import dev.olog.presentation.fragment_player.PlayerFragment
import dev.olog.presentation.utils.delegates.weakRef
import dev.olog.shared_android.extension.isPortrait
import javax.inject.Inject

class InnerPanelSlideListener @Inject constructor(
        private val activity: AppCompatActivity
) : SlidingUpPanelLayout.PanelSlideListener {

    private val playerFragmentTag = activity.getString(R.string.player_fragment_tag)

    private val playerLayout by weakRef(activity.findViewById<View>(R.id.playerLayout))
    private val playingQueueLayout by lazy { activity.findViewById<View>(R.id.playingQueueLayout) }
    private val slidingPanel by weakRef(activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel))
    private val isPortrait = activity.isPortrait

    private var fragmentSlidingViewLandscape : View? = null

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        // translate player layout on inner sliding panel translation

        val viewToSlide = if (isPortrait){
            playerLayout
        } else {
            if (fragmentSlidingViewLandscape == null){
                val playerFragment = activity.supportFragmentManager.findFragmentByTag(playerFragmentTag) as PlayerFragment
                fragmentSlidingViewLandscape = playerFragment.view?.findViewById(R.id.fragment_wrapper)
            }
            fragmentSlidingViewLandscape
        }

        viewToSlide?.let {
            it.translationY = - Math.abs((playingQueueLayout?.top ?: 0) - it.bottom).toFloat()
        }
    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
        // disable outer panel touch if inner is expanded
        slidingPanel?.isTouchEnabled = (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
    }
}