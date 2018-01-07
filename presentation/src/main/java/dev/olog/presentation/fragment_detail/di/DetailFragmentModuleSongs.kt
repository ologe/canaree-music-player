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
import dev.olog.presentation.fragment_detail.DetailFragmentViewModel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaId
import dev.olog.shared.groupMap
import dev.olog.shared_android.Constants
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
            mediaId: MediaId,
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
            mediaId: MediaId,
            useCase: GetMostPlayedSongsUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toMostPlayedDetailDisplayableItem(mediaId) }
    }

    @Provides
    @IntoMap
    @StringKey(DetailFragmentViewModel.SONGS)
    internal fun provideSongList(
            @ApplicationContext context: Context,
            mediaId: MediaId,
            useCase: GetSortedSongListByParamUseCase,
            getSortOrderUseCase: GetSortOrderUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .flatMapSingle { songList -> getSortOrderUseCase.execute(mediaId)
                        .firstOrError()
                        .map { sort -> Triple(songList, songList.sumBy { it.duration.toInt() }, sort) }
                }
                .flatMapSingle { (songList, totalDuration, sort) ->
                    songList.toFlowable().map { it.toDetailDisplayableItem(mediaId, sort) }.toList()
                            .map { it to totalDuration }
                }.map { createSongFooter(context, it) }
    }

    private fun createSongFooter(context: Context, pair: Pair<MutableList<DisplayableItem>, Int>): List<DisplayableItem> {
        val (list, duration) = pair
        list.add(DisplayableItem(R.layout.item_detail_footer, MediaId.headerId("song footer"),
                context.resources.getQuantityString(R.plurals.song_count, list.size, list.size) + TextUtils.MIDDLE_DOT_SPACED +
                        TimeUtils.formatMillis(context, duration.toLong())))
        return list
    }

    @Provides
    @IntoMap
    @StringKey(DetailFragmentViewModel.RELATED_ARTISTS)
    internal fun provideRelatedArtists(
            @ApplicationContext context: Context,
            mediaId: MediaId,
            useCase: GetSongListByParamUseCase): Flowable<List<DisplayableItem>> {

        val unknownArtist = context.getString(R.string.unknown_artist)
        val inThisItemHeader = context.resources.getStringArray(R.array.detail_in_this_item)[mediaId.source]

        return useCase.execute(mediaId)
                .map {
                    if (!mediaId.isAlbum && !mediaId.isArtist){
                        it.asSequence().filter { it.artist != unknownArtist }
                                .map { it.artist }
                                .distinct()
                                .joinToString()
                    } else ""
                }
                .map { DisplayableItem(R.layout.item_detail_related_artist, MediaId.headerId("related artists"), it, inThisItemHeader) }
                .map { listOf(it) }
    }

}

private fun Song.toDetailDisplayableItem(parentId: MediaId, sortType: SortType): DisplayableItem {
    val viewType = when {
        parentId.isAlbum -> R.layout.item_detail_song_with_track
        parentId.isPlaylist && sortType == SortType.CUSTOM -> {
            val playlistId = parentId.categoryValue.toLong()
            if (Constants.autoPlaylists.contains(playlistId)) {
                R.layout.item_detail_song
            } else R.layout.item_detail_song_with_drag_handle
        }
        else -> R.layout.item_detail_song
    }

    val secondText = when {
        parentId.isAlbum -> this.artist
        parentId.isArtist -> this.album
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
            MediaId.playableItem(parentId, id),
            title,
            secondText,
            image,
            true,
            isRemix,
            isExplicit,
            trackAsString
    )
}

private fun Song.toMostPlayedDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_song_most_played,
            MediaId.playableItem(parentId, id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}

private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_song_recent,
            MediaId.playableItem(parentId, id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}
