package dev.olog.presentation.library

import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.tab.TabCategory
import dev.olog.presentation.tab.TabFolderContent
import dev.olog.presentation.tab.TabScreen
import dev.olog.presentation.tab.asString
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.shared.compose.ComposeAndroidView
import dev.olog.shared.compose.component.EmptyState
import dev.olog.shared.compose.component.Fab
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.component.Tab
import dev.olog.shared.compose.component.TabRow
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.component.Toolbar
import dev.olog.shared.compose.screen.Screen
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    libraryPage: LibraryPage,
    onLibraryPageChange: (LibraryPage) -> Unit,
    onFloatingWindowClick: (View) -> Unit,
    onMoreClick: (View, TabCategory) -> Unit,
    onFabClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onPlayableClick: (MediaId) -> Unit,
    onAlbumClick: (MediaId) -> Unit,
    toDialog: (MediaId) -> Unit,
) {
    val viewModel = viewModel<LibraryFragmentViewModel>()
    val state by viewModel.stateFlow(libraryPage).collectAsState(null)

    LibraryContent(
        state = state ?: return,
        onLibraryPageChange = {
            viewModel.setLibraryPage(it)
            onLibraryPageChange(it)
        },
        onPageChange = { page ->
            viewModel.setViewPagerLastPage(page, libraryPage)
        },
        showFloatingWindowTutorial = { view ->
            if (viewModel.showFloatingWindowTutorialIfNeverShown()) {
                TutorialTapTarget.floatingWindow(view)
            }
        },
        onFloatingWindowClick = onFloatingWindowClick,
        onMoreClick = onMoreClick,
        onFabClick = onFabClick,
        onShuffleClick = onShuffleClick,
        onPlayableClick = onPlayableClick,
        onAlbumClick = onAlbumClick,
        toDialog = toDialog
    )
}

@Composable
private fun LibraryContent(
    state: LibraryScreenState,
    onLibraryPageChange: (LibraryPage) -> Unit,
    onPageChange: (Int) -> Unit,
    showFloatingWindowTutorial: (View) -> Unit,
    onFloatingWindowClick: (View) -> Unit,
    onMoreClick: (View, TabCategory) -> Unit,
    onFabClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onPlayableClick: (MediaId) -> Unit,
    onAlbumClick: (MediaId) -> Unit,
    toDialog: (MediaId) -> Unit,
) {
    val pagerState = rememberPagerState(state.initialTab) { state.tabs.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { onPageChange(it) }
    }

    Screen(
        toolbarContent = {
            LibraryToolbar(
                libraryPage = state.libraryPage,
                showPodcast = state.showPodcast,
                onLibraryPageChange = onLibraryPageChange,
                showFloatingWindowTutorial = showFloatingWindowTutorial,
                onFloatingWindowClick = onFloatingWindowClick,
                onMoreClick = { view ->
                    state.tabs.getOrNull(pagerState.currentPage)?.let { category ->
                        onMoreClick(view, category)
                    }
                },
            )
            Tabs(
                pagerState = pagerState,
                tabs = state.tabs,
            )
        },
        fabContent = { contentPadding ->
            CreatePlaylistFab(
                pagerState = pagerState,
                tabs = state.tabs,
                contentPadding = contentPadding,
                onClick = onFabClick,
            )
        }
    ) { contentPadding ->
        Box {
            Pager(
                tabs = state.tabs,
                pagerState = pagerState,
                contentPadding = contentPadding,
                onShuffleClick = onShuffleClick,
                onPlayableClick = onPlayableClick,
                onAlbumClick = onAlbumClick,
                toDialog = toDialog,
            )

            if (state.tabs.isEmpty()) {
                EmptyState(stringResource(R.string.category_no_tab_visible))
            }
        }
    }
}

@Composable
private fun Pager(
    tabs: List<TabCategory>,
    pagerState: PagerState,
    contentPadding: PaddingValues,
    onShuffleClick: () -> Unit,
    onPlayableClick: (MediaId) -> Unit,
    onAlbumClick: (MediaId) -> Unit,
    toDialog: (MediaId) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 1,
    ) { index ->
        if (LocalInspectionMode.current) {
            return@HorizontalPager
        }
        val category = tabs[index]
        if (category == TabCategory.FOLDERS) {
            TabFolderContent(
                contentPadding = contentPadding,
                onShuffleClick = onShuffleClick,
                onPlayableClick = onPlayableClick,
                onAlbumClick = onAlbumClick,
                toDialog = toDialog,
            )
        } else {
            TabScreen(
                category = category,
                contentPadding = contentPadding,
                onShuffleClick = onShuffleClick,
                onPlayableClick = onPlayableClick,
                onAlbumClick = onAlbumClick,
                toDialog = toDialog,
            )
        }
    }
}

@Composable
private fun CreatePlaylistFab(
    pagerState: PagerState,
    tabs: List<TabCategory>,
    contentPadding: PaddingValues,
    onClick: () -> Unit,
) {
    val category = tabs.getOrNull(pagerState.currentPage)
    val show = category == TabCategory.PLAYLISTS ||
        category == TabCategory.PODCASTS_PLAYLIST
    AnimatedVisibility(
        visible = show,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Fab(
            drawableRes = R.drawable.vd_add,
            modifier = Modifier.padding(contentPadding),
            onClick = onClick,
        )
    }
}

@Composable
private fun LibraryToolbar(
    libraryPage: LibraryPage,
    showPodcast: Boolean,
    onLibraryPageChange: (LibraryPage) -> Unit,
    showFloatingWindowTutorial: (View) -> Unit,
    onFloatingWindowClick: (View) -> Unit,
    onMoreClick: (View) -> Unit,
) {
    Toolbar(
        title = {
            Text(
                text = stringResource(R.string.common_tracks),
                color = Theme.colors.textColor(libraryPage == LibraryPage.TRACKS).enabled,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onLibraryPageChange(LibraryPage.TRACKS) },
                ),
            )
            if (showPodcast) {
                Text(
                    text = stringResource(R.string.common_podcasts),
                    color = Theme.colors.textColor(libraryPage == LibraryPage.PODCASTS).enabled,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onLibraryPageChange(LibraryPage.PODCASTS) }
                    ),
                )
            }
        },
        icons = {
            ComposeAndroidView {
                LaunchedEffect(Unit) {
                    showFloatingWindowTutorial(it)
                }
                IconButton(
                    drawableRes = R.drawable.vd_search_text,
                    onClick = { onFloatingWindowClick(it) }
                )
            }
            ComposeAndroidView {
                IconButton(
                    drawableRes = R.drawable.vd_more,
                    onClick = { onMoreClick(it) }
                )
            }
        }
    )
}

@Composable
private fun Tabs(
    tabs: List<TabCategory>,
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()
    TabRow(pagerState) {
        for ((index, item) in tabs.withIndex()) {
            Tab(
                selected = index == pagerState.currentPage,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                content = { Text(text = item.asString()) }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            LibraryContent(
                state = LibraryScreenState(
                    libraryPage = LibraryPage.TRACKS,
                    initialTab = 1,
                    tabs = TabCategory.entries,
                    showPodcast = true,
                ),
                onPageChange = {},
                onLibraryPageChange = {},
                showFloatingWindowTutorial = {},
                onFloatingWindowClick = {},
                onMoreClick = { _, _ -> },
                onFabClick = {},
                onShuffleClick = {},
                onPlayableClick = {},
                onAlbumClick = {},
                toDialog = {},
            )
        }
    }
}