package dev.olog.presentation.fragment_related_artist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.content.res.Resources
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.SmallPlayType
import dev.olog.domain.interactor.GetSmallPlayType
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.extension.asLiveData
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.toFlowable

class RelatedArtistViewModel(
        application: Application,
        mediaId: MediaId,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getArtistUseCase: GetArtistUseCase,
        private val getSmallPlayType: GetSmallPlayType

): AndroidViewModel(application) {

    private val resources = application.resources

    private val unknownArtist = application.getString(R.string.unknown_artist)

    val data: LiveData<List<DisplayableItem>> = getSongListByParamUseCase
            .execute(mediaId)
            .flatMapSingle { it.toFlowable()
                    .distinct { it.artist }
                    .filter { it.artist != unknownArtist }
                    .flatMapSingle { song -> Singles.zip(
                            getArtistUseCase.execute(MediaId.artistId(song.artistId)).firstOrError(),
                            getSmallPlayType.execute().firstOrError(), { data, smallPlayType ->
                                data.toRelatedArtist(resources, smallPlayType)
                            })
                    }.toSortedList(compareBy { it.title.toLowerCase() })
            }.asLiveData()

}

private fun Artist.toRelatedArtist(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.song_count, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.album_count, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
            R.layout.item_related_artist,
            MediaId.artistId(id),
            this.name,
            "$albums$songs".toLowerCase(),
            this.image,
            smallPlayType = smallPlayType
    )
}