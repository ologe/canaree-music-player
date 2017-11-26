package dev.olog.presentation.fragment_detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDetailDisplayableItem
import dev.olog.presentation.model.toRecentDetailDisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit

class DetailFragmentViewModel(
        application: Application,
        mediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        getSongListByParamUseCase: GetSongListByParamUseCase

) : AndroidViewModel(application) {

    companion object {
        private val ONE_WEEK = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)
    }

    private val unknownArtist = application.getString(R.string.unknown_artist)
    private val category = MediaIdHelper.extractCategory(mediaId)
    private val source = MediaIdHelper.mapCategoryToSource(mediaId)
    private val inThisItemTitles = application.resources.getStringArray(R.array.detail_in_this_item)

    val itemLiveData: LiveData<DisplayableItem> = item[category]!!.asLiveData()

    val albumsLiveData : LiveData<List<DisplayableItem>> = data[category]!!.asLiveData()

    private val sharedSongObserver = getSongListByParamUseCase
            .execute(mediaId)
            .replay(1)
            .refCount()

    val songsLiveData: LiveData<List<DisplayableItem>> = sharedSongObserver
            .map { it.to(it.sumBy { it.duration.toInt() }) }
            .flatMapSingle { (songList, totalDuration) ->
                songList.toFlowable().map { it.toDetailDisplayableItem(mediaId) }.toList().map {
                it.to(TimeUnit.MINUTES.convert(totalDuration.toLong(), TimeUnit.MILLISECONDS).toInt())
            } }
            .map { (list, totalDuration) ->
                list.add(DisplayableItem(R.layout.item_detail_footer, "song footer id",
                        application.resources.getQuantityString(R.plurals.song_count, list.size, list.size) + dev.olog.shared.TextUtils.MIDDLE_DOT_SPACED +
                                application.resources.getQuantityString(R.plurals.duration_count, totalDuration, totalDuration)))
                list
            }.asLiveData()

    val recentlyAddedLiveData: LiveData<List<DisplayableItem>> = sharedSongObserver
            .filter { it.size >= 5 }
            .flatMapSingle { it.toFlowable()
                    .filter { (System.currentTimeMillis() - it.dateAdded * 1000) <= ONE_WEEK }
                    .map { it.toRecentDetailDisplayableItem(mediaId) }
                    .take(11)
                    .toList()

            }.asLiveData()

    val artistsInDataLiveData: LiveData<List<DisplayableItem>> = sharedSongObserver
            .filter { source != TabViewPagerAdapter.ALBUM && source != TabViewPagerAdapter.ARTIST }
            .map { it.asSequence()
                    .filter { it.artist != unknownArtist }
                    .map { it.artist }
                    .distinct()
                    .joinToString()
            }
            .map { DisplayableItem(R.layout.item_related_artists, "related id", it, inThisItemTitles[source]) }
            .map { listOf(it) }
            .asLiveData()

}