package dev.olog.feature.library.tab

import android.os.Bundle
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.scroller.WaveSideBarView
import dev.olog.feature.library.R
import dev.olog.feature.library.tab.adapter.TabFragmentAdapterFactory
import dev.olog.feature.library.tab.layout.manager.TabFragmentLayoutManagerFactory
import dev.olog.feature.library.tab.layout.manager.TabFragmentSpanSizeLookup
import dev.olog.feature.playlist.FeaturePlaylistNavigator
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.collectOnLifecycle
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.android.extensions.requireArgument
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import javax.inject.Inject

@AndroidEntryPoint
class TabFragment : Fragment(R.layout.fragment_tab) {

    companion object {

        const val CATEGORY = "category"
        const val TYPE = "type"

        fun newInstance(
            category: MediaUri.Category,
            type: MediaStoreType,
        ): TabFragment {
            return TabFragment().withArguments(
                CATEGORY to category,
                TYPE to type,
            )
        }
    }

    private val category by requireArgument<MediaUri.Category>(CATEGORY)
    private val mediaStoreType by requireArgument<MediaStoreType>(TYPE)

    @Inject
    lateinit var playlistNavigator: FeaturePlaylistNavigator
    @Inject
    lateinit var adapterFactory: TabFragmentAdapterFactory

    private val viewModel by viewModels<TabFragmentViewModel>()

    private val adapter by lazyFast {
        adapterFactory.create(category, mediaStoreType)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val requestedSpanSize = viewModel.spanCount().get()
        val gridLayoutManager = TabFragmentLayoutManagerFactory.get(list, category, adapter, requestedSpanSize)
        list.layoutManager = gridLayoutManager
        list.adapter = adapter
        list.setHasFixedSize(true)

        if (category == MediaUri.Category.Track) {
            list.updatePadding(right = requireContext().dimen(dev.olog.shared.android.R.dimen.playing_queue_margin_horizontal))
        }

        fab.isVisible = category == MediaUri.Category.Playlist
        emptyStateText.text = getEmptyState()
        setupScrollers()

        viewModel.data
            .collectOnLifecycle(this) { list ->
                emptyStateText.isVisible = list.isEmpty()
                adapter.submitMain(category, list)
                sidebar.onDataChanged(list)
            }

        viewModel.autoPlaylists
            .collectOnLifecycle(this) {
                adapter.submitAutoPlaylist(it)
            }

        viewModel.recentlyAdded
            .combine(viewModel.recentlyPlayed) { added, played ->
                added to played
            }
            .collectOnLifecycle(this) { (added, played) ->
                adapter.submitRecent(added, played)
            }

        viewModel.spanCount().observe()
            .drop(1) // drop initial value, already used
            .distinctUntilChanged()
            .collectOnLifecycle(this) {
                (gridLayoutManager.spanSizeLookup as TabFragmentSpanSizeLookup).requestedSpanSize = it
//                TransitionManager.beginDelayedTransition(list)
                list.requestLayout()
            }

        fab.setOnClickListener {
            playlistNavigator.toChooseTracksForPlaylistFragment(requireActivity(), mediaStoreType)
        }
    }

    private fun getEmptyState(): String {
        // TODO separate localization.R.array.tab_empty_state
        return "todo empty"
//        return when (category) {
//            MediaUri.Category.Folder -> TODO()
//            MediaUri.Category.Playlist -> TODO()
//            MediaUri.Category.Track -> TODO()
//            MediaUri.Category.Author -> TODO()
//            MediaUri.Category.Collection -> TODO()
//            MediaUri.Category.Genre -> TODO()
//        }
    }

    private fun setupScrollers() {
//        fastScroller.attachRecyclerView(list) todo
        fastScroller.showBubble(false)
        sidebar.setListener(letterTouchListener)
    }

    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
        list.stopScroll()

        val position = when (letter) {
            TextUtils.MIDDLE_DOT -> RecyclerView.NO_POSITION
            "#" -> adapter.indexOf {
                val sorting = viewModel.getCurrentSorting(it)
                sorting.firstOrNull()?.uppercase()?.isDigitsOnly() == true
            }
            "?" -> adapter.indexOf {
                val sorting = viewModel.getCurrentSorting(it)
                sorting.firstOrNull()?.uppercase().orEmpty() > "Z"
            }
            else -> adapter.indexOf {
                val sorting = viewModel.getCurrentSorting(it)
                sorting.firstOrNull()?.uppercase().orEmpty() == letter
            }
        }
        if (position != RecyclerView.NO_POSITION) {
            val layoutManager = list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

}