package dev.olog.msc.data.repository

import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.SongImageEntity
import dev.olog.msc.domain.gateway.SongImageGateway
import io.reactivex.Completable
import javax.inject.Inject

class SongImagesRepository @Inject constructor(
        appDatabase: AppDatabase

) : SongImageGateway {

    private val dao = appDatabase.songImagesDao()

    override fun getAll(): List<SongImageEntity> {
        return dao.getAll()
    }

    override fun insert(entity: SongImageEntity): Completable {
        return Completable.fromCallable { dao.insert(entity) }
    }

    override fun delete(id: Long): Completable {
        return Completable.fromCallable { dao.delete(id) }
    }
}