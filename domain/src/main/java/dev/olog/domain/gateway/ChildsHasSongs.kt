package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Flowable

interface ChildsHasSongs<in Param> {

    fun observeSongListByParam(param: Param): Flowable<List<Song>>

}
