package dev.olog.msc.presentation.library.tab

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.GridLayoutManager
import android.view.View
import androidx.core.text.isDigitsOnly
import dagger.Lazy
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.widget.fast.scroller.WaveSideBarView
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject
import javax.inject.Provider

class TabFragment : BaseFragment() {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"

        @JvmStatic
        fun newInstance(category: MediaIdCategory): TabFragment {
            return TabFragment().withArguments(ARGUMENTS_SOURCE to category.ordinal)
        }
    }

    @Inject lateinit var adapter: TabFragmentAdapter
    @Inject lateinit var viewModel: TabFragmentViewModel
    @Inject lateinit var category: MediaIdCategory
    @Inject lateinit var lastAlbumsAdapter : Lazy<TabFragmentLastPlayedAlbumsAdapter>
    @Inject lateinit var lastArtistsAdapter : Lazy<TabFragmentLastPlayedArtistsAdapter>
    @Inject lateinit var layoutManager: Provider<GridLayoutManager>
    @Inject lateinit var navigator : Lazy<Navigator>

    private fun handleEmptyStateVisibility(isEmpty: Boolean){
        emptyStateText.toggleVisibility(isEmpty, true)
        if (isEmpty){
            val emptyText = resources.getStringArray(R.array.tab_empty_state)
            emptyStateText.text = emptyText[category.ordinal]
        }
    }

    @CallSuper
    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val gridLayoutManager = layoutManager.get()
        view.list.layoutManager = gridLayoutManager
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        applyMarginToList(view)

        val scrollableLayoutId = when (category) {
            MediaIdCategory.SONGS -> R.layout.item_tab_song
            MediaIdCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
        view.sidebar.scrollableLayoutId = scrollableLayoutId

        view.fab.toggleVisibility(category == MediaIdCategory.PLAYLISTS, true)

//        if (category == MediaIdCategory.ALBUMS){
//            viewModel.observeAlbumSpanSize(category)
//                    .asLiveData()
//                    .subscribe(viewLifecycleOwner, { (one, two) ->
//                        val spanSizeLookup = gridLayoutManager.spanSizeLookup as AbsSpanSizeLookup
//                        spanSizeLookup.updateSpan(one, two)
//                        gridLayoutManager.spanSizeLookup = spanSizeLookup
//                        view.list.invalidate()
//                    })
//        }

        viewModel.observeData(category)
                .subscribe(viewLifecycleOwner) { list ->
                    handleEmptyStateVisibility(list.isEmpty())
                    adapter.updateDataSet(list)
                    sidebar.onDataChanged(list)
                }

        when (category){
            MediaIdCategory.ALBUMS -> {
                viewModel.observeData(MediaIdCategory.RECENT_ALBUMS)
                        .subscribe(viewLifecycleOwner) { lastAlbumsAdapter.get().updateDataSet(it) }
            }
            MediaIdCategory.ARTISTS -> {
                viewModel.observeData(MediaIdCategory.RECENT_ARTISTS)
                        .subscribe(viewLifecycleOwner) { lastArtistsAdapter.get().updateDataSet(it) }
            }
            else -> {/*making lint happy*/}
        }
    }

    override fun onResume() {
        super.onResume()
        sidebar.setListener(letterTouchListener)
        fab.setOnClickListener { navigator.get().toChooseTracksForPlaylistFragment(fab) }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        fab.setOnClickListener(null)
    }

    private fun applyMarginToList(view: View){
        if (category == MediaIdCategory.SONGS){
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
                    if (it.title.isBlank()) false
                    else it.title[0].toUpperCase().toString().isDigitsOnly()
                }
            }
            "?" -> adapter.indexOf {
                if (it.type != scrollableItem){
                    false
                } else {
                    if (it.title.isBlank()) false
                    else it.title[0].toUpperCase().toString() > "Z"
                }
            }
            else -> adapter.indexOf {
                if (it.type != scrollableItem){
                    false
                } else {
                    if (it.title.isBlank()) false
                    else it.title[0].toUpperCase().toString() == letter
                }
            }
        }
        if (position != -1){
            val layoutManager = list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_tab
}