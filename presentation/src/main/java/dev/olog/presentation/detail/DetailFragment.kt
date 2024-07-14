package dev.olog.presentation.detail


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.restoreUpperWidgetsTranslation
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentDetailBinding
import dev.olog.presentation.detail.adapter.DetailFragmentAdapter
import dev.olog.presentation.detail.adapter.DetailMostPlayedAdapter
import dev.olog.presentation.detail.adapter.DetailRecentlyAddedAdapter
import dev.olog.presentation.detail.adapter.DetailRelatedArtistsAdapter
import dev.olog.presentation.detail.adapter.DetailSiblingsAdapter
import dev.olog.presentation.interfaces.CanChangeStatusBarColor
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.DisplayableHeader
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail),
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

    private val mostPlayedAdapter by lazyFast {
        DetailMostPlayedAdapter(navigator, act.findInContext<MediaProvider>())
    }
    private val recentlyAddedAdapter by lazyFast {
        DetailRecentlyAddedAdapter(navigator, act.findInContext<MediaProvider>())
    }
    private val relatedArtistAdapter by lazyFast {
        DetailRelatedArtistsAdapter(navigator)
    }
    private val albumsAdapter by lazyFast {
        DetailSiblingsAdapter(navigator)
    }

    private val adapter by lazyFast {
        DetailFragmentAdapter(
            lifecycle,
            mediaId,
            this,
            navigator,
            act.findInContext<MediaProvider>(),
            viewModel,
            this
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

    private val binding by viewBinding(FragmentDetailBinding::bind) {
        it.list.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter
        binding.list.setRecycledViewPool(recycledViewPool)
        binding.list.setHasFixedSize(true)

        var swipeDirections = ItemTouchHelper.LEFT
        if (adapter.canSwipeRight) {
            swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        }
        setupDragListener(binding.list, swipeDirections)

        binding.fastScroller.attachRecyclerView(binding.list)
        binding.fastScroller.showBubble(false)

        viewModel.observeMostPlayed()
            .subscribe(viewLifecycleOwner, mostPlayedAdapter::submitList)

        viewModel.observeRecentlyAdded()
            .subscribe(viewLifecycleOwner, recentlyAddedAdapter::submitList)

        viewModel.observeRelatedArtists()
            .subscribe(viewLifecycleOwner, relatedArtistAdapter::submitList)

        viewModel.observeSiblings()
            .subscribe(viewLifecycleOwner, albumsAdapter::submitList)

        viewModel.observeSongs()
            .subscribe(viewLifecycleOwner) { list ->
                if (list.isEmpty()) {
                    act.onBackPressed()
                } else {
                    adapter.updateDataSet(list)
                    restoreUpperWidgetsTranslation()
                }
            }

        viewModel.observeItem().subscribe(viewLifecycleOwner) { item ->
            require(item is DisplayableHeader)
            binding.headerText.text = item.title
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

    private fun setupHorizontalListAsGrid(list: RecyclerView, adapter: RecyclerView.Adapter<*>) {
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

    private fun setupHorizontalListAsList(list: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        val layoutManager = LinearLayoutManager(list.context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = DetailFragmentViewModel.NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
    }

    override fun onResume() {
        super.onResume()
        binding.list.addOnScrollListener(recyclerOnScrollListener)
        binding.list.addOnScrollListener(scrollListener)
        binding.back.setOnClickListener { act.onBackPressed() }
        binding.more.setOnClickListener { navigator.toDialog(viewModel.mediaId, binding.more) }
        binding.filter.setOnClickListener {
            binding.searchWrapper.toggleVisibility(!binding.searchWrapper.isVisible, true)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.list.removeOnScrollListener(recyclerOnScrollListener)
        binding.list.removeOnScrollListener(scrollListener)
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

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val alpha = 1 - abs(binding.toolbar.translationY) / binding.toolbar.height
            binding.back.alpha = alpha
            binding.filter.alpha = alpha
            binding.more.alpha = alpha
            binding.searchWrapper.alpha = alpha
            binding.headerText.alpha = alpha
        }
    }
}
