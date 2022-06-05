package dev.olog.feature.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import dev.olog.compose.Background
import dev.olog.compose.CanareeFab
import dev.olog.compose.CanareeIconButton
import dev.olog.compose.CanareeIcons
import dev.olog.compose.CanareeToolbar
import dev.olog.compose.EmptyState
import dev.olog.compose.SearchBox
import dev.olog.compose.StatusBar
import dev.olog.compose.elevation
import dev.olog.core.MediaId
import dev.olog.feature.search.model.SearchState
import dev.olog.feature.search.widget.SearchList
import dev.olog.feature.search.widget.SearchRecentList
import dev.olog.shared.extension.exhaustive

@Composable
fun SearchContent(
    data: SearchState,
    query: String,
    onQueryChange: (String) -> Unit,
    onQueryClear: () -> Unit,
    onBubbleClick: () -> Unit,
    onMoreClick: () -> Unit,
    onPlayableClick: (MediaId) -> Unit,
    onItemLongClick: (MediaId) -> Unit,
    onNonPlayableClick: (MediaId) -> Unit,
    onClearItemClick: (MediaId) -> Unit,
    onClearAllClick: () -> Unit,
    onPlayNext: (MediaId) -> Unit,
    onDelete: (MediaId) -> Unit,
) {
    Background(
        modifier = Modifier.fillMaxSize(),
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (statusBar, toolbar, search, list, fab) = createRefs()

            // status bar
            StatusBar(
                Modifier.constrainAs(statusBar) { top.linkTo(parent.top) }
            )

            // toolbar
            CanareeToolbar(
                text = stringResource(id = localization.R.string.common_search),
                modifier = Modifier
                    .constrainAs(toolbar) { top.linkTo(statusBar.bottom) }
                    .zIndex(10f), // to avoid shadow from searchbox
                icons = {
                    CanareeIconButton(CanareeIcons.Bubble, onClick = onBubbleClick)
                    CanareeIconButton(CanareeIcons.MoreVert, onClick = onMoreClick)
                }
            )

            // search box
            SearchBox(
                value = query,
                hint = stringResource(id = localization.R.string.search_hint),
                onValueChange = onQueryChange,
                modifier = Modifier
                    .elevation(
                        elevation = dimensionResource(id = dev.olog.ui.R.dimen.toolbar_elevation),
                        color = MaterialTheme.colors.surface,
                    )
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
                    .constrainAs(search) { top.linkTo(toolbar.bottom) },
                onClearTextClick = onQueryClear,
            )

            // list
            LazyColumn(
                modifier = Modifier.constrainAs(list) { top.linkTo(search.bottom) },
            ) {
                when (data) {
                    is SearchState.Items -> SearchList(
                        data = data,
                        onPlayableClick = onPlayableClick,
                        onNonPlayableClick = onNonPlayableClick,
                        onItemLongClick = onItemLongClick,
                        onPlayNext = onPlayNext,
                        onDelete = onDelete,
                    )
                    is SearchState.Recents -> SearchRecentList(
                        data = data,
                        onPlayableClick = onPlayableClick,
                        onNonPlayableClick = onNonPlayableClick,
                        onItemLongClick = onItemLongClick,
                        onClearItemClick = onClearItemClick,
                        onClearAllClick = onClearAllClick,
                        onPlayNext = onPlayNext,
                    )
                    // todo move out of lazy column, position is bugged
                    is SearchState.NoRecents -> item {
                        EmptyState(
                            text = stringResource(localization.R.string.common_no_results), // todo improve text/design
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // todo move out of lazy column, position is bugged
                    is SearchState.NoResults -> item {
                        EmptyState(
                            text = stringResource(localization.R.string.common_no_results), // todo improve text/design
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }.exhaustive
            }

            val inputService = LocalTextInputService.current

            CanareeFab(
                imageVector = CanareeIcons.Keyboard,
                onClick = {
                    // todo not working if none textfield was focused
                    inputService?.showSoftwareKeyboard()
                },
                modifier = Modifier
                    .constrainAs(fab) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(16.dp)
            )
        }
    }
}