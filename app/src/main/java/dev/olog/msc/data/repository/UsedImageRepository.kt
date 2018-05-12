package dev.olog.msc.data.repository

import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.UsedAlbumImageEntity
import dev.olog.msc.data.entity.UsedArtistImageEntity
import dev.olog.msc.data.entity.UsedTrackImageEntity
import dev.olog.msc.domain.entity.UsedAlbumImage
import dev.olog.msc.domain.entity.UsedArtistImage
import dev.olog.msc.domain.entity.UsedTrackImage
import dev.olog.msc.domain.gateway.UsedImageGateway
import javax.inject.Inject

class UsedImageRepository @Inject constructor(
        appDatabase: AppDatabase

) : UsedImageGateway {

    private val dao = appDatabase.usedImageDao()

    override fun getAllForTracks(): List<UsedTrackImage> {
        return dao.getAllImagesForTracks().map { UsedTrackImage(it.id, it.image) }
    }

    override fun getAllForAlbums(): List<UsedAlbumImage> {
        return dao.getAllImagesForAlbums().map { UsedAlbumImage(it.id, it.image) }
    }

    override fun getAllForArtists(): List<UsedArtistImage> {
        return dao.getAllImagesForArtists().map { UsedArtistImage(it.id, it.image) }
    }

    override fun getForTrack(id: Long): String? {
        return dao.getImageForTrack(id)
    }

    override fun getForAlbum(id: Long): String? {
        return dao.getImageForAlbum(id)
    }

    override fun getForArtist(id: Long): String? {
        return dao.getImageForArtist(id)
    }

    override fun setForTrack(id: Long, image: String?) {
        if (image == null){
            dao.deleteForTrack(id)
        } else {
            dao.insertForTrack(UsedTrackImageEntity(id, image))
        }
    }

    override fun setForAlbum(id: Long, image: String?) {
        if (image == null){
            dao.deleteForAlbum(id)
        } else {
            dao.insertForAlbum(UsedAlbumImageEntity(id, image))
        }
    }

    override fun setForArtist(id: Long, image: String?) {
        if (image == null){
            dao.deleteForArtist(id)
        } else {
            dao.insertForArtist(UsedArtistImageEntity(id, image))
        }
    }
}