package dev.olog.feature.detail.detail.model

import android.content.Context
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.interactor.ObserveMostPlayedSongsUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.feature.detail.detail.DetailFragmentHeaders
import dev.olog.feature.detail.detail.DetailFragmentViewModel.Companion.VISIBLE_RECENTLY_ADDED_PAGES
import dev.olog.feature.detail.detail.mapper.*
import dev.olog.shared.TextUtils
import dev.olog.shared.android.DisplayableItemUtils
import dev.olog.shared.android.utils.TimeUtils
import dev.olog.shared.component6
import dev.olog.shared.exhaustive
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class DetailDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val headers: DetailFragmentHeaders,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    // podcast
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,

    private val recentlyAddedUseCase: ObserveRecentlyAddedUseCase,
    private val mostPlayedUseCase: ObserveMostPlayedSongsUseCase,
    private val relatedArtistsUseCase: ObserveRelatedArtistsUseCase,
    private val sortOrderUseCase: ObserveDetailSortUseCase,
    private val observeSongListByParamUseCase: ObserveSongListByParamUseCase
) {

    private val resources = context.resources


    fun observeHeader(
        parentMediaId: MediaId
    ): Flow<List<DetailFragmentModel>> {
        val item = when (parentMediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeByParam(parentMediaId.categoryValue)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeByParam(parentMediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.ALBUMS -> albumGateway.observeByParam(parentMediaId.categoryId)
                .mapNotNull { it?.toHeaderItem() }
            MediaIdCategory.ARTISTS -> artistGateway.observeByParam(parentMediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.GENRES -> genreGateway.observeByParam(parentMediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeByParam(parentMediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeByParam(parentMediaId.categoryId)
                .mapNotNull { it?.toHeaderItem() }
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.observeByParam(parentMediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE,
            MediaIdCategory.SONGS,
            MediaIdCategory.PODCASTS -> error("invalid category=$parentMediaId")
        }.exhaustive
        return item.map { header ->
            listOf(
                header,
                headers.biography(parentMediaId)
            ).mapNotNull { it }
        }
    }

    fun observe(
        parentMediaId: MediaId,
        filterFlow: Flow<String>
    ): Flow<List<DetailFragmentModel>> {
        val songListFlow = sortOrderUseCase(parentMediaId)
            .flatMapLatest { order ->
                observeSongListByParamUseCase(parentMediaId)
                    .combine(filterFlow) { songList, filter ->
                        val filteredSongList: MutableList<Song> = songList.asSequence()
                            .filter {
                                it.title.contains(filter, true) ||
                                        it.artist.contains(filter, true) ||
                                        it.album.contains(filter, true)
                            }.toMutableList()

                        val songListDuration = filteredSongList.sumBy { it.duration.toInt() }
                        val songListSize = filteredSongList.size

                        val result: MutableList<DetailFragmentModel> = filteredSongList.asSequence()
                            .map { it.toDetailDisplayableItem(parentMediaId, order.type) }
                            .toMutableList()

                        if (result.isNotEmpty()) {
                            result.addAll(0, headers.songs)
                            result.add(createDurationFooter(songListSize, songListDuration))
                        } else {
                            result.add(headers.noTracks)
                        }

                        result
                    }
            }

        return combine(
            observeHeader(parentMediaId),
            observeSiblings(parentMediaId).map { if (it.isNotEmpty()) headers.albums(parentMediaId) else listOf() },
            observeMostPlayed(parentMediaId).map { if (it.isNotEmpty()) headers.mostPlayed else listOf() },
            observeRecentlyAdded(parentMediaId).map {
                if (it.isNotEmpty()) headers.recent(
                    it.size,
                    it.size > VISIBLE_RECENTLY_ADDED_PAGES
                ) else listOf()
            },
            songListFlow,
            observeRelatedArtists(parentMediaId).map { if (it.isNotEmpty()) headers.relatedArtists(it.size > 10) else listOf() }
        ) { array ->
            val list = array.toList()
            val (header, siblings, mostPlayed, recentlyAdded, songList, relatedArtists) = list
            if (parentMediaId.isArtist) {

                header + siblings + mostPlayed + recentlyAdded + songList + relatedArtists
            } else {
                header + mostPlayed + recentlyAdded + songList + relatedArtists + siblings
            }
        }
    }

    fun observeMostPlayed(
        parentMediaId: MediaId
    ): Flow<List<DetailFragmentMostPlayedModel>> {
        return mostPlayedUseCase(parentMediaId).map {
            it.mapIndexed { index, song -> song.toMostPlayedDetailDisplayableItem(parentMediaId, index) }
        }
    }

    fun observeRecentlyAdded(
        parentMediaId: MediaId
    ): Flow<List<DetailFragmentRecentlyAddedModel>> {
        return recentlyAddedUseCase(parentMediaId).mapListItem { it.toRecentDetailDisplayableItem(parentMediaId) }
    }

    fun observeRelatedArtists(
        parentMediaId: MediaId
    ): Flow<List<DetailFragmentRelatedArtistModel>> {
        return relatedArtistsUseCase(parentMediaId).mapListItem { it.toRelatedArtist(resources) }
    }

    fun observeSiblings(
        parentMediaId: MediaId
    ): Flow<List<DetailFragmentAlbumModel>> = when (parentMediaId.category) {
        MediaIdCategory.FOLDERS -> folderGateway.observeSiblings(parentMediaId.categoryValue).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.PLAYLISTS -> playlistGateway.observeSiblings(parentMediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.ALBUMS -> albumGateway.observeSiblings(parentMediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.ARTISTS -> albumGateway.observeArtistsAlbums(parentMediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.GENRES -> genreGateway.observeSiblings(parentMediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        // podcasts
        MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeSiblings(parentMediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeSiblings(parentMediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.PODCASTS_ARTISTS -> podcastAlbumGateway.observeArtistsAlbums(parentMediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        else -> throw IllegalArgumentException("invalid category=$parentMediaId")
    }

    private fun createDurationFooter(songCount: Int, duration: Int): DetailFragmentModel {
        val songs = DisplayableItemUtils.readableSongCount(resources, songCount)
        val time = TimeUtils.formatMillis(context, duration)

        return DetailFragmentModel.Duration(
            content = songs + TextUtils.MIDDLE_DOT_SPACED + time
        )
    }

}