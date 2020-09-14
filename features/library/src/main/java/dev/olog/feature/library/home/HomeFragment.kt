package dev.olog.feature.library.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.sample.HomeModelProvider
import dev.olog.feature.presentation.base.activity.BaseComposeFragment
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.components.CanareeToolbar
import dev.olog.shared.components.Header
import dev.olog.shared.components.StatusBar
import dev.olog.shared.components.item.ListItemAlbum
import dev.olog.shared.components.theme.CanareeTheme
import dev.olog.shared.exhaustive

@AndroidEntryPoint
class HomeFragment : BaseComposeFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                CanareeTheme {
                    val viewModel = viewModel<HomeFragmentViewModel>()
                    val items by viewModel.data
                        .collectAsState(initial = emptyList())
                    if (items.contains(HomeFragmentModel.Empty)) {
                        EmptyContent()
                    } else {
                        HomeContent(items)
                    }
                }
            }
        }
    }

}

@Preview
@Composable
private fun EmptyContentPreview() {
    CanareeTheme {
        EmptyContent()
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    CanareeTheme {
        HomeContent(HomeModelProvider.data)
    }
}

@Composable
private fun EmptyContent() {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column {
            StatusBar()
            CanareeToolbar("Home") // TODO use string resource and localiza
            Box(gravity = ContentGravity.Center) {
                Text(text = "Nothing here")
            }
        }
    }
}

@Composable
private fun HomeContent(items: List<HomeFragmentModel>) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column {
            StatusBar()
            CanareeToolbar("Home") // TODO use string resource and localiza
            LazyColumnFor(items = items) {
                when (it) {
                    is HomeFragmentModel.Header -> Header(it.title)
                    is HomeFragmentModel.RecentlyPlayedAlbums -> HorizontalList(it.items)
                    is HomeFragmentModel.RecentlyAddedAlbums -> HorizontalList(it.items)
                    is HomeFragmentModel.RecentlyPlayedArtists -> HorizontalList(it.items)
                    is HomeFragmentModel.RecentlyAddedArtists -> HorizontalList(it.items)
                    is HomeFragmentModel.Empty,
                    is HomeFragmentModel.Item -> throw IllegalArgumentException("invalid $it")
                }.exhaustive
            }
        }
    }
}

@Composable
private fun HorizontalList(items: List<HomeFragmentModel.Item>) {
    LazyRowFor(items = items) {
        Box(Modifier.preferredWidth(100.dp)) {
            ListItemAlbum(
                mediaId = it.mediaId.toDomain(),
                title = it.title,
                subtitle = it.subtitle
            )
        }
    }
}