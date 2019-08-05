package dev.olog.presentation.edit.song

class DisplayableSong(
    @JvmField
    val id: Long,
    @JvmField
    val artistId: Long,
    @JvmField
    val albumId: Long,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val album: String,
    @JvmField
    val genre: String,
    @JvmField
    val year: String,
    @JvmField
    val disc: String,
    @JvmField
    val track: String,
    @JvmField
    val path: String,
    @JvmField
    val bitrate: String,
    @JvmField
    val format: String,
    @JvmField
    val sampling: String,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableSong

        if (id != other.id) return false
        if (artistId != other.artistId) return false
        if (albumId != other.albumId) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (albumArtist != other.albumArtist) return false
        if (album != other.album) return false
        if (genre != other.genre) return false
        if (year != other.year) return false
        if (disc != other.disc) return false
        if (track != other.track) return false
        if (path != other.path) return false
        if (bitrate != other.bitrate) return false
        if (format != other.format) return false
        if (sampling != other.sampling) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + artistId.hashCode()
        result = 31 * result + albumId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + genre.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + disc.hashCode()
        result = 31 * result + track.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + bitrate.hashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + sampling.hashCode()
        result = 31 * result + isPodcast.hashCode()
        return result
    }

    fun with(
        title: String,
        artist: String,
        album: String
    ) : DisplayableSong {
        return DisplayableSong(
            id,
            artistId,
            albumId,
            title,
            artist,
            albumArtist,
            album,
            genre,
            year,
            disc,
            track,
            path,
            bitrate,
            format,
            sampling,
            isPodcast
        )
    }

}