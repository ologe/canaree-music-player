package dev.olog.presentation.tab

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.sort.SortType
import dev.olog.media.MediaProvider
import dev.olog.presentation.DottedDividerDecorator
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
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class TabFragment : BaseFragment(), SetupNestedList {

    companion object {
        @JvmStatic
        private val TAG = TabFragment::class.java.name
        const val ARGUMENTS_SOURCE = "dataSource"

        @JvmStatic
        fun newInstance(category: MediaIdCategory): TabFragment {
            return TabFragment().withArguments(
                ARGUMENTS_SOURCE to category.toString()
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val lastAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(navigator)
    }
    private val lastArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(navigator)
    }
    private val newAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(navigator)
    }
    private val newArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(navigator)
    }

    private val viewModel by activityViewModels<TabFragmentViewModel> {
        viewModelFactory
    }

    internal val category: TabCategory by lazyFast {
        val categoryString = getArgument<String>(ARGUMENTS_SOURCE)
        MediaIdCategory.valueOf(categoryString).toTabCategory()
    }

    private val adapter by lazyFast {
        TabFragmentAdapter(navigator, act as MediaProvider, viewModel, this)
    }

    private fun handleEmptyStateVisibility(isEmpty: Boolean) {
        emptyStateText.toggleVisibility(isEmpty, true)
        if (isEmpty) {
            if (isPodcastFragment()) {
                val emptyText = resources.getStringArray(R.array.tab_empty_podcast)
                emptyStateText.text = emptyText[category.ordinal - 6]
            } else {
                val emptyText = resources.getStringArray(R.array.tab_empty_state)
                emptyStateText.text = emptyText[category.ordinal]
            }
        }
    }

    private fun isPodcastFragment(): Boolean {
        return category == TabCategory.PODCASTS ||
                category == TabCategory.PODCASTS_PLAYLIST ||
                category == TabCategory.PODCASTS_AUTHORS
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requestedSpanSize = viewModel.getSpanCount(category)
        val gridLayoutManager = LayoutManagerFactory.get(list, category, adapter, requestedSpanSize)
        list.layoutManager = gridLayoutManager
        list.adapter = adapter
        list.setHasFixedSize(true)
        list.addItemDecoration(DottedDividerDecorator(
            requireContext(), listOf(R.layout.item_tab_header, R.layout.item_tab_shuffle)
        ))

        if (category == TabCategory.SONGS || category == TabCategory.PODCASTS) {
            list.updatePadding(right = requireContext().dimen(R.dimen.playing_queue_margin_horizontal))
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
            .onEach { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.submitList(list)
                sidebar.onDataChanged(list)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.observeSpanCount(category)
            .drop(1) // drop initial value, already used
            .onEach {
                if (list != null && list.isLaidOut) {
                    TransitionManager.beginDelayedTransition(list)
                    (gridLayoutManager.spanSizeLookup as AbsSpanSizeLookup).requestedSpanSize = it
                    adapter.notifyDataSetChanged()
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        observePodcastPositions()

        when (category) {
            TabCategory.ALBUMS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_ALBUMS)
                    .onEach { lastAlbumsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.observeData(TabCategory.RECENTLY_ADDED_ALBUMS)
                    .onEach { newAlbumsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
            TabCategory.ARTISTS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_ARTISTS)
                    .onEach { lastArtistsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.observeData(TabCategory.RECENTLY_ADDED_ARTISTS)
                    .onEach { newArtistsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
            TabCategory.PODCASTS_AUTHORS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_PODCAST_ARTISTS)
                    .onEach { lastArtistsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.observeData(TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS)
                    .onEach {  newArtistsAdapter.submitList(it)}
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
            else -> {/*making lint happy*/
            }
        }

    }

    private fun observePodcastPositions() {
        if (category != TabCategory.PODCASTS) {
            return
        }
        viewModel.observeAllCurrentPositions()
            .onEach { adapter.updatePodcastPositions(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
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
        fab.setOnClickListener { fab ->
            val type =
                if (category == TabCategory.PLAYLISTS) PlaylistType.TRACK else PlaylistType.PODCAST

            val sharedFab = (parentFragment!!.requireView() as ViewGroup).findViewById<View>(R.id.sharedFab)
            matchFabs(sharedFab, fab)

            navigator.toChooseTracksForPlaylistFragment(sharedFab, type)
        }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        fab.setOnClickListener(null)
    }

    /**
     * Workaround: copy the current position of [fab] to [sharedFab] in [LibraryFragment] to start
     * shared element transition. Bad solution bad move fab from [TabFragment] to [LibraryFragment]
     * and observe various scroll requires too much work!!
     */
    private fun matchFabs(sharedFab: View, fab: View) {
        sharedFab.isVisible = true
        sharedFab.translationY = fab.translationY
        sharedFab.updatePadding(bottom = fab.paddingBottom)
        sharedFab.setMargin(bottom = fab.marginBottom)
        fab.visibility = View.INVISIBLE
    }

    override fun onCurrentPlayingChanged(mediaId: MediaId) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
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
            TabCategory.SONGS,
            TabCategory.PODCASTS -> {
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