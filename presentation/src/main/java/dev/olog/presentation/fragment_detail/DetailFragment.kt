package dev.olog.presentation.fragment_detail

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.math.MathUtils
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
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
        const val ARGUMENTS_LIST_POSITION = "$TAG.arguments.list_position"

        fun newInstance(mediaId: String, position: Int): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_POSITION to position
            )
        }
    }

    @Inject lateinit var viewModel: DetailFragmentViewModel
    @Inject lateinit var adapter: DetailAdapter
    @Inject lateinit var recentlyAddedAdapter : DetailRecentlyAddedAdapter
    @Inject lateinit var mostPlayedAdapter: DetailMostPlayedAdapter
    @Inject lateinit var mediaId: String
    @Inject lateinit var recyclerViewPool : RecyclerView.RecycledViewPool
    @Inject @JvmField var listPosition: Int = 0

    private val source by lazy { MediaIdHelper.mapCategoryToSource(mediaId) }

    private val marginDecorator by lazy (NONE){ HorizontalMarginDecoration(context!!) }

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
                it[DetailDataType.HEADER]!!.clear()
            }
            adapter.updateDataSet(it)
        })

    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context!!, 2)
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.recycledViewPool = recyclerViewPool
        view.list.setHasFixedSize(true)
        layoutManager.spanSizeLookup = DetailSpanSizeLookup(view.list)

        if (context!!.isPortrait){
            val listObservable = RxRecyclerView.scrollEvents(view.list)
                    .share()

            listObservable
                    .map { layoutManager.findFirstVisibleItemPosition() >= 1 }
                    .distinctUntilChanged()
                    .asLiveData()
                    .subscribe(this, { lightStatusBar ->
                        view.toolbar.isActivated = lightStatusBar
                        view.back.isActivated = lightStatusBar
                        view.header.isActivated = lightStatusBar
                    })

            listObservable.map { layoutManager.findFirstCompletelyVisibleItemPosition() != 0 }
                    .distinctUntilChanged()
                    .asLiveData()
                    .subscribe(this, { lightStatusBar ->
                        if (lightStatusBar){
                            setDarkButtons()
                        } else {
                            setLightButtons()
                        }
                    })

            listObservable.map { it.dy() }
                    .asLiveData()
                    .subscribe(this, { dy ->
                        val floatDiff = dy.toFloat()
                        if (layoutManager.findFirstVisibleItemPosition() < 1){
                            // change alpha based on scroll
                            val alpha = MathUtils.clamp(view.toolbar.alpha + floatDiff / 400, 0f, 1f)
                            view.toolbar.alpha = alpha
                            view.header.alpha = alpha
                        } else if (view.toolbar.alpha != 1f) {
                            // after the main image is covered only increase the alpha
                            val alpha = MathUtils.clamp(view.toolbar.alpha + Math.abs(floatDiff / 400), 0f, 1f)
                            view.toolbar.alpha = alpha
                            view.header.alpha = alpha
                        }
                    })
        }

        viewModel.itemTitleLiveData.subscribe(this, {
            view.header.text = it.title

            if (context!!.isLandscape){
                setImage(it)
            }
        })
    }

    private fun setImage(item: DisplayableItem){
        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(Uri.parse(item.image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(600)
                .priority(Priority.IMMEDIATE)
                .error(CoverUtils.getGradient(context!!, listPosition, source))
                .into(view!!.cover)
    }

    private fun setLightButtons(){
        activity!!.window.removeLightStatusBar()
        view?.back?.setColorFilter(Color.WHITE)
    }

    private fun setDarkButtons(){
        activity!!.window.setLightStatusBar()
        view?.back?.setColorFilter(ContextCompat.getColor(context!!, R.color.dark_grey))
    }

    override fun onStart() {
        super.onStart()
        view!!.list.addItemDecoration(marginDecorator)
        activity!!.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel)
                .addPanelSlideListener(slidingPanelListener)
    }

    override fun onPause() {
        super.onPause()
        activity!!.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel)
                .removePanelSlideListener(slidingPanelListener)
    }

    override fun onStop() {
        super.onStop()
        view!!.list.removeItemDecoration(marginDecorator)
    }

    private val slidingPanelListener = object : SlidingUpPanelLayout.PanelSlideListener {

        override fun onPanelSlide(panel: View?, slideOffset: Float) {
        }

        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            if (newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                setDarkButtons()
            } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                setLightButtons()
            }
        }

    }

    override fun onDestroyView() {
        activity!!.window.setLightStatusBar()
        super.onDestroyView()
    }

    override fun startTransition() {
        startPostponedEnterTransition()
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_detail, container, false)
}