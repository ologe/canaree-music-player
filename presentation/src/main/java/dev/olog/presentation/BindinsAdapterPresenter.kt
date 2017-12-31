package dev.olog.presentation

import android.databinding.BindingAdapter
import android.view.View
import dev.olog.domain.entity.SmallPlayEnum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.service_music.MusicController

object BindinsAdapterPresenter {

    @BindingAdapter("smallPlayListenerItem", "musicController")
    @JvmStatic
    fun onSmallTypeClick(view: View, item: DisplayableItem, musicController: MusicController){
        view.setOnClickListener {
            val smallPlayType = item.smallPlayType

            when (smallPlayType?.enum){
                SmallPlayEnum.PLAY -> musicController.playFromMediaId(item.mediaId)
                SmallPlayEnum.SHUFFLE -> musicController.playShuffle(item.mediaId)
            }
        }
    }

}