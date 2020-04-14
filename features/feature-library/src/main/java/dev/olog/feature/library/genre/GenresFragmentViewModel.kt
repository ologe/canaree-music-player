package dev.olog.feature.library.genre

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Genre
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.presentationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GenresFragmentViewModel @Inject constructor(
    private val context: Context,
    private val genreGateway: GenreGateway
) : ViewModel() {

    val data: Flow<List<DisplayableAlbum>>
        get() {
            return genreGateway.observeAll().map { list ->
                list.map { it.toTabDisplayableItem(context.resources) }
            }
        }

    private fun Genre.toTabDisplayableItem(
        resources: Resources
    ): DisplayableAlbum {
        return DisplayableAlbum(
            type = R.layout.item_genre,
            mediaId = presentationId,
            title = name,
            subtitle = DisplayableAlbum.readableSongCount(resources, size)
        )
    }

}