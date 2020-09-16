package dev.olog.feature.library.folder

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.ViewStream
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dev.olog.feature.library.R
import dev.olog.feature.library.sample.FolderModelProvider
import dev.olog.feature.library.sample.SpanCountProvider
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.components.*
import dev.olog.shared.components.item.ListItemAlbum
import dev.olog.shared.components.item.ListItemTrack
import dev.olog.shared.components.theme.CanareeTheme
import dev.olog.shared.exhaustive
import java.io.File

object FolderFragment {
    @Composable
    operator fun invoke() = FolderFragment()
}

@Composable
private fun FolderFragment() {
    val viewModel = viewModel<FolderFragmentViewModel>()
    val items by viewModel.data.collectAsState(initial = emptyList())
    val isFolderHierarchy by viewModel.isFolderHierarchyFlow.collectAsState(null)
    if (isFolderHierarchy == null) {
        return
    }

    if (isFolderHierarchy!!) {
        val file by viewModel.currentFolder.collectAsState(null)
        if (file == null) {
            return
        }
        FolderFragmentHierarchyContent(
            items = items,
            file = file!!,
            onHierarchyClick = viewModel::setIsFolderHierarchy,
            onCurrentDirChanged = viewModel::updateFolder
        )
    } else {
        val span by viewModel.observeSpanCount().collectAsState(null)
        if (span == null) {
            return
        }
        FolderFragmentContent(
            items = items,
            spanCount = span!!,
            onHierarchyClick = viewModel::setIsFolderHierarchy
        )
    }
}

// region hierarchy

@Preview(group = "hierarchy")
@Composable
private fun FolderFragmentContentHierarchyPreview() {
    CanareeTheme {
        FolderFragmentHierarchyContent(
            FolderModelProvider.hierarchy,
            File("storage${File.separator}emulated${File.separator}music")
        )
    }
}

@Composable
private fun FolderFragmentHierarchyContent(
    items: List<FolderFragmentModel>,
    file: File,
    onHierarchyClick: () -> Unit = {},
    onCurrentDirChanged: (File) -> Unit = {}
) {
    Background {
        Column {
            StatusBar()
            CanareeToolbar(stringResource(id = R.string.common_folders)) {
                HierarchyButtons(onHierarchyClick)
            }
            BreadCrumb(file, onClick = onCurrentDirChanged)
            LazyColumnFor(items = items) {
                ListContentHierarchy(it, onCurrentDirChanged)
            }
        }
    }
}

@Composable
private fun ListContentHierarchy(
    item: FolderFragmentModel,
    onCurrentDirChanged: (File) -> Unit
) {
    when (item) {
        is FolderFragmentModel.Header -> Header(item.title)
        is FolderFragmentModel.File -> ListItemTrack(
            mediaId = item.mediaId.toDomain(),
            title = item.title,
            subtitle = ""//item.subtitle
        )
        is FolderFragmentModel.Folder -> ListItemTrack(
            mediaId = item.mediaId.toDomain(),
            title = item.title,
            subtitle = "",
            onClick = { onCurrentDirChanged(item.file) }
        )
        is FolderFragmentModel.Album -> throw IllegalArgumentException("invalid $item")
    }.exhaustive
}

@Composable
private fun HierarchyButtons(
    onHierarchyClick: () -> Unit
) {
    IconButton(onClick = onHierarchyClick) {
        Icon(
            asset = vectorResource(R.drawable.vd_merge), // TODO change icon??
            tint = MaterialTheme.colors.secondary
        )
    }
    IconButton(onClick = {}) {
        Icon(asset = Icons.Rounded.MoreVert)
    }
}

// endregion

// region default

@Preview(group = "default")
@Composable
private fun FolderFragmentContentPreview(
    @PreviewParameter(SpanCountProvider::class) spanCount: Int
) {
    CanareeTheme {
        FolderFragmentContent(
            items = FolderModelProvider.default,
            spanCount = spanCount
        )
    }
}

@Composable
private fun FolderFragmentContent(
    items: List<FolderFragmentModel>,
    spanCount: Int,
    onHierarchyClick: () -> Unit = {},
) {
    Background {
        Column {
            StatusBar()
            CanareeToolbar(stringResource(id = R.string.common_folders)) {
                Buttons(onHierarchyClick)
            }
            GridList(list = items, spanCount = spanCount) {
                ListContent(it, spanCount)
            }
        }
    }
}

@Composable
private fun ListContent(
    item: FolderFragmentModel,
    spanCount: Int
) {
    when (item) {
        is FolderFragmentModel.Album -> {
            if (spanCount == 1) {
                ListItemTrack(
                    mediaId = item.mediaId.toDomain(),
                    title = item.title,
                    subtitle = item.subtitle
                )
            } else {
                ListItemAlbum(
                    mediaId = item.mediaId.toDomain(),
                    title = item.title,
                    subtitle = item.subtitle
                )
            }
        }
        is FolderFragmentModel.Header,
        is FolderFragmentModel.File,
        is FolderFragmentModel.Folder -> throw IllegalArgumentException("invalid $item")
    }.exhaustive
}

@Composable
private fun Buttons(
    onHierarchyClick: () -> Unit
) {
    IconButton(onClick = onHierarchyClick) {
        Icon(asset = vectorResource(R.drawable.vd_merge)) // TODO change icon??
    }
    IconButton(onClick = {}) {
        Icon(asset = Icons.Rounded.ViewStream)
    }
    IconButton(onClick = {}) {
        Icon(asset = Icons.Rounded.MoreVert)
    }
}

// endregion