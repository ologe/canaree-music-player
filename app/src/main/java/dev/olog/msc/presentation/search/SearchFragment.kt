package dev.olog.msc.presentation.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.catchNothing
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.drag.TouchHelperAdapterCallback
import dev.olog.presentation.navigator.Navigator
import dev.olog.msc.presentation.utils.ImeUtils
import dev.olog.shared.extensions.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import javax.inject.Inject

class SearchFragment : BaseFragment() {

    companion object {
        val TAG = SearchFragment::class.java.name

        @JvmStatic
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<SearchFragmentViewModel>(
            viewModelFactory
        )
    }

    @Inject lateinit var adapter : SearchFragmentAdapter
    @Inject lateinit var albumAdapter: SearchFragmentAlbumAdapter
    @Inject lateinit var artistAdapter: SearchFragmentArtistAdapter
    @Inject lateinit var genreAdapter: SearchFragmentGenreAdapter
    @Inject lateinit var playlistAdapter: SearchFragmentPlaylistAdapter
    @Inject lateinit var folderAdapter: SearchFragmentFolderAdapter
    @Inject lateinit var recycledViewPool : androidx.recyclerview.widget.RecyclerView.RecycledViewPool
    @Inject lateinit var navigator: Navigator
    private lateinit var layoutManager: androidx.recyclerview.widget.LinearLayoutManager

    private var queryDisposable : Disposable? = null

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setRecycledViewPool(recycledViewPool)
        view.list.setHasFixedSize(true)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.LEFT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        viewModel.searchData.subscribe(viewLifecycleOwner) { (map, query) ->

            if (query.isNotBlank()){
                val isEmpty = map.map { it.value }
                        .map { it.isEmpty() }
                        .reduce { all, current -> all && current }
            }

            val albums = map[SearchFragmentType.ALBUMS]!!.toList()
            val artists = map[SearchFragmentType.ARTISTS]!!.toList()
            val playlists = map[SearchFragmentType.PLAYLISTS]!!.toList()
            val genres = map[SearchFragmentType.GENRES]!!.toList()
            val folders = map[SearchFragmentType.FOLDERS]!!.toList()
            albumAdapter.updateDataSet(albums)
            artistAdapter.updateDataSet(artists)
            playlistAdapter.updateDataSet(playlists)
            genreAdapter.updateDataSet(genres)
            folderAdapter.updateDataSet(folders)
            adapter.updateDataSet(viewModel.adjustDataMap(map))
        }
    }

    override fun onResume() {
        super.onResume()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        keyboard.setOnClickListener { ImeUtils.showIme(editText) }

        queryDisposable = RxTextView.afterTextChangeEvents(editText)
                .map { it.editable()!!.toString() }
                .filter { it.isBlank() || it.trim().length >= 2 }
                .subscribe(viewModel::setNewQuery, Throwable::printStackTrace)

        adapter.setAfterDataChanged({
            updateLayoutVisibility(it)
        }, false)

        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
        more.setOnClickListener { catchNothing { navigator.toMainPopup(it, null) } }
    }

    override fun onPause() {
        super.onPause()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        keyboard.setOnClickListener(null)
        queryDisposable.unsubscribe()
        adapter.setAfterDataChanged(null)
        floatingWindow.setOnClickListener(null)
        more.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission(){
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }


    private fun updateLayoutVisibility(list: List<*>){
        val itemCount = list.size
        val isEmpty = itemCount == 0
        val queryLength = editText.text.toString().length
        this.searchImage.toggleVisibility(isEmpty && queryLength < 2, true)
        this.list.toggleVisibility(!isEmpty, true)

        val showEmptyState = isEmpty && queryLength >= 2
        this.emptyStateText.toggleVisibility(showEmptyState, true)
    }

    override fun onStop() {
        super.onStop()
        ImeUtils.hideIme(editText)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}