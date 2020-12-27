package dev.olog.feature.detail.detail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.*
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.detail.R
import dev.olog.feature.detail.detail.adapter.DetailFragmentAdapter
import dev.olog.feature.detail.detail.adapter.DetailMostPlayedAdapter
import dev.olog.feature.detail.detail.adapter.DetailRecentlyAddedAdapter
import dev.olog.feature.detail.detail.adapter.DetailRelatedArtistsAdapter
import dev.olog.feature.detail.detail.adapter.DetailSiblingsAdapter
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.interfaces.CanChangeStatusBarColor
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.abs
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailFragment : BaseFragment(),
    CanChangeStatusBarColor,
    SetupNestedList,
    IDragListener by DragListenerImpl() {

    @Inject
    lateinit var navigator: Navigator

    private val viewModel by viewModels<DetailFragmentViewModel>()

    private val mediaId by argument(Params.MEDIA_ID, MediaId::fromString)

    private val mostPlayedAdapter by lazyFast {
        DetailMostPlayedAdapter(navigator, requireActivity().mediaProvider)
    }
    private val recentlyAddedAdapter by lazyFast {
        DetailRecentlyAddedAdapter(navigator, requireActivity().mediaProvider)
    }
    private val relatedArtistAdapter by lazyFast {
        DetailRelatedArtistsAdapter(navigator)
    }
    private val albumsAdapter by lazyFast {
        DetailSiblingsAdapter(navigator)
    }

    private val adapter by lazyFast {
        DetailFragmentAdapter(
            mediaId = mediaId,
            setupNestedList = this,
            navigator = navigator,
            mediaProvider = requireActivity().mediaProvider,
            viewModel = viewModel,
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
        setupDragListener(list, swipeDirections)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        viewModel.observeMostPlayed()
            .onEach(mostPlayedAdapter::submitList)
            .launchIn(this)

        viewModel.observeRecentlyAdded()
            .onEach(recentlyAddedAdapter::submitList)
            .launchIn(this)

        viewModel.observeRelatedArtists()
            .onEach(relatedArtistAdapter::submitList)
            .launchIn(this)

        viewModel.observeSiblings()
            .onEach(albumsAdapter::submitList)
            .launchIn(this)

        viewModel.observeSongs()
            .onEach { list ->
                if (list.isEmpty()) {
                    requireActivity().onBackPressed()
                } else {
                    adapter.submitList(list)
                    restoreUpperWidgetsTranslation()
                }
            }.launchIn(this)

        viewModel.observeItem()
            .onEach { headerText.text = it.title }
            .launchIn(this)

        editText.afterTextChange()
            .debounce(200)
            .filter { it.isEmpty() || it.length >= 2 }
            .onEach(viewModel::updateFilter)
            .launchIn(this)
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
        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        more.setOnClickListener {
            navigator.toDialog(viewModel.parentMediaId, more)
        }
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
        if (requireContext().isDarkMode) {
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
