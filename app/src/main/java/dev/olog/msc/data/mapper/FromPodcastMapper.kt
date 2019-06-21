package dev.olog.msc.data.mapper

import dev.olog.core.entity.Podcast
import dev.olog.core.entity.PodcastAlbum
import dev.olog.core.entity.PodcastArtist

fun Podcast.toAlbum(songCount: Int) : PodcastAlbum {
    return PodcastAlbum(
        this.albumId,
        this.artistId,
        this.album,
        this.artist,
        this.albumArtist,
        this.image,
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
        albumsCount,
        ""
    )
}

fun Podcast.toFakeArtist(songCount: Int, albumsCount: Int) : PodcastArtist {
    return PodcastArtist(
        this.artistId,
        this.artist,
        this.albumArtist,
        songCount,
        albumsCount,
        getFakeImage(this.artistId)
    )
}