package dev.olog.feature.tab

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.sort.SortType
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.base.Navigator
import dev.olog.feature.base.SetupNestedList
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.model.DisplayableTrack
import dev.olog.feature.base.scroller.WaveSideBarView
import dev.olog.feature.library.R
import dev.olog.feature.library.TabCategory
import dev.olog.feature.tab.adapter.TabFragmentAdapter
import dev.olog.feature.tab.adapter.TabFragmentNestedAdapter
import dev.olog.feature.tab.layout.manager.AbsSpanSizeLookup
import dev.olog.feature.tab.layout.manager.LayoutManagerFactory
import dev.olog.feature.library.toTabCategory
import dev.olog.media.MediaProvider
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.coroutines.flow.drop
import javax.inject.Inject

@AndroidEntryPoint
class TabFragment : BaseFragment(), SetupNestedList {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"

        @JvmStatic
        fun newInstance(category: MediaIdCategory): TabFragment {
            return TabFragment().withArguments(ARGUMENTS_SOURCE to category.toString())
        }
    }

    @Inject
    lateinit var navigator: Navigator

    private val lastAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            lifecycle,
            navigator
        )
    }
    private val lastArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            lifecycle,
            navigator
        )
    }
    private val newAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            lifecycle,
            navigator
        )
    }
    private val newArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            lifecycle,
            navigator
        )
    }

    private val viewModel by viewModels<TabFragmentViewModel>(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { requireParentFragment().defaultViewModelProviderFactory }
    )

    val category: TabCategory by lazyFast {
        val categoryString = getArgument<String>(ARGUMENTS_SOURCE)
        MediaIdCategory.valueOf(categoryString).toTabCategory()
    }

    private val adapter by lazyFast {
        TabFragmentAdapter(lifecycle, navigator, act as MediaProvider, viewModel, this)
    }

    private fun handleEmptyStateVisibility(isEmpty: Boolean) {
        emptyStateText.toggleVisibility(isEmpty, true)
        if (isEmpty) {
            if (isPodcastFragment()) {
                val emptyText = resources.getStringArray(localization.R.array.tab_empty_podcast)
                emptyStateText.text = emptyText[category.ordinal - 6]
            } else {
                val emptyText = resources.getStringArray(localization.R.array.tab_empty_state)
                emptyStateText.text = emptyText[category.ordinal]
            }
        }
    }

    private fun isPodcastFragment(): Boolean {
        return category == TabCategory.PODCASTS || category == TabCategory.PODCASTS_PLAYLIST ||
                category == TabCategory.PODCASTS_ALBUMS || category == TabCategory.PODCASTS_ARTISTS
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val requestedSpanSize = viewModel.getSpanCount(category)
        val gridLayoutManager = LayoutManagerFactory.get(list, category, adapter, requestedSpanSize)
        list.layoutManager = gridLayoutManager
        list.adapter = adapter
        list.setHasFixedSize(true)

        if (category == TabCategory.SONGS || category == TabCategory.PODCASTS) {
            list.updatePadding(right = requireContext().dimen(dev.olog.shared.android.R.dimen.playing_queue_margin_horizontal))
        }

        val scrollableLayoutId = when (category) {
            TabCategory.SONGS -> R.layout.item_tab_song
            TabCategory.PODCASTS -> R.layout.item_tab_podcast
            TabCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
        sidebar.scrollableLayoutId = scrollableLayoutId

        fab.toggleVisibility(
            category == TabCategory.PLAYLISTS ||
                    category == TabCategory.PODCASTS_PLAYLIST, true
        )

        viewModel.observeData(category)
            .subscribe(viewLifecycleOwner) { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.updateDataSet(list)
                sidebar.onDataChanged(list)
            }

        viewModel.observeSpanCount(category)
            .drop(1) // drop initial value, already used
            .collectOnLifecycle(this) {
                if (list != null && list.isLaidOut) {
                    TransitionManager.beginDelayedTransition(list)
                    (gridLayoutManager.spanSizeLookup as AbsSpanSizeLookup).requestedSpanSize = it
                    adapter.notifyDataSetChanged()
                }
            }

        when (category) {
            TabCategory.ALBUMS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_ALBUMS)
                    .subscribe(viewLifecycleOwner) { lastAlbumsAdapter.updateDataSet(it) }
                viewModel.observeData(TabCategory.RECENTLY_ADDED_ALBUMS)
                    .subscribe(viewLifecycleOwner) { newAlbumsAdapter.updateDataSet(it) }
            }
            TabCategory.ARTISTS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_ARTISTS)
                    .subscribe(viewLifecycleOwner) { lastArtistsAdapter.updateDataSet(it) }
                viewModel.observeData(TabCategory.RECENTLY_ADDED_ARTISTS)
                    .subscribe(viewLifecycleOwner) { newArtistsAdapter.updateDataSet(it) }
            }
            TabCategory.PODCASTS_ALBUMS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_PODCAST_ALBUMS)
                    .subscribe(viewLifecycleOwner) { lastAlbumsAdapter.updateDataSet(it) }
                viewModel.observeData(TabCategory.RECENTLY_ADDED_PODCAST_ALBUMS)
                    .subscribe(viewLifecycleOwner) { newAlbumsAdapter.updateDataSet(it) }
            }
            TabCategory.PODCASTS_ARTISTS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_PODCAST_ARTISTS)
                    .subscribe(viewLifecycleOwner) { lastArtistsAdapter.updateDataSet(it) }
                viewModel.observeData(TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS)
                    .subscribe(viewLifecycleOwner) { newArtistsAdapter.updateDataSet(it) }
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
                lastAlbumsAdapter
            )
            R.layout.item_tab_last_played_artist_horizontal_list -> setupHorizontalList(
                recyclerView,
                lastArtistsAdapter
            )
            R.layout.item_tab_new_album_horizontal_list -> setupHorizontalList(
                recyclerView,
                newAlbumsAdapter
            )
            R.layout.item_tab_new_artist_horizontal_list -> setupHorizontalList(
                recyclerView,
                newArtistsAdapter
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
            val type =
                if (category == TabCategory.PLAYLISTS) PlaylistType.TRACK else PlaylistType.PODCAST
            navigator.toChooseTracksForPlaylistFragment(type)

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
            val layoutManager = list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    private fun getCurrentSorting(item: DisplayableItem): String {
        return when (category) {
            TabCategory.SONGS -> {
                require(item is DisplayableTrack)
                val sortOrder = viewModel.getAllTracksSortOrder(MediaId.songId(-1))!!
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