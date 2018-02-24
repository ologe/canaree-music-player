package dev.olog.msc.presentation.detail


import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.BindingsAdapter
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adp.TouchHelperAdapterCallback
import dev.olog.msc.presentation.detail.scroll.listener.HeaderVisibilityScrollListener
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.properties.Delegates

class DetailFragment : BaseFragment() {

    companion object {
        const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var viewModel: DetailFragmentViewModel
    @Inject lateinit var mediaId: MediaId
    @Inject lateinit var adapter: DetailFragmentAdapter
    @Inject lateinit var recentlyAddedAdapter : DetailRecentlyAddedAdapter
    @Inject lateinit var mostPlayedAdapter: DetailMostPlayedAdapter
    @Inject lateinit var relatedArtistAdapter: DetailRelatedArtistsAdapter
    @Inject lateinit var recycledViewPool : RecyclerView.RecycledViewPool
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var layoutManager: Provider<GridLayoutManager>
    @Inject lateinit var detailListMargin: DetailListMargin
    private val recyclerOnScrollListener by lazy(NONE) { HeaderVisibilityScrollListener(this) }

    internal var hasLightStatusBarColor by Delegates.observable(false, { _, _, new ->
        adjustStatusBarColor(new)
    })

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.mostPlayedLiveData
                .subscribe(this, mostPlayedAdapter::updateDataSet)

        viewModel.recentlyAddedLiveData
                .subscribe(this, recentlyAddedAdapter::updateDataSet)

        viewModel.relatedArtistsLiveData
                .subscribe(this, relatedArtistAdapter::updateDataSet)

        viewModel.data.subscribe(this, {
            if (context!!.isLandscape){
                // header in list is not used in landscape
                it[DetailFragmentDataType.HEADER]!!.clear()
            }
            adapter.updateDataSet(it)
        })

        viewModel.itemLiveData.subscribe(this, { item ->
            headerText.text = item[1].title
            if (!isPortrait()){
                BindingsAdapter.loadBigAlbumImage(cover, item[0])
            } else {
                if (cover.isVisible()){
                    val id = BindingsAdapter.resolveId(item[0].mediaId)
                    val drawable = CoverUtils.getGradient(context!!, id, item[0].mediaId.source)
                    cover.setImageDrawable(drawable)
                }
            }
        })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = layoutManager.get()
        view.list.adapter = adapter
        view.list.recycledViewPool = recycledViewPool
        view.list.setHasFixedSize(true)
        if (adapter.hasTouchBehavior){
            val callback = TouchHelperAdapterCallback(adapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(view.list)
            adapter.touchHelper = touchHelper
        }
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

//        if (!act.supportFragmentManager.searchInFragmentBackStack(TAG)) {
//            view.cover.setVisible()
//
//            adapter.setAfterDataChanged({
//                adapter.setAfterDataChanged(null)
//                Single.timer(200, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({ cover.setGone() }, Throwable::printStackTrace)
//            })
//        } todo
    }

    override fun onResume() {
        super.onResume()
        if (context!!.isPortrait){
            list.addOnScrollListener(recyclerOnScrollListener)
            list.addItemDecoration(detailListMargin)
        }
        back.setOnClickListener { activity!!.onBackPressed() }
        search.setOnClickListener { navigator.toSearchFragment(search) }
        more.setOnClickListener { navigator.toDialog(viewModel.mediaId, more) }
    }

    override fun onPause() {
        super.onPause()
        if (context!!.isPortrait){
            list.removeOnScrollListener(recyclerOnScrollListener)
            list.removeItemDecoration(detailListMargin)
        }
        back.setOnClickListener(null)
        search.setOnClickListener(null)
        more.setOnClickListener(null)
    }

    internal fun adjustStatusBarColor(lightStatusBar: Boolean = hasLightStatusBarColor){
        if (lightStatusBar){
            setLightStatusBar()
        } else {
            removeLightStatusBar()
        }
    }

    private fun removeLightStatusBar(){
        activity!!.window.removeLightStatusBar()
        back.setColorFilter(Color.WHITE)
        search.setColorFilter(Color.WHITE)
        more.setColorFilter(Color.WHITE)
    }

    private fun setLightStatusBar(){
        activity!!.window.setLightStatusBar()
        val color = ContextCompat.getColor(context!!, R.color.dark_grey)
        back.setColorFilter(color)
        search.setColorFilter(color)
        more.setColorFilter(color)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail
}
