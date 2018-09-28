package dev.olog.msc.domain.interactor.all

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.entity.toSong
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.*
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Observable
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val genreDataStore: GenreGateway,
        private val playlistDataStore: PlaylistGateway,
        private val albumDataStore: AlbumGateway,
        private val artistDataStore: ArtistGateway,
        private val folderDataStore: FolderGateway,
        private val songDataStore: SongGateway,
        private val podcastDataStore: PodcastGateway,
        private val podcastPlaylistDataStore: PodcastPlaylistGateway,
        private val podcastAlbumDataStore: PodcastArtistGateway,
        private val podcastArtistDataStore: PodcastAlbumGateway

) : ObservableUseCaseUseCaseWithParam<List<Song>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {
        if (mediaId.isAll){
            return songDataStore.getAll()
        }

        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderDataStore.observeSongListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.SONGS -> songDataStore.getAll()
            MediaIdCategory.ALBUMS -> albumDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> artistDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> genreDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS -> podcastDataStore.getAll().mapToList { it.toSong() }
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistDataStore.observeSongListByParam(mediaId.resolveId)
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumDataStore.observeSongListByParam(mediaId.resolveId)
                    .mapToList { it.toSong() }
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistDataStore.observeSongListByParam(mediaId.resolveId)
                    .mapToList { it.toSong() }
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }



}
