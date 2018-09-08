package dev.olog.msc.presentation.search

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.library.categories.CategoriesFragment
import dev.olog.msc.presentation.utils.ImeUtils
import dev.olog.msc.presentation.utils.animation.CircularReveal
import dev.olog.msc.presentation.utils.animation.HasSafeTransition
import dev.olog.msc.presentation.utils.animation.SafeTransition
import dev.olog.msc.presentation.utils.lazyFast
import dev.olog.msc.presentation.viewModelProvider
import dev.olog.msc.utils.k.extension.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import javax.inject.Inject

class SearchFragment : BaseFragment(), HasSafeTransition {

    companion object {
        const val TAG = "SearchFragment"
        private const val ARGUMENT_ICON_POS_X = "$TAG.argument.pos.x"
        private const val ARGUMENT_ICON_POS_Y = "$TAG.argument.pos.y"

        @JvmStatic
        fun newInstance(icon: View?): SearchFragment {
            val x = icon?.let { (it.x + it.width / 2).toInt() } ?: 0
            val y = icon?.let { (it.y + it.height / 2).toInt() } ?: 0
            return SearchFragment().withArguments(
                    ARGUMENT_ICON_POS_X to x,
                    ARGUMENT_ICON_POS_Y to y
            )
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<SearchFragmentViewModel>(viewModelFactory) }

    @Inject lateinit var adapter : SearchFragmentAdapter
    @Inject lateinit var albumAdapter: SearchFragmentAlbumAdapter
    @Inject lateinit var artistAdapter: SearchFragmentArtistAdapter
    @Inject lateinit var recycledViewPool : RecyclerView.RecycledViewPool
    @Inject lateinit var safeTransition: SafeTransition
    private lateinit var layoutManager: LinearLayoutManager
    private var bestMatchDisposable : Disposable? = null

    private var queryDisposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            val x = arguments!!.getInt(ARGUMENT_ICON_POS_X)
            val y = arguments!!.getInt(ARGUMENT_ICON_POS_Y)
            val transition = CircularReveal(ctx, x, y, onAppearFinished = {
                val fragmentManager = activity?.supportFragmentManager

                act.fragmentTransaction {
                    fragmentManager?.findFragmentByTag(CategoriesFragment.TAG)?.let { hide(it) }
                    fragmentManager?.findFragmentByTag(DetailFragment.TAG)?.let { hide(it) }
                    setReorderingAllowed(true)
                }
            })
            safeTransition.execute(this, transition)
        }
    }

    override fun isAnimating(): Boolean = safeTransition.isAnimating

    override fun onDetach() {
        val fragmentManager = activity?.supportFragmentManager
        act.fragmentTransaction {
            fragmentManager?.findFragmentByTag(DetailFragment.TAG)?.let { show(it) }
                    ?: fragmentManager!!.findFragmentByTag(CategoriesFragment.TAG)?.let { show(it) }
            setReorderingAllowed(true)
        }
        super.onDetach()
    }

    private fun searchForBestMatch(query: String){
        bestMatchDisposable.unsubscribe()
        bestMatchDisposable = viewModel.getBestMatch(query)
                .subscribe({
                    didYouMean.text = it
                    didYouMeanHeader.toggleVisibility(it.isNotBlank(), true)
                    didYouMean.toggleVisibility(it.isNotBlank(), true)
                }, Throwable::printStackTrace)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setRecycledViewPool(recycledViewPool)
        view.list.setHasFixedSize(true)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.LEFT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        viewModel.searchData.subscribe(viewLifecycleOwner) { (map, query) ->

            didYouMean.setGone()
            didYouMeanHeader.setGone()

            if (query.isNotBlank()){
                val isEmpty = map.map { it.value }
                        .map { it.isEmpty() }
                        .reduce { all, current -> all && current }
                if (isEmpty){
                    searchForBestMatch(query)
                }
            }

            val albums = map[SearchFragmentType.ALBUMS]!!.toList()
            val artists = map[SearchFragmentType.ARTISTS]!!.toList()
            albumAdapter.updateDataSet(albums)
            artistAdapter.updateDataSet(artists)
            adapter.updateDataSet(viewModel.adjustDataMap(map))
        }
    }

    override fun onResume() {
        super.onResume()
        clear.setOnClickListener { editText.setText("") }
        back.setOnClickListener {
            ImeUtils.hideIme(editText)
            activity!!.onBackPressed()
        }
        root.setOnClickListener { ImeUtils.showIme(editText) }
        didYouMean.setOnClickListener { editText.setText(didYouMean.text.toString()) }

        queryDisposable = RxTextView.afterTextChangeEvents(editText)
                .map { it.editable()!!.toString() }
                .filter { it.isBlank() || it.trim().length >= 2 }
                .subscribe(viewModel::setNewQuery, Throwable::printStackTrace)

        adapter.setAfterDataChanged({
            updateLayoutVisibility(it)
        }, false)
    }

    override fun onPause() {
        super.onPause()
        clear.setOnClickListener(null)
        back.setOnClickListener(null)
        root.setOnClickListener(null)
        didYouMean.setOnClickListener(null)
        queryDisposable.unsubscribe()
        adapter.setAfterDataChanged(null)
    }


    private fun updateLayoutVisibility(list: List<*>){
        val itemCount = list.size
        val isEmpty = itemCount == 0
        val queryLength = editText.text.toString().length
        this.searchImage.toggleVisibility(isEmpty && queryLength < 2, true)
        this.searchText.toggleVisibility(isEmpty && queryLength < 2, true)
        this.list.toggleVisibility(!isEmpty, true)

        val showEmptyState = isEmpty && queryLength >= 2
        this.emptyStateText.toggleVisibility(showEmptyState, true)
        this.emptyStateImage.toggleVisibility(showEmptyState, true)
        if(showEmptyState){
            this.emptyStateImage.resumeAnimation()
        } else {
            this.emptyStateImage.progress = 0f
        }
    }

    override fun onStop() {
        super.onStop()
        ImeUtils.hideIme(editText)
        bestMatchDisposable.unsubscribe()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}