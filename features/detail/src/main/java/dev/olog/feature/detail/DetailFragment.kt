package dev.olog.feature.detail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.MediaId
import dev.olog.feature.detail.adapter.*
import dev.olog.feature.detail.model.DetailValues
import dev.olog.feature.presentation.base.CanChangeStatusBarColor
import dev.olog.feature.presentation.base.DottedDividerDecorator
import dev.olog.feature.presentation.base.SetupNestedList
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.adapter.drag.DragListenerImpl
import dev.olog.feature.presentation.base.adapter.drag.IDragListener
import dev.olog.feature.presentation.base.extensions.*
import dev.olog.feature.presentation.base.model.DisplayableHeader
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.feature.presentation.base.model.toPresentation
import dev.olog.feature.presentation.base.transition.FastAutoTransition
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.dark.mode.isDarkMode
import dev.olog.shared.android.extensions.colorControlNormal
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailFragment : BaseFragment(),
    CanChangeStatusBarColor,
    SetupNestedList,
    IDragListener by DragListenerImpl() {

    @Inject
    internal lateinit var navigator: Navigator

    private val viewModel by viewModels<DetailFragmentViewModel>()

    private val mediaId by argument<String, PresentationId.Category>(Params.MEDIA_ID) {
        MediaId.fromString(it).toPresentation() as PresentationId.Category
    }

    private val mostPlayedAdapter by lazyFast {
        DetailMostPlayedAdapter(navigator, mediaProvider)
    }
    private val recentlyAddedAdapter by lazyFast {
        DetailRecentlyAddedAdapter(navigator, mediaProvider)
    }
    private val relatedArtistAdapter by lazyFast {
        DetailRelatedArtistsAdapter(navigator)
    }
    private val albumsAdapter by lazyFast {
        DetailSiblingsAdapter(navigator)
    }

    private val spotifySinglesAdapter by lazyFast { DetailSpotifySinglesAdapter() }
    private val spotifyAlbumsAdapter by lazyFast { DetailSpotifyAlbumsAdapter() }

    private val adapter by lazyFast {
        DetailFragmentAdapter(
            mediaId = mediaId,
            setupNestedList = this,
            navigator = navigator,
            mediaProvider = mediaProvider,
            viewModel = viewModel,
            dragListener = this,
            afterImageLoad = { startPostponedEnterTransition() }
        )
    }

    private val recyclerOnScrollListener by lazyFast {
        HeaderVisibilityScrollListener(this)
    }
    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    internal var hasLightStatusBarColor by Delegates.observable(false) { _, old, new ->
        if (old != new) {
            adjustStatusBarColor(new)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.transitionName = requireArguments().getString(Params.CONTAINER_TRANSITION_NAME)

        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)
        list.addItemDecoration(
            DottedDividerDecorator(
                requireContext(), listOf(
                    R.layout.item_detail_header,
                    R.layout.item_detail_header_albums,
                    R.layout.item_detail_header_all_song,
                    R.layout.item_detail_header_recently_added
                )
            )
        )

        var swipeDirections = ItemTouchHelper.LEFT
        if (adapter.canSwipeRight) {
            swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        }
        setupDragListener(list, swipeDirections)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        combine(
            viewModel.songs,
            viewModel.mostPlayed,
            viewModel.recentlyAdded,
            viewModel.relatedArtists,
            viewModel.siblings,
            viewModel.spotifyAlbums,
            viewModel.spotifySingle
        ) { list ->
            DetailValues(list[0], list[1], list[2], list[3], list[4], list[5], list[6])
        }.onEach {
            if (it.songs.isEmpty()) {
                requireActivity().onBackPressed()
            } else {
                mostPlayedAdapter.submitList(it.mostPlayed)
                recentlyAddedAdapter.submitList(it.recentlyAdded)
                relatedArtistAdapter.submitList(it.relatedArtists)
                albumsAdapter.submitList(it.siblings)
                mostPlayedAdapter.submitList(it.mostPlayed)
                spotifyAlbumsAdapter.submitList(it.spotifySingles)
                spotifySinglesAdapter.submitList(it.spotifyAlbums)

                adapter.submitList(it.songs)
            }
            restoreTranslations()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.item
            .onEach { item ->
                require(item is DisplayableHeader)
                headerText.text = item.title
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        if (mediaId.isAnyPodcast) {
            viewModel.observeAllCurrentPositions()
                .onEach { adapter.updatePodcastPositions(it) }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }

        editText.afterTextChange()
            .debounce(200)
            .filter { it.isEmpty() || it.length >= 2 }
            .onEach { viewModel.updateFilter(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
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
            R.layout.item_detail_list_spotify_singles -> {
                setupHorizontalListAsList(recyclerView, spotifySinglesAdapter)
            }
            R.layout.item_detail_list_spotify_albums -> {
                setupHorizontalListAsList(recyclerView, spotifyAlbumsAdapter)
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
        more.setOnClickListener { navigator.toDialog(viewModel.mediaId.toDomain(), more, null) }
        filter.setOnClickListener {
            TransitionManager.beginDelayedTransition(toolbar,
                FastAutoTransition
            )
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
        disposeDragListener()
    }

    override fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
        mostPlayedAdapter.onCurrentPlayingChanged(mostPlayedAdapter, mediaId)
        recentlyAddedAdapter.onCurrentPlayingChanged(recentlyAddedAdapter, mediaId)
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

        requireActivity().window.setLightStatusBar()
    }

    private fun restoreTranslations() {
        restoreUpperWidgetsTranslation()
        back.animate().alpha(1f)
        filter.animate().alpha(1f)
        more.animate().alpha(1f)
        searchWrapper.animate().alpha(1f)
        headerText.animate().alpha(1f)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail

    private val scrollListener = object : RecyclerView.OnScrollListener() {
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
