package dev.olog.msc.domain.gateway

import dev.olog.msc.data.entity.SongImageEntity
import io.reactivex.Completable

interface SongImageGateway {

    fun getAll(): List<SongImageEntity>

    fun insert(entity: SongImageEntity): Completable

    fun delete(id: Long): Completable

}