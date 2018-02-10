package dev.olog.msc.presentation

import android.databinding.BindingAdapter
import android.view.View
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.shared_android.Constants
import dev.olog.shared_android.entity.QuickActionEnum

object BindinsAdapterPresenter {

    @BindingAdapter("item", "musicController")
    @JvmStatic
    fun onSmallTypeClick(view: View, item: DisplayableItem, musicController: MusicController){
        view.setOnClickListener {

            when (Constants.quickAction.enum){
                QuickActionEnum.PLAY -> musicController.playFromMediaId(item.mediaId)
                QuickActionEnum.SHUFFLE -> musicController.playShuffle(item.mediaId)
            }
        }
    }

}