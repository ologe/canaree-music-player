package dev.olog.msc.domain.interactor.all.related.artists

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Artist
import dev.olog.core.executor.ComputationScheduler
import dev.olog.core.gateway.FolderGateway
import dev.olog.core.gateway.GenreGateway
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetRelatedArtistsUseCase @Inject constructor(
    executors: ComputationScheduler,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway2,
    private val genreGateway: GenreGateway

) : ObservableUseCaseWithParam<List<Artist>, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Artist>> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeRelatedArtists(mediaId.categoryValue).asObservable()
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeRelatedArtists(mediaId.categoryId).asObservable()
            MediaIdCategory.GENRES -> genreGateway.observeRelatedArtists(mediaId.categoryId).asObservable()
            else -> Observable.just(emptyList())
        }
    }
}