package dev.olog.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import dev.olog.compose.CanareeIcons
import dev.olog.compose.components.CanareeBackground
import dev.olog.compose.components.CanareeEmptyState
import dev.olog.compose.components.CanareeFab
import dev.olog.compose.components.CanareeIconButton
import dev.olog.compose.components.CanareeSearchBox
import dev.olog.compose.components.CanareeToolbar
import dev.olog.compose.components.StatusBar
import dev.olog.compose.modifier.elevation
import dev.olog.core.MediaId
import dev.olog.feature.search.model.SearchState
import dev.olog.feature.search.widget.SearchList
import dev.olog.feature.search.widget.SearchRecentList
import dev.olog.shared.extension.exhaustive
import localization.R

@Composable
fun SearchContent(
    state: SearchState,
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
    CanareeBackground(
        modifier = Modifier.fillMaxSize(),
    ) {
        ConstraintLayout {
            val (
                statusBar,
                toolbar,
                search,
                list,
                fab,
                emptyState,
            ) = createRefs()

            // status bar
            StatusBar(
                Modifier.constrainAs(statusBar) { top.linkTo(parent.top) }
            )

            // toolbar
            CanareeToolbar(
                text = stringResource(id = R.string.common_search),
                modifier = Modifier
                    .constrainAs(toolbar) { top.linkTo(statusBar.bottom) }
                    .zIndex(10f), // to avoid shadow from searchbox
                icons = {
                    CanareeIconButton(CanareeIcons.Bubble, onClick = onBubbleClick)
                    CanareeIconButton(CanareeIcons.MoreVert, onClick = onMoreClick)
                }
            )

            val focusRequester = remember { FocusRequester() }

            // search box
            CanareeSearchBox(
                value = query,
                hint = stringResource(id = R.string.search_hint),
                onValueChange = onQueryChange,
                focusRequester = focusRequester,
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
                when (state) {
                    is SearchState.Items -> SearchList(
                        state = state,
                        onPlayableClick = onPlayableClick,
                        onNonPlayableClick = onNonPlayableClick,
                        onItemLongClick = onItemLongClick,
                        onPlayNext = onPlayNext,
                        onDelete = onDelete,
                    )
                    is SearchState.Recents -> SearchRecentList(
                        state = state,
                        onPlayableClick = onPlayableClick,
                        onNonPlayableClick = onNonPlayableClick,
                        onItemLongClick = onItemLongClick,
                        onClearItemClick = onClearItemClick,
                        onClearAllClick = onClearAllClick,
                        onPlayNext = onPlayNext,
                    )
                    is SearchState.NoRecents,
                    is SearchState.NoResults -> {
                    }
                }.exhaustive
            }

            val text = when (state) {
                is SearchState.Items -> ""
                is SearchState.Recents -> ""
                is SearchState.NoRecents -> stringResource(R.string.common_no_results) // todo improve text/design
                is SearchState.NoResults -> stringResource(R.string.common_no_results) // todo improve text/design
            }
            AnimatedVisibility(
                visible = text.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut(animationSpec = snap())
            ) {
                CanareeEmptyState(
                    text = text,
                    modifier = Modifier
                        .fillMaxSize()
                        .constrainAs(emptyState) {
                            centerVerticallyTo(list)
                        }
                )
            }

            val inputService = LocalTextInputService.current

            CanareeFab(
                imageVector = CanareeIcons.Keyboard,
                onClick = {
                    focusRequester.requestFocus()
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