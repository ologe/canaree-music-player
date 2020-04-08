package dev.olog.presentation.edit

import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.edit.domain.UpdateMultipleTracksUseCase
import dev.olog.presentation.edit.domain.UpdateTrackUseCase
import dev.olog.feature.presentation.base.model.presentationId
import org.jaudiotagger.tag.FieldKey
import javax.inject.Inject

class EditItemPresenter @Inject constructor(
    private val lastFmGateway: ImageRetrieverGateway,
    private val updateTrackUseCase: UpdateTrackUseCase,
    private val updateMultipleTracksUseCase: UpdateMultipleTracksUseCase

) {

    suspend fun deleteTrack(id: Long) {
        return lastFmGateway.deleteTrack(id)
    }

    suspend fun deleteAlbum(mediaId: PresentationId.Category) {
        return lastFmGateway.deleteAlbum(mediaId.categoryId.toLong())
    }

    suspend fun deleteArtist(mediaId: PresentationId.Category) {
        return lastFmGateway.deleteArtist(mediaId.categoryId.toLong())
    }

    fun updateSingle(info: UpdateSongInfo) {
        val albumArtist = if (info.albumArtist.isBlank()) info.artist else info.albumArtist

        return updateTrackUseCase(
            UpdateTrackUseCase.Data(
                info.originalSong.presentationId,
                info.originalSong.path,
                mapOf(
                    FieldKey.TITLE to info.title,
                    FieldKey.ARTIST to info.artist,
                    FieldKey.ALBUM_ARTIST to albumArtist,
                    FieldKey.ALBUM to info.album,
                    FieldKey.GENRE to info.genre,
                    FieldKey.YEAR to info.year,
                    FieldKey.DISC_NO to info.disc,
                    FieldKey.TRACK to info.track
                ),
                info.isPodcast
            )
        )
    }

    fun updateAlbum(info: UpdateAlbumInfo) {
        val albumArtist = if (info.albumArtist.isBlank()) info.artist else info.albumArtist
        return updateMultipleTracksUseCase(
            UpdateMultipleTracksUseCase.Data(
                info.mediaId,
                mapOf(
                    FieldKey.ALBUM to info.title,
                    FieldKey.ARTIST to info.artist,
                    FieldKey.ALBUM_ARTIST to albumArtist,
                    FieldKey.GENRE to info.genre,
                    FieldKey.YEAR to info.year
                ),
                false
            )
        )
    }


    fun updateArtist(info: UpdateArtistInfo) {
        val albumArtist = if (info.albumArtist.isBlank()) info.name else info.albumArtist
        return updateMultipleTracksUseCase(
            UpdateMultipleTracksUseCase.Data(
                info.mediaId,
                mapOf(
                    FieldKey.ARTIST to info.name,
                    FieldKey.ALBUM_ARTIST to albumArtist
                ),
                info.isPodcast
            )
        )
    }

}