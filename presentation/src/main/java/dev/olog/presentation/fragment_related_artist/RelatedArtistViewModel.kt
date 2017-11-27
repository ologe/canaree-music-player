package dev.olog.presentation.fragment_related_artist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.rxkotlin.toFlowable

class RelatedArtistViewModel(
        application: Application,
        mediaId: String,
        getSongListByParamUseCase: GetSongListByParamUseCase

): AndroidViewModel(application) {

    private val unknownArtist = application.getString(R.string.unknown_artist)

    val data: LiveData<List<DisplayableItem>> = getSongListByParamUseCase
            .execute(mediaId)
            .flatMapSingle { it.toFlowable()
                    .distinct { it.artist }
                    .filter { it.artist != unknownArtist }
                    .map { DisplayableItem(R.layout.item_related_artist,
                            MediaIdHelper.artistId(it.artistId),
                            it.artist)
                    }.toSortedList(compareBy { it.title.toLowerCase() })
            }.asLiveData()

}