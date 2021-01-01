package dev.olog.feature.detail.detail

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.detail.R
import dev.olog.feature.detail.detail.model.DetailFragmentModel
import javax.inject.Inject

internal class DetailFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun biography(mediaId: MediaId): DetailFragmentModel.Biography? {
        if (mediaId.isArtist || mediaId.isAlbum){
            return DetailFragmentModel.Biography(
                content = ""
            )
        }
        return null
    }

    val mostPlayed: List<DetailFragmentModel> = listOf(
        DetailFragmentModel.MostPlayedHeader(context.getString(R.string.detail_most_played),),
        DetailFragmentModel.MostPlayedList
    )

    fun relatedArtists(showSeeAll: Boolean): List<DetailFragmentModel> = listOf(
        DetailFragmentModel.RelatedArtistHeader(
            title = context.getString(R.string.detail_related_artists),
            showSeeAll = showSeeAll
        ),
        DetailFragmentModel.RelatedArtistList
    )

    fun recent(listSize: Int, showSeeAll: Boolean): List<DetailFragmentModel> = listOf(
        DetailFragmentModel.RecentlyAddedHeader(
            title = context.getString(R.string.detail_recently_added),
            subtitle = context.resources.getQuantityString(
                R.plurals.detail_xx_new_songs,
                listSize,
                listSize
            ),
            showSeeAll = showSeeAll
        ),
        DetailFragmentModel.RecentlyAddedList
    )

    fun albums(parentMediaId: MediaId): List<DetailFragmentModel> = listOf(
        DetailFragmentModel.AlbumsHeader(
            title = context.resources.getStringArray(R.array.detail_album_header)[0/*TODO parentMediaId.source*/]
        ),
        DetailFragmentModel.AlbumsList
    )

    val shuffle: DetailFragmentModel = DetailFragmentModel.Shuffle

    val songs: List<DetailFragmentModel> = listOf(
        DetailFragmentModel.AllTracksHeader(
            title = context.getString(R.string.detail_tracks),
            subtitle = context.getString(R.string.detail_sort_by).toLowerCase()
        ),
        shuffle
    )

    val noTracks: DetailFragmentModel = DetailFragmentModel.EmptyState

}