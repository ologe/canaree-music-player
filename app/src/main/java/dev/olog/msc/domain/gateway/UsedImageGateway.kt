package dev.olog.msc.domain.gateway

import dev.olog.core.entity.UsedAlbumImage
import dev.olog.core.entity.UsedArtistImage
import dev.olog.core.entity.UsedTrackImage

interface UsedImageGateway {

    fun getAllForTracks(): List<UsedTrackImage>
    fun getAllForAlbums(): List<UsedAlbumImage>
    fun getAllForArtists(): List<UsedArtistImage>

    fun getForTrack(id: Long): String?
    fun getForAlbum(id: Long): String?
    fun getForArtist(id: Long): String?

    fun setForTrack(id: Long, image: String?)
    fun setForAlbum(id: Long, image: String?)
    fun setForArtist(id: Long, image: String?)

}