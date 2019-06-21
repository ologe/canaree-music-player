package dev.olog.msc.presentation.library.tab

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.R
import dev.olog.msc.domain.entity.PlaylistType
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.parentViewModelProvider
import dev.olog.msc.presentation.utils.lazyFast
import dev.olog.msc.presentation.widget.fast.scroller.WaveSideBarView
import dev.olog.core.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject

class TabFragment : BaseFragment() {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"

        @JvmStatic
        fun newInstance(category: MediaIdCategory): TabFragment {
            return TabFragment().withArguments(ARGUMENTS_SOURCE to category.ordinal)
        }
    }

    @Inject lateinit var navigator : Navigator
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val lastAlbumsAdapter by lazyFast { TabHorizontalAdapters.getLastPlayedAlbums(this) }
    private val lastArtistsAdapter by lazyFast { TabHorizontalAdapters.getLastPlayedArtists(this) }
    private val newAlbumsAdapter by lazyFast { TabHorizontalAdapters.getNewAlbums(this)  }
    private val newArtistsAdapter by lazyFast { TabHorizontalAdapters.getNewArtists(this) }

    private val viewModel by lazyFast { parentViewModelProvider<TabFragmentViewModel>(viewModelFactory) }

    internal val category by lazyFast {
        val ordinalCategory = arguments!!.getInt(TabFragment.ARGUMENTS_SOURCE)
        MediaIdCategory.values()[ordinalCategory]
    }

    private val adapter by lazyFast { TabFragmentAdapter(
            lifecycle, navigator, act as MediaProvider, lastArtistsAdapter,lastAlbumsAdapter,
            newAlbumsAdapter, newArtistsAdapter, viewModel
    ) }

    private fun handleEmptyStateVisibility(isEmpty: Boolean){
        emptyStateText.toggleVisibility(isEmpty, true)
        if (isEmpty){
             if (isPodcastFragment()){
                 val emptyText = resources.getStringArray(R.array.tab_empty_podcast)
                 emptyStateText.text = emptyText[category.ordinal - 6]
            } else {
                 val emptyText = resources.getStringArray(R.array.tab_empty_state)
                 emptyStateText.text = emptyText[category.ordinal]
            }
        }
    }

    private fun isPodcastFragment(): Boolean {
        return category == MediaIdCategory.PODCASTS || category == MediaIdCategory.PODCASTS_PLAYLIST ||
                category == MediaIdCategory.PODCASTS_ALBUMS || category == MediaIdCategory.PODCASTS_ARTISTS
    }

    @CallSuper
    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val gridLayoutManager = LayoutManagerFactory.get(act, category, adapter)
        view.list.layoutManager = gridLayoutManager
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        applyMarginToList(view)

        val scrollableLayoutId = when (category) {
            MediaIdCategory.SONGS -> R.layout.item_tab_song
            MediaIdCategory.PODCASTS -> R.layout.item_tab_podcast
            MediaIdCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
        view.sidebar.scrollableLayoutId = scrollableLayoutId

        view.fab.toggleVisibility(category == MediaIdCategory.PLAYLISTS ||
                category == MediaIdCategory.PODCASTS_PLAYLIST, true)

        viewModel.observeData(category)
                .subscribe(viewLifecycleOwner) { list ->
                    handleEmptyStateVisibility(list.isEmpty())
                    adapter.updateDataSet(list)
                    sidebar.onDataChanged(list)
                }

        when (category){
            MediaIdCategory.ALBUMS -> {
                viewModel.observeData(MediaIdCategory.RECENT_ALBUMS)
                        .subscribe(viewLifecycleOwner) { lastAlbumsAdapter!!.updateDataSet(it) }
                viewModel.observeData(MediaIdCategory.NEW_ALBUMS)
                        .subscribe(viewLifecycleOwner) { newAlbumsAdapter!!.updateDataSet(it) }
            }
            MediaIdCategory.ARTISTS -> {
                viewModel.observeData(MediaIdCategory.RECENT_ARTISTS)
                        .subscribe(viewLifecycleOwner) { lastArtistsAdapter!!.updateDataSet(it) }
                viewModel.observeData(MediaIdCategory.NEW_ARTISTS)
                        .subscribe(viewLifecycleOwner) { newArtistsAdapter!!.updateDataSet(it) }
            }
            MediaIdCategory.PODCASTS_ALBUMS -> {
                viewModel.observeData(MediaIdCategory.RECENT_PODCAST_ALBUMS)
                        .subscribe(viewLifecycleOwner) { lastAlbumsAdapter!!.updateDataSet(it) }
                viewModel.observeData(MediaIdCategory.NEW_PODCSAT_ALBUMS)
                        .subscribe(viewLifecycleOwner) { newAlbumsAdapter!!.updateDataSet(it) }
            }
            MediaIdCategory.PODCASTS_ARTISTS -> {
                viewModel.observeData(MediaIdCategory.RECENT_PODCAST_ARTISTS)
                        .subscribe(viewLifecycleOwner) { lastArtistsAdapter!!.updateDataSet(it) }
                viewModel.observeData(MediaIdCategory.NEW_PODCSAT_ARTISTS)
                        .subscribe(viewLifecycleOwner) { newArtistsAdapter!!.updateDataSet(it) }
            }
            else -> {/*making lint happy*/}
        }
    }

    override fun onResume() {
        super.onResume()
        sidebar.setListener(letterTouchListener)
        fab.setOnClickListener {
            if (category == MediaIdCategory.PLAYLISTS){
                navigator.toChooseTracksForPlaylistFragment(PlaylistType.TRACK)
            } else {
                navigator.toChooseTracksForPlaylistFragment(PlaylistType.PODCAST)
            }

        }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        fab?.setOnClickListener(null)
    }

    private fun applyMarginToList(view: View){
        if (category == MediaIdCategory.SONGS || category == MediaIdCategory.PODCASTS){
            // start/end margin is set in item
            view.list.setPadding(view.list.paddingLeft, view.list.paddingTop,
                    view.list.paddingRight, ctx.dimen(R.dimen.tab_margin_bottom))
        } else {
            view.list.setPadding(
                    ctx.dimen(R.dimen.tab_margin_start), ctx.dimen(R.dimen.tab_margin_top),
                    ctx.dimen(R.dimen.tab_margin_end), ctx.dimen(R.dimen.tab_margin_bottom)
            )
        }

    }

    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
        list.stopScroll()

        val scrollableItem = sidebar.scrollableLayoutId

        val position = when (letter){
            TextUtils.MIDDLE_DOT -> -1
            "#" -> adapter.indexOf {
                if (it.type != scrollableItem){
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].toUpperCase().toString().isDigitsOnly()
                }
            }
            "?" -> adapter.indexOf {
                if (it.type != scrollableItem){
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].toUpperCase().toString() > "Z"
                }
            }
            else -> adapter.indexOf {
                if (it.type != scrollableItem){
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].toUpperCase().toString() == letter
                }
            }
        }
        if (position != -1){
            val layoutManager = list.layoutManager as androidx.recyclerview.widget.GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    private fun getCurrentSorting(item: DisplayableItem): String {
        return when (category){
            MediaIdCategory.SONGS -> {
                val sortOrder = viewModel.getAllTracksSortOrder()
                when (sortOrder.type){
                    SortType.ARTIST -> item.subtitle!!
                    SortType.ALBUM -> item.subtitle!!.substring(item.subtitle.indexOf(TextUtils.MIDDLE_DOT) + 1).trim()
                    else -> item.title
                }
            }
            MediaIdCategory.ALBUMS -> {
                val sortOrder = viewModel.getAllAlbumsSortOrder()
                when (sortOrder.type){
                    SortType.TITLE -> item.title
                    else -> item.subtitle!!
                }
            }
            else -> item.title
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_tab
}