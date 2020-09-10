package dev.olog.feature.library.tab

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.entity.sort.SortType
import dev.olog.feature.library.R
import dev.olog.feature.library.model.TabCategory
import dev.olog.feature.presentation.base.DottedDividerDecorator
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.SetupNestedList
import dev.olog.feature.presentation.base.extensions.*
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.navigation.Navigator
import dev.olog.feature.library.model.TabCategory.PLAYLISTS
import dev.olog.feature.library.model.TabCategory.PODCASTS_PLAYLIST
import dev.olog.feature.library.model.toTabCategory
import dev.olog.feature.library.tab.adapter.TabFragmentAdapter
import dev.olog.feature.library.tab.adapter.TabFragmentNestedAdapter
import dev.olog.feature.library.tab.layout.manager.AbsSpanSizeLookup
import dev.olog.feature.library.tab.layout.manager.LayoutManagerFactory
import dev.olog.feature.presentation.base.widget.fastscroller.WaveSideBarView
import dev.olog.shared.TextUtils
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
internal class TabFragment : BaseFragment(),
    SetupNestedList {

    companion object {
        @JvmStatic
        private val TAG = TabFragment::class.java.name
        const val ARGUMENTS_SOURCE = "dataSource"

        @JvmStatic
        fun newInstance(category: PresentationIdCategory): TabFragment {
            return TabFragment().withArguments(
                ARGUMENTS_SOURCE to category
            )
        }
    }

    @Inject
    internal lateinit var navigator: Navigator

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

    private val viewModel by activityViewModels<TabFragmentViewModel>()

    internal val category: TabCategory by lazyFast {
        getArgument<PresentationIdCategory>(ARGUMENTS_SOURCE).toTabCategory()
    }

    private val adapter by lazyFast {
        TabFragmentAdapter(navigator, mediaProvider, viewModel, this)
    }

    private fun handleEmptyStateVisibility(isEmpty: Boolean) {
//        emptyStateText.isVisible = isEmpty
        if (isEmpty) {
            if (isPodcastFragment()) {
//                val emptyText = resources.getStringArray(R.array.tab_empty_podcast)
//                emptyStateText.text = emptyText[category.ordinal - 6]
            } else {
//                val emptyText = resources.getStringArray(R.array.tab_empty_state)
//                emptyStateText.text = emptyText[category.ordinal]
            }
        }
    }

    private fun isPodcastFragment(): Boolean {
        return category == TabCategory.PODCASTS ||
                category == PODCASTS_PLAYLIST ||
                category == TabCategory.PODCASTS_AUTHORS
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requestedSpanSize = viewModel.getSpanCount(category)
//        val gridLayoutManager = LayoutManagerFactory.get(list, category, adapter, requestedSpanSize)
//        list.layoutManager = gridLayoutManager
//        list.adapter = adapter
//        list.setHasFixedSize(true)
//        list.addItemDecoration(
//            DottedDividerDecorator(
//                requireContext(), listOf(R.layout.item_tab_header, R.layout.item_tab_shuffle)
//            )
//        )

        if (category == TabCategory.SONGS || category == TabCategory.PODCASTS) {
//            list.updatePadding(right = requireContext().dimen(R.dimen.default_list_margin_horizontal))
        }

        val scrollableLayoutId = when (category) {
            TabCategory.SONGS -> R.layout.item_tab_song
            TabCategory.PODCASTS -> R.layout.item_tab_podcast
            TabCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
//        sidebar.scrollableLayoutId = scrollableLayoutId

//        fab.isVisible = category == PLAYLISTS || category == PODCASTS_PLAYLIST

        viewModel.observeData(category)
            .onEach { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.submitList(list)
//                sidebar.onDataChanged(list)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.observeSpanCount(category)
            .onEach { span ->
//                list.awaitAnimationEnd()
//                list.doOnLayout {
//                     TODO check
//                    TransitionManager.beginDelayedTransition(list)
//                    (gridLayoutManager.spanSizeLookup as AbsSpanSizeLookup).requestedSpanSize = span
//                    adapter.notifyDataSetChanged()
//                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        observePodcastPositions()

        when (category) {
            TabCategory.ALBUMS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_ALBUMS)
                    .filterIsInstance<List<DisplayableAlbum>>()
                    .onEach { lastAlbumsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)

                viewModel.observeData(TabCategory.RECENTLY_ADDED_ALBUMS)
                    .filterIsInstance<List<DisplayableAlbum>>()
                    .onEach { newAlbumsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
            TabCategory.ARTISTS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_ARTISTS)
                    .filterIsInstance<List<DisplayableAlbum>>()
                    .onEach { lastArtistsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)

                viewModel.observeData(TabCategory.RECENTLY_ADDED_ARTISTS)
                    .filterIsInstance<List<DisplayableAlbum>>()
                    .onEach { newArtistsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
            TabCategory.PODCASTS_AUTHORS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_PODCAST_ARTISTS)
                    .filterIsInstance<List<DisplayableAlbum>>()
                    .onEach { lastArtistsAdapter.submitList(it) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)

                viewModel.observeData(TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS)
                    .filterIsInstance<List<DisplayableAlbum>>()
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
//        list.adapter = null
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
//        sidebar.setListener(letterTouchListener)
//        fab.setOnClickListener { fab ->
//            val type =
//                if (category == PLAYLISTS) PlaylistType.TRACK else PlaylistType.PODCAST
//
//            val sharedFab = (requireParentFragment().requireView() as ViewGroup)
//                .findViewById<View>(R.id.sharedFab)
//
//            matchFabs(sharedFab, fab)
//
//            navigator.toChooseTracksForPlaylistFragment(type, sharedFab)
//        }
    }

    override fun onPause() {
        super.onPause()
//        sidebar.setListener(null)
//        fab.setOnClickListener(null)
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

    override fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
    }

    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
//        list.stopScroll()

//        val scrollableItem = sidebar.scrollableLayoutId

//        val position = when (letter) {
//            TextUtils.MIDDLE_DOT -> -1
//            "#" -> adapter.indexOf {
//                if (it.type != scrollableItem) {
//                    false
//                } else {
//                    val sorting = getCurrentSorting(it)
//                    if (sorting.isBlank()) false
//                    else sorting[0].toUpperCase().toString().isDigitsOnly()
//                }
//            }
//            "?" -> adapter.indexOf {
//                if (it.type != scrollableItem) {
//                    false
//                } else {
//                    val sorting = getCurrentSorting(it)
//                    if (sorting.isBlank()) false
//                    else sorting[0].toUpperCase().toString() > "Z"
//                }
//            }
//            else -> adapter.indexOf {
//                if (it.type != scrollableItem) {
//                    false
//                } else {
//                    val sorting = getCurrentSorting(it)
//                    if (sorting.isBlank()) false
//                    else sorting[0].toUpperCase().toString() == letter
//                }
//            }
//        }
//        if (position != -1) {
//            val layoutManager = list.layoutManager as GridLayoutManager
//            layoutManager.scrollToPositionWithOffset(position, 0)
//        }
    }

    private fun getCurrentSorting(item: DisplayableItem): String {
        return when (category) {
            TabCategory.SONGS,
            TabCategory.PODCASTS -> {
                require(item is DisplayableTrack)
                val sortOrder = viewModel.getAllTracksSortOrder()
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