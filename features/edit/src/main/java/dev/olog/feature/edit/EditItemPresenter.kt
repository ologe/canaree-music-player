package dev.olog.feature.edit

import dev.olog.core.mediaid.MediaId
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.feature.edit.domain.UpdateMultipleTracksUseCase
import dev.olog.feature.edit.domain.UpdateTrackUseCase
import org.jaudiotagger.tag.FieldKey
import javax.inject.Inject

internal class EditItemPresenter @Inject constructor(
    private val lastFmGateway: ImageRetrieverGateway,
    private val updateTrackUseCase: UpdateTrackUseCase,
    private val updateMultipleTracksUseCase: UpdateMultipleTracksUseCase
) {

    suspend fun deleteTrack(id: Long) {
        return lastFmGateway.deleteTrack(id)
    }

    suspend fun deleteAlbum(mediaId: MediaId) {
        return lastFmGateway.deleteAlbum(mediaId.categoryId)
    }

    suspend fun deleteArtist(mediaId: MediaId) {
        return lastFmGateway.deleteArtist(mediaId.categoryId)
    }

    fun updateSingle(info: UpdateSongInfo) {
        val albumArtist = if (info.albumArtist.isBlank()) info.artist else info.albumArtist

        return updateTrackUseCase(
            UpdateTrackUseCase.Data(
                info.originalSong.getMediaId(),
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

    suspend fun updateAlbum(info: UpdateAlbumInfo) {
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
                info.isPodcast
            )
        )
    }


    suspend fun updateArtist(info: UpdateArtistInfo) {
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