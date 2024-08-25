package dev.olog.presentation.detail

import android.content.Context
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.interactor.ObserveMostPlayedSongsUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.presentation.detail.adapter.DetailItem
import dev.olog.presentation.detail.mapper.*
import dev.olog.shared.android.TextUtils
import dev.olog.shared.android.utils.TimeUtils
import dev.olog.shared.component6
import dev.olog.shared.exhaustive
import dev.olog.shared.mapListItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
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
    private val observeSongListByParamUseCase: ObserveSongListByParamUseCase,
    private val imageRetrieverGateway: ImageRetrieverGateway,
) {

    private val resources = context.resources


    private fun observeHeader(mediaId: MediaId): Flow<List<DetailItem>> {
        val headerFlow = when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeByParam(mediaId.categoryValue)
                .mapNotNull { it?.toHeaderItem(resources) }
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeByParam(mediaId.categoryId)
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
            MediaIdCategory.PLAYING_QUEUE,
            MediaIdCategory.SONGS,
            MediaIdCategory.PODCASTS -> throw IllegalArgumentException("invalid category=$mediaId")
        }.exhaustive

        return combine(
            headerFlow,
            observeBiography(mediaId)
        ) { header, biography ->
            listOf(header.copy(biography = biography))
        }.flowOn(Dispatchers.Default)
    }

    private fun observeBiography(mediaId: MediaId): Flow<String?> = flow {
        emit(null)
        try {
            if (mediaId.isArtist) {
                emit(imageRetrieverGateway.getArtist(mediaId.categoryId)?.wiki)
            } else if (mediaId.isAlbum) {
                emit(imageRetrieverGateway.getAlbum(mediaId.categoryId)?.wiki)
            }
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    fun observe(mediaId: MediaId): Flow<List<DetailItem>> {
        val songListFlow: Flow<List<DetailItem>> = sortOrderUseCase(mediaId)
            .flatMapLatest { order ->
                observeSongListByParamUseCase(mediaId)
                    .map { songList ->
                        val songListDuration = songList.sumBy { it.duration.toInt() }
                        val songListSize = songList.size

                        val result: MutableList<DetailItem> = songList.asSequence()
                            .map { it.toDetailDisplayableItem(mediaId, order.type) }
                            .toMutableList()

                        if (result.isNotEmpty()) {
                            result.addAll(0, headers.songs(mediaId, order))
                            result.add(createDurationFooter(songListSize, songListDuration))
                        }

                        result
                    }
            }

        return combine(
            observeHeader(mediaId),
            observeSiblings(mediaId).map { headers.albums(mediaId, it) },
            observeMostPlayed(mediaId).map { headers.mostPlayed(it) },
            observeRecentlyAdded(mediaId).map { headers.recentlyAdded(mediaId, it) },
            songListFlow,
            observeRelatedArtists(mediaId).map { headers.relatedArtists(mediaId, it) }
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

    private fun observeMostPlayed(mediaId: MediaId): Flow<List<DetailItem.MostPlayed>> {
        return mostPlayedUseCase(mediaId).map {
            it.mapIndexed { index, song -> song.toDetailMostPlayed(mediaId, index) }
        }
    }

    private fun observeRecentlyAdded(mediaId: MediaId): Flow<List<DetailItem.RecentlyAdded>> {
        return recentlyAddedUseCase(mediaId).mapListItem { it.toDetailRecentlyAdded(mediaId) }
    }

    private fun observeRelatedArtists(mediaId: MediaId): Flow<List<DetailItem.Album>> {
        return relatedArtistsUseCase(mediaId).mapListItem { it.toDetailRelatedArtist(resources) }
    }

    private fun observeSiblings(mediaId: MediaId): Flow<List<DetailItem.Album>> = when (mediaId.category) {
        MediaIdCategory.FOLDERS -> folderGateway.observeSiblings(mediaId.categoryValue).mapListItem {
            it.toDetailSiblingItem(resources)
        }
        MediaIdCategory.PLAYLISTS -> playlistGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailSiblingItem(resources)
        }
        MediaIdCategory.ALBUMS -> albumGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailSiblingItem(resources)
        }
        MediaIdCategory.ARTISTS -> albumGateway.observeArtistsAlbums(mediaId.categoryId).mapListItem {
            it.toDetailSiblingItem(resources)
        }
        MediaIdCategory.GENRES -> genreGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailSiblingItem(resources)
        }
        // podcasts
        MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailSiblingItem(resources)
        }
        MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeSiblings(mediaId.categoryId).mapListItem {
            it.toDetailSiblingItem(resources)
        }
        MediaIdCategory.PODCASTS_ARTISTS -> podcastAlbumGateway.observeArtistsAlbums(mediaId.categoryId).mapListItem {
            it.toDetailSiblingItem(resources)
        }

        MediaIdCategory.SONGS,
        MediaIdCategory.PODCASTS,
        MediaIdCategory.PLAYING_QUEUE -> throw IllegalArgumentException("invalid category=$mediaId")
    }

    private fun createDurationFooter(songCount: Int, duration: Int): DetailItem {
        val songs = TextUtils.readableSongCount(resources, songCount)
        val time = TimeUtils.formatMillis(context, duration)

        return DetailItem.Footer(
            title = songs + TextUtils.MIDDLE_DOT_SPACED + time
        )
    }

}