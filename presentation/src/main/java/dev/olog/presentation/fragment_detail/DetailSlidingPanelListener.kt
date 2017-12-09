package dev.olog.presentation.fragment_detail

import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.utils.delegates.weakRef

class DetailSlidingPanelListener(
        fragment: DetailFragment

) : SlidingUpPanelLayout.PanelSlideListener {

    private val view by weakRef(fragment)

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED){
            view.setDarkButtons()
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
            view.setLightButtons()
        }
    }

}