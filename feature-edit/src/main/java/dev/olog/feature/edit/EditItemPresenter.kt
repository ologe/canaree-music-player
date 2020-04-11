package dev.olog.feature.edit

import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.feature.edit.domain.UpdateMultipleTracksUseCase
import dev.olog.feature.edit.domain.UpdateTrackUseCase
import dev.olog.feature.edit.model.UpdateAlbumInfo
import dev.olog.feature.edit.model.UpdateArtistInfo
import dev.olog.feature.edit.model.UpdateSongInfo
import dev.olog.feature.presentation.base.model.PresentationId
import java.io.File
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
        return updateTrackUseCase(
            UpdateTrackUseCase.Data(
                info.trackId,
                File(info.path),
                info.tags,
                info.isPodcast
            )
        )
    }

    fun updateAlbum(info: UpdateAlbumInfo) {
        return updateMultipleTracksUseCase(
            UpdateMultipleTracksUseCase.Data(
                info.mediaId,
                info.tags,
                false
            )
        )
    }


    fun updateArtist(info: UpdateArtistInfo) {
        return updateMultipleTracksUseCase(
            UpdateMultipleTracksUseCase.Data(
                info.mediaId,
                info.tags,
                info.isPodcast
            )
        )
    }

}