package dev.olog.msc.domain.interactor.all

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.podcast.toSong
import dev.olog.core.entity.track.Song
import dev.olog.core.executor.ComputationScheduler
import dev.olog.core.gateway.*
import dev.olog.msc.domain.gateway.*
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.shared.mapToList
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val genreDataStore: GenreGateway2,
        private val playlistDataStore: PlaylistGateway2,
        private val albumDataStore: AlbumGateway2,
        private val artistDataStore: ArtistGateway2,
        private val folderDataStore: FolderGateway2,
        private val songDataStore: SongGateway2,
        private val podcastDataStore: PodcastGateway2,
        private val podcastPlaylistDataStore: PodcastPlaylistGateway2,
        private val podcastAlbumDataStore: PodcastAlbumGateway2,
        private val podcastArtistDataStore: PodcastArtistGateway2

) : ObservableUseCaseWithParam<List<Song>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {
        if (mediaId.isAll) {
            return songDataStore.observeAll().asObservable()
        }

        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderDataStore.observeTrackListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistDataStore.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.SONGS -> songDataStore.observeAll()
            MediaIdCategory.ALBUMS -> albumDataStore.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> artistDataStore.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> genreDataStore.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS -> podcastDataStore.observeAll()
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistDataStore.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumDataStore.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistDataStore.observeTrackListByParam(mediaId.categoryValue.toLong())
            else -> throw AssertionError("invalid media id $mediaId")
        }.asObservable()
    }


}
