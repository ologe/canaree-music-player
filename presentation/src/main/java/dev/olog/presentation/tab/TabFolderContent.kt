package dev.olog.presentation.tab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.presentation.databinding.FragmentFolderTreeContainerBinding
import dev.olog.presentation.folder.tree.FolderTreeFragment
import dev.olog.presentation.model.PresentationPreferencesGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Composable
fun TabFolderContent(
    contentPadding: PaddingValues,
    onShuffleClick: () -> Unit,
    onPlayableClick: (MediaId) -> Unit,
    onAlbumClick: (MediaId) -> Unit,
    toDialog: (MediaId) -> Unit,
) {
    val viewModel = viewModel<TabFolderContentViewModel>()
    val showAsHierarchy = viewModel.showFolderAsHierarchy().collectAsState(null).value ?: return

    if (showAsHierarchy) {
        // TODO not working correctly after scroll
        val layoutDirection = LocalLayoutDirection.current
        val density = LocalDensity.current
        AndroidViewBinding(FragmentFolderTreeContainerBinding::inflate) {
            // TODO fragment is null
            val fragment = folderTreeContainer.getFragment<FolderTreeFragment?>()
            with(density) {
                fragment?.updatePadding(
                    left = contentPadding.calculateLeftPadding(layoutDirection).roundToPx(),
                    top = contentPadding.calculateTopPadding().roundToPx(),
                    right = contentPadding.calculateRightPadding(layoutDirection).roundToPx(),
                    bottom = contentPadding.calculateBottomPadding().roundToPx(),
                )
            }
        }
    } else {
        TabScreen(
            category = TabCategory.FOLDERS,
            contentPadding = contentPadding,
            onShuffleClick = onShuffleClick,
            onPlayableClick = onPlayableClick,
            onAlbumClick = onAlbumClick,
            toDialog = toDialog,
        )
    }
}

@HiltViewModel
class TabFolderContentViewModel @Inject constructor(
    private val prefs: PresentationPreferencesGateway,
) : ViewModel() {

    fun showFolderAsHierarchy(): Flow<Boolean> {
        return prefs.showFolderAsHierarchy()
    }

}