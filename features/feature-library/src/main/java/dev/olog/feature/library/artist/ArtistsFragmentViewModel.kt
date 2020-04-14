package dev.olog.feature.library.artist

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.feature.library.R
import dev.olog.feature.library.model.TabCategory
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.presentationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// TODO podcast
internal class ArtistsFragmentViewModel @Inject constructor(
    private val context: Context,
    private val artistGateway: ArtistGateway,
    private val preferences: LibraryPreferences
) : ViewModel() {

    val data: Flow<List<DisplayableAlbum>>
        get() {
            return artistGateway.observeAll().map { list ->
                val span = getSpanCount()
                list.map { it.toTabDisplayableItem(context.resources, span) }
            }
        }

    private fun Artist.toTabDisplayableItem(
        resources: Resources,
        span: Int
    ): DisplayableAlbum {
        val songs = DisplayableAlbum.readableSongCount(resources, songs)

        return DisplayableAlbum(
            type = if (span == 1) R.layout.item_artist_single_line else R.layout.item_artist,
            mediaId = presentationId,
            title = name,
            subtitle = songs
        )
    }

    fun getSpanCount(): Int = preferences.getSpanCount(TabCategory.ARTISTS)

    fun observeSpanCount() = preferences
        .observeSpanCount(TabCategory.ARTISTS)
        .drop(1) // drop initial value, already used

}