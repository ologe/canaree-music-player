package dev.olog.presentation.fragment_related_artist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.rxkotlin.toFlowable

class RelatedArtistViewModel(
        mediaId: String,
        getSongListByParamUseCase: GetSongListByParamUseCase

): ViewModel() {

    val data: LiveData<List<DisplayableItem>> = getSongListByParamUseCase
            .execute(mediaId)
            .flatMapSingle { it.toFlowable()
                    .map { DisplayableItem(R.layout.item_related_artist,
                            MediaIdHelper.artistId(it.artistId),
                            it.artist)
                    }.toList()
            }.asLiveData()

}