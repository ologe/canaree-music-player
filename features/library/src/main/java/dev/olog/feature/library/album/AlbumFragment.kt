package dev.olog.feature.library.album

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dev.olog.feature.library.R
import dev.olog.feature.library.sample.AlbumModelProvider
import dev.olog.feature.library.sample.SpanCountProvider
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.components.Background
import dev.olog.shared.components.CanareeToolbar
import dev.olog.shared.components.GridList
import dev.olog.shared.components.StatusBar
import dev.olog.shared.components.item.ListItemAlbum
import dev.olog.shared.components.item.ListItemTrack
import dev.olog.shared.components.theme.CanareeTheme

// TODO item click
// TODO wave side bar
// TODO empty state
object AlbumFragment {
    @Composable
    operator fun invoke() = AlbumFragment()
}

@Composable
private fun AlbumFragment() {
    val viewModel = viewModel<AlbumFragmentViewModel>()
    val items by viewModel.data.collectAsState(initial = emptyList())
    val span by viewModel.observeSpanCount().collectAsState(null)
    if (span == null) {
        return
    }
    AlbumFragmentContent(items, span!!, viewModel::updateSpan) // TODO on more click
}

@Composable
@Preview
private fun TrackFragmentUiPreview(
    @PreviewParameter(SpanCountProvider::class) spanCount: Int
) {
    CanareeTheme {
        AlbumFragmentContent(AlbumModelProvider.data, spanCount)
    }
}

@Composable
private fun AlbumFragmentContent(
    items: List<AlbumFragmentModel>,
    spanCount: Int,
    updateSpan: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    Background {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBar()
            CanareeToolbar(stringResource(id = R.string.common_albums)) {
                IconButton(onClick = updateSpan) {
                    Icon(asset = Icons.Rounded.ViewStream)
                }
                IconButton(onClick = onMoreClick) {
                    Icon(asset = Icons.Rounded.MoreVert)
                }
            }
            AlbumsList(items, spanCount)
        }
    }
}

@Composable
private fun AlbumsList(
    items: List<AlbumFragmentModel>,
    spanCount: Int
) {
    GridList(list = items, spanCount = spanCount) {
        if (spanCount == 1) {
            ListItemTrack(it.mediaId.toDomain(), it.title, it.subtitle)
        } else {
            ListItemAlbum(it.mediaId.toDomain(), it.title, it.subtitle)
        }
    }
}