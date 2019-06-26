package dev.olog.msc.presentation.detail


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.detail.adapter.*
import dev.olog.msc.presentation.detail.scroll.listener.HeaderVisibilityScrollListener
import dev.olog.msc.utils.k.extension.removeLightStatusBar
import dev.olog.msc.utils.k.extension.setLightStatusBar
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.CanChangeStatusBarColor
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.extensions.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class DetailFragment : BaseFragment(), CanChangeStatusBarColor {

    companion object {
        val TAG = DetailFragment::class.java.name
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazyFast {
        viewModelProvider<DetailFragmentViewModel>(
            viewModelFactory
        )
    }

    private val recyclerOnScrollListener by lazyFast { HeaderVisibilityScrollListener(this) }

    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    private val mediaId by lazyFast {
        val mediaId = arguments!!.getString(ARGUMENTS_MEDIA_ID)!!
        MediaId.fromString(mediaId)
    }

    private val mostPlayedAdapter by lazyFast {
        DetailMostPlayedAdapter(
            lifecycle,
            navigator,
            act as MediaProvider
        )
    }
    private val recentlyAddedAdapter by lazyFast {
        DetailRecentlyAddedAdapter(
            lifecycle,
            navigator,
            act as MediaProvider
        )
    }
    private val relatedArtistAdapter by lazyFast {
        DetailRelatedArtistsAdapter(
            lifecycle,
            navigator
        )
    }
    private val albumsAdapter by lazyFast {
        DetailAlbumsAdapter(
            lifecycle,
            navigator
        )
    }

    private val adapter by lazyFast {
        DetailFragmentAdapter(
            lifecycle, mediaId, recentlyAddedAdapter, mostPlayedAdapter, relatedArtistAdapter,
            albumsAdapter, navigator, act as MediaProvider, viewModel, recycledViewPool
        )
    }

    internal var hasLightStatusBarColor by Delegates.observable(false) { _, _, new ->
        adjustStatusBarColor(new)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
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

        viewModel.observeMostPlayed()
                .subscribe(viewLifecycleOwner, mostPlayedAdapter::updateDataSet)

        viewModel.observeRecentlyAdded()
                .subscribe(viewLifecycleOwner, recentlyAddedAdapter::updateDataSet)

        viewModel.observeRelatedArtists()
                .subscribe(viewLifecycleOwner, relatedArtistAdapter::updateDataSet)

        viewModel.observeSiblings()
                .subscribe(viewLifecycleOwner) {
                    albumsAdapter.updateDataSet(it)
                }

        viewModel.observeSongs()
                .subscribe(viewLifecycleOwner) { list ->
                    if (list.isEmpty()){
                        act.onBackPressed()
                    } else {
                        adapter.updateDataSet(list)
                    }
                }

        viewModel.observeItem().subscribe(viewLifecycleOwner) { item ->
            headerText.text = item.title
//            val cover = view.findViewById<View>(R.id.cover) TODO
//            if (cover is ShapeImageView){
//                BindingsAdapter.loadBigAlbumImage(cover, item)
//            }
        }

        RxTextView.afterTextChangeEvents(view.editText)
                .map { it.view() }
                .asLiveData()
                .subscribe(viewLifecycleOwner) { edit ->
                    val isEmpty = edit.text.isEmpty()
                    viewModel.updateFilter(edit.text.toString())
                }
    }

    override fun onResume() {
        super.onResume()
        list.addOnScrollListener(recyclerOnScrollListener)
        back.setOnClickListener { act.onBackPressed() }
        more.setOnClickListener { navigator.toDialog(viewModel.mediaId, more) }
        filter.setOnClickListener {
            searchWrapper.toggleVisibility(!searchWrapper.isVisible, true)
        }
    }

    override fun onPause() {
        super.onPause()
        list.removeOnScrollListener(recyclerOnScrollListener)
        back.setOnClickListener(null)
        more.setOnClickListener(null)
        filter.setOnClickListener(null)
    }

    override fun adjustStatusBarColor() {
        adjustStatusBarColor(hasLightStatusBarColor)
    }

    override fun adjustStatusBarColor(lightStatusBar: Boolean) {
        if (lightStatusBar) {
            setLightStatusBar()
        } else {
            removeLightStatusBar()
        }
    }

    private fun removeLightStatusBar(){
        act.window.removeLightStatusBar()
        val color = Color.WHITE
        back.setColorFilter(color)
        more.setColorFilter(color)
        filter.setColorFilter(color)
    }

    private fun setLightStatusBar(){
        if (requireContext().isDarkMode()){
            return
        }

        act.window.setLightStatusBar()
        val color = requireContext().colorControlNormal()
        back.setColorFilter(color)
        more.setColorFilter(color)
        filter.setColorFilter(color)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail
}
