package dev.olog.feature.library.artist

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.domain.prefs.SortPreferences
import dev.olog.feature.library.model.TabCategory
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.navigation.Params
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop

internal class ArtistFragmentViewModel @ViewModelInject constructor(
    @Assisted private val bundle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val sortPrefs: SortPreferences,
    private val preferences: LibraryPreferences,
    private val gateway: ArtistGateway,
    private val podcastGateway: PodcastAuthorGateway
) : ViewModel() {

    private val isPodcast: Boolean
        get() = bundle.get(Params.PODCAST)!!

    val data: Flow<List<ArtistFragmentItem>>
        get() {
            return if (isPodcast) {
                podcastGateway.observeAll()
            } else {
                gateway.observeAll()
            }.mapListItem { it.toPresentation() }
        }

    // TODO remove TabCategory
    fun getSpanCount() = preferences.getSpanCount(TabCategory.ARTISTS)

    fun observeSpanCount() = preferences
        .observeSpanCount(TabCategory.ARTISTS)
        .drop(1) // drop initial value, already used

    private fun Artist.toPresentation(): ArtistFragmentItem {
        return ArtistFragmentItem(
//            type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_artist, TODO??
            mediaId = presentationId,
            title = name,
            subtitle = DisplayableAlbum.readableSongCount(context.resources, songs)
        )
    }

}