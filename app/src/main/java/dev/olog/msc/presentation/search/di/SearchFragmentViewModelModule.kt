package dev.olog.msc.presentation.search.di

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import dagger.Module
import dagger.Provides
import dev.olog.core.MediaId
import dev.olog.core.RecentSearchesTypes
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.SearchResult
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.*
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.search.GetAllRecentSearchesUseCase
import dev.olog.msc.presentation.search.SearchFragmentHeaders
import dev.olog.msc.presentation.search.SearchFragmentType
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.mapToList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.asObservable

@Module
class SearchFragmentViewModelModule {

    @Provides
    @PerFragment
    internal fun provideQueryLiveData(): MutableLiveData<String> = MutableLiveData()

    @Provides
    internal fun provideSearchData(
        @ApplicationContext context: Context,
            // tracks
        getAllArtistsUseCase: ArtistGateway2,
        getAllAlbumsUseCase: AlbumGateway2,
        getAllPlaylistsUseCase: PlaylistGateway2,
        getAllGenresUseCase: GenreGateway2,
        getAllFoldersUseCase: FolderGateway2,
        getAllSongsUseCase: SongGateway2,
            // podcasts
        getAllPodcastUseCase: PodcastGateway2,
        getAllPodcastAlbumsUseCase: PodcastAlbumGateway2,
        getAllPodcastArtistUseCase: PodcastArtistGateway2,
        getAllPodcastPlaylistUseCase: PodcastPlaylistGateway2,
            //recent
        getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
        searchHeaders: SearchFragmentHeaders,
        queryLiveData: MutableLiveData<String>)
            : LiveData<Pair<MutableMap<SearchFragmentType, MutableList<DisplayableItem>>, String>> {

        return Transformations.switchMap(queryLiveData) { input ->

            if (input.isBlank()) {
                provideRecents(context, getAllRecentSearchesUseCase, searchHeaders)
                        .map {
                            mutableMapOf(
                                    SearchFragmentType.RECENT to it.toMutableList(),
                                    SearchFragmentType.ARTISTS to mutableListOf(),
                                    SearchFragmentType.ALBUMS to mutableListOf(),
                                    SearchFragmentType.PLAYLISTS to mutableListOf(),
                                    SearchFragmentType.FOLDERS to mutableListOf(),
                                    SearchFragmentType.GENRES to mutableListOf(),
                                    SearchFragmentType.SONGS to mutableListOf()
                            )
                        }
                        .map { Pair(it, input) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .asLiveData()
            } else {
                getAllSongsUseCase.observeAll().asObservable()
                        .flatMap {
                            Observables.combineLatest(
                                    provideSearchByArtist(getAllArtistsUseCase, getAllPodcastArtistUseCase, input),
                                    provideSearchByAlbum(getAllAlbumsUseCase, getAllPodcastAlbumsUseCase, input),
                                    provideSearchByPlaylist(getAllPlaylistsUseCase, getAllPodcastPlaylistUseCase, input),
                                    provideSearchByGenre(getAllGenresUseCase, input),
                                    provideSearchByFolder(getAllFoldersUseCase, input),
                                    provideSearchBySong(getAllSongsUseCase, getAllPodcastUseCase, input)
                            ) { artists, albums, playlists, genres, folders, songs ->
                                mutableMapOf(
                                        SearchFragmentType.RECENT to mutableListOf(),
                                        SearchFragmentType.ARTISTS to artists.toMutableList(),
                                        SearchFragmentType.ALBUMS to albums.toMutableList(),
                                        SearchFragmentType.PLAYLISTS to playlists.toMutableList(),
                                        SearchFragmentType.GENRES to genres.toMutableList(),
                                        SearchFragmentType.FOLDERS to folders.toMutableList(),
                                        SearchFragmentType.SONGS to songs.toMutableList()
                                )
                            }
                        }
                        .map { it to input }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .asLiveData()
            }
        }
    }

    private fun provideSearchBySong(
            getAllSongsUseCase: SongGateway2,
            getAllPodcastUseCase: PodcastGateway2,
            query: String): Observable<MutableList<DisplayableItem>> {

        return Observables.combineLatest(
                getAllSongsUseCase.observeAll().asObservable()
                        .flatMapSingle { songs -> songs.toFlowable()
                                .filter { it.title.contains(query, true)  ||
                                        it.artist.contains(query, true) ||
                                        it.album.contains(query, true)
                                }.map { it.toSearchDisplayableItem() }
                                .toList()
                        },
                getAllPodcastUseCase.observeAll().asObservable()
                        .flatMapSingle { songs -> songs.toFlowable()
                                .filter { it.title.contains(query, true)  ||
                                        it.artist.contains(query, true) ||
                                        it.album.contains(query, true)
                                }.map { it.toSearchDisplayableItem() }
                                .toList()
                        }
        ) { tracks, podcasts ->
            tracks.asSequence().plus(podcasts).sortedBy { it.title }.toMutableList()
        }
    }

    private fun provideSearchByAlbum(
            getAllAlbumsUseCase: AlbumGateway2,
            getAllPodcastAlbumsUseCase: PodcastAlbumGateway2,
            query: String): Observable<MutableList<DisplayableItem>> {

        return Observables.combineLatest(
                getAllAlbumsUseCase.observeAll().asObservable()
                        .flatMapSingle { albums -> albums.toFlowable()
                                .filter { it.title.contains(query, true)  ||
                                        it.artist.contains(query, true)
                                }.map { it.toSearchDisplayableItem() }
                                .toList()
                        },
                getAllPodcastAlbumsUseCase.observeAll().asObservable()
                        .flatMapSingle { albums -> albums.toFlowable()
                                .filter { it.title.contains(query, true)  ||
                                        it.artist.contains(query, true)
                                }.map { it.toSearchDisplayableItem() }
                                .toList()
                        }, { albums, podcasts ->
            albums.asSequence().plus(podcasts).sortedBy { it.title }.toMutableList()
        }
        )
    }

    private fun provideSearchByArtist(
            getAllArtistsUseCase: ArtistGateway2,
            getAllPodcastArtistUseCase: PodcastArtistGateway2,
            query: String): Observable<MutableList<DisplayableItem>> {

        return Observables.combineLatest(
                getAllArtistsUseCase.observeAll().asObservable()
                        .flatMapSingle { artists -> artists.toFlowable()
                                .filter { it.name.contains(query, true) }
                                .map { it.toSearchDisplayableItem() }
                                .toList()
                        },
                getAllPodcastArtistUseCase.observeAll().asObservable()
                        .flatMapSingle { artists -> artists.toFlowable()
                                .filter { it.name.contains(query, true) }
                                .map { it.toSearchDisplayableItem() }
                                .toList()
                        }
        ) { artists, podcasts ->
            artists.asSequence().plus(podcasts).sortedBy { it.title }.toMutableList()
        }
    }

    private fun provideSearchByPlaylist(
            playlistGateway2: PlaylistGateway2,
            getAllPodcastPlaylistUseCase: PodcastPlaylistGateway2,
            query: String): Observable<MutableList<DisplayableItem>> {

        return Observables.combineLatest(
                playlistGateway2.observeAll().asObservable()
                        .flatMapSingle { artists -> artists.toFlowable()
                                .filter { it.title.contains(query, true) }
                                .map { it.toSearchDisplayableItem() }
                                .toList()
                        },
                getAllPodcastPlaylistUseCase.observeAll().asObservable()
                        .flatMapSingle { artists -> artists.toFlowable()
                                .filter { it.title.contains(query, true) }
                                .map { it.toSearchDisplayableItem() }
                                .toList()
                        }
        ) { playlists, podcasts ->
            playlists.asSequence().plus(podcasts).sortedBy { it.title }.toMutableList()
        }
    }

    private fun provideSearchByGenre(
            getAllGenresUseCase: GenreGateway2,
            query: String): Observable<MutableList<DisplayableItem>> {

        return getAllGenresUseCase.observeAll().asObservable()
                .flatMapSingle { artists -> artists.toFlowable()
                        .filter { it.name.contains(query, true) }
                        .map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private fun provideSearchByFolder(
            folderGateway: FolderGateway2,
            query: String): Observable<MutableList<DisplayableItem>> {

        return folderGateway.observeAll().asObservable()
                .flatMapSingle { artists -> artists.toFlowable()
                        .filter { it.title.contains(query, true) }
                        .map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private fun provideRecents(
            context: Context,
            getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
            searchHeaders: SearchFragmentHeaders): Observable<MutableList<DisplayableItem>> {

        return getAllRecentSearchesUseCase.execute()
                .mapToList { it.toSearchDisplayableItem(context) }
                .map { it.toMutableList() }
                .map {
                    if (it.isNotEmpty()){
                        it.add(
                            DisplayableItem(
                                R.layout.item_search_clear_recent,
                                MediaId.headerId("clear recent"),
                                ""
                            )
                        )
                        it.addAll(0, searchHeaders.recents)
                    }
                    it
                }
    }

}

private fun Song.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_song,
        getMediaId(),
        title,
        DisplayableItem.adjustArtist(artist),
        true
    )
}

private fun Album.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_album,
        getMediaId(),
        title,
        DisplayableItem.adjustArtist(artist)
    )
}

private fun Artist.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_artist,
        getMediaId(),
        name,
        null
    )
}

