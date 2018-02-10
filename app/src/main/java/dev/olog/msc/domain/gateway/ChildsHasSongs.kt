package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Song
import io.reactivex.Flowable

interface ChildsHasSongs<in Param> {

    fun observeSongListByParam(param: Param): Flowable<List<Song>>

}
