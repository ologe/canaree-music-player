package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Podcast
import io.reactivex.Observable

interface ChildsHasPodcasts<in Param> {

    fun observeSongListByParam(param: Param): Observable<List<Podcast>>

}
