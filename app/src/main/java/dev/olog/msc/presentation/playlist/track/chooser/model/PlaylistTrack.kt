package dev.olog.msc.presentation.playlist.track.chooser.model

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.msc.R
import dev.olog.presentation.model.DisplayableItem
import java.io.File

data class PlaylistTrack (
        val id: Long,
        val artistId: Long,
        val albumId: Long,
        val title: String,
        val artist: String,
        val albumArtist: String,
        val album: String,
        val duration: Long,
        val dateAdded: Long,
        val path: String,
        val folder: String,
        val discNumber: Int,
        val trackNumber: Int,
        val isPodcast: Boolean) {

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

}

fun PlaylistTrack.getMediaId(): MediaId{
    val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
    val mediaId = MediaId.createCategoryValue(category, "")
    return MediaId.playableItem(mediaId, id)
}

internal fun PlaylistTrack.toDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_choose_track,
        getMediaId(),
        this.title,
        DisplayableItem.adjustArtist(this.artist),
        true
    )
}

internal fun Song.toPlaylistTrack(): PlaylistTrack {
    return PlaylistTrack(
            this.id,
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.albumArtist,
            this.album,
            this.duration,
            this.dateAdded,
            this.path,
            this.folder,
            this.discNumber,
            this.trackNumber,
            false
    )
}