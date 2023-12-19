package dev.olog.presentation.detail

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.ImageRetrieverGateway
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
import dev.olog.presentation.R
import dev.olog.presentation.detail.adapter.DetailFragmentItem
import dev.olog.presentation.detail.mapper.toDetailDisplayableItem
import dev.olog.presentation.detail.mapper.toHeaderItem
import dev.olog.presentation.detail.mapper.toMostPlayedDetailDisplayableItem
import dev.olog.presentation.detail.mapper.toRecentDetailDisplayableItem
import dev.olog.presentation.detail.mapper.toRelatedArtist
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.shared.TextUtils
import dev.olog.shared.android.utils.TimeUtils
import dev.olog.shared.component6
import dev.olog.shared.exhaustive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class DetailDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
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
    private val imageRetrieverGateway: ImageRetrieverGateway
) {

    private val resources = context.resources


    private fun observeHeader(mediaId: MediaId): Flow<DetailFragmentItem> {
        val itemFlow = when (mediaId.category) {
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
            itemFlow,
            observeBiography(mediaId)
        ) { item, biography ->
            item.copy(biography = biography)
        }
    }

    private fun observeBiography(mediaId: MediaId): Flow<String?> = flow {
        emit(null)
        try {
            val biography = when {
                mediaId.isArtist -> imageRetrieverGateway.getArtist(mediaId.categoryId)?.wiki
                mediaId.isAlbum -> imageRetrieverGateway.getAlbum(mediaId.categoryId)?.wiki
                else -> null
            }
            emit(biography)
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    fun observe(mediaId: MediaId, filterFlow: Flow<String>): Flow<List<DetailFragmentItem>> {
        val songListFlow = sortOrderUseCase(mediaId)
            .flatMapLatest { order ->
                observeSongListByParamUseCase(mediaId)
                    .combine(filterFlow) { songList, filter ->
                        val filteredSongList = songList.asSequence()
                            .filter {
                                it.title.contains(filter, true) ||
                                        it.artist.contains(filter, true) ||
                                        it.album.contains(filter, true)
                            }.toMutableList()

                        val songListDuration = filteredSongList.sumBy { it.duration.toInt() }
                        val songListSize = filteredSongList.size

                        val result: MutableList<DetailFragmentItem> = filteredSongList.asSequence()
                            .map { it.toDetailDisplayableItem(mediaId, order.type) }
                            .toMutableList()

                        if (result.isNotEmpty()) {
                            result.add(0, DetailFragmentItem.Shuffle)
                            result.add(0, DetailFragmentItem.SongsHeader(order))
                            result.add(createDurationFooter(songListSize, songListDuration))
                        } else {
//                            result.add(headers.no_songs) TODO
                        }

                        result
                    }
            }

        return combine(
            observeHeader(mediaId),
            observeSiblings(mediaId),
            observeMostPlayed(mediaId),
            observeRecentlyAdded(mediaId),
            songListFlow,
            observeRelatedArtists(mediaId),
        ) { array ->
            val list = array.toList()
            val (header, siblings, mostPlayed, recentlyAdded, songList, relatedArtists) = list
            if (mediaId.isArtist) {
                buildList {
                    add(header as DetailFragmentItem)
                    add(siblings as DetailFragmentItem)
                    add(mostPlayed as DetailFragmentItem)
                    add(recentlyAdded as DetailFragmentItem)
                    addAll(songList as List<DetailFragmentItem>)
                    add(relatedArtists as DetailFragmentItem)
                }
            } else {
                buildList {
                    add(header as DetailFragmentItem)
                    add(mostPlayed as DetailFragmentItem)
                    add(recentlyAdded as DetailFragmentItem)
                    addAll(songList as List<DetailFragmentItem>)
                    add(relatedArtists as DetailFragmentItem)
                    add(siblings as DetailFragmentItem)
                }
            }
        }
    }

    private fun observeMostPlayed(mediaId: MediaId): Flow<DetailFragmentItem> {
        return mostPlayedUseCase(mediaId).map { list ->
            DetailFragmentItem.MostPlayed(
                items = list.mapIndexed { index, song ->
                    song.toMostPlayedDetailDisplayableItem(mediaId, index + 1)
                }
            )
        }
    }

    private fun observeRecentlyAdded(mediaId: MediaId): Flow<DetailFragmentItem> {
        return recentlyAddedUseCase(mediaId).map { list ->
            DetailFragmentItem.RecentlyAdded(
                items = list.map { it.toRecentDetailDisplayableItem(mediaId) }
            )
        }
    }

    private fun observeRelatedArtists(mediaId: MediaId): Flow<DetailFragmentItem> {
        return relatedArtistsUseCase(mediaId).map { list ->
            DetailFragmentItem.RelatedArtists(
                items = list.map { it.toRelatedArtist(resources) }
            )
        }
    }

    private fun observeSiblings(mediaId: MediaId): Flow<DetailFragmentItem> {
        // TODO improve
        val header = try {
            context.resources.getStringArray(R.array.detail_album_header)[mediaId.source]
        } catch (ex: Throwable) {
            ""
        }
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeSiblings(mediaId.categoryValue).map { list ->
                DetailFragmentItem.Siblings(
                    header = header,
                    items = list.map { it.toDetailDisplayableItem(resources) }
                )
            }
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeSiblings(mediaId.categoryId).map { list ->
                DetailFragmentItem.Siblings(
                    header = header,
                    items = list.map { it.toDetailDisplayableItem(resources) }
                )
            }
            MediaIdCategory.ALBUMS -> albumGateway.observeSiblings(mediaId.categoryId).map { list ->
                DetailFragmentItem.Siblings(
                    header = header,
                    items = list.map { it.toDetailDisplayableItem(resources) }
                )
            }
            MediaIdCategory.ARTISTS -> albumGateway.observeArtistsAlbums(mediaId.categoryId).map { list ->
                DetailFragmentItem.Siblings(
                    header = header,
                    items = list.map { it.toDetailDisplayableItem(resources) }
                )
            }
            MediaIdCategory.GENRES -> genreGateway.observeSiblings(mediaId.categoryId).map { list ->
                DetailFragmentItem.Siblings(
                    header = header,
                    items = list.map { it.toDetailDisplayableItem(resources) }
                )
            }
            // podcasts
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeSiblings(mediaId.categoryId).map { list ->
                DetailFragmentItem.Siblings(
                    header = header,
                    items = list.map { it.toDetailDisplayableItem(resources) }
                )
            }
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeSiblings(mediaId.categoryId).map { list ->
                DetailFragmentItem.Siblings(
                    header = header,
                    items = list.map { it.toDetailDisplayableItem(resources) }
                )
            }
            MediaIdCategory.PODCASTS_ARTISTS -> podcastAlbumGateway.observeArtistsAlbums(mediaId.categoryId).map { list ->
                DetailFragmentItem.Siblings(
                    header = header,
                    items = list.map { it.toDetailDisplayableItem(resources) }
                )
            }
            MediaIdCategory.SONGS,
            MediaIdCategory.PODCASTS,
            MediaIdCategory.PLAYING_QUEUE -> throw IllegalArgumentException("invalid category=$mediaId")
        }
    }

    private fun createDurationFooter(songCount: Int, duration: Int): DetailFragmentItem.DurationFooter {
        val songs = DisplayableAlbum.readableSongCount(resources, songCount)
        val time = TimeUtils.formatMillis(context, duration)

        return DetailFragmentItem.DurationFooter(
            text = songs + TextUtils.MIDDLE_DOT_SPACED + time,
        )
    }

}