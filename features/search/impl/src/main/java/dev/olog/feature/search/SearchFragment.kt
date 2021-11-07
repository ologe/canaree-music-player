package dev.olog.feature.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.base.SetupNestedList
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.drag.DragListenerImpl
import dev.olog.feature.base.drag.IDragListener
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.dialogs.FeatureDialogsNavigator
import dev.olog.feature.floating.FeatureFloatingNavigator
import dev.olog.feature.search.adapter.SearchFragmentAdapter
import dev.olog.feature.search.adapter.SearchFragmentNestedAdapter
import dev.olog.media.mediaProvider
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.extension.hideIme
import dev.olog.shared.widgets.extension.showIme
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

@AndroidEntryPoint
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
    lateinit var detailNavigator: FeatureDetailNavigator
    @Inject
    lateinit var dialogNavigator: FeatureDialogsNavigator
    @Inject
    lateinit var floatingNavigator: FeatureFloatingNavigator

    private val viewModel by viewModels<SearchFragmentViewModel>()

    private val adapter by lazyFast {
        SearchFragmentAdapter(
            lifecycle = lifecycle,
            setupNestedList = this,
            mediaProvider = mediaProvider,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
            viewModel = viewModel
        )
    }
    private val albumAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            lifecycle = lifecycle,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
            viewModel = viewModel
        )
    }
    private val artistAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            lifecycle = lifecycle,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
            viewModel = viewModel
        )
    }
    private val genreAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            lifecycle = lifecycle,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
            viewModel = viewModel
        )
    }
    private val playlistAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            lifecycle = lifecycle,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
            viewModel = viewModel
        )
    }

    private val folderAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            lifecycle = lifecycle,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
            viewModel = viewModel
        )
    }

    private fun onItemClick(mediaId: MediaId) {
        detailNavigator.toDetailFragment(requireActivity(), mediaId)
    }

    private fun onItemLongClick(mediaId: MediaId, view: View) {
        dialogNavigator.toDialog(requireActivity(), mediaId, view)
    }

    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

        setupDragListener(list, ItemTouchHelper.LEFT)

        viewModel.observeData()
            .subscribe(viewLifecycleOwner) {
                adapter.updateDataSet(it)
                emptyStateText.toggleVisibility(it.isEmpty(), true)
                restoreUpperWidgets()
            }

        viewModel.observeAlbumsData()
            .subscribe(viewLifecycleOwner, albumAdapter::updateDataSet)

        viewModel.observeArtistsData()
            .subscribe(viewLifecycleOwner, artistAdapter::updateDataSet)

        viewModel.observePlaylistsData()
            .subscribe(viewLifecycleOwner, playlistAdapter::updateDataSet)

        viewModel.observeFoldersData()
            .subscribe(viewLifecycleOwner, folderAdapter::updateDataSet)

        viewModel.observeGenresData()
            .subscribe(viewLifecycleOwner, genreAdapter::updateDataSet)

        editText.afterTextChangeFlow()
            .debounce(200)
            .filter { it.isBlank() || it.trim().length >= 2 }
            .collectOnLifecycle(this) { viewModel.updateQuery(it) }
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
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        fab.setOnClickListener { editText.showIme() }

        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
        more.setOnClickListener { dialogNavigator.toMainPopup(requireActivity(), it, null) }
    }

    override fun onPause() {
        super.onPause()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        fab.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        more.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    private fun startServiceOrRequestOverlayPermission() {
        floatingNavigator.startService(requireActivity())
    }


    override fun onStop() {
        super.onStop()
        editText.hideIme()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}