package dev.olog.presentation.detail

import android.content.Context
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.interactor.ObserveMostPlayedSongsUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.presentation.R
import dev.olog.presentation.detail.DetailFragmentViewModel.Companion.VISIBLE_RECENTLY_ADDED_PAGES
import dev.olog.presentation.detail.mapper.*
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.feature.media.api.DurationUtils
import dev.olog.platform.TimeUtils
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
    private val autoPlaylistGateway: AutoPlaylistGateway,

    private val recentlyAddedUseCase: ObserveRecentlyAddedUseCase,
    private val mostPlayedUseCase: ObserveMostPlayedSongsUseCase,
    private val relatedArtistsUseCase: ObserveRelatedArtistsUseCase,
    private val sortOrderUseCase: ObserveDetailSortUseCase,
    private val observeSongListByParamUseCase: ObserveSongListByParamUseCase
) {

    private val resources = context.resources


    fun observeHeader(mediaId: MediaId): Flow<List<DisplayableItem>> {
        val item = when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeById(mediaId.id)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeById(mediaId.id)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.AUTO_PLAYLISTS -> autoPlaylistGateway.observeById(mediaId.id)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.ALBUMS -> albumGateway.observeById(mediaId.id)
                .mapNotNull { it?.toHeaderItem() }
            MediaIdCategory.ARTISTS -> artistGateway.observeById(mediaId.id)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.GENRES -> genreGateway.observeById(mediaId.id)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE,
            MediaIdCategory.SONGS -> throw IllegalArgumentException("invalid category=$mediaId")
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
            if (mediaId.category == MediaIdCategory.ARTISTS) {

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
        MediaIdCategory.FOLDERS -> folderGateway.observeSiblings(mediaId.id).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.PLAYLISTS -> playlistGateway.observeSiblings(mediaId).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.AUTO_PLAYLISTS -> flowOf(emptyList())
        MediaIdCategory.ALBUMS -> albumGateway.observeSiblings(mediaId.id).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.ARTISTS -> albumGateway.observeArtistsAlbums(mediaId.id).mapListItem {
            it.toDetailDisplayableItem(
                resources
            )
        }
        MediaIdCategory.GENRES -> genreGateway.observeSiblings(mediaId.id).mapListItem {
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
            title = songs + DurationUtils.MIDDLE_DOT_SPACED + time
        )
    }

}