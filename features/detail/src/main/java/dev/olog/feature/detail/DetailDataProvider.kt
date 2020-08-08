package dev.olog.feature.detail

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.shared.coroutines.mapListItem
import dev.olog.domain.entity.spotify.SpotifyAlbumType
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.spotify.SpotifyGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.interactor.mostplayed.ObserveMostPlayedSongsUseCase
import dev.olog.domain.interactor.ObserveRecentlyAddedUseCase
import dev.olog.domain.interactor.ObserveRelatedArtistsUseCase
import dev.olog.domain.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.domain.interactor.sort.ObserveDetailSortUseCase
import dev.olog.feature.detail.DetailFragmentViewModel.Companion.VISIBLE_RECENTLY_ADDED_PAGES
import dev.olog.feature.detail.mapper.*
import dev.olog.feature.detail.mapper.toDetailDisplayableItem
import dev.olog.feature.detail.mapper.toHeaderItem
import dev.olog.feature.detail.mapper.toMostPlayedDetailDisplayableItem
import dev.olog.feature.detail.mapper.toRecentDetailDisplayableItem
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableHeader
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.*
import dev.olog.shared.android.utils.TimeUtils
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class DetailDataProvider @Inject constructor(
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
    private val observeSongListByParamUseCase: ObserveSongListByParamUseCase,
    private val spotifyGateway: SpotifyGateway
) {

    private val resources = context.resources


    fun observeHeader(mediaId: PresentationId.Category): Flow<List<DisplayableItem>> {
        val item = when (mediaId.category) {
            PresentationIdCategory.FOLDERS -> folderGateway.observeByParam(mediaId.categoryId)
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.PLAYLISTS -> playlistGateway.observeByParam(mediaId.categoryId.toLong())
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.ALBUMS -> albumGateway.observeByParam(mediaId.categoryId.toLong())
                .mapNotNull { it?.toHeaderItem() }
            PresentationIdCategory.ARTISTS -> artistGateway.observeByParam(mediaId.categoryId.toLong())
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.GENRES -> genreGateway.observeByParam(mediaId.categoryId.toLong())
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeByParam(
                mediaId.categoryId.toLong()
            )
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.PODCASTS_AUTHORS -> podcastAuthorGateway.observeByParam(mediaId.categoryId.toLong())
                .mapNotNull { it?.toHeaderItem(resources) }
            PresentationIdCategory.HEADER,
            PresentationIdCategory.SONGS,
            PresentationIdCategory.PODCASTS,
            PresentationIdCategory.SPOTIFY_ALBUMS,
            PresentationIdCategory.SPOTIFY_TRACK -> throw IllegalArgumentException("invalid category=$mediaId")
        }.exhaustive
        return item.map { header ->
            listOf(
                header,
                headers.biography(mediaId.category)
            ).mapNotNull { it }
        }
    }

    fun observe(
        mediaId: PresentationId.Category,
        filterFlow: Flow<String>
    ): Flow<List<DisplayableItem>> {
        val spotifySongs = flow {
            emit(emptyList())
            if (mediaId.category == PresentationIdCategory.ALBUMS) {
                emit(spotifyGateway.getAlbumTracks(mediaId.toDomain()))
            }
        }

        val songListFlow = sortOrderUseCase(mediaId.toDomain())
            .combine(spotifySongs) { sort, spotify -> sort to spotify }
            .flatMapLatest { (order, spotify) ->
                combine(
                    observeSongListByParamUseCase(mediaId.toDomain()),
                    filterFlow
                ) { songList, filter ->
                    val filteredSongList = songList
                        .filter { it.title.contains(filter, true) }
                        .toList()

                    val spotifyFiltered = spotify
                        .filter { it.name.contains(filter, true) }
                        .toList()

                    val songListDuration = filteredSongList.sumBy { it.duration.toInt() } + spotifyFiltered.sumBy { it.duration.toInt() }
                    val songListSize = filteredSongList.size + spotifyFiltered.size

                    val (finalSongList, finalSpotifyList) = mergeTracks(
                        filteredSongList, spotifyFiltered, mediaId, order
                    )

                    val result = finalSongList
                        .map { it as DisplayableItem } // downcast
                        .toMutableList()

                    if (result.isNotEmpty()) {
                        result.addAll(0, headers.songs(mediaId.isAnyPodcast))
                        if (finalSpotifyList.isNotEmpty()) {
                            result.add(createSpotifyDivider())
                            result.addAll(finalSpotifyList)
                        }
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
            observeSiblings(mediaId).map { if (it.isNotEmpty()) headers.albums(mediaId) else listOf() },
            observeMostPlayed(mediaId).map { if (it.isNotEmpty()) headers.mostPlayed else listOf() },
            observeRecentlyAdded(mediaId).map {
                if (it.isNotEmpty()) headers.recent(
                    it.size,
                    it.size > VISIBLE_RECENTLY_ADDED_PAGES
                ) else listOf()
            },
            songListFlow,
            observeRelatedArtists(mediaId).map { if (it.isNotEmpty()) headers.relatedArtists(it.size > 10) else listOf() },
            observeSpotifyArtistAlbums(mediaId).map { if (it.isNotEmpty()) headers.spotifyAlbums() else listOf() },
            observeSpotifyArtistSingles(mediaId).map { if (it.isNotEmpty()) headers.spotifySingles() else listOf() }
        ) { array ->
            val list = array.toList()
            val header = list[0]
            val siblings = list[1]
            val mostPlayed = list[2]
            val recentlyAdded = list[3]
            val songList = list[4]
            val relatedArtists = list[5]
            val spotifyAlbums = list[6]
            val spotifySingles = list[7]

            if (mediaId.category == PresentationIdCategory.ARTISTS) {
                header + siblings + mostPlayed + recentlyAdded + songList + relatedArtists + spotifyAlbums + spotifySingles
            } else {
                header + mostPlayed + recentlyAdded + songList + relatedArtists + siblings
            }
        }
    }

    fun observeSpotifyArtistAlbums(mediaId: PresentationId.Category): Flow<List<DisplayableAlbum>> {
        if (mediaId.category == PresentationIdCategory.ARTISTS) {
            return flow {
                emit(emptyList())
                val artistAlbums = spotifyGateway
                    .getArtistAlbums(mediaId.toDomain(), SpotifyAlbumType.ALBUM)
                    .map { it.toDetailDisplayableItem(resources) }
                emit(artistAlbums)
            }
        }
        return flowOf(emptyList())
    }

    fun observeSpotifyArtistSingles(mediaId: PresentationId.Category): Flow<List<DisplayableAlbum>> {
        if (mediaId.category == PresentationIdCategory.ARTISTS) {
            return flow {
                emit(emptyList())
                val artistAlbums = spotifyGateway
                    .getArtistAlbums(mediaId.toDomain(), SpotifyAlbumType.SINGLE)
                    .map { it.toDetailDisplayableItem(resources) }
                emit(artistAlbums)
            }
        }
        return flowOf(emptyList())
    }

    fun observeMostPlayed(mediaId: PresentationId.Category): Flow<List<DisplayableTrack>> {
        return mostPlayedUseCase(mediaId.toDomain()).map {
            it.mapIndexed { index, song -> song.toMostPlayedDetailDisplayableItem(mediaId, index) }
        }
    }

    fun observeRecentlyAdded(mediaId: PresentationId.Category): Flow<List<DisplayableTrack>> {
        return recentlyAddedUseCase(mediaId.toDomain()).mapListItem {
            it.toRecentDetailDisplayableItem(
                mediaId
            )
        }
    }

    fun observeRelatedArtists(mediaId: PresentationId.Category): Flow<List<DisplayableAlbum>> {
        return relatedArtistsUseCase(mediaId.toDomain()).mapListItem { it.toRelatedArtist(resources) }
    }

    fun observeSiblings(mediaId: PresentationId.Category): Flow<List<DisplayableAlbum>> = when (mediaId.category) {
        PresentationIdCategory.FOLDERS -> folderGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.PLAYLISTS -> playlistGateway.observeSiblings(mediaId.categoryId.toLong()).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.ALBUMS -> albumGateway.observeSiblings(mediaId.categoryId.toLong()).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.ARTISTS -> albumGateway.observeArtistsAlbums(mediaId.categoryId.toLong()).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.GENRES -> genreGateway.observeSiblings(mediaId.categoryId.toLong()).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        // podcasts
        PresentationIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeSiblings(mediaId.categoryId.toLong()).mapListItem {
            it.toDetailDisplayableItem(resources)
        }
        PresentationIdCategory.PODCASTS_AUTHORS -> flowOf(emptyList())
//        else ->
        PresentationIdCategory.SONGS,
        PresentationIdCategory.PODCASTS,
        PresentationIdCategory.HEADER,
        PresentationIdCategory.SPOTIFY_ALBUMS,
        PresentationIdCategory.SPOTIFY_TRACK -> throw IllegalArgumentException("invalid category=$mediaId")
    }

    private fun createSpotifyDivider(): DisplayableItem {
        return DisplayableHeader(
            type = R.layout.item_detail_song_divider,
            mediaId = PresentationId.headerId("detail spotify song divider"),
            title = ""
        )
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