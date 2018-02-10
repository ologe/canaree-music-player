package dev.olog.msc.presentation.detail

import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class DetailFragmentSlidingPanelListener(
        private val fragment: DetailFragment

) : SlidingUpPanelLayout.PanelSlideListener {

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED){
            fragment.setDarkButtons()
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
            fragment.setLightButtons()
        }
    }

}