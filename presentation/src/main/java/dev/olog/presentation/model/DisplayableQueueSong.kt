package dev.olog.presentation.model

import dev.olog.core.MediaId

data class DisplayableQueueSong(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val idInPlaylist: Int,
    val isCurrentSong: Boolean

) : BaseModel {

    fun positionInList(currentPosition: Int): String {
        return when {
            currentPosition == -1 -> "-"
            idInPlaylist > currentPosition -> "+${idInPlaylist - currentPosition}"
            idInPlaylist < currentPosition -> "${idInPlaylist - currentPosition}"
            else -> "-"
        }
    }

}