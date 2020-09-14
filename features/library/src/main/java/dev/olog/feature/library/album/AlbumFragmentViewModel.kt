package dev.olog.feature.library.album

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.prefs.SortPreferences
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.navigation.Navigator
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow

internal class AlbumFragmentViewModel @ViewModelInject constructor(
    private val navigator: Navigator,
    private val sortPrefs: SortPreferences,
    private val preferences: LibraryPreferences,
    private val gateway: AlbumGateway,
) : ViewModel() {

    val data: Flow<List<AlbumFragmentModel>>
        get() = gateway.observeAll()
            .mapListItem { it.toPresentation() }

    val sortOrder: SortEntity
        get() = sortPrefs.getAllAlbumsSort()

    fun observeSpanCount() = preferences
        .observeSpanCount(MediaIdCategory.ALBUMS)

    private fun Album.toPresentation(): AlbumFragmentModel {
        return AlbumFragmentModel(
            mediaId = presentationId,
            title = title,
            subtitle = artist
        )
    }

    fun updateSpan() {
        navigator.toLibrarySpan(MediaIdCategory.ALBUMS)
    }

}