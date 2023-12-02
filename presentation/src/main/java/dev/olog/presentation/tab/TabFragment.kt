package dev.olog.presentation.tab

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentTabBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.adapter.TabFragmentAdapter
import dev.olog.presentation.tab.adapter.TabFragmentItem
import dev.olog.presentation.tab.layoutmanager.LayoutManagerFactory
import dev.olog.presentation.tab.layoutmanager.TabSpanSizeLookup
import dev.olog.presentation.widgets.fascroller.ScrollableItem
import dev.olog.presentation.widgets.fascroller.WaveSideBarView
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.android.extensions.viewLifecycleScope
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabFragment : Fragment(R.layout.fragment_tab) {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"

        fun newInstance(category: MediaIdCategory): TabFragment {
            return TabFragment().withArguments(ARGUMENTS_SOURCE to category)
        }
    }

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentTabBinding::bind) { binding ->
        binding.list.adapter = null
    }

    private val viewModel by viewModels<TabFragmentViewModel>(
        ownerProducer = { requireParentFragment() }
    )

    internal val category: TabCategory by lazyFast {
        getArgument<MediaIdCategory>(ARGUMENTS_SOURCE).toTabCategory()
    }

    private val adapter by lazyFast {
        TabFragmentAdapter(
            navigator = navigator,
            mediaProvider = act.findInContext(),
            viewModel = viewModel
        )
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
        val gridLayoutManager = LayoutManagerFactory.create(binding.list, adapter, requestedSpanSize)
        binding.list.layoutManager = gridLayoutManager
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        updateListPadding(viewModel.getSpanCount(category))

        binding.fab.toggleVisibility(
            category == TabCategory.PLAYLISTS ||
                    category == TabCategory.PODCASTS_PLAYLIST, true
        )

        viewModel.observeData(category)
            .subscribe(viewLifecycleOwner) { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.submitList(list)
            }

        viewModel.observeSortLetters(category)
            .subscribe(viewLifecycleOwner) { list ->
                binding.sidebar.onLettersChanged(list)
            }

        viewLifecycleScope.launch {
            viewModel.observeSpanCount(category)
                .drop(1) // drop initial value, already used
                .collect {
                    // TODO improve?
                    if (binding.list.isLaidOut) {
                        (gridLayoutManager.spanSizeLookup as TabSpanSizeLookup).requestedSpanSize = it
                        updateListPadding(it)
                        adapter.notifyDataSetChanged()
                    }
                }
        }
    }

    private fun updateListPadding(spanCount: Int) {
        if (spanCount == 1 &&
            category != TabCategory.PLAYLISTS &&
            category != TabCategory.PODCASTS_PLAYLIST) {
            binding.list.updatePadding(left = 0, right = requireContext().dip(8))
        } else {
            binding.list.updatePadding(
                left = requireContext().dimen(R.dimen.playing_queue_margin_horizontal),
                right = requireContext().dimen(R.dimen.tab_margin_end),
            )
        }
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

        val position = when (letter) {
            TextUtils.MIDDLE_DOT -> -1
            "#" -> adapter.indexOf {
                if (it !is ScrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercase().isDigitsOnly()
                }
            }
            "?" -> adapter.indexOf {
                if (it !is ScrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercase() > "Z"
                }
            }
            else -> adapter.indexOf {
                if (it !is ScrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercase() == letter
                }
            }
        }
        if (position != -1) {
            val layoutManager = binding.list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    private fun getCurrentSorting(item: TabFragmentItem): String {
        val sort = viewModel.getSort(category)
        if (item is ScrollableItem) {
            return item.getText(sort.type)
        }
        return ""
    }
}