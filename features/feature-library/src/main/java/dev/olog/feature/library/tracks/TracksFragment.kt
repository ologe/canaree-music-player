package dev.olog.feature.library.tracks

import android.os.Bundle
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.olog.domain.entity.sort.SortType
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.extensions.dipf
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.widget.fastscroller.WaveSideBarView
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.TextUtils
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_tracks.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class TracksFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TracksFragmentViewModel> {
        viewModelFactory
    }

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        TracksFragmentAdapter(requireActivity() as MediaProvider, navigator, viewModel)
    }
    private lateinit var layoutManager: LinearLayoutManager

    private val maxElevation by lazyFast { requireContext().dipf(1) }
    private val scrollListener = TotalScrollListener { totalScroll ->
        val totalHeight = toolbar.height
        if (totalScroll > totalHeight) {
            statusBar.elevation = maxElevation
            toolbar.elevation = maxElevation
        } else {
            statusBar.elevation = 0f
            toolbar.elevation = 0f
        }
    }

    // TODO podcast
    private val isPodcast = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scrollableLayoutId = if (isPodcast) {
            R.layout.item_track_podcast
        } else {
            R.layout.item_track
        }
        sidebar.scrollableLayoutId = scrollableLayoutId

        layoutManager = OverScrollLinearLayoutManager(requireContext())
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setHasFixedSize(true)

        viewModel.data(isPodcast)
            .onEach { data ->
                adapter.submitList(data) {
                    emptyStateText.isVisible = data.isEmpty()
                }
                sidebar.onDataChanged(data)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        observePodcastPositions()

    }

    override fun onResume() {
        super.onResume()
        sidebar.setListener(letterTouchListener)
        list.addOnScrollListener(scrollListener)
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        list.removeOnScrollListener(scrollListener)
    }

    private fun observePodcastPositions() {
        if (!isPodcast) {
            return
        }
        viewModel.observeAllCurrentPositions()
            .onEach { adapter.updatePodcastPositions(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
    }

    // TODO improvde, maybe cache values
    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
        list.stopScroll()

        val scrollableItem = sidebar.scrollableLayoutId

        val position = when (letter) {
            TextUtils.MIDDLE_DOT -> -1
            "#" -> adapter.indexOf {
                if (it.type != scrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].toUpperCase().toString().isDigitsOnly()
                }
            }
            "?" -> adapter.indexOf {
                if (it.type != scrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].toUpperCase().toString() > "Z"
                }
            }
            else -> adapter.indexOf {
                if (it.type != scrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].toUpperCase().toString() == letter
                }
            }
        }
        if (position != -1) {
            val layoutManager = list.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    private fun getCurrentSorting(item: DisplayableItem): String {
        require(item is DisplayableTrack)
        val sortOrder = viewModel.getAllTracksSortOrder()
        return when (sortOrder.type) {
            SortType.ARTIST -> item.artist
            SortType.ALBUM -> item.album
            else -> item.title
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_tracks
}