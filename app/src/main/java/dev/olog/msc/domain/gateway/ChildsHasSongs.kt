package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Song
import io.reactivex.Observable

interface ChildsHasSongs<in Param> {

    fun observeSongListByParam(param: Param): Observable<List<Song>>

}
