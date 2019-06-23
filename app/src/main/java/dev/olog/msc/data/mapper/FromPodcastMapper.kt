package dev.olog.msc.data.mapper

import dev.olog.core.entity.podcast.Podcast
import dev.olog.core.entity.podcast.PodcastAlbum
import dev.olog.core.entity.podcast.PodcastArtist

fun Podcast.toAlbum(songCount: Int) : PodcastAlbum {
    return PodcastAlbum(
        this.albumId,
        this.artistId,
        this.album,
        this.artist,
        this.albumArtist,
        songCount,
        this.hasAlbumNameAsFolder
    )
}

fun Podcast.toArtist(songCount: Int, albumsCount: Int) : PodcastArtist {
    return PodcastArtist(
        this.artistId,
        this.artist,
        this.albumArtist,
        songCount,
        albumsCount
    )
}