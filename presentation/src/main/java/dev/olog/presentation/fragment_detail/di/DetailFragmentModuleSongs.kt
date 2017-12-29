package dev.olog.presentation.fragment_detail.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.entity.Song
import dev.olog.domain.entity.SortType
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.most_played.GetMostPlayedSongsUseCase
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.domain.interactor.detail.sorting.GetSortOrderUseCase
import dev.olog.domain.interactor.detail.sorting.GetSortedSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_detail.DetailFragmentViewModel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.groupMap
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.TimeUtils
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable

@Module
class DetailFragmentModuleSongs {

    @Provides
    @IntoMap
    @StringKey(DetailFragmentViewModel.RECENTLY_ADDED)
    internal fun provideRecentlyAdded(
            mediaId: String,
            useCase: GetRecentlyAddedUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .flatMapSingle { it.toFlowable()
                        .map { it.toRecentDetailDisplayableItem(mediaId) }
                        .take(11)
                        .toList()
                }
    }

    @Provides
    @IntoMap
    @StringKey(DetailFragmentViewModel.MOST_PLAYED)
    internal fun provideMostPlayed(
            mediaId: String,
            useCase: GetMostPlayedSongsUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toMostPlayedDetailDisplayableItem(mediaId) }
    }

    @Provides
    @IntoMap
    @StringKey(DetailFragmentViewModel.SONGS)
    internal fun provideSongList(
            @ApplicationContext context: Context,
            mediaId: String,
            useCase: GetSortedSongListByParamUseCase,
            getSortOrderUseCase: GetSortOrderUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .flatMapSingle { songList -> getSortOrderUseCase.execute(mediaId).firstOrError()
                        .map { sort -> Triple(songList, songList.sumBy { it.duration.toInt() }, sort) }
                }
                .flatMapSingle { (songList, totalDuration, sort) ->
                    songList.toFlowable().map { it.toDetailDisplayableItem(mediaId, sort) }.toList()
                            .map { it.to(totalDuration) }
                }.map { createSongFooter(context, it) }
    }

    private fun createSongFooter(context: Context, pair: Pair<MutableList<DisplayableItem>, Int>): List<DisplayableItem> {
        val (list, duration) = pair
        list.add(DisplayableItem(R.layout.item_detail_footer, "song footer id",
                context.resources.getQuantityString(R.plurals.song_count, list.size, list.size) + TextUtils.MIDDLE_DOT_SPACED +
                        TimeUtils.formatMillis(context, duration.toLong())))
        return list
    }

    @Provides
    @IntoMap
    @StringKey(DetailFragmentViewModel.RELATED_ARTISTS)
    internal fun provideRelatedArtists(
            @ApplicationContext context: Context,
            mediaId: String,
            useCase: GetSongListByParamUseCase): Flowable<List<DisplayableItem>> {

        val source = MediaIdHelper.mapCategoryToSource(mediaId)
        val unknownArtist = context.getString(R.string.unknown_artist)
        val inThisItemTitles = context.resources.getStringArray(R.array.detail_in_this_item)

        return useCase.execute(mediaId)
                .map {
                    if (source != TabViewPagerAdapter.ALBUM && source != TabViewPagerAdapter.ARTIST){
                        it.asSequence().filter { it.artist != unknownArtist }
                                .map { it.artist }
                                .distinct()
                                .joinToString()
                    } else ""
                }
                .map { DisplayableItem(R.layout.item_detail_related_artist, "related id", it, inThisItemTitles[source]) }
                .map { listOf(it) }
    }

}

private fun Song.toDetailDisplayableItem(parentId: String, sortType: SortType): DisplayableItem {
    val category = MediaIdHelper.extractCategory(parentId)
    val viewType = when {
        category == MediaIdHelper.MEDIA_ID_BY_ALBUM -> R.layout.item_detail_song_with_track
        category == MediaIdHelper.MEDIA_ID_BY_PLAYLIST && sortType == SortType.CUSTOM ->
            R.layout.item_detail_song_with_drag_handle
        else -> R.layout.item_detail_song
    }

    val secondText = when (category){
        MediaIdHelper.MEDIA_ID_BY_ALBUM -> this.artist
        MediaIdHelper.MEDIA_ID_BY_ARTIST -> this.album
        else -> "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"
    }

    var trackAsString = trackNumber.toString()
    if (trackAsString.length > 3){
        trackAsString = trackAsString.substring(1)
    }
    val trackResult = trackAsString.toInt()
    trackAsString = if (trackResult == 0){
        "-"
    } else {
        trackResult.toString()
    }

    return DisplayableItem(
            viewType,
            MediaIdHelper.playableItem(parentId, id),
            title,
            secondText,
            image,
            true,
            isRemix,
            isExplicit,
            trackAsString
    )
}

private fun Song.toMostPlayedDetailDisplayableItem(parentId: String): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_song_most_played,
            MediaIdHelper.playableItem(parentId, id),
            title,
            artist,
            image,
            true,
            isRemix,
            isExplicit
    )
}

private fun Song.toRecentDetailDisplayableItem(parentId: String): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_song_recent,
            MediaIdHelper.playableItem(parentId, id),
            title,
            artist,
            image,
            true,
            isRemix,
            isExplicit
    )
}
