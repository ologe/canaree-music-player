package dev.olog.domain.entity

fun PlayingQueueSong.toSong(): Song {
    return Song(
            this.id,
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.album,
            this.image,
            this.duration,
            this.dateAdded,
            this.isRemix,
            this.isExplicit,
            this.path,
            this.folder,
            this.trackNumber
    )
}