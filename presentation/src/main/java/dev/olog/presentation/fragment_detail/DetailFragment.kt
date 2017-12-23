package dev.olog.presentation.fragment_detail

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import dagger.Lazy
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.GlideApp
import dev.olog.presentation.HasSlidingPanel
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.images.CoverUtils
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.extension.*
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback
import dev.olog.shared.MediaIdHelper
import kotlinx.android.synthetic.main.fragment_detail.view.*
import java.io.File
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class DetailFragment : BaseFragment(), DetailFragmentView {

    companion object {
        const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    @Inject lateinit var viewModel: DetailFragmentViewModel
    @Inject lateinit var adapter: DetailFragmentAdapter
    @Inject lateinit var recentlyAddedAdapter : DetailFragmentRecentlyAddedAdapter
    @Inject lateinit var mostPlayedAdapter: DetailFragmentMostPlayedAdapter
    @Inject lateinit var mediaId: String
    @Inject lateinit var recycledViewPool : RecyclerView.RecycledViewPool
    @Inject lateinit var navigator: Lazy<Navigator>
    private val slidingPanelListener by lazy (NONE) { DetailFragmentSlidingPanelListener(this) }
    private val source by lazy { MediaIdHelper.mapCategoryToSource(mediaId) }
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
                // header in list is not used in landscape
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
        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.setSectionIndexer(adapter)

        setupListScroll(view)

        viewModel.itemLiveData.subscribe(this, {
            view.header.text = it.title
            setImage(it)
        })
    }

    private fun setupListScroll(view: View){
        if (context!!.isLandscape) {
            return
        }

        RxRecyclerView.scrollEvents(view.list)
                .map { layoutManager.findFirstCompletelyVisibleItemPosition() > 2 }
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(this, { isActive ->
                    view.statusBar.isActivated = isActive
                    view.toolbar.isActivated = isActive
                    view.header.isActivated = isActive
                    if (isActive){
                        setDarkButtons()
                    } else {
                        setLightButtons()
                    }
                })

    }

    private fun setImage(item: DisplayableItem){

        val id = if (source == TabViewPagerAdapter.FOLDER){
            MediaIdHelper.extractCategoryValue(item.mediaId).hashCode()
        } else MediaIdHelper.extractCategoryValue(item.mediaId).toInt()

        GlideApp.with(context).clear(view)

        val file = File(item.image)
        val imageUri = if(file.exists()){
            Uri.fromFile(file)
        } else {
            Uri.parse(item.image)
        }

        GlideApp.with(context)
                .load(imageUri)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(BindingsAdapter.OVERRIDE_BIG)
                .priority(Priority.IMMEDIATE)
                .error(CoverUtils.getGradient(context!!, id, source))
                .into(view!!.cover)
    }

    internal fun setLightButtons(){
        activity!!.window.removeLightStatusBar()
        view?.back?.setColorFilter(Color.WHITE)
        view?.search?.setColorFilter(Color.WHITE)
    }

    internal fun setDarkButtons(){
        activity!!.window.setLightStatusBar()
        view?.back?.setColorFilter(ContextCompat.getColor(context!!, R.color.dark_grey))
        view?.search?.setColorFilter(ContextCompat.getColor(context!!, R.color.dark_grey))
    }

    override fun onResume() {
        super.onResume()
        if (context!!.isPortrait){
            (activity as HasSlidingPanel).addSlidingPanel(slidingPanelListener)
        }
        view!!.back.setOnClickListener { activity!!.onBackPressed() }
        view!!.search.setOnClickListener { navigator.get().toSearchFragment() }
    }

    override fun onPause() {
        super.onPause()
        if (context!!.isPortrait){
            (activity as HasSlidingPanel).removeSlidingPanel(slidingPanelListener)
        }
        view!!.back.setOnClickListener(null)
        view!!.search.setOnClickListener(null)
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