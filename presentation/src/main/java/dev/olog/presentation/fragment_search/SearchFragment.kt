package dev.olog.presentation.fragment_search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.ImeUtils
import dev.olog.presentation.utils.animation.CircularReveal
import dev.olog.presentation.utils.extension.setLightStatusBar
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.toggleVisibility
import dev.olog.presentation.utils.extension.withArguments
import dev.olog.shared_android.extension.asLiveData
import dev.olog.shared_android.isOreo
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_tab_view_pager.*
import javax.inject.Inject

class SearchFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchFragment"
        private const val ARGUMENT_SHOW_KEYBOARD = TAG + ".arguments.show_keyboard"

        fun newInstance(showKeyboard: Boolean): SearchFragment {
            return SearchFragment().withArguments(
                    ARGUMENT_SHOW_KEYBOARD to showKeyboard
            )
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
        if (savedInstanceState == null){
            enterTransition = CircularReveal(activity!!.search)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.searchData.subscribe(this, { (map, query) ->
            val itemCount = map.values.sumBy { it.size }
            val isEmpty = itemCount == 0
            view!!.searchText.toggleVisibility(isEmpty)
            view!!.search.toggleVisibility(isEmpty && query.length < 2)
            view!!.searchText.toggleVisibility(isEmpty && query.length < 2)
            view!!.list.toggleVisibility(!isEmpty)

            val showEmptyState = isEmpty && query.length >= 2
            view!!.emptyStateText.toggleVisibility(showEmptyState)
            view!!.emptyState.toggleVisibility(showEmptyState)
            if(showEmptyState && !view!!.emptyState.isAnimating){
                view!!.emptyState.progress = 0f
                view!!.emptyState.playAnimation()
            } else {
                view!!.emptyState.progress = 0f
            }

            val albums = map[SearchFragmentType.ALBUMS]!!
            val artists = map[SearchFragmentType.ARTISTS]!!
            albumAdapter.updateDataSet(albums)
            artistAdapter.updateDataSet(artists)
            viewModel.adjustDataMap(map)
            adapter.updateDataSet(map)
        })

        if (isOreo()){
            activity!!.window.setLightStatusBar()
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

        if (savedInstanceState == null){
            val mustShowKeyboard = arguments!!.getBoolean(ARGUMENT_SHOW_KEYBOARD)
            if (mustShowKeyboard){
                showKeyboard()
            }
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
    }

    private fun showKeyboard(){
        view!!.editText.requestFocus()
        ImeUtils.showIme(view!!.editText)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}