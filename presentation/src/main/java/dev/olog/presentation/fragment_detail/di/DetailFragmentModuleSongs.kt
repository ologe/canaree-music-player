package dev.olog.presentation.fragment_detail.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.most_played.GetMostPlayedSongsUseCase
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_detail.DetailFragmentViewModel
import dev.olog.presentation.fragment_detail.model.toDetailDisplayableItem
import dev.olog.presentation.fragment_detail.model.toMostPlayedDetailDisplayableItem
import dev.olog.presentation.fragment_detail.model.toRecentDetailDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit


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

        return useCase.execute(mediaId)
                .flatMapSingle { it.toFlowable()
                        .map { it.toMostPlayedDetailDisplayableItem(mediaId) }
                        .toList()
                }
    }

    @Provides
    @IntoMap
    @StringKey(DetailFragmentViewModel.SONGS)
    internal fun provideSongList(
            @ApplicationContext context: Context,
            mediaId: String,
            useCase: GetSongListByParamUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.to(it.sumBy { it.duration.toInt() }) }
                .flatMapSingle { (songList, totalDuration) ->
                    songList.toFlowable().map { it.toDetailDisplayableItem(mediaId) }.toList().map {
                        it.to(TimeUnit.MINUTES.convert(totalDuration.toLong(), TimeUnit.MILLISECONDS).toInt())
                    } }
                .map { createSongFooter(context, it) }
    }

    private fun createSongFooter(context: Context, pair: Pair<MutableList<DisplayableItem>, Int>): List<DisplayableItem> {
        val (list, duration) = pair
        list.add(DisplayableItem(R.layout.item_detail_footer, "song footer id",
                context.resources.getQuantityString(R.plurals.song_count, list.size, list.size) + TextUtils.MIDDLE_DOT_SPACED +
                        context.resources.getQuantityString(R.plurals.duration_count, duration, duration)))
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
