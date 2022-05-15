package dev.olog.feature.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.feature.media.api.MediaProvider
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.SetupNestedList
import dev.olog.platform.adapter.drag.DragListenerImpl
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.fragment.BaseFragment
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.afterTextChange
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.subscribe
import dev.olog.shared.hideIme
import dev.olog.shared.showIme
import dev.olog.ui.adapter.drag.CircularRevealAnimationController
import dev.olog.ui.model.DisplayableTrack
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment(),
    SetupNestedList,
    IDragListener by DragListenerImpl() {

    companion object {
        val TAG = FragmentTagFactory.create(SearchFragment::class)

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private val mediaProvider: MediaProvider
        get() = requireActivity().findInContext()

    private val viewModel by viewModels<SearchFragmentViewModel>()

    private val adapter by lazyFast {
        SearchFragmentAdapter(
            setupNestedList = this,
            onItemClick = {
                mediaProvider.playFromMediaId(it, null, null)
                viewModel.insertToRecent(it)
            },
            onRecentItemClick = { item ->
                if (item is DisplayableTrack) {
                    mediaProvider.playFromMediaId(item.mediaId, null, null)
                } else {
                    featureDetailNavigator.toDetail(requireActivity(), item.mediaId)
                }
            },
            onItemLongClick = { view, mediaId ->
                featureMainPopupNavigator.toItemDialog(view, mediaId)
            },
            onSwipeLeft = { mediaProvider.addToPlayNext(it) },
            onClearRecentItemClick = { viewModel.deleteFromRecent(it) },
            onClearAllRecentsClick = { viewModel.clearRecentSearches() },
        )
    }
    private val albumAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedItemLongClick,
        )
    }
    private val artistAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedItemLongClick,
        )
    }
    private val genreAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedItemLongClick,
        )
    }
    private val playlistAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedItemLongClick,
        )
    }

    private val folderAdapter by lazyFast {
        SearchFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedItemLongClick,
        )
    }

    private fun onNestedItemClick(mediaId: MediaId) {
        featureDetailNavigator.toDetail(requireActivity(), mediaId)
        viewModel.insertToRecent(mediaId)
    }

    private fun onNestedItemLongClick(view: View, mediaId: MediaId) {
        featureMainPopupNavigator.toItemDialog(view, mediaId)
    }

    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    @Inject
    lateinit var featureDetailNavigator: FeatureDetailNavigator
    @Inject
    lateinit var featureBubbleNavigator: FeatureBubbleNavigator
    @Inject
    lateinit var featureMainNavigator: FeatureMainNavigator
    @Inject
    lateinit var featureMainPopupNavigator: FeatureMainPopupNavigator

    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

        setupDragListener(
            scope = viewLifecycleOwner.lifecycleScope,
            list = list,
            direction = ItemTouchHelper.LEFT,
            animation = CircularRevealAnimationController(),
        )

        viewModel.observeData()
            .subscribe(viewLifecycleOwner) {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
                restoreToInitialTranslation()
            }

        viewModel.observeAlbumsData()
            .subscribe(viewLifecycleOwner, albumAdapter::submitList)

        viewModel.observeArtistsData()
            .subscribe(viewLifecycleOwner, artistAdapter::submitList)

        viewModel.observePlaylistsData()
            .subscribe(viewLifecycleOwner, playlistAdapter::submitList)

        viewModel.observeFoldersData()
            .subscribe(viewLifecycleOwner, folderAdapter::submitList)

        viewModel.observeGenresData()
            .subscribe(viewLifecycleOwner, genreAdapter::submitList)

        editText.afterTextChange()
            .debounce(200)
            .filter { it.isBlank() || it.trim().length >= 2 }
            .collectOnViewLifecycle(this) {
                viewModel.updateQuery(it)
            }
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
        more.setOnClickListener { featureMainNavigator.toMainPopup(requireActivity(), it, null) }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        fab.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        more.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    private fun startServiceOrRequestOverlayPermission() {
        featureBubbleNavigator.startServiceOrRequestOverlayPermission(activity!!)
    }


    override fun onStop() {
        super.onStop()
        editText.hideIme()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}