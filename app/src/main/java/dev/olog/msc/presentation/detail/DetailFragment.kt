package dev.olog.msc.presentation.detail


import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import androidx.view.doOnPreDraw
import dagger.Lazy
import dev.olog.msc.FirebaseAnalytics
import dev.olog.msc.R
import dev.olog.msc.presentation.BindingsAdapter
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import org.jetbrains.anko.dimen
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class DetailFragment : BaseFragment() {

    companion object {
        const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "${TAG}.arguments.media_id"

        fun newInstance(mediaId: MediaId): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var viewModel: DetailFragmentViewModel
    @Inject lateinit var adapter: DetailFragmentAdapter
    @Inject lateinit var recentlyAddedAdapter : DetailRecentlyAddedAdapter
    @Inject lateinit var mostPlayedAdapter: DetailMostPlayedAdapter
    @Inject lateinit var recycledViewPool : RecyclerView.RecycledViewPool
    @Inject lateinit var navigator: Lazy<Navigator>
    private lateinit var layoutManager : GridLayoutManager
    private val toolbarHeight by lazy(NONE) { context!!.dimen(R.dimen.status_bar) + context!!.dimen(R.dimen.toolbar) }

    private val parallaxOnScrollListener: ParallaxScrollListener
            by lazy { ParallaxScrollListener(view!!.cover) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        FirebaseAnalytics.trackFragment(activity!!, TAG)
        if (context!!.isPortrait){
            setLightButtons()
        }

        viewModel.mostPlayedFlowable
                .subscribe(this, mostPlayedAdapter::updateDataSet)

        viewModel.recentlyAddedFlowable
                .subscribe(this, recentlyAddedAdapter::updateDataSet)

        viewModel.data.subscribe(this, {
            if (context!!.isLandscape){
                // header in list is not used in landscape
                it[DetailFragmentDataType.HEADER]!!.clear()
            }
            adapter.updateDataSet(it)
        })

        viewModel.itemLiveData.subscribe(this, {
            headerText.text = it.title
            BindingsAdapter.loadBigAlbumImage(cover, it)
        })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context!!, 2)
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.recycledViewPool = recycledViewPool
        layoutManager.spanSizeLookup = DetailFragmentSpanSizeLookup(view.list)
        view.list.setHasFixedSize(true)
        adapter.touchHelper()?.attachToRecyclerView(view.list)
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        if (activity!!.isPortrait){
            view.doOnPreDraw {
                view.list.setPadding(view.list.paddingLeft, view.cover.bottom, view.list.paddingRight, view.list.paddingBottom)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (context!!.isPortrait){
            list.addOnScrollListener(recyclerOnScrollListener)
            list.addOnScrollListener(parallaxOnScrollListener)
        }
        back.setOnClickListener { activity!!.onBackPressed() }
        search.setOnClickListener { navigator.get().toSearchFragment() }
    }

    override fun onPause() {
        super.onPause()
        if (context!!.isPortrait){
            list.removeOnScrollListener(recyclerOnScrollListener)
            list.removeOnScrollListener(parallaxOnScrollListener)
        }
        back.setOnClickListener(null)
        search.setOnClickListener(null)
    }

    override fun onDestroyView() {
        setDarkButtons()
        super.onDestroyView()
    }

    private val recyclerOnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val child = recyclerView.getChildAt(0)
            val holder = recyclerView.getChildViewHolder(child)

            if (holder.itemViewType == R.layout.item_detail_item_info) {
                val bottom = child.bottom
                val needDarkLayout = bottom - toolbarHeight * 2 < 0

                statusBar.isActivated = needDarkLayout
                toolbar.isActivated = needDarkLayout
                headerText.isActivated = needDarkLayout

                if (needDarkLayout) {
                    setDarkButtons()
                } else {
                    setLightButtons()
                }
            }
        }
    }

    internal fun setLightButtons(){
        if (isMarshmallow()){
            activity!!.window.removeLightStatusBar()
        }
        view?.back?.setColorFilter(Color.WHITE)
        view?.search?.setColorFilter(Color.WHITE)
    }

    internal fun setDarkButtons(){
        if (isMarshmallow()){
            activity!!.window.setLightStatusBar()
        }
        view?.back?.setColorFilter(ContextCompat.getColor(context!!, R.color.dark_grey))
        view?.search?.setColorFilter(ContextCompat.getColor(context!!, R.color.dark_grey))
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail
}
