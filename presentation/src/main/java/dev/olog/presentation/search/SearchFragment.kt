package dev.olog.presentation.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.lib.media.MediaProvider
import dev.olog.feature.presentation.base.DottedDividerDecorator
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.adapter.drag.DragListenerImpl
import dev.olog.feature.presentation.base.adapter.drag.IDragListener
import dev.olog.feature.presentation.base.SetupNestedList
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.popup.main.MainPopupCategory
import dev.olog.presentation.search.adapter.SearchFragmentAdapter
import dev.olog.presentation.search.adapter.SearchFragmentNestedAdapter
import dev.olog.feature.presentation.base.utils.hideIme
import dev.olog.feature.presentation.base.utils.showIme
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.feature.presentation.base.extensions.afterTextChange
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class SearchFragment : BaseFragment(),
    SetupNestedList,
    IDragListener by DragListenerImpl() {

    companion object {
        @JvmStatic
        val TAG = SearchFragment::class.java.name

        @JvmStatic
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<SearchFragmentViewModel> {
        viewModelFactory
    }

    private val adapter by lazyFast {
        SearchFragmentAdapter(this, requireActivity() as MediaProvider, navigator, viewModel)
    }
    private val albumAdapter by lazyFast {
        SearchFragmentNestedAdapter(navigator, viewModel)
    }
    private val artistAdapter by lazyFast {
        SearchFragmentNestedAdapter(navigator, viewModel)
    }
    private val genreAdapter by lazyFast {
        SearchFragmentNestedAdapter(navigator, viewModel)
    }
    private val playlistAdapter by lazyFast {
        SearchFragmentNestedAdapter(navigator, viewModel)
    }

    private val folderAdapter by lazyFast {
        SearchFragmentNestedAdapter(navigator, viewModel)
    }
    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    @Inject
    internal lateinit var navigator: Navigator
    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)
        list.addItemDecoration(
            DottedDividerDecorator(
                requireContext(),
                listOf(R.layout.item_search_header, R.layout.item_search_recent_header)
            )
        )

        setupDragListener(list, ItemTouchHelper.LEFT)

        podcasts.isVisible = viewModel.canShowPodcasts()
        podcasts.isSelected = false

        viewModel.data
            .onEach {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
                restoreUpperWidgetsTranslation()
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.albumsData
            .onEach { albumAdapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.artistsData
            .onEach { artistAdapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.playlistsData
            .onEach { playlistAdapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.foldersData
            .onEach { folderAdapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.genresData
            .onEach { genreAdapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        editText.afterTextChange()
            .debounce(200)
            .filter { it.isBlank() || it.trim().length >= 2 }
            .onEach { viewModel.updateQuery(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }


    override fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_search_list_albums -> setupHorizontalList(recyclerView, albumAdapter)
            R.layout.item_search_list_artists -> setupHorizontalList(recyclerView, artistAdapter)
            R.layout.item_search_list_folder -> setupHorizontalList(recyclerView, folderAdapter)
            R.layout.item_search_list_playlists -> setupHorizontalList(
                recyclerView,
                playlistAdapter
            )
            R.layout.item_search_list_genre -> setupHorizontalList(recyclerView, genreAdapter)
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: ObservableAdapter<*>) {
        val layoutManager = LinearLayoutManager(
            list.context,
            LinearLayoutManager.HORIZONTAL, false
        )
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

        val snapHelper = androidx.recyclerview.widget.LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        fab.setOnClickListener { editText.showIme() }

        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
        more.setOnClickListener { navigator.toMainPopup(it, MainPopupCategory.SEARCH) }

        podcasts.setOnClickListener {
            podcasts.isSelected = !podcasts.isSelected
            viewModel.updateShowPodcast(podcasts.isSelected)
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        fab.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        more.setOnClickListener(null)
        podcasts.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
        disposeDragListener()
    }

    override fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(requireActivity())
    }


    override fun onStop() {
        super.onStop()
        editText.hideIme()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}