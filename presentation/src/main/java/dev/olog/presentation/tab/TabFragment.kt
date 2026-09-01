package dev.olog.presentation.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.sort.SortType
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.databinding.FragmentTabBinding
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

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

    private val viewModel by viewModels<TabFragmentViewModel>({ requireParentFragment() })

    internal val category: TabCategory by lazyFast {
        val categoryString = getArgument<String>(ARGUMENTS_SOURCE)
        MediaIdCategory.valueOf(categoryString).toTabCategory()
    }

    private val adapter by lazyFast {
        TabFragmentAdapter(lifecycle, navigator, act.asType<MediaProvider>(), viewModel, this)
    }

    private var _binding: FragmentTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.list.adapter = null
        _binding = null
    }

    private fun handleEmptyStateVisibility(isEmpty: Boolean) {
        binding.emptyStateText.toggleVisibility(isEmpty, true)
        if (isEmpty) {
            if (isPodcastFragment()) {
                val emptyText = resources.getStringArray(R.array.tab_empty_podcast)
                binding.emptyStateText.text = emptyText[category.ordinal - 6]
            } else {
                val emptyText = resources.getStringArray(R.array.tab_empty_state)
                binding.emptyStateText.text = emptyText[category.ordinal]
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
        val gridLayoutManager = LayoutManagerFactory.get(binding.list, category, adapter, requestedSpanSize)
        binding.list.layoutManager = gridLayoutManager
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        if (category == TabCategory.SONGS || category == TabCategory.PODCASTS) {
            binding.list.updatePadding(right = requireContext().dimen(R.dimen.playing_queue_margin_horizontal))
        }

        val scrollableLayoutId = when (category) {
            TabCategory.SONGS -> R.layout.item_tab_song
            TabCategory.PODCASTS -> R.layout.item_tab_podcast
            TabCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
        binding.sidebar.scrollableLayoutId = scrollableLayoutId

        binding.fab.toggleVisibility(
            category == TabCategory.PLAYLISTS ||
                    category == TabCategory.PODCASTS_PLAYLIST, true
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeData(category)
                .subscribe(viewLifecycleOwner) { list ->
                    handleEmptyStateVisibility(list.isEmpty())
                    adapter.updateDataSet(list)
                    binding.sidebar.onDataChanged(list)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeSpanCount(category)
                .drop(1) // drop initial value, already used
                .collect {
                    if (binding.list.isLaidOut) {
                        TransitionManager.beginDelayedTransition(binding.list)
                        (gridLayoutManager.spanSizeLookup as AbsSpanSizeLookup).requestedSpanSize = it
                        adapter.notifyDataSetChanged()
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
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
        binding.sidebar.setListener(letterTouchListener)
        binding.fab.setOnClickListener {
            val type =
                if (category == TabCategory.PLAYLISTS) PlaylistType.TRACK else PlaylistType.PODCAST
            navigator.toChooseTracksForPlaylistFragment(type)

        }
    }

    override fun onPause() {
        super.onPause()
        binding.sidebar.setListener(null)
        binding.fab.setOnClickListener(null)
    }

    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
        binding.list.stopScroll()

        val scrollableItem = binding.sidebar.scrollableLayoutId

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
            val layoutManager = binding.list.layoutManager as GridLayoutManager
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

}