package dev.olog.presentation.detail


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.restoreUpperWidgetsTranslation
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentDetailBinding
import dev.olog.presentation.detail.adapter.DetailFragmentAdapter
import dev.olog.presentation.interfaces.CanChangeStatusBarColor
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.afterTextChange
import dev.olog.shared.android.extensions.colorControlNormal
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.extensions.isTablet
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail),
    CanChangeStatusBarColor,
    IDragListener by DragListenerImpl() {

    companion object {
        @JvmStatic
        val TAG = DetailFragment::class.java.name
        @JvmStatic
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): DetailFragment {
            return DetailFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator

    private val viewModel by viewModels<DetailFragmentViewModel>()

    private val mediaId by lazyFast {
        val mediaId = getArgument<String>(ARGUMENTS_MEDIA_ID)
        MediaId.fromString(mediaId)
    }

    private val mediaProvider: MediaProvider
        get() = requireActivity().findInContext()

    private val adapter by lazyFast {
        DetailFragmentAdapter(
            onShuffleClick = { mediaProvider.shuffle(it, viewModel.getFilter()) },
            onSongClick = { mediaId ->
                viewModel.detailSortDataUseCase(mediaId) {
                    mediaProvider.playFromMediaId(mediaId, viewModel.getFilter(), it)
                }
            },
            onMostPlayedClick = { mediaProvider.playMostPlayed(it) },
            onRecentlyAddedClick = { mediaProvider.playRecentlyAdded(it) },
            onAlbumClick = { navigator.toDetailFragment(it) },
            onLongClick = { mediaId, view ->
                navigator.toDialog(mediaId, view)
            },
            goToRelatedArtists = { navigator.toRelatedArtists(it) },
            goToRecentlyAdded = { navigator.toRecentlyAdded(it) },
            onAddToPlayNext = { mediaProvider.addToPlayNext(it) },
            onAddMove = { from, to -> viewModel.addMove(from, to) },
            onProcessMove = { viewModel.processMove() },
            onRemoveFromPlaylist = { mediaId, idInPlaylist ->
                viewModel.removeFromPlaylist(mediaId, idInPlaylist)
            },
            onUpdateSort = viewModel::updateSortOrder, // TODO sort is not working anymore on mediastore side
            toggleSortDirection = viewModel::toggleSortArranging,
            onStartDrag = ::onStartDrag
        )
    }

    private val recyclerOnScrollListener by lazyFast {
        HeaderVisibilityScrollListener(
            fragment = this,
            binding = binding,
        )
    }

    internal var hasLightStatusBarColor by Delegates.observable(false) { _, old, new ->
        if (old != new){
            adjustStatusBarColor(new)
        }
    }

    private val binding by viewBinding(FragmentDetailBinding::bind) {
        it.list.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        var swipeDirections = ItemTouchHelper.LEFT
        val canSwipeRight = if (mediaId.isAnyPlaylist) {
            val playlistId = mediaId.resolveId
            playlistId != AutoPlaylist.LAST_ADDED.id || !AutoPlaylist.isAutoPlaylist(playlistId)
        } else {
            false
        }
        if (canSwipeRight) {
            swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        }
        setupDragListener(viewLifecycleScope, binding.list, swipeDirections)

        viewModel.observeData()
            .subscribe(viewLifecycleOwner) { list ->
                if (list.isEmpty()) {
                    act.onBackPressed()
                } else {
                    adapter.submitList(list)
                    restoreUpperWidgetsTranslation()
                }
            }

        viewLifecycleScope.launch {
            binding.editText.afterTextChange()
                .debounce(200)
                .filter { it.isEmpty() || it.length >= 2 }
                .collect {
                    viewModel.updateFilter(it)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.list.addOnScrollListener(recyclerOnScrollListener)
        binding.back.setOnClickListener { act.onBackPressed() }
        binding.more.setOnClickListener { navigator.toDialog(viewModel.parentMediaId, binding.more) }
        binding.filter.setOnClickListener {
            binding.searchWrapper.toggleVisibility(!binding.searchWrapper.isVisible, true)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.list.removeOnScrollListener(recyclerOnScrollListener)
        binding.back.setOnClickListener(null)
        binding.more.setOnClickListener(null)
        binding.filter.setOnClickListener(null)
    }

    override fun adjustStatusBarColor() {
        adjustStatusBarColor(hasLightStatusBarColor)
    }

    override fun adjustStatusBarColor(lightStatusBar: Boolean) {
        if (lightStatusBar) {
            setLightStatusBar()
        } else {
            removeLightStatusBar()
        }
    }

    private fun removeLightStatusBar() {
        val color = Color.WHITE
        binding.back.setColorFilter(color)
        binding.more.setColorFilter(color)
        binding.filter.setColorFilter(color)

        if (requireContext().isTablet){
            return
        }
        act.window.removeLightStatusBar()
    }

    private fun setLightStatusBar() {
        if (requireContext().isDarkMode()) {
            return
        }
        val color = requireContext().colorControlNormal()
        binding.back.setColorFilter(color)
        binding.more.setColorFilter(color)
        binding.filter.setColorFilter(color)

        if (requireContext().isTablet){
            return
        }

        act.window.setLightStatusBar()
    }

}
