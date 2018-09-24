package dev.olog.msc.presentation.detail


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.BindingsAdapter
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.detail.scroll.listener.HeaderVisibilityScrollListener
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.presentation.utils.lazyFast
import dev.olog.msc.presentation.viewModelProvider
import dev.olog.msc.presentation.widget.image.view.ShapeImageView
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import javax.inject.Inject
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

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<DetailFragmentViewModel>(viewModelFactory) }

    @Inject lateinit var mediaId: MediaId
    @Inject lateinit var adapter: DetailFragmentAdapter
    @Inject lateinit var recentlyAddedAdapter : DetailRecentlyAddedAdapter
    @Inject lateinit var mostPlayedAdapter: DetailMostPlayedAdapter
    @Inject lateinit var relatedArtistAdapter: DetailRelatedArtistsAdapter
    @Inject lateinit var albumsAdapter: DetailAlbumsAdapter
    @Inject lateinit var recycledViewPool : RecyclerView.RecycledViewPool
    @Inject lateinit var navigator: Navigator
    private val recyclerOnScrollListener by lazyFast { HeaderVisibilityScrollListener(this) }

    internal var hasLightStatusBarColor by Delegates.observable(false) { _, _, new ->
        adjustStatusBarColor(new)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = LinearLayoutManager(ctx)
        view.list.adapter = adapter
        view.list.setRecycledViewPool(recycledViewPool)
        view.list.setHasFixedSize(true)

        var swipeDirections = ItemTouchHelper.LEFT
        if (adapter.canSwipeRight){
            swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        }
        val callback = TouchHelperAdapterCallback(adapter, swipeDirections)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        view.cover?.setVisible()

        viewModel.mostPlayedLiveData
                .subscribe(viewLifecycleOwner, mostPlayedAdapter::updateDataSet)

        viewModel.recentlyAddedLiveData
                .subscribe(viewLifecycleOwner, recentlyAddedAdapter::updateDataSet)

        viewModel.relatedArtistsLiveData
                .subscribe(viewLifecycleOwner, relatedArtistAdapter::updateDataSet)

        viewModel.albumsLiveData
                .subscribe(viewLifecycleOwner) {
                    albumsAdapter.updateDataSet(it)
                }

        viewModel.observeData()
                .subscribe(viewLifecycleOwner) { map ->
                    val copy = map.deepCopy()
                    if (copy.isEmpty()){
                        act.onBackPressed()
                    } else {
                        if (ctx.isLandscape){
                            // header in list is not used in landscape
                            copy[DetailFragmentDataType.HEADER]!!.clear()
                        }
                        adapter.updateDataSet(copy)
                    }
                }

        viewModel.itemLiveData.subscribe(viewLifecycleOwner) { item ->
            if (item.isNotEmpty()){
                headerText.text = item[1].title
                val cover = view.findViewById<View>(R.id.cover)
                if (!isPortrait() && cover is ShapeImageView){
                    BindingsAdapter.loadBigAlbumImage(cover, item[0])
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ctx.isPortrait){
            list.addOnScrollListener(recyclerOnScrollListener)
        }
        back.setOnClickListener { act.onBackPressed() }
        more.setOnClickListener { navigator.toDialog(viewModel.mediaId, more) }
    }

    override fun onPause() {
        super.onPause()
        if (ctx.isPortrait){
            list.removeOnScrollListener(recyclerOnScrollListener)
//            list.removeItemDecoration(detailListMargin)
        }
        back.setOnClickListener(null)
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
        act.window.removeLightStatusBar()
        val color = ContextCompat.getColor(ctx, R.color.detail_button_color_light)
        view?.back?.setColorFilter(color)
        more?.setColorFilter(color)
    }

    private fun setLightStatusBar(){
        if (AppTheme.isDarkTheme()){
            return
        }

        act.window.setLightStatusBar()
        val color = ContextCompat.getColor(ctx, R.color.detail_button_color_dark)
        view?.back?.setColorFilter(color)
        more?.setColorFilter(color)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail
}
