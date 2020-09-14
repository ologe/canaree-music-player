package dev.olog.feature.library.artist

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.navigation.Navigator
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow

internal class ArtistFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: LibraryPreferences,
    private val gateway: ArtistGateway,
    private val podcastGateway: PodcastAuthorGateway,
    private val navigator: Navigator
) : ViewModel() {

    val data: Flow<List<ArtistFragmentModel>>
        get() {
//            return if (isPodcast) {
//                podcastGateway.observeAll()
//            } else {
                return gateway.observeAll().mapListItem { it.toPresentation() }
        }

    fun observeSpanCount() = preferences
        .observeSpanCount(MediaIdCategory.ARTISTS)

    private fun Artist.toPresentation(): ArtistFragmentModel {
        return ArtistFragmentModel(
            mediaId = presentationId,
            title = name,
            subtitle = DisplayableAlbum.readableSongCount(context.resources, songs)
        )
    }

    fun updateSpan() {
        navigator.toLibrarySpan(MediaIdCategory.ARTISTS)
    }

}