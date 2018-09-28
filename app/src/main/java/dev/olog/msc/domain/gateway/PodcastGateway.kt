package dev.olog.msc.domain.gateway

import android.net.Uri
import dev.olog.msc.domain.entity.Podcast
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PodcastGateway : BaseGateway<Podcast, Long> {

    fun getAllUnfiltered(): Observable<List<Podcast>>

    fun deleteSingle(podcastId: Long): Completable

    fun deleteGroup(podcastList: List<Podcast>): Completable

    fun getUneditedByParam(podcastId: Long): Observable<Podcast>

    fun getByUri(uri: Uri): Single<Podcast>

}