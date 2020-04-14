package dev.olog.feature.library.album

import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.feature.library.R
import dev.olog.feature.library.model.TabCategory
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.presentationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AlbumsFragmentViewModel @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val preferences: LibraryPreferences
) : ViewModel() {

    val data: Flow<List<DisplayableAlbum>>
        get() {
            return albumGateway.observeAll().map { list ->
                val span = getSpanCount()
                list.map { it.toTabDisplayableItem(span) }
            }
        }

    private fun Album.toTabDisplayableItem(
        span: Int
    ): DisplayableAlbum {
        return DisplayableAlbum(
            type = if (span == 1) R.layout.item_album_single_line else R.layout.item_album,
            mediaId = presentationId,
            title = title,
            subtitle = artist
        )
    }

    fun getSpanCount(): Int = preferences.getSpanCount(TabCategory.ALBUMS)

    fun observeSpanCount() = preferences
        .observeSpanCount(TabCategory.ALBUMS)
        .drop(1) // drop initial value, already used

}