package dev.olog.presentation.navigation

interface Navigator {

    fun toMainActivity()

    fun toDetailActivity(mediaId: String, position: Int)

    fun toRelatedArtists(mediaId: String)

    fun toDialog(mediaId: String, position: Int)

    fun toSetRingtoneDialog(mediaId: String)

    fun toCreatePlaylistDialog(mediaId: String)

    fun toAddToPlaylistDialog(mediaId: String)

    fun toAddToFavoriteDialog(mediaId: String)

    fun toAddToQueueDialog(mediaId: String)

    fun toRenameDialog(mediaId: String)

    fun toDeleteDialog(mediaId: String)

}