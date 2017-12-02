package dev.olog.presentation.fragment_detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Color
import android.support.v7.graphics.Palette
import dev.olog.domain.interactor.detail.most_played.InsertMostPlayedUseCase
import dev.olog.presentation.images.ImageUtils
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.ColorUtils
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

class DetailFragmentViewModel(
        application: Application,
        mediaId: String,
        itemPosition: Int,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val insertMostPlayedUseCase: InsertMostPlayedUseCase,
        private val headers: DetailHeaders

) : AndroidViewModel(application) {

    companion object {
        const val RECENTLY_ADDED = "RECENTLY_ADDED"
        const val MOST_PLAYED = "MOST_PLAYED"
        const val RELATED_ARTISTS = "RELATED_ARTISTS"
        const val SONGS = "SONGS"
    }

    private val category = MediaIdHelper.extractCategory(mediaId)
    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    val itemTitleLiveData: LiveData<String> = item[category]!!
            .map { it.title }
            .asLiveData()

    val isCoverDarkLiveData: LiveData<Boolean> = item[category]!!
            .observeOn(Schedulers.computation())
            .map {
                ImageUtils.getBitmapFromUri(application, it.image, source, itemPosition)
            }
            .map { Palette.from(it).setRegion(0,0, it.width, (it.height * 0.2).toInt()) }
            .map { it.generate() }
            .map { it.getVibrantColor(Color.WHITE) }
            .map { ColorUtils.isColorDark(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .asLiveData()

    val mostPlayedFlowable: LiveData<List<DisplayableItem>> = data[MOST_PLAYED]!!
            .asLiveData()


    val recentlyAddedFlowable: LiveData<List<DisplayableItem>> = data[RECENTLY_ADDED]!!
            .flatMapSingle { it.toFlowable().take(10).toList() }
            .asLiveData()

    fun addToMostPlayed(mediaId: String): Completable {
        return insertMostPlayedUseCase.execute(mediaId)
    }

    val data : LiveData<MutableMap<DetailDataType, MutableList<DisplayableItem>>> = Flowables.combineLatest(
            item[category]!!.doOnNext { println("item") },
            data[MOST_PLAYED]!!.doOnNext { println("most played") },
            data[RECENTLY_ADDED]!!.doOnNext { println("recent") },
            data[category]!!.doOnNext { println("albums") },
            data[RELATED_ARTISTS]!!.doOnNext { println("artits") },
            data[SONGS]!!.doOnNext { println("songs") },
            { item, mostPlayed, recent, albums, artists, songs ->

        mutableMapOf(
                DetailDataType.HEADER to mutableListOf(item),
                DetailDataType.MOST_PLAYED to handleMostPlayedHeader(mostPlayed.toMutableList()),
                DetailDataType.RECENT to handleRecentlyAddedHeader(recent.toMutableList()),
                DetailDataType.ALBUMS to handleAlbumsHeader(albums.toMutableList()),
                DetailDataType.ARTISTS_IN to handleArtistsInHeader(artists.toMutableList()),
                DetailDataType.SONGS to handleSongsHeader(songs.toMutableList())
        ) }
    ).asLiveData()

    private fun handleMostPlayedHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        if (list.isNotEmpty()){
            list.clear()
            list.addAll(0, headers.mostPlayed)
        }
        return list
    }

    private fun handleRecentlyAddedHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        if (list.isNotEmpty()){
            if (list.size > 10){
                list.clear()
                list.addAll(0, headers.recentWithSeeAll)
            } else {
                list.clear()
                list.addAll(0, headers.recent)
            }
        }
        return list
    }

    private fun handleAlbumsHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        val albumsList = list.take(4).toMutableList()
        if (albumsList.isNotEmpty()){
            if (list.size > 4){
                albumsList.add(0, headers.albumsWithSeeAll)
            } else {
                albumsList.add(0, headers.albums)
            }
        }

        return albumsList
    }

    private fun handleArtistsInHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        val (_, _, title) = list[0]
        if (title == ""){
            list.clear()
        }
        return list
    }

    private fun handleSongsHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        list.addAll(0, headers.songs)
        return list
    }

}