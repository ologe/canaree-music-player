package dev.olog.feature.library.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import dev.olog.navigation.screens.LibraryPage
import dev.olog.shared.components.Background
import dev.olog.shared.components.SingleChoiceList
import dev.olog.shared.components.theme.CanareeTheme
import javax.inject.Inject

@AndroidEntryPoint
class LibraryChooserFragment : BottomSheetDialogFragment() {

    @Inject
    internal lateinit var prefs: CommonPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                CanareeTheme {
                    val viewModel = viewModel<LibraryChooserFragmentViewModel>()
                    Background(Modifier.fillMaxWidth()) {
                        LibraryChooserFragmentContent(viewModel.libraryPage) {
                            viewModel.libraryPage = it
                            dismiss()
                        }
                    }
                }
            }
        }
    }

}

@Composable
@Preview
private fun LibraryChooserFragmentContentPreview() {
    CanareeTheme {
        LibraryChooserFragmentContent(LibraryPage.ALBUMS)
    }
}

@Composable
private fun LibraryChooserFragmentContent(
    current: LibraryPage,
    dismiss: (LibraryPage) -> Unit = {}
) {
    SingleChoiceList(
        items = LibraryPage.values().toList(),
        selected = current,
        text = { it.textify() },
        onClick = dismiss
    )
}

@Composable
private fun LibraryPage.textify(): String = when (this) {
    LibraryPage.FOLDERS -> stringResource(R.string.common_folders)
    LibraryPage.TRACKS -> stringResource(R.string.common_tracks)
    LibraryPage.ALBUMS -> stringResource(R.string.common_albums)
    LibraryPage.ARTISTS -> stringResource(R.string.common_artists)
    LibraryPage.GENRES -> stringResource(R.string.common_genres)
//    LibraryPage.PODCASTS -> stringResource(R.string.common_podcasts) TODO
//    LibraryPage.PODCSATS_ARTISTS -> stringResource(R.string.common_podcast_artist)
}