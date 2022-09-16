package dev.olog.feature.detail.main

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.interactor.ObserveMostPlayedSongsUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.feature.detail.R
import dev.olog.feature.detail.main.DetailFragmentViewModel.Companion.VISIBLE_RECENTLY_ADDED_PAGES
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableItem
import dev.olog.shared.TextUtils
import dev.olog.shared.TimeUtils
import dev.olog.shared.extension.component6
import dev.olog.shared.extension.exhaustive
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
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


    fun observeHeader(mediaId: MediaId): Flow<List<DisplayableItem>> {
        val item = when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeByParam(mediaId.categoryValue)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeByParam(mediaId.categoryValue)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.ALBUMS -> albumGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem() }
            MediaIdCategory.ARTISTS -> artistGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.GENRES -> genreGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem() }
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.HEADER,
            MediaIdCategory.SONGS,
            MediaIdCategory.PODCASTS -> throw IllegalArgumentException("invalid category=$mediaId")
        }.exhaustive
        return item.map { header ->
            listOf(
                header,
                headers.biography(mediaId)
            ).mapNotNull { it }
        }
    }

    fun observe(mediaId: MediaId, filterFlow: Flow<String>): Flow<List<DisplayableItem>> {
        val songListFlow = sortOrderUseCase(mediaId)
            .flatMapLatest { order ->
                observeSongListByParamUseCase(mediaId)
                    .combine(filterFlow) { songList, filter ->
                        val filteredSongList: MutableList<Song> = songList.asSequence()
                            .filter {
                                it.title.contains(filter, true) ||
                                        it.artist.contains(filter, true) ||
                                        it.album.contains(filter, true)
                            }.toMutableList()

                        val songListDuration = filteredSongList.sumBy { it.duration.toInt() }
                        val songListSize = filteredSongList.size

                        val result: MutableList<DisplayableItem> = filteredSongList.asSequence()
                            .map { it.toDetailDisplayableItem(mediaId, order.type) }
                            .toMutableList()

                        if (result.isNotEmpty()) {
                            result.addAll(0, headers.songs)
                            result.add(createDurationFooter(songListSize, songListDuration))
                        } else {
                            result.add(headers.no_songs)
                        }

                        result
                    }
            }

        return combine(
            observeHeader(mediaId),
            observeSiblings(mediaId).map { if (it.isNotEmpty()) headers.albums(mediaId) else listOf() },
            observeMostPlayed(mediaId).map { if (it.isNotEmpty()) headers.mostPlayed else listOf() },
            observeRecentlyAdded(mediaId).map {
                if (it.isNotEmpty()) headers.recent(
                    it.size,
                    it.size > VISIBLE_RECENTLY_ADDED_PAGES
                ) else listOf()
            },
            songListFlow,
            observeRelatedArtists(mediaId).map { if (it.isNotEmpty()) headers.relatedArtists(it.size > 10) else listOf() }
        ) { array ->
            val list = array.toList()
            val (header, siblings, mostPlayed, recentlyAdded, songList, relatedArtists) = list
            if (mediaId.isArtist) {

                header + siblings + mostPlayed + recentlyAdded + songList + relatedArtists
            } else {
                header + mostPlayed + recentlyAdded + songList + relatedArtists + siblings
            }
        }
    }

    fun observeMostPlayed(mediaId: MediaId): Flow<List<DisplayableItem>> {
        return mostPlayedUseCase(mediaId).map {
            it.mapIndexed { index, song -> song.toMostPlayedDetailDisplayableItem(mediaId, index) }
        }
    }

    fun observeRecentlyAdded(mediaId: MediaId): Flow<List<DisplayableItem>> {
        return recentlyAddedUseCase(mediaId).mapListItem { it.toRecentDetailDisplayableItem(mediaId) }
    }

    fun observeRelatedArtists(mediaId: MediaId): Flow<List<DisplayableItem>> {
        return relatedArtistsUseCase(mediaId).mapListItem { it.toRelatedArtist(resources) }
    }

    fun observeSiblings(mediaId: MediaId): Flow<List<DisplayableItem>> = when (mediaId.category) {
        MediaIdCategory.FOLDERS -> folderGateway.observeSiblings(mediaId.categoryValue).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.PLAYLISTS -> playlistGateway.observeSiblings(mediaId.categoryValue).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.ALBUMS -> albumGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.ARTISTS -> albumGateway.observeArtistsAlbums(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.GENRES -> genreGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        // podcasts
        MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.PODCASTS_ARTISTS -> podcastAlbumGateway.observeArtistsAlbums(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        else -> throw IllegalArgumentException("invalid category=$mediaId")
    }

    private fun createDurationFooter(songCount: Int, duration: Int): DisplayableItem {
        val songs = DisplayableAlbum.readableSongCount(resources, songCount)
        val time = TimeUtils.formatMillis(context, duration)

        return DisplayableHeader(
            type = R.layout.item_detail_song_footer,
            mediaId = MediaId.headerId("duration footer"),
            title = songs + TextUtils.MIDDLE_DOT_SPACED + time
        )
    }

}