package dev.olog.msc.presentation.search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.FirebaseAnalytics
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.utils.CircularReveal
import dev.olog.msc.presentation.utils.ImeUtils
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchFragment"

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    @Inject lateinit var adapter : SearchFragmentAdapter
    @Inject lateinit var albumAdapter: SearchFragmentAlbumAdapter
    @Inject lateinit var artistAdapter: SearchFragmentArtistAdapter
    @Inject lateinit var viewModel: SearchFragmentViewModel
    @Inject lateinit var recycledViewPool: RecyclerView.RecycledViewPool
    private lateinit var layoutManager: LinearLayoutManager

    private var showKeyboardDisposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            enterTransition = CircularReveal(activity!!.search)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FirebaseAnalytics.trackFragment(activity!!, TAG)

        viewModel.searchData.subscribe(this, { (map, query) ->
            val itemCount = map.values.sumBy { it.size }
            val isEmpty = itemCount == 0
            view!!.searchImage.toggleVisibility(isEmpty && query.length < 2)
            view!!.searchText.toggleVisibility(isEmpty && query.length < 2)
            view!!.list.toggleVisibility(!isEmpty)

            val showEmptyState = isEmpty && query.length >= 2
            view!!.emptyStateText.toggleVisibility(showEmptyState)
            view!!.emptyStateImage.toggleVisibility(showEmptyState)
            if(showEmptyState){
                view!!.emptyStateImage.resumeAnimation()
            } else {
                view!!.emptyStateImage.progress = 0f
            }

            val albums = map[SearchFragmentType.ALBUMS]!!
            val artists = map[SearchFragmentType.ARTISTS]!!
            albumAdapter.updateDataSet(albums)
            artistAdapter.updateDataSet(artists)
            viewModel.adjustDataMap(map)
            adapter.updateDataSet(map)
        })

        if (savedInstanceState == null){
            showKeyboardDisposable = Single.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ showKeyboard() }, Throwable::printStackTrace)
        }
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.recycledViewPool = recycledViewPool

        RxTextView.afterTextChangeEvents(view.editText)
                .map { it.editable()!!.toString() }
                .filter { it.isBlank() || it.trim().length >= 2 }
                .asLiveData()
                .subscribe(this, viewModel::setNewQuery)
    }

    override fun onResume() {
        super.onResume()
        view!!.clear.setOnClickListener {
            viewModel.setNewQuery("")
            view!!.editText.setText("")
        }
        view!!.back.setOnClickListener {
            ImeUtils.hideIme(view!!.editText)
            activity!!.onBackPressed()
        }
        view!!.root.setOnClickListener { showKeyboard() }
    }

    override fun onPause() {
        super.onPause()
        view!!.clear.setOnClickListener(null)
        view!!.back.setOnClickListener(null)
        view!!.root.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        view!!.editText.clearFocus()
        showKeyboardDisposable.unsubscribe()
    }

    private fun showKeyboard(){
        view!!.editText.requestFocus()
        ImeUtils.showIme(view!!.editText)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}