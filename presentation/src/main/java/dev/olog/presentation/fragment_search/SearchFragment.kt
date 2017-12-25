package dev.olog.presentation.fragment_search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.AnimationUtils
import dev.olog.presentation.utils.ImeUtils
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.extension.setLightStatusBar
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.toggleVisibility
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_tab_view_pager.*
import javax.inject.Inject

class SearchFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchFragment"
        const val ANIMATION_DONE = "$TAG.ANIMATION_DONE"

        fun newInstance(): SearchFragment = SearchFragment()
    }

    @Inject lateinit var adapter : SearchFragmentAdapter
    @Inject lateinit var albumAdapter: SearchFragmentAlbumAdapter
    @Inject lateinit var artistAdapter: SearchFragmentArtistAdapter
    @Inject lateinit var viewModel: SearchFragmentViewModel
    @Inject lateinit var recycledViewPool: RecyclerView.RecycledViewPool
    private lateinit var layoutManager: LinearLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.searchData.subscribe(this, { (map, query) ->
            val itemCount = map.values.sumBy { it.size }
            val isEmpty = itemCount == 0
            view!!.searchText.toggleVisibility(isEmpty)
            view!!.search.toggleVisibility(isEmpty && query.length < 2)
            view!!.searchText.toggleVisibility(isEmpty && query.length < 2)
            view!!.list.toggleVisibility(!isEmpty)
            view!!.emptyState.toggleVisibility(isEmpty && query.length >= 2)

            val albums = map[SearchFragmentType.ALBUMS]!!
            val artists = map[SearchFragmentType.ARTISTS]!!
            albumAdapter.updateDataSet(albums)
            artistAdapter.updateDataSet(artists)
            viewModel.adjustDataMap(map)
            adapter.updateDataSet(map)
        })

        activity!!.window.setLightStatusBar()
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

        if (savedInstanceState == null){
            view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
                    v?.removeOnLayoutChangeListener(this)
                    AnimationUtils.startCircularReveal(view.root, activity!!.search)
                }
            })
        }
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
        view!!.root.setOnClickListener {
            view!!.editText.requestFocus()
            ImeUtils.showIme(view!!.editText)
        }
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ANIMATION_DONE, true)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

    fun onBackPressed(){
        AnimationUtils.stopCircularReveal(view!!, activity!!.search,
                activity!!.supportFragmentManager)
    }

}