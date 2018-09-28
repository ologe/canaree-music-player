package dev.olog.msc.data.mapper

import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.entity.PodcastArtist

fun Podcast.toAlbum(songCount: Int) : PodcastAlbum {
    return PodcastAlbum(
            this.albumId,
            this.artistId,
            this.album,
            this.artist,
            this.albumArtist,
            this.image,
            songCount
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