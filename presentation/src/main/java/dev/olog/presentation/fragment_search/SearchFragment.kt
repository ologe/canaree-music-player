package dev.olog.presentation.fragment_search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.ImeUtils
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.extension.subscribe
import kotlinx.android.synthetic.main.fragment_search.view.*
import javax.inject.Inject

class SearchFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchFragment"

        fun newInstance(): SearchFragment{
            return SearchFragment()
        }
    }

    @Inject lateinit var adapter : SearchFragmentAdapter
    @Inject lateinit var albumAdapter: SearchFragmentAlbumAdapter
    @Inject lateinit var artistAdapter: SearchFragmentArtistAdapter
    @Inject lateinit var viewModel: SearchFragmentViewModel
    @Inject lateinit var recycledViewPool: RecyclerView.RecycledViewPool
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, { map ->
            val itemCount = map.values.sumBy { it.size }
            val visibility = if (itemCount == 0) View.VISIBLE else View.GONE
            view!!.search.visibility = visibility
            view!!.searchText.visibility = visibility
            view!!.list.visibility = if (itemCount == 0) View.GONE else View.VISIBLE

            val albums = map[SearchType.ALBUMS]!!
            val artists = map[SearchType.ARTISTS]!!
            albumAdapter.updateDataSet(albums)
            artistAdapter.updateDataSet(artists)
            viewModel.adjustDataMap(map)
            adapter.updateDataSet(map)
            startPostponedEnterTransition()
        })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.recycledViewPool = recycledViewPool

        RxTextView.afterTextChangeEvents(view.editText)
                .map { it.editable()!!.toString() }
                .filter { TextUtils.isEmpty(it) || it.length >= 2 }
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
    }

    override fun onPause() {
        super.onPause()
        view!!.clear.setOnClickListener(null)
        view!!.back.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        view!!.editText.clearFocus()
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

}