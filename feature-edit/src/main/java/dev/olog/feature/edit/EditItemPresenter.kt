package dev.olog.feature.edit

import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.feature.edit.domain.UpdateMultipleTracksUseCase
import dev.olog.feature.edit.domain.UpdateTrackUseCase
import dev.olog.feature.edit.model.UpdateAlbumInfo
import dev.olog.feature.edit.model.UpdateArtistInfo
import dev.olog.feature.edit.model.UpdateSongInfo
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.lib.audio.tagger.model.AudioTaggerKey
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
                    AudioTaggerKey.TITLE to info.title,
                    AudioTaggerKey.ARTIST to info.artist,
                    AudioTaggerKey.ALBUM_ARTIST to albumArtist,
                    AudioTaggerKey.ALBUM to info.album,
                    AudioTaggerKey.GENRE to info.genre,
                    AudioTaggerKey.YEAR to info.year,
                    AudioTaggerKey.DISC to info.disc,
                    AudioTaggerKey.TRACK to info.track
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
                    AudioTaggerKey.ALBUM to info.title,
                    AudioTaggerKey.ARTIST to info.artist,
                    AudioTaggerKey.ALBUM_ARTIST to albumArtist,
                    AudioTaggerKey.GENRE to info.genre,
                    AudioTaggerKey.YEAR to info.year
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
                    AudioTaggerKey.ARTIST to info.name,
                    AudioTaggerKey.ALBUM_ARTIST to albumArtist
                ),
                info.isPodcast
            )
        )
    }

}