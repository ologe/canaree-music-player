package dev.olog.presentation.fragment_detail

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.math.MathUtils
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import dev.olog.presentation.GlideApp
import dev.olog.presentation.HasSlidingPanel
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.images.CoverUtils
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.*
import dev.olog.shared.MediaIdHelper
import kotlinx.android.synthetic.main.fragment_detail.view.*
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class DetailFragment : BaseFragment(), DetailFragmentView {

    companion object {
        const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId)
        }
    }

    @Inject lateinit var viewModel: DetailFragmentViewModel
    @Inject lateinit var adapter: DetailFragmentAdapter
    @Inject lateinit var recentlyAddedAdapter : DetailFragmentRecentlyAddedAdapter
    @Inject lateinit var mostPlayedAdapter: DetailFragmentMostPlayedAdapter
    @Inject lateinit var mediaId: String
    @Inject lateinit var recycledViewPool : RecyclerView.RecycledViewPool
    private val slidingPanelListener by lazy (NONE) { DetailFragmentSlidingPanelListener(this) }
    private val source by lazy { MediaIdHelper.mapCategoryToSource(mediaId) }
    private val marginDecorator by lazy (NONE){ DetailFragmentHorizontalMarginDecoration(context!!) }
    private lateinit var layoutManager : GridLayoutManager

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        postponeEnterTransition()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context!!.isPortrait){
            setLightButtons()
        }

        viewModel.mostPlayedFlowable
                .subscribe(this, mostPlayedAdapter::updateDataSet)

        viewModel.recentlyAddedFlowable
                .subscribe(this, recentlyAddedAdapter::updateDataSet)

        viewModel.data.subscribe(this, {
            if (context!!.isLandscape){
                // header in list is not need in landscape
                it[DetailFragmentDataType.HEADER]!!.clear()
            }
            adapter.updateDataSet(it)
        })

    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context!!, 2)
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.recycledViewPool = recycledViewPool
        layoutManager.spanSizeLookup = DetailFragmentSpanSizeLookup(view.list)
        view.list.setHasFixedSize(true)

        setupListScroll(view)

        viewModel.itemLiveData.subscribe(this, {
            view.header.text = it.title
            if (context!!.isLandscape){
                setImage(it)
            }
        })
    }

    private fun setupListScroll(view: View){
        if (context!!.isLandscape) {
            return
        }

        val sharedObservable = RxRecyclerView.scrollEvents(view.list)
                .share()

        sharedObservable.map { layoutManager.findFirstCompletelyVisibleItemPosition() != 0 }
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(this, { lightStatusBar ->
                    if (lightStatusBar){
                        setDarkButtons()
                    } else {
                        setLightButtons()
                    }
                })

        sharedObservable.map { it.dy() }
                .asLiveData()
                .subscribe(this, { dy ->
                    val floatDiff = dy.toFloat()
                    if (layoutManager.findFirstVisibleItemPosition() < 1){
                        // change alpha based on scroll
                        val alpha = MathUtils.clamp(view.toolbar.alpha + floatDiff / 400, 0f, 1f)
                        view.toolbar.alpha = alpha
                        view.header.alpha = alpha
                        view.statusBar.alpha = alpha
                    } else if (view.toolbar.alpha != 1f) {
                        // after the main image is covered only increase the alpha
                        val alpha = MathUtils.clamp(view.toolbar.alpha + Math.abs(floatDiff / 400), 0f, 1f)
                        view.toolbar.alpha = alpha
                        view.header.alpha = alpha
                        view.statusBar.alpha = alpha
                    }
                })

    }

    private fun setImage(item: DisplayableItem){

        val id = if (source == TabViewPagerAdapter.FOLDER){
            MediaIdHelper.extractCategoryValue(item.mediaId).hashCode()
        } else MediaIdHelper.extractCategoryValue(item.mediaId).toInt()

        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(Uri.parse(item.image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(600)
                .priority(Priority.IMMEDIATE)
                .error(CoverUtils.getGradient(context!!, id, source))
                .into(view!!.cover)
    }

    internal fun setLightButtons(){
        activity!!.window.removeLightStatusBar()
        view?.back?.setColorFilter(Color.WHITE)
    }

    internal fun setDarkButtons(){
        activity!!.window.setLightStatusBar()
        view?.back?.setColorFilter(ContextCompat.getColor(context!!, R.color.dark_grey))
    }

    override fun onStart() {
        super.onStart()
        view!!.list.addItemDecoration(marginDecorator)
    }

    override fun onResume() {
        super.onResume()
        if (context!!.isPortrait){
            (activity as HasSlidingPanel).addSlidingPanel(slidingPanelListener)
        }
        view!!.back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        if (context!!.isPortrait){
            (activity as HasSlidingPanel).removeSlidingPanel(slidingPanelListener)
        }
        view!!.back.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        view!!.list.removeItemDecoration(marginDecorator)
    }

    override fun onDestroyView() {
        activity!!.window.setLightStatusBar()
        super.onDestroyView()
    }

    override fun startTransition() {
        startPostponedEnterTransition()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail
}