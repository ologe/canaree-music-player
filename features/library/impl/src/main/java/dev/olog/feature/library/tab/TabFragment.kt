package dev.olog.feature.library.tab

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.sort.SortType
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.library.R
import dev.olog.feature.library.api.TabCategory
import dev.olog.feature.library.api.toTabCategory
import dev.olog.feature.library.tab.adapter.TabFragmentAdapter
import dev.olog.feature.library.tab.adapter.TabFragmentNestedAdapter
import dev.olog.feature.library.tab.manager.AbsSpanSizeLookup
import dev.olog.feature.library.tab.manager.LayoutManagerFactory
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.feature.media.api.MediaProvider
import dev.olog.feature.playlist.api.FeaturePlaylistNavigator
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.SetupNestedList
import dev.olog.platform.fragment.BaseFragment
import dev.olog.shared.TextUtils
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.dimen
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.withArguments
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import dev.olog.ui.scroller.WaveSideBarView
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
            return TabFragment().withArguments(
                ARGUMENTS_SOURCE to category
            )
        }
    }

    @Inject
    lateinit var featureMainPopupNavigator: FeatureMainPopupNavigator
    @Inject
    lateinit var featureDetailNavigator: FeatureDetailNavigator
    @Inject
    lateinit var featurePlaylistNavigator: FeaturePlaylistNavigator

    private val lastAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedLongItemClick,
        )
    }
    private val lastArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedLongItemClick,
        )
    }
    private val newAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedLongItemClick,
        )
    }
    private val newArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(
            onItemClick = ::onNestedItemClick,
            onItemLongClick = ::onNestedLongItemClick,
        )
    }

    private fun onNestedItemClick(mediaId: MediaId) {
        featureDetailNavigator.toDetail(requireActivity(), mediaId)
    }

    private fun onNestedLongItemClick(view: View, mediaId: MediaId) {
        featureMainPopupNavigator.toItemDialog(view, mediaId)
    }

    private val viewModel by activityViewModels<TabFragmentViewModel>()

    internal val source by argument<MediaIdCategory>(ARGUMENTS_SOURCE)
    private val category by lazyFast { source.toTabCategory() }

    private val mediaProvider: MediaProvider
        get() = requireActivity().findInContext()

    private val adapter by lazyFast {
        TabFragmentAdapter(
            onShuffleClick = { mediaProvider.shuffle(it, null) },
            setupNestedList = this,
            onItemClick = { item ->
                if (item is DisplayableTrack){
                    val sort = viewModel.getAllTracksSortOrder(item.mediaId)
                    mediaProvider.playFromMediaId(item.mediaId, null, sort)
                } else if (item is DisplayableAlbum){
                    featureDetailNavigator.toDetail(requireActivity(), item.mediaId)
                }
            },
            onItemLongClick = { view, mediaId ->
                featureMainPopupNavigator.toItemDialog(view, mediaId)
            }
        )
    }

    private fun handleEmptyStateVisibility(isEmpty: Boolean) {
        emptyStateText.isVisible = isEmpty
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
            list.updatePadding(right = requireContext().dimen(dev.olog.ui.R.dimen.playing_queue_margin_horizontal))
        }

        val scrollableLayoutId = when (category) {
            TabCategory.SONGS -> R.layout.item_tab_song
            TabCategory.PODCASTS -> R.layout.item_tab_podcast
            TabCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
        sidebar.scrollableLayoutId = scrollableLayoutId

        fab.isVisible = category == TabCategory.PLAYLISTS || category == TabCategory.PODCASTS_PLAYLIST

        viewModel.observeData(category)
            .collectOnViewLifecycle(this) { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.submitList(list)
                sidebar.onDataChanged(list)
            }

        viewModel.observeSpanCount(category)
            .drop(1) // drop initial value, already used
            .collectOnViewLifecycle(this) {
                if (list != null && list.isLaidOut) {
                    TransitionManager.beginDelayedTransition(list)
                    (gridLayoutManager.spanSizeLookup as AbsSpanSizeLookup).requestedSpanSize = it
                    adapter.notifyDataSetChanged()
                }
            }

        when (category) {
            TabCategory.ALBUMS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_ALBUMS)
                    .collectOnViewLifecycle(this) { lastAlbumsAdapter.submitList(it) }
                viewModel.observeData(TabCategory.RECENTLY_ADDED_ALBUMS)
                    .collectOnViewLifecycle(this) { newAlbumsAdapter.submitList(it) }
            }
            TabCategory.ARTISTS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_ARTISTS)
                    .collectOnViewLifecycle(this) { lastArtistsAdapter.submitList(it) }
                viewModel.observeData(TabCategory.RECENTLY_ADDED_ARTISTS)
                    .collectOnViewLifecycle(this) { newArtistsAdapter.submitList(it) }
            }
            TabCategory.PODCASTS_ALBUMS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_PODCAST_ALBUMS)
                    .collectOnViewLifecycle(this) { lastAlbumsAdapter.submitList(it) }
                viewModel.observeData(TabCategory.RECENTLY_ADDED_PODCAST_ALBUMS)
                    .collectOnViewLifecycle(this) { newAlbumsAdapter.submitList(it) }
            }
            TabCategory.PODCASTS_ARTISTS -> {
                viewModel.observeData(TabCategory.LAST_PLAYED_PODCAST_ARTISTS)
                    .collectOnViewLifecycle(this) { lastArtistsAdapter.submitList(it) }
                viewModel.observeData(TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS)
                    .collectOnViewLifecycle(this) { newArtistsAdapter.submitList(it) }
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
            featurePlaylistNavigator.toCreatePlaylist(requireActivity(), type)

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