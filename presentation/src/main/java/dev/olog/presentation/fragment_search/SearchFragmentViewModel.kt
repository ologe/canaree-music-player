package dev.olog.presentation.fragment_search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.text.TextUtils
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.notContainsAll
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

class SearchFragmentViewModel(
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val getAllArtistsUseCase: GetAllArtistsUseCase,
    private val searchHeaders: SearchHeaders

) : ViewModel() {

    private val queryText = MutableLiveData<String>()

    fun setNewQuery(newQuery: String){
        queryText.value = newQuery
    }

    val data : LiveData<MutableMap<SearchType, MutableList<DisplayableItem>>> = Transformations.switchMap(queryText, { input -> Flowables.zip(
            setupArtistsUseCase(input), setupAlbumsUseCase(input), setupSongsUseCase(input),
            { artists, albums, songs -> mutableMapOf(
                SearchType.ARTISTS to artists,
                SearchType.ALBUMS to albums,
                SearchType.SONGS to songs)
            }).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .asLiveData()
    })

    fun adjustDataMap(data: MutableMap<SearchType, MutableList<DisplayableItem>>) {
        val albums = data[SearchType.ALBUMS]!!
        if (albums.isNotEmpty() && albums.notContainsAll(searchHeaders.albums)){
            albums.clear()
            albums.addAll(0, searchHeaders.albums)
        }

        val artists = data[SearchType.ARTISTS]!!
        if (artists.isNotEmpty() && artists.notContainsAll(searchHeaders.artists)){
            artists.clear()
            artists.addAll(0, searchHeaders.artists)
        }

        val songs = data[SearchType.SONGS]!!
        if (songs.isNotEmpty() && songs.notContainsAll(searchHeaders.songs)){
            songs.addAll(0, searchHeaders.songs)
        }
    }

    private fun setupSongsUseCase(query: String): Flowable<MutableList<DisplayableItem>> {
        return getAllSongsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true) ||
                                it.album.contains(query, true)
                        }.map { it.toDisplayableItem() }
                        .toList()
                }
    }

    private fun setupAlbumsUseCase(query: String): Flowable<MutableList<DisplayableItem>> {
        return getAllAlbumsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true)
                        }.map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private fun setupArtistsUseCase(query: String): Flowable<MutableList<DisplayableItem>> {
        return getAllArtistsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.name.contains(query, true) }
                        .map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

}