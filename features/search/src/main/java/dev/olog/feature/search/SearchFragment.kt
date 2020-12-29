package dev.olog.feature.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.drag.DragListenerImpl
import dev.olog.feature.base.adapter.drag.IDragListener
import dev.olog.feature.base.restoreUpperWidgetsTranslation
import dev.olog.feature.search.adapter.SearchFragmentAdapter
import dev.olog.feature.search.adapter.SearchFragmentNestedAdapter
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.afterTextChange
import dev.olog.shared.android.extensions.hideIme
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.android.extensions.showIme
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    IDragListener by DragListenerImpl() {

    private val viewModel by viewModels<SearchFragmentViewModel>()

    private val adapter by lazyFast {
        SearchFragmentAdapter(
            setupNestedList = this::setupNestedList,
            mediaProvider = requireActivity().mediaProvider,
            navigator = navigator,
            viewModel = viewModel
        )
    }
    private val albumAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            navigator = navigator,
            viewModel = viewModel
        )
    }
    private val artistAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            navigator = navigator,
            viewModel = viewModel
        )
    }
    private val genreAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            navigator = navigator,
            viewModel = viewModel
        )
    }
    private val playlistAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            navigator = navigator,
            viewModel = viewModel
        )
    }

    private val folderAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            navigator = navigator,
            viewModel = viewModel
        )
    }
    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    @Inject
    lateinit var navigator: Navigator
    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

        setupDragListener(list, ItemTouchHelper.LEFT)

        viewModel.observeData()
            .onEach {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
                restoreUpperWidgetsTranslation()
            }.launchIn(this)

        viewModel.observeAlbumsData()
            .onEach(albumAdapter::submitList)
            .launchIn(this)

        viewModel.observeArtistsData()
            .onEach(artistAdapter::submitList)
            .launchIn(this)

        viewModel.observePlaylistsData()
            .onEach(playlistAdapter::submitList)
            .launchIn(this)

        viewModel.observeFoldersData()
            .onEach(folderAdapter::submitList)
            .launchIn(this)

        viewModel.observeGenresData()
            .onEach(genreAdapter::submitList)
            .launchIn(this)

        editText.afterTextChange()
            .debounce(200)
            .filter { it.isBlank() || it.trim().length >= 2 }
            .onEach { viewModel.updateQuery(it) }
            .launchIn(this)
    }


    private fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_search_list_albums -> setupHorizontalList(recyclerView, albumAdapter)
            R.layout.item_search_list_artists -> setupHorizontalList(recyclerView, artistAdapter)
            R.layout.item_search_list_folders -> setupHorizontalList(recyclerView, folderAdapter)
            R.layout.item_search_list_playlists -> setupHorizontalList(
                recyclerView,
                playlistAdapter
            )
            R.layout.item_search_list_genres -> setupHorizontalList(recyclerView, genreAdapter)
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
        more.setOnClickListener {
//            navigator.toMainPopup(it, null) // TODO create search popup
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        fab.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        more.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission() {
//        FloatingWindowHelper.startServiceOrRequestOverlayPermission(requireActivity()) TODO
    }


    override fun onStop() {
        super.onStop()
        editText.hideIme()
    }

}