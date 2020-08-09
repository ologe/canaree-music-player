package dev.olog.feature.library.album

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.prefs.SortPreferences
import dev.olog.feature.library.model.TabCategory
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop

internal class AlbumFragmentViewModel @ViewModelInject constructor(
    private val appPreferencesUseCase: SortPreferences,
    private val preferences: LibraryPreferences,
    private val albumGateway: AlbumGateway
) : ViewModel() {

    val data: Flow<List<AlbumFragmentItem>>
        get() = albumGateway.observeAll()
            .mapListItem { it.toPresentation() }

    val sortOrder: SortEntity
        get() = appPreferencesUseCase.getAllAlbumsSort()

    // TODO remove TabCategory
    fun getSpanCount() = preferences.getSpanCount(TabCategory.ALBUMS)

    fun observeSpanCount() = preferences
        .observeSpanCount(TabCategory.ALBUMS)
        .drop(1) // drop initial value, already used

    private fun Album.toPresentation(): AlbumFragmentItem {
        return AlbumFragmentItem(
//            type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album, TODO??
            mediaId = presentationId,
            title = title,
            subtitle = artist
        )
    }

}