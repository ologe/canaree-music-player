package dev.olog.presentation.edit.domain

import dev.olog.core.MediaId
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.presentation.edit.model.SaveImageType
import org.jaudiotagger.tag.FieldKey
import javax.inject.Inject

class UpdateMultipleTracksUseCase @Inject constructor(
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val updateTrackUseCase: UpdateTrackUseCase,
    private val gateway: UsedImageGateway,
    private val imageVersionGateway: ImageVersionGateway

) {

    operator fun invoke(param: Data) {
        val songList = getSongListByParamUseCase(param.mediaId)
        for (song in songList) {
            updateTrackUseCase(
                UpdateTrackUseCase.Data(
                    mediaId = null, // set to null because do not want to update track image
                    path = song.path,
                    image = SaveImageType.NotSet,
                    fields = param.fields,
                    isPodcast = null
                )
            )

            TODO()
//            if (param.mediaId.isArtist) {
//                gateway.setForArtist(param.mediaId.resolveId, param.image)
//            } else if (param.mediaId.isAlbum) {
//                gateway.setForAlbum(param.mediaId.resolveId, param.image)
//            } else {
//                throw IllegalStateException("invalid media id category ${param.mediaId}")
//            }
//            imageVersionGateway.increaseCurrentVersion(param.mediaId)
        }

    }

    data class Data(
        val mediaId: MediaId,
        val image: SaveImageType,
        val fields: Map<FieldKey, String>
    )

}