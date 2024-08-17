package dev.olog.presentation.detail.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.detail.adapter.DetailFragmentAdapter
import dev.olog.presentation.detail.adapter.DetailItem
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.ThemePreviews

@ThemePreviews
@Composable
private fun FullPreview() {
    CanareeTheme {
        val list = buildList {
            this += DetailPreviewData.detailHeader()
            this += DetailPreviewData.mostPlayedHeader()
            this += DetailPreviewData.mostPlayedList(itemCount = 2)
            this += DetailPreviewData.recentlyAddedHeader()
            this += DetailPreviewData.recentlyAddedList(itemCount = 3)
            this += DetailPreviewData.songHeader()
            this += DetailPreviewData.shuffle()
            repeat(3) {
                this += DetailPreviewData.song()
            }
            this += DetailPreviewData.songFooter()
            this += DetailPreviewData.relatedArtistsHeader()
            this += DetailPreviewData.relatedArtistsList()
            this += DetailPreviewData.siblingsHeader()
            this += DetailPreviewData.siblingsList()
        }
        DetailList(list)
    }
}

@ThemePreviews
@Composable
private fun SongsPreview() { // TODO fix preview
    CanareeTheme {
        val list = buildList {
            this += DetailPreviewData.detailHeader()
            this += DetailPreviewData.songHeader()
            this += DetailPreviewData.shuffle()
            repeat(3) {
                this += DetailPreviewData.song()
            }
            this += DetailPreviewData.songFooter()
        }
        DetailList(list)
    }
}

@ThemePreviews
@Composable
private fun RelatedArtistsPreview() {
    CanareeTheme {
        val list = buildList {
            this += DetailPreviewData.detailHeader()
            this += DetailPreviewData.relatedArtistsHeader()
            this += DetailPreviewData.relatedArtistsList()
        }
        DetailList(list)
    }
}

@ThemePreviews
@Composable
private fun SiblingsPreview() {
    CanareeTheme {
        val list = buildList {
            this += DetailPreviewData.detailHeader()
            this += DetailPreviewData.siblingsHeader()
            this += DetailPreviewData.siblingsList()
        }
        DetailList(list)
    }
}

@ThemePreviews
@Composable
private fun MostPlayedPreview() {
    CanareeTheme {
        val list = buildList {
            this += DetailPreviewData.detailHeader()
            this += DetailPreviewData.mostPlayedHeader()
            this += DetailPreviewData.mostPlayedList()
        }
        DetailList(list)
    }
}

@ThemePreviews
@Composable
private fun RecentlyAddedPreview() {
    CanareeTheme {
        val list = buildList {
            this += DetailPreviewData.detailHeader()
            this += DetailPreviewData.recentlyAddedHeader()
            this += DetailPreviewData.recentlyAddedList()
        }
        DetailList(list)
    }
}

@Composable
private fun DetailList(list: List<DetailItem>) {
    AndroidView(
        factory = { RecyclerView(it) },
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.background),
        update = {
            it.layoutManager = LinearLayoutManager(it.context)
            val adapter = DetailFragmentAdapter(
                onShuffleClick = {},
                onSongClick = {},
                onMostPlayedClick = {},
                onRecentlyAddedClick = {},
                onAlbumClick = {},
                onLongClick = { _, _ ->},
                goToRelatedArtists = {},
                goToRecentlyAdded = {},
                onAddToPlayNext = {},
                onAddMove = { _, _ ->},
                onProcessMove = {},
                onRemoveFromPlaylist = { _, _ -> },
                onUpdateSort = {},
                toggleSortDirection = {},
                onStartDrag = {}
            )
            it.adapter = adapter
            adapter.submitList(list)
        }
    )
}