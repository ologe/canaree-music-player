package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.gateway.PlayingQueueGateway
import javax.inject.Inject

class UpdatePlayingQueueUseCase @Inject constructor(
    private val gateway: PlayingQueueGateway
) {

    operator fun invoke(param: List<UpdatePlayingQueueUseCaseRequest>) {
        gateway.update(param)
    }

}

class UpdatePlayingQueueUseCaseRequest(
    @JvmField
    val mediaId: MediaId,
    @JvmField
    val songId: Long,
    @JvmField
    val idInPlaylist: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdatePlayingQueueUseCaseRequest

        if (mediaId != other.mediaId) return false
        if (songId != other.songId) return false
        if (idInPlaylist != other.idInPlaylist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + songId.hashCode()
        result = 31 * result + idInPlaylist
        return result
    }
}