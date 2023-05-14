package dev.olog.presentation.tab

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortType
import dev.olog.feature.media.api.mediaProvider
import dev.olog.platform.extension.dimen
import dev.olog.platform.extension.getArgument
import dev.olog.platform.extension.toggleVisibility
import dev.olog.platform.extension.withArguments
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.adapter.TabFragmentAdapter
import dev.olog.presentation.tab.adapter.TabFragmentNestedAdapter
import dev.olog.presentation.tab.layoutmanager.AbsSpanSizeLookup
import dev.olog.presentation.tab.layoutmanager.LayoutManagerFactory
import dev.olog.presentation.widgets.fascroller.WaveSideBarView
import dev.olog.feature.media.api.DurationUtils
import dev.olog.shared.lazyFast
import dev.olog.shared.subscribe
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabFragment : BaseFragment(), SetupNestedList {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"
        const val ARGUMENTS_IS_PODCAST = "$TAG.argument.isPodcast"

        fun newInstance(
            category: MediaIdCategory,
            isPodcast: Boolean,
        ): TabFragment {
            return TabFragment().withArguments(
                ARGUMENTS_SOURCE to category.toString(),
                ARGUMENTS_IS_PODCAST to isPodcast,
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator

    private val recentlyPlayedAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            lifecycle,
            navigator
        )
    }
    private val recentlyPlayedArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            lifecycle,
            navigator
        )
    }
    private val recentlyAddedAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            lifecycle,
            navigator
        )
    }
    private val recentlyAddedArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            lifecycle,
            navigator
        )
    }

    private val viewModel by viewModels<TabFragmentViewModel>(
        ownerProducer = { requireParentFragment() }
    )

    internal val category: TabCategory by lazyFast {
        val categoryString = getArgument<String>(ARGUMENTS_SOURCE)
        MediaIdCategory.valueOf(categoryString).toTabCategory()
    }
    private val isPodcast: Boolean
        get() = getArgument(ARGUMENTS_IS_PODCAST)

    private val adapter by lazyFast {
        TabFragmentAdapter(
            lifecycle = lifecycle,
            navigator = navigator,
            mediaProvider = mediaProvider,
            viewModel = viewModel,
            setupNestedList = this
        )
    }

    private fun handleEmptyStateVisibility(isEmpty: Boolean) {
        emptyStateText.toggleVisibility(isEmpty, true)
        if (isEmpty) {
            // TODO empty state
//            if (isPodcastFragment()) {
//                val emptyText = resources.getStringArray(R.array.tab_empty_podcast)
//                emptyStateText.text = emptyText[category.ordinal - 6]
//            } else {
//                val emptyText = resources.getStringArray(R.array.tab_empty_state)
//                emptyStateText.text = emptyText[category.ordinal]
//            }
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val requestedSpanSize = viewModel.getSpanCount(category)
        val gridLayoutManager = LayoutManagerFactory.get(list, category, adapter, requestedSpanSize)
        list.layoutManager = gridLayoutManager
        list.adapter = adapter
        list.setHasFixedSize(true)

        if (category == TabCategory.SONGS) {
            list.updatePadding(right = requireContext().dimen(R.dimen.playing_queue_margin_horizontal))
        }

        val scrollableLayoutId = when (category) {
            TabCategory.SONGS -> R.layout.item_tab_song
//            TabCategory.PODCASTS -> R.layout.item_tab_podcast todo
            TabCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
        sidebar.scrollableLayoutId = scrollableLayoutId

        fab.isVisible = category == TabCategory.PLAYLISTS

        viewModel.observeData(category, isPodcast)
            .subscribe(viewLifecycleOwner) { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.updateDataSet(list)
                sidebar.onDataChanged(list)
            }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeSpanCount(category)
                .drop(1) // drop initial value, already used
                .collect {
                    if (list != null && list.isLaidOut) {
                        TransitionManager.beginDelayedTransition(list)
                        (gridLayoutManager.spanSizeLookup as AbsSpanSizeLookup).requestedSpanSize = it
                        adapter.notifyDataSetChanged()
                    }
                }
        }

        when (category) {
            TabCategory.ALBUMS -> {
                viewModel.getRecentlyPlayedAlbums(isPodcast)
                    .subscribe(viewLifecycleOwner) { recentlyPlayedAlbumsAdapter.updateDataSet(it) }
                viewModel.getRecentlyAddedAlbums(isPodcast)
                    .subscribe(viewLifecycleOwner) { recentlyAddedAlbumsAdapter.updateDataSet(it) }
            }
            TabCategory.ARTISTS -> {
                viewModel.getRecentlyPlayedArtists(isPodcast)
                    .subscribe(viewLifecycleOwner) { recentlyPlayedArtistsAdapter.updateDataSet(it) }
                viewModel.getRecentlyAddedArtists(isPodcast)
                    .subscribe(viewLifecycleOwner) { recentlyAddedArtistsAdapter.updateDataSet(it) }
            }
            else -> {/*making lint happy*/
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    override fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_tab_last_played_album_horizontal_list -> setupHorizontalList(
                recyclerView,
                recentlyPlayedAlbumsAdapter
            )
            R.layout.item_tab_last_played_artist_horizontal_list -> setupHorizontalList(
                recyclerView,
                recentlyPlayedArtistsAdapter
            )
            R.layout.item_tab_new_album_horizontal_list -> setupHorizontalList(
                recyclerView,
                recentlyAddedAlbumsAdapter
            )
            R.layout.item_tab_new_artist_horizontal_list -> setupHorizontalList(
                recyclerView,
                recentlyAddedArtistsAdapter
            )
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: ObservableAdapter<*>) {
        val layoutManager = LinearLayoutManager(list.context, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        sidebar.setListener(letterTouchListener)
        fab.setOnClickListener {
            navigator.toChooseTracksForPlaylistFragment(isPodcast = isPodcast)

        }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        fab.setOnClickListener(null)
    }

    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
        list.stopScroll()

        val scrollableItem = sidebar.scrollableLayoutId

        val position = when (letter) {
            DurationUtils.MIDDLE_DOT -> -1
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
            val layoutManager = list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    private fun getCurrentSorting(item: DisplayableItem): String {
        return when (category) {
            TabCategory.SONGS -> {
                require(item is DisplayableTrack)
                val sortOrder = viewModel.getAllTracksSortOrder(isPodcast)!!
                when (sortOrder.type) {
                    SortType.ARTIST -> item.artist
                    SortType.ALBUM -> item.album
                    else -> item.title
                }
            }
            TabCategory.ALBUMS -> {
                require(item is DisplayableAlbum)
                val sortOrder = viewModel.getAllAlbumsSortOrder()
                when (sortOrder.type) {
                    SortType.TITLE -> item.title
                    else -> item.subtitle
                }
            }
            else -> {
                require(item is DisplayableAlbum)
                item.title
            }
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_tab
}