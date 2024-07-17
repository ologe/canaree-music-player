package dev.olog.presentation.tab

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.sort.SortType
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.indexOf
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentTabBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.adapter.TabFragmentAdapter
import dev.olog.presentation.tab.adapter.TabItem
import dev.olog.presentation.tab.adapter.isScrollable
import dev.olog.presentation.tab.layoutmanager.AbsSpanSizeLookup
import dev.olog.presentation.tab.layoutmanager.LayoutManagerFactory
import dev.olog.presentation.widgets.fascroller.WaveSideBarView
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabFragment : Fragment(R.layout.fragment_tab) {

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

    private val viewModel by activityViewModels<TabFragmentViewModel>()

    internal val category: MediaIdCategory by lazyFast {
        val categoryString = getArgument<String>(ARGUMENTS_SOURCE)
        MediaIdCategory.valueOf(categoryString)
    }

    private val adapter by lazyFast {
        TabFragmentAdapter(navigator, act.findInContext<MediaProvider>(), viewModel)
    }

    private val binding by viewBinding(FragmentTabBinding::bind) {
        it.list.adapter = null
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
        return category == MediaIdCategory.PODCASTS || category == MediaIdCategory.PODCASTS_PLAYLIST ||
                category == MediaIdCategory.PODCASTS_ALBUMS || category == MediaIdCategory.PODCASTS_ARTISTS
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val requestedSpanSize = viewModel.getSpanCount(category)
        val gridLayoutManager = LayoutManagerFactory.get(binding.list, category, adapter, requestedSpanSize)
        binding.list.layoutManager = gridLayoutManager
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        if (category == MediaIdCategory.SONGS || category == MediaIdCategory.PODCASTS) {
            binding.list.updatePadding(right = requireContext().dimen(R.dimen.playing_queue_margin_horizontal))
        }

        binding.fab.toggleVisibility(
            category == MediaIdCategory.PLAYLISTS ||
                    category == MediaIdCategory.PODCASTS_PLAYLIST, true
        )

        viewModel.observeData(category)
            .subscribe(viewLifecycleOwner) { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.submitList(list)
                binding.sidebar.onDataChanged(list) {
                    // TODO this should follow the sort
                    when (it) {
                        is TabItem.Song -> it.title.firstOrNull()
                        is TabItem.Podcast -> it.title.firstOrNull()
                        is TabItem.Album -> it.title.firstOrNull()
                        is TabItem.Header,
                        is TabItem.HorizontalList,
                        TabItem.Shuffle -> null
                    }
                }
            }

        viewLifecycleScope.launch {
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

    }

    override fun onResume() {
        super.onResume()
        binding.sidebar.setListener(letterTouchListener)
        binding.fab.setOnClickListener {
            val type =
                if (category == MediaIdCategory.PLAYLISTS) PlaylistType.TRACK else PlaylistType.PODCAST
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

        val position = when (letter) {
            TextUtils.MIDDLE_DOT -> -1
            "#" -> adapter.indexOf {
                if (!it.isScrollable()) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercaseChar().toString().isDigitsOnly()
                }
            }
            "?" -> adapter.indexOf {
                if (!it.isScrollable()) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercaseChar().toString() > "Z"
                }
            }
            else -> adapter.indexOf {
                if (!it.isScrollable()) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercaseChar().toString() == letter
                }
            }
        }
        if (position != -1) {
            val layoutManager = binding.list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    private fun getCurrentSorting(item: TabItem): String {
        return when (item) {
            is TabItem.Song -> {
                val sortOrder = viewModel.getAllTracksSortOrder()
                when (sortOrder.type) {
                    SortType.ARTIST -> item.artist
                    SortType.ALBUM -> item.album
                    else -> item.title
                }
            }
            is TabItem.Podcast -> item.title
            is TabItem.Album -> {
                when (item.mediaId.category) {
                    MediaIdCategory.ALBUMS -> {
                        val sortOrder = viewModel.getAllAlbumsSortOrder()
                        when (sortOrder.type) {
                            SortType.TITLE -> item.title
                            else -> item.subtitle.orEmpty()
                        }
                    }
                    else -> item.title
                }
            }

            is TabItem.Header,
            is TabItem.HorizontalList,
            TabItem.Shuffle -> ""
        }
    }

}