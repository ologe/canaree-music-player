package dev.olog.feature.library.artist

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.ViewStream
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.fragment.app.Fragment
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.R
import dev.olog.feature.library.sample.ArtistModelProvider
import dev.olog.feature.library.sample.SpanCountProvider
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.components.CanareeToolbar
import dev.olog.shared.components.GridList
import dev.olog.shared.components.StatusBar
import dev.olog.shared.components.item.ListItemArtist
import dev.olog.shared.components.item.ListItemTrack
import dev.olog.shared.components.theme.CanareeTheme

// TODO item click
// TODO wave side bar
// TODO empty state

@AndroidEntryPoint
class ArtistFragment : Fragment()

object ArtistFragment2 {
    @Composable
    operator fun invoke() = ArtistFragment2()
}

@Composable
private fun ArtistFragment2() {
    val viewModel = viewModel<ArtistFragmentViewModel>()
    val items by viewModel.data.collectAsState(initial = emptyList())
    val span by viewModel.observeSpanCount().collectAsState(null)
    if (span == null) {
        return
    }
    ArtistFragmentContent(items, span!!, viewModel::updateSpan) // TODO on more click
}

@Composable
@Preview
private fun TrackFragmentUiPreview(
    @PreviewParameter(SpanCountProvider::class) spanCount: Int
) {
    CanareeTheme {
        ArtistFragmentContent(ArtistModelProvider.data, spanCount)
    }
}

@Composable
private fun ArtistFragmentContent(
    items: List<ArtistFragmentModel>,
    spanCount: Int,
    updateSpan: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    Surface(color = MaterialTheme.colors.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBar()
            CanareeToolbar(stringResource(id = R.string.common_artists)) {
                IconButton(onClick = updateSpan) {
                    Icon(asset = Icons.Rounded.ViewStream)
                }
                IconButton(onClick = onMoreClick) {
                    Icon(asset = Icons.Rounded.MoreVert)
                }
            }
            ArtistsList(items, spanCount)
        }
    }
}

@Composable
private fun ArtistsList(
    items: List<ArtistFragmentModel>,
    spanCount: Int
) {
    GridList(list = items, spanCount = spanCount) {
        if (spanCount == 1) {
            ListItemTrack(
                mediaId = it.mediaId.toDomain(),
                title = it.title,
                subtitle = it.subtitle,
                shape = CircleShape
            )
        } else {
            ListItemArtist(
                mediaId = it.mediaId.toDomain(),
                title = it.title,
                subtitle = it.subtitle
            )
        }
    }
}