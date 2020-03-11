package dev.olog.presentation.detail

import android.content.Context
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.interactor.ObserveMostPlayedSongsUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.presentation.PresentationId
import dev.olog.presentation.PresentationIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.detail.DetailFragmentViewModel.Companion.VISIBLE_RECENTLY_ADDED_PAGES
import dev.olog.presentation.detail.mapper.*
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.toDomain
import dev.olog.shared.*
import dev.olog.shared.android.utils.TimeUtils
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
    private val podcastAuthorGateway: PodcastAuthorGateway,

    private val recentlyAddedUseCase: ObserveRecentlyAddedUseCase,
    private val mostPlayedUseCase: ObserveMostPlayedSongsUseCase,
    private val relatedArtistsUseCase: ObserveRelatedArtistsUseCase,
    private val sortOrderUseCase: ObserveDetailSortUseCase,
    private val observeSongListByParamUseCase: ObserveSongListByParamUseCase
) {

    private val resources = context.resources


    fun observeHeader(mediaId: PresentationId.Category): Flow<List<DisplayableItem>> {
        val item = when (mediaId.category) {
            PresentationIdCategory.FOLDERS -> folderGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.PLAYLISTS -> playlistGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.ALBUMS -> albumGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem() }
            PresentationIdCategory.ARTISTS -> artistGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.GENRES -> genreGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.PODCASTS_AUTHORS -> podcastAuthorGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.HEADER,
            PresentationIdCategory.SONGS,
            PresentationIdCategory.PODCASTS -> throw IllegalArgumentException("invalid category=$mediaId")
        }.exhaustive
        return item.map { header ->
            listOf(
                header,
                headers.biography(mediaId.category)
            ).mapNotNull { it }
        }
    }

    fun observe(mediaId: PresentationId.Category, filterFlow: Flow<String>): Flow<List<DisplayableItem>> {
        val songListFlow = sortOrderUseCase(mediaId.toDomain())
            .flatMapLatest { order ->
                observeSongListByParamUseCase(mediaId.toDomain())
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
                            result.addAll(0, headers.songs(mediaId.isAnyPodcast))
                            if (!mediaId.isAnyPodcast) {
                                result.add(createDurationFooter(songListSize, songListDuration))
                            }
                        } else {
                            result.add(headers.no_songs(mediaId.isAnyPodcast))
                        }

                        result
                    }
            }

        return combine(
            observeHeader(mediaId),
            observeSiblings(mediaId).map { if (it.isNotEmpty()) headers.albums() else listOf() },
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
            if (mediaId.category == PresentationIdCategory.ARTISTS) {
                header + siblings + mostPlayed + recentlyAdded + songList + relatedArtists
            } else {
                header + mostPlayed + recentlyAdded + songList + relatedArtists + siblings
            }
        }
    }

    fun observeMostPlayed(mediaId: PresentationId.Category): Flow<List<DisplayableTrack>> {
        return mostPlayedUseCase(mediaId.toDomain()).map {
            it.mapIndexed { index, song -> song.toMostPlayedDetailDisplayableItem(mediaId, index) }
        }
    }

    fun observeRecentlyAdded(mediaId: PresentationId.Category): Flow<List<DisplayableTrack>> {
        return recentlyAddedUseCase(mediaId.toDomain()).mapListItem { it.toRecentDetailDisplayableItem(mediaId) }
    }

    fun observeRelatedArtists(mediaId: PresentationId.Category): Flow<List<DisplayableAlbum>> {
        return relatedArtistsUseCase(mediaId.toDomain()).mapListItem { it.toRelatedArtist(resources) }
    }

    fun observeSiblings(mediaId: PresentationId.Category): Flow<List<DisplayableAlbum>> = when (mediaId.category) {
        PresentationIdCategory.FOLDERS -> folderGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.PLAYLISTS -> playlistGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.ALBUMS -> albumGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.ARTISTS -> albumGateway.observeArtistsAlbums(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.GENRES -> genreGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        // podcasts
        PresentationIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.PODCASTS_AUTHORS -> flowOf(emptyList())
//        else ->
        PresentationIdCategory.SONGS,
        PresentationIdCategory.PODCASTS,
        PresentationIdCategory.HEADER -> throw IllegalArgumentException("invalid category=$mediaId")
    }

    private fun createDurationFooter(songCount: Int, duration: Int): DisplayableItem {
        val songs = DisplayableAlbum.readableSongCount(resources, songCount)
        val time = TimeUtils.formatMillis(context, duration)

        return DisplayableHeader(
            type = R.layout.item_detail_song_footer,
            mediaId = PresentationId.headerId("duration footer"),
            title = songs + TextUtils.MIDDLE_DOT_SPACED + time
        )
    }

}