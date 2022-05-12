package dev.olog.feature.detail.main


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.detail.DetailSortDialog
import dev.olog.feature.detail.DetailTapTarget
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.platform.fragment.BaseFragment
import dev.olog.feature.detail.R
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.drag.DragListenerImpl
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.feature.detail.main.adapter.DetailFragmentAdapter
import dev.olog.feature.detail.main.adapter.DetailMostPlayedAdapter
import dev.olog.feature.detail.main.adapter.DetailRecentlyAddedAdapter
import dev.olog.feature.detail.main.adapter.DetailRelatedArtistsAdapter
import dev.olog.feature.detail.main.adapter.DetailSiblingsAdapter
import dev.olog.feature.main.FeatureMainNavigator
import dev.olog.feature.media.MediaProvider
import dev.olog.platform.CanChangeStatusBarColor
import dev.olog.platform.adapter.SetupNestedList
import dev.olog.ui.model.DisplayableHeader
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.afterTextChange
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.isDarkMode
import dev.olog.shared.extension.isTablet
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.subscribe
import dev.olog.shared.extension.withArguments
import dev.olog.ui.activity.removeLightStatusBar
import dev.olog.ui.activity.setLightStatusBar
import dev.olog.ui.adapter.drag.CircularRevealAnimationController
import dev.olog.ui.colorControlNormal
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import kotlin.math.abs
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailFragment : BaseFragment(),
    CanChangeStatusBarColor,
    SetupNestedList,
    IDragListener by DragListenerImpl() {

    companion object {
        @JvmStatic
        val TAG = DetailFragment::class.java.name
        @JvmStatic
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): DetailFragment {
            return DetailFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    @Inject
    lateinit var featureMainNavigator: FeatureMainNavigator
    @Inject
    lateinit var featureDetailNavigator: FeatureDetailNavigator

    private val viewModel by viewModels<DetailFragmentViewModel>()

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)

    private val mediaProvider: MediaProvider
        get() = requireActivity().findInContext()

    private val mostPlayedAdapter by lazyFast {
        DetailMostPlayedAdapter(
            onItemClick = { mediaProvider.playMostPlayed(it) },
            onItemLongClick = { view, mediaId ->
                featureMainNavigator.toItemDialog(requireActivity(), view, mediaId)
            }
        )
    }
    private val recentlyAddedAdapter by lazyFast {
        DetailRecentlyAddedAdapter(
            onItemClick = { mediaProvider.playRecentlyAdded(it) },
            onItemLongClick = { view, mediaId ->
                featureMainNavigator.toItemDialog(requireActivity(), view, mediaId)
            }
        )
    }
    private val relatedArtistAdapter by lazyFast {
        DetailRelatedArtistsAdapter(
            onItemClick = { featureDetailNavigator.toDetail(requireActivity(), it) },
            onItemLongClick = { view, mediaId ->
                featureMainNavigator.toItemDialog(requireActivity(), view, mediaId)
            }
        )
    }
    private val albumsAdapter by lazyFast {
        DetailSiblingsAdapter(
            onItemClick = { featureDetailNavigator.toDetail(requireActivity(), it) },
            onItemLongClick = { view, mediaId ->
                featureMainNavigator.toItemDialog(requireActivity(), view, mediaId)
            }
        )
    }

    private val adapter by lazyFast {
        DetailFragmentAdapter(
            mediaId = mediaId,
            onShuffleClick = { mediaProvider.shuffle(mediaId, viewModel.getFilter()) },
            onRecentlyAddedHeaderClick = { featureDetailNavigator.toRecentlyAdded(requireActivity(), mediaId) },
            onRelatedArtistsHeaderClick = { featureDetailNavigator.toRelatedArtists(requireActivity(), mediaId) },
            onItemClick = { mediaId ->
                viewModel.detailSortDataUseCase(mediaId) {
                    mediaProvider.playFromMediaId(mediaId, viewModel.getFilter(), it)
                }
            },
            onItemLongClick = { view, mediaId ->
                featureMainNavigator.toItemDialog(requireActivity(), view, mediaId)
            },
            onSortTypeClick = { view ->
                viewModel.observeSortOrder { currentSortType ->
                    DetailSortDialog().show(view, mediaId, currentSortType) { newSortType ->
                        viewModel.updateSortOrder(newSortType)
                    }
                }
            },
            onSortDirectionClick = { viewModel.toggleSortArranging() },
            onShowSortTutorial = { text, image ->
                if (viewModel.showSortByTutorialIfNeverShown()) {
                    DetailTapTarget.sortBy(text, image)
                }
            },
            onSwipeRight = { viewModel.removeFromPlaylist(it) },
            onSwipeLeft = { mediaProvider.addToPlayNext(it) },
            onSwipeDone = { viewModel.processMove() },
            onItemMove = { from , to ->
                viewModel.addMove(from, to)
            },
            viewModel = viewModel,
            setupNestedList = this,
            dragListener = this
        )
    }

    private val recyclerOnScrollListener by lazyFast {
        HeaderVisibilityScrollListener(
            this
        )
    }
    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    internal var hasLightStatusBarColor by Delegates.observable(false) { _, old, new ->
        if (old != new){
            adjustStatusBarColor(new)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

        var swipeDirections = ItemTouchHelper.LEFT
        if (adapter.canSwipeRight) {
            swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        }
        setupDragListener(
            scope = viewLifecycleOwner.lifecycleScope,
            list = list,
            direction = swipeDirections,
            animation = CircularRevealAnimationController(),
        )

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        viewModel.observeMostPlayed()
            .subscribe(viewLifecycleOwner, mostPlayedAdapter::submitList)

        viewModel.observeRecentlyAdded()
            .subscribe(viewLifecycleOwner, recentlyAddedAdapter::submitList)

        viewModel.observeRelatedArtists()
            .subscribe(viewLifecycleOwner, relatedArtistAdapter::submitList)

        viewModel.observeSiblings()
            .subscribe(viewLifecycleOwner) {
                albumsAdapter.submitList(it)
            }

        viewModel.observeSongs()
            .subscribe(viewLifecycleOwner) { list ->
                if (list.isEmpty()) {
                    requireActivity().onBackPressed()
                } else {
                    adapter.submitList(list)
                    restoreToInitialTranslation()
                }
            }

        viewModel.observeItem().subscribe(viewLifecycleOwner) { item ->
            require(item is DisplayableHeader)
            headerText.text = item.title
        }

        editText.afterTextChange()
            .debounce(200)
            .filter { it.isEmpty() || it.length >= 2 }
            .collectOnViewLifecycle(this) {
                viewModel.updateFilter(it)
            }
    }

    override fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_detail_list_most_played -> {
                setupHorizontalListAsGrid(recyclerView, mostPlayedAdapter)
            }
            R.layout.item_detail_list_recently_added -> {
                setupHorizontalListAsGrid(recyclerView, recentlyAddedAdapter)
            }
            R.layout.item_detail_list_related_artists -> {
                setupHorizontalListAsList(recyclerView, relatedArtistAdapter)
            }
            R.layout.item_detail_list_albums -> {
                setupHorizontalListAsList(recyclerView, albumsAdapter)
            }
        }
    }

    private fun setupHorizontalListAsGrid(list: RecyclerView, adapter: ObservableAdapter<*>) {
        val layoutManager = GridLayoutManager(
            list.context, DetailFragmentViewModel.NESTED_SPAN_COUNT,
            GridLayoutManager.HORIZONTAL, false
        )
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = DetailFragmentViewModel.NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    private fun setupHorizontalListAsList(list: RecyclerView, adapter: ObservableAdapter<*>) {
        val layoutManager = LinearLayoutManager(list.context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = DetailFragmentViewModel.NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
    }

    override fun onResume() {
        super.onResume()
        list.addOnScrollListener(recyclerOnScrollListener)
        list.addOnScrollListener(scrollListener)
        back.setOnClickListener { requireActivity().onBackPressed() }
        more.setOnClickListener { featureMainNavigator.toItemDialog(requireActivity(), more, viewModel.mediaId) }
        filter.setOnClickListener {
            searchWrapper.isVisible = !searchWrapper.isVisible
        }
    }

    override fun onPause() {
        super.onPause()
        list.removeOnScrollListener(recyclerOnScrollListener)
        list.removeOnScrollListener(scrollListener)
        back.setOnClickListener(null)
        more.setOnClickListener(null)
        filter.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
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
        back.setColorFilter(color)
        more.setColorFilter(color)
        filter.setColorFilter(color)

        if (requireContext().isTablet){
            return
        }
        requireActivity().window.removeLightStatusBar()
    }

    private fun setLightStatusBar() {
        if (requireContext().isDarkMode()) {
            return
        }
        val color = requireContext().colorControlNormal()
        back.setColorFilter(color)
        more.setColorFilter(color)
        filter.setColorFilter(color)

        if (requireContext().isTablet){
            return
        }

        requireActivity().window.setLightStatusBar()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val alpha = 1 - abs(toolbar.translationY) / toolbar.height
            back.alpha = alpha
            filter.alpha = alpha
            more.alpha = alpha
            searchWrapper.alpha = alpha
            headerText.alpha = alpha
        }
    }
}