private fun Playlist.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_album,
        getMediaId(),
        title,
        null
    )
}

private fun Genre.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_album,
        getMediaId(),
        name,
        null
    )
}

private fun Folder.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_album,
        getMediaId(),
        title,
        null
    )
}

private fun SearchResult.toSearchDisplayableItem(context: Context) : DisplayableItem {
    val subtitle = when (this.itemType) {
        RecentSearchesTypes.SONG -> context.getString(R.string.search_type_track)
        RecentSearchesTypes.ALBUM -> context.getString(R.string.search_type_album)
        RecentSearchesTypes.ARTIST -> context.getString(R.string.search_type_artist)
        RecentSearchesTypes.PLAYLIST -> context.getString(R.string.search_type_playlist)
        RecentSearchesTypes.GENRE -> context.getString(R.string.search_type_genre)
        RecentSearchesTypes.FOLDER -> context.getString(R.string.search_type_folder)
        RecentSearchesTypes.PODCAST -> context.getString(R.string.search_type_podcast)
        RecentSearchesTypes.PODCAST_PLAYLIST -> context.getString(R.string.search_type_podcast_playlist)
        RecentSearchesTypes.PODCAST_ALBUM -> context.getString(R.string.search_type_podcast_album)
        RecentSearchesTypes.PODCAST_ARTIST -> context.getString(R.string.search_type_podcast_artist)
        else -> throw IllegalArgumentException("invalid item type $itemType")
    }

    val isPlayable = this.itemType == RecentSearchesTypes.SONG || this.itemType == RecentSearchesTypes.PODCAST

    val layout = when (this.itemType){
        RecentSearchesTypes.ARTIST,
        RecentSearchesTypes.PODCAST_ARTIST -> R.layout.item_search_recent_artist
        RecentSearchesTypes.ALBUM,
        RecentSearchesTypes.PODCAST_ALBUM -> R.layout.item_search_recent_album
        else -> R.layout.item_search_recent
    }

    return DisplayableItem(
        layout,
        this.mediaId,
        this.title,
        subtitle,
        isPlayable
    )
}