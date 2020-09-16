package dev.olog.feature.library.track

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import dev.olog.feature.library.R
import dev.olog.feature.library.sample.TrackModelProvider
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.components.Background
import dev.olog.shared.components.CanareeToolbar
import dev.olog.shared.components.StatusBar
import dev.olog.shared.components.item.ListItemShuffle
import dev.olog.shared.components.item.ListItemTrack
import dev.olog.shared.components.theme.CanareeTheme
import dev.olog.shared.exhaustive

// TODO item click
// TODO wave sidebarr bar
// TODO more
// TODO current playing
// TODO swipe to delete/add to queue?
// TODO finish podcast
// TODO empty state
object TrackFragment {
    @Composable
    operator fun invoke() = TrackFragment()
}

@Composable
@Preview
private fun TrackFragmentUiPreview() {
    CanareeTheme {
        TrackFragmentContent(TrackModelProvider.data)
    }
}

@Composable
private fun TrackFragment() {
    val items by viewModel<TrackFragmentViewModel>()
        .data.collectAsState(initial = emptyList())
    TrackFragmentContent(items)
}

@Composable
private fun TrackFragmentContent(items: List<TracksFragmentModel>) {
    Background {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBar()
            CanareeToolbar(stringResource(id = R.string.common_tracks)) {
                IconButton(onClick = {}) {
                    Icon(asset = Icons.Rounded.MoreVert)
                }
            }
            TracksList(items)
        }
    }
}

@Composable
private fun TracksList(items: List<TracksFragmentModel>) {
    LazyColumnFor(items = items) {
        when (it) {
            is TracksFragmentModel.Shuffle -> ListItemShuffle {
                // TODO click
            }
            is TracksFragmentModel.Track -> ListItemTrack(
                mediaId = it.mediaId.toDomain(),
                title = it.title,
                subtitle = it.subtitle
            )
            is TracksFragmentModel.Podcast -> ListItemTrack(
                mediaId = it.mediaId.toDomain(),
                title = it.title,
                subtitle = it.subtitle
            )
        }.exhaustive
    }
}