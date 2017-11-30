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
import dev.olog.presentation.utils.asLiveData
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
        private val insertMostPlayedUseCase: InsertMostPlayedUseCase

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
            item[category]!!, data[MOST_PLAYED]!!, data[RECENTLY_ADDED]!!,
            data[category]!!, data[RELATED_ARTISTS]!!, data[SONGS]!!, { item, mostPlayed, recent, albums, artists, songs ->

        mutableMapOf(
                DetailDataType.HEADER to mutableListOf(item),
                DetailDataType.MOST_PLAYED to mostPlayed.toMutableList(),
                DetailDataType.RECENT to recent.toMutableList(),
                DetailDataType.ALBUMS to albums.toMutableList(),
                DetailDataType.ARTISTS_IN to artists.toMutableList(),
                DetailDataType.SONGS to songs.toMutableList()
        ) }
    ).asLiveData()

}