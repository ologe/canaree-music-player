package dev.olog.msc.data.repository

import dev.olog.msc.api.last.fm.model.SearchedImage
import dev.olog.msc.api.last.fm.model.SearchedTrack
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.LastFmArtistImageEntity
import dev.olog.msc.data.entity.LastFmTrackEntity
import dev.olog.msc.data.entity.LastFmTrackImageEntity
import dev.olog.msc.domain.gateway.LastFmCacheGateway
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class LastFmRepository @Inject constructor(
        appDatabase: AppDatabase

) : LastFmCacheGateway {

    private val trackDao = appDatabase.lastFmDao()

    override fun getTrack(songId: Long): Single<SearchedTrack> {
        return trackDao.getInfoById(songId).map { it.toDomain() }
    }

    override fun getTrackImage(songId: Long): Single<SearchedImage> {
        return trackDao.getTrackImageById(songId).map { it.toDomain() }
    }

    override fun getArtistImage(artistId: Long): Single<SearchedImage> {
        return trackDao.getArtistImageById(artistId).map { it.toDomain() }
    }

    override fun insertTrack(info: SearchedTrack): Completable {
        return Completable.fromCallable {
            val model = info.toModel()
            trackDao.insertInfo(model)
        }
    }

    override fun insertTrackImage(image: SearchedImage): Completable {
        return Completable.fromCallable {
            val model = image.toTrackImage()
            trackDao.insertTrackImage(model)
        }
    }

    override fun insertArtistImage(image: SearchedImage): Completable {
        return Completable.fromCallable {
            val model = image.toArtistImage()
            trackDao.insertArtistImage(model)
        }
    }

    private fun SearchedTrack.toModel(): LastFmTrackEntity {
        return LastFmTrackEntity(id, title, artist, album)
    }

    private fun SearchedImage.toTrackImage(): LastFmTrackImageEntity {
        return LastFmTrackImageEntity(id, image, false)
    }

    private fun SearchedImage.toArtistImage(): LastFmArtistImageEntity {
        return LastFmArtistImageEntity(id, image)
    }

    private fun LastFmTrackEntity.toDomain(): SearchedTrack {
        return SearchedTrack(id, title, artist, album)
    }

    private fun LastFmTrackImageEntity.toDomain(): SearchedImage {
        return SearchedImage(id, image)
    }

    private fun LastFmArtistImageEntity.toDomain(): SearchedImage {
        return SearchedImage(id, image)
    }

}
