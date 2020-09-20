package dev.olog.feature.library.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.album.AlbumFragment
import dev.olog.feature.library.artist.ArtistFragment
import dev.olog.feature.library.folder.FolderFragment
import dev.olog.feature.library.track.TrackFragment
import dev.olog.feature.presentation.base.activity.BaseComposeFragment
import dev.olog.navigation.screens.LibraryPage
import dev.olog.shared.components.theme.CanareeTheme

@AndroidEntryPoint
internal class LibraryFragment : BaseComposeFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                CanareeTheme {
                    LibraryFragmentContent()
                }
            }
        }
    }

}

@Composable
private fun LibraryFragmentContent() {
    val viewModel = viewModel<LibraryChooserFragmentViewModel>()
    val page by viewModel.libraryPageFlow.collectAsState(null)
    Stack {
        Crossfade(current = page) {
            when (it) {
                LibraryPage.FOLDERS -> FolderFragment()
                LibraryPage.TRACKS -> TrackFragment()
                LibraryPage.ALBUMS -> AlbumFragment()
                LibraryPage.ARTISTS -> ArtistFragment()
                LibraryPage.GENRES -> Surface {

                }
                null -> {
                    Stack(Modifier.fillMaxSize()) {
                        // TODO bad background
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = viewModel::toChooseLibrary
        ) {
            Icon(asset = Icons.Rounded.Category)
        }
    }
}