package dev.olog.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.media.MediaProvider
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.interfaces.HasBottomNavigation
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.toMediaIdCategory
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.requireArgument
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.compose.ComposeView
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    companion object {
        val TAG_TRACK = LibraryFragment::class.java.name
        val TAG_PODCAST = LibraryFragment::class.java.name + ".podcast"
        private const val PAGE = "page"

        fun newInstance(page: LibraryPage): LibraryFragment {
            return LibraryFragment().withArguments(
                PAGE to page
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator

    private val libraryPage by requireArgument<LibraryPage>(PAGE)
    private val viewModel by viewModels<LibraryFragmentViewModel>()
    private val mediaProvider: MediaProvider
        get() = requireActivity().findInContext<MediaProvider>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(this) {
            LibraryScreen(
                libraryPage = libraryPage,
                onLibraryPageChange = { newPage ->
                    if (libraryPage != newPage) {
                        // TODO find an alternative
                        (requireActivity().findInContext<HasBottomNavigation>())
                            .navigate(BottomNavigationPage.LIBRARY)
                    }
                },
                onFloatingWindowClick = {
                    FloatingWindowHelper.startServiceOrRequestOverlayPermission(requireActivity())
                },
                onMoreClick = { view, category ->
                    navigator.toMainPopup(view, category.toMediaIdCategory())
                },
                onFabClick = {
                    // TODO needed another type?
                    val type = when (libraryPage) {
                        LibraryPage.TRACKS -> PlaylistType.TRACK
                        LibraryPage.PODCASTS -> PlaylistType.PODCAST
                    }
                    navigator.toChooseTracksForPlaylistFragment(type)
                },
                onShuffleClick = {
                    mediaProvider.shuffle(MediaId.shuffleId(), null)
                },
                onPlayableClick = { mediaId ->
                    val sort = viewModel.getAllTracksSortOrder(mediaId)
                    mediaProvider.playFromMediaId(mediaId, null, sort)
                },
                onAlbumClick = { mediaId ->
                    navigator.toDetailFragment(mediaId)
                },
                toDialog = { mediaId ->
                    navigator.toDialog(mediaId, requireView())
                },
            )
        }
    }

}