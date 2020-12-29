package dev.olog.feature.library.tab

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.library.R
import dev.olog.feature.library.tab.adapter.TabFragmentAdapter
import dev.olog.feature.library.tab.adapter.TabFragmentNestedAdapter
import dev.olog.feature.library.tab.model.TabFragmentCategory
import dev.olog.feature.library.tab.model.TabFragmentModel
import dev.olog.feature.library.tab.model.toTabCategory
import dev.olog.feature.library.tab.span.AbsSpanSizeLookup
import dev.olog.feature.library.tab.span.TabFragmentLayoutManagerFactory
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.scroller.WaveSideBarView
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class TabFragment : Fragment(R.layout.fragment_tab) {

    companion object {

        fun newInstance(category: MediaIdCategory): TabFragment {
            return TabFragment().withArguments(
                Params.CATEGORY to category.toTabCategory()
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator

    private val lastAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(navigator)
    }
    private val lastArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(navigator = navigator)
    }
    private val newAlbumsAdapter by lazyFast {
        TabFragmentNestedAdapter(navigator = navigator)
    }
    private val newArtistsAdapter by lazyFast {
        TabFragmentNestedAdapter(navigator = navigator)
    }

    private val viewModel by activityViewModels<TabFragmentViewModel>()

    internal val category by argument<TabFragmentCategory>(Params.CATEGORY)

    private val adapter by lazyFast {
        TabFragmentAdapter(
            navigator = navigator,
            mediaProvider = mediaProvider,
            viewModel = viewModel,
            setupNestedList = this::setupNestedList
        )
    }

    @Deprecated("refactor")
    private fun handleEmptyStateVisibility(isEmpty: Boolean) {
        emptyStateText.isVisible = isEmpty
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
        return category == TabFragmentCategory.PODCASTS || category == TabFragmentCategory.PODCASTS_PLAYLIST ||
                category == TabFragmentCategory.PODCASTS_ALBUMS || category == TabFragmentCategory.PODCASTS_ARTISTS
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val requestedSpanSize = viewModel.getSpanCount(category)
        val gridLayoutManager = TabFragmentLayoutManagerFactory.create(list, category, adapter, requestedSpanSize)
        list.layoutManager = gridLayoutManager
        list.adapter = adapter
        list.setHasFixedSize(true)

        if (category == TabFragmentCategory.SONGS || category == TabFragmentCategory.PODCASTS) {
//            list.updatePadding(right = dimen(R.dimen.playing_queue_margin_horizontal)) TODO
            list.updatePadding(right = dip(8)) // this is temp
        }

        fab.isVisible = category in listOf(TabFragmentCategory.PLAYLISTS, TabFragmentCategory.PODCASTS_PLAYLIST)

        viewModel.observeData(category)
            .onEach { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.submitList(list)
                sidebar.onDataChanged(viewModel.generateScrollerLetters(category, list))
            }.launchIn(this)

        viewModel.observeSpanCount(category)
            .drop(1) // drop initial value, already used
            .onEach {
                if (list != null && list.isLaidOut) {
                    TransitionManager.beginDelayedTransition(list)
                    (gridLayoutManager.spanSizeLookup as AbsSpanSizeLookup).requestedSpanSize = it
                    adapter.notifyDataSetChanged()
                }
            }.launchIn(this)

        when (category) {
            TabFragmentCategory.ALBUMS -> {
                viewModel.observeData(TabFragmentCategory.LAST_PLAYED_ALBUMS)
                    .filterIsInstance<List<TabFragmentModel.Album>>()
                    .onEach(lastAlbumsAdapter::submitList)
                    .launchIn(this)
                viewModel.observeData(TabFragmentCategory.RECENTLY_ADDED_ALBUMS)
                    .filterIsInstance<List<TabFragmentModel.Album>>()
                    .onEach(newAlbumsAdapter::submitList)
                    .launchIn(this)
            }
            TabFragmentCategory.ARTISTS -> {
                viewModel.observeData(TabFragmentCategory.LAST_PLAYED_ARTISTS)
                    .filterIsInstance<List<TabFragmentModel.Album>>()
                    .onEach(lastArtistsAdapter::submitList)
                    .launchIn(this)
                viewModel.observeData(TabFragmentCategory.RECENTLY_ADDED_ARTISTS)
                    .filterIsInstance<List<TabFragmentModel.Album>>()
                    .onEach(newArtistsAdapter::submitList)
                    .launchIn(this)
            }
            TabFragmentCategory.PODCASTS_ALBUMS -> {
                viewModel.observeData(TabFragmentCategory.LAST_PLAYED_PODCAST_ALBUMS)
                    .filterIsInstance<List<TabFragmentModel.Album>>()
                    .onEach(lastAlbumsAdapter::submitList)
                    .launchIn(this)
                viewModel.observeData(TabFragmentCategory.RECENTLY_ADDED_PODCAST_ALBUMS)
                    .filterIsInstance<List<TabFragmentModel.Album>>()
                    .onEach(newAlbumsAdapter::submitList)
                    .launchIn(this)
            }
            TabFragmentCategory.PODCASTS_ARTISTS -> {
                viewModel.observeData(TabFragmentCategory.LAST_PLAYED_PODCAST_ARTISTS)
                    .filterIsInstance<List<TabFragmentModel.Album>>()
                    .onEach(lastArtistsAdapter::submitList)
                    .launchIn(this)
                viewModel.observeData(TabFragmentCategory.RECENTLY_ADDED_PODCAST_ARTISTS)
                    .filterIsInstance<List<TabFragmentModel.Album>>()
                    .onEach(newArtistsAdapter::submitList)
                    .launchIn(this)
            }
            else -> {/*making lint happy*/
            }
        }

    }

    private fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_tab_last_played_album_list -> setupHorizontalList(
                recyclerView,
                lastAlbumsAdapter
            )
            R.layout.item_tab_last_played_artist_list -> setupHorizontalList(
                recyclerView,
                lastArtistsAdapter
            )
            R.layout.item_tab_recently_added_album_list -> setupHorizontalList(
                recyclerView,
                newAlbumsAdapter
            )
            R.layout.item_tab_recently_added_artist_list -> setupHorizontalList(
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
                if (category == TabFragmentCategory.PLAYLISTS) PlaylistType.TRACK else PlaylistType.PODCAST
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

        val position = viewModel.computePosition(category, adapter.currentList, letter)
        if (position != RecyclerView.NO_POSITION) {
            val layoutManager = list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

}