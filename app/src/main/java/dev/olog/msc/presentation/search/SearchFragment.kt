package dev.olog.msc.presentation.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.media.MediaProvider
import dev.olog.msc.R
import dev.olog.msc.catchNothing
import dev.olog.msc.presentation.search.adapter.SearchFragmentAdapter
import dev.olog.msc.presentation.search.adapter.SearchFragmentNestedAdapter
import dev.olog.msc.presentation.utils.ImeUtils
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.extensions.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class SearchFragment : BaseFragment(), SetupNestedList {

    companion object {
        val TAG = SearchFragment::class.java.name

        @JvmStatic
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<SearchFragmentViewModel>(viewModelFactory) }

    private val adapter by lazyFast {
        SearchFragmentAdapter(lifecycle, this, requireActivity() as MediaProvider, navigator, viewModel)
    }
    private val albumAdapter by lazyFast { SearchFragmentNestedAdapter(lifecycle, navigator, viewModel) }
    private val artistAdapter by lazyFast { SearchFragmentNestedAdapter(lifecycle, navigator, viewModel) }
    private val genreAdapter by lazyFast { SearchFragmentNestedAdapter(lifecycle, navigator, viewModel) }
    private val playlistAdapter by lazyFast { SearchFragmentNestedAdapter(lifecycle, navigator, viewModel) }

    private val folderAdapter by lazyFast { SearchFragmentNestedAdapter(lifecycle, navigator, viewModel) }
    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    @Inject
    lateinit var navigator: Navigator
    private lateinit var layoutManager: LinearLayoutManager

    private var queryDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

//        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.LEFT)
//        val touchHelper = ItemTouchHelper(callback)
//        touchHelper.attachToRecyclerView(list)
//        adapter.touchHelper = touchHelper TODO

        viewModel.observeData()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)

        viewModel.observeAlbumsData()
            .subscribe(viewLifecycleOwner, albumAdapter::updateDataSet)

        viewModel.observeArtistsData()
            .subscribe(viewLifecycleOwner, artistAdapter::updateDataSet)

        viewModel.observePlaylistsData()
            .subscribe(viewLifecycleOwner, playlistAdapter::updateDataSet)

        viewModel.observeFoldersData()
            .subscribe(viewLifecycleOwner, folderAdapter::updateDataSet)

        viewModel.observeGenresData()
            .subscribe(viewLifecycleOwner, genreAdapter::updateDataSet)
    }


    override fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_search_albums_horizontal_list -> setupHorizontalList(recyclerView, albumAdapter)
            R.layout.item_search_artists_horizontal_list -> setupHorizontalList(recyclerView, artistAdapter)
            R.layout.item_search_folder_horizontal_list -> setupHorizontalList(recyclerView, folderAdapter)
            R.layout.item_search_playlists_horizontal_list -> setupHorizontalList(recyclerView, playlistAdapter)
            R.layout.item_search_genre_horizontal_list -> setupHorizontalList(recyclerView, genreAdapter)
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: ObservableAdapter<*>) {
        val layoutManager = LinearLayoutManager(
            list.context,
            LinearLayoutManager.HORIZONTAL, false
        )
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

        val snapHelper = androidx.recyclerview.widget.LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    override fun onResume() {
        super.onResume()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        fab.setOnClickListener { ImeUtils.showIme(editText) }

        queryDisposable = RxTextView.afterTextChangeEvents(editText) // TODO
            .map { it.editable()!!.toString() }
            .filter { it.isBlank() || it.trim().length >= 2 }
            .debounceFirst()
            .subscribe(viewModel::updateQuery, Throwable::printStackTrace)

        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
        more.setOnClickListener { catchNothing { navigator.toMainPopup(it, null) } }
    }

    override fun onPause() {
        super.onPause()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        fab.setOnClickListener(null)
        queryDisposable.unsubscribe()
//        adapter.setAfterDataChanged(null)
        floatingWindow.setOnClickListener(null)
        more.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }


    override fun onStop() {
        super.onStop()
        ImeUtils.hideIme(editText)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}