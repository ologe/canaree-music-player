package dev.olog.core.entity.track

data class PlaylistSong(
    val song: Song,
    val idInPlaylist: Long,
)

fun Song.toPlaylistSong(
    idInPlaylist: Long,
): PlaylistSong {
    return PlaylistSong(
        song = this,
        idInPlaylist = idInPlaylist,
    )
}