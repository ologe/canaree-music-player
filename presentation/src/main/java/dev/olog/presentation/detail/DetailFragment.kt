package dev.olog.presentation.detail


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.base.drag.OnStartDragListener
import dev.olog.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.presentation.detail.adapter.*
import dev.olog.presentation.interfaces.CanChangeStatusBarColor
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.ripple.RippleTarget
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.extensions.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

class DetailFragment : BaseFragment(),
    CanChangeStatusBarColor,
    SetupNestedList,
    OnStartDragListener {

    companion object {
        val TAG = DetailFragment::class.java.name
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): DetailFragment {
            return DetailFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazyFast { viewModelProvider<DetailFragmentViewModel>(viewModelFactory) }

    private val mediaId by lazyFast {
        val mediaId = getArgument<String>(ARGUMENTS_MEDIA_ID)
        MediaId.fromString(mediaId)
    }

    private var itemTouchHelper: ItemTouchHelper? = null

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
            lifecycle,
            mediaId,
            this,
            navigator,
            act as MediaProvider,
            viewModel,
            this
        )
    }

    private val recyclerOnScrollListener by lazyFast {
        HeaderVisibilityScrollListener(
            this
        )
    }
    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    internal var hasLightStatusBarColor by Delegates.observable(false) { _, _, new ->
        adjustStatusBarColor(new)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadImage()

        list.layoutManager = LinearLayoutManager(ctx)
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

        var swipeDirections = ItemTouchHelper.LEFT
        if (adapter.canSwipeRight) {
            swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        }
        val callback = TouchHelperAdapterCallback(adapter, swipeDirections)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper!!.attachToRecyclerView(list)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

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
                if (list.isEmpty()) {
                    act.onBackPressed()
                } else {
                    adapter.updateDataSet(list)
                }
            }

        viewModel.observeItem().subscribe(viewLifecycleOwner) { item ->
            headerText.text = item.title
        }

        launch {
            editText.afterTextChange()
                .debounce(200)
                .filter { it.isEmpty() || it.length >= 2 }
                .collect {
                    viewModel.updateFilter(it)
                }
        }
    }

    private fun loadImage() {
        postponeEnterTransition()
        GlideApp.with(requireContext())
            .load(mediaId)
            .priority(Priority.IMMEDIATE)
            .onlyRetrieveFromCache(true)
            .error(CoverUtils.getGradient(requireContext(), mediaId))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(RippleTarget(cover))

        // setup for parallax
        list.setView(cover)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

    override fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_detail_most_played_list -> {
                setupHorizontalListAsGrid(recyclerView, mostPlayedAdapter)
            }
            R.layout.item_detail_recently_added_list -> {
                setupHorizontalListAsGrid(recyclerView, recentlyAddedAdapter)
            }
            R.layout.item_detail_related_artists_list -> {
                setupHorizontalListAsList(recyclerView, relatedArtistAdapter)
            }
            R.layout.item_detail_albums_list -> {
                setupHorizontalListAsList(recyclerView, albumsAdapter)
            }
        }
    }

    private fun setupHorizontalListAsGrid(list: RecyclerView, adapter: ObservableAdapter<*>) {
        val layoutManager = GridLayoutManager(
            list.context, DetailFragmentViewModel.NESTED_SPAN_COUNT,
            GridLayoutManager.HORIZONTAL, false
        )
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = DetailFragmentViewModel.NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    private fun setupHorizontalListAsList(list: RecyclerView, adapter: ObservableAdapter<*>) {
        val layoutManager = LinearLayoutManager(list.context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = DetailFragmentViewModel.NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
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

    private fun removeLightStatusBar() {
        act.window.removeLightStatusBar()
        val color = Color.WHITE
        back.setColorFilter(color)
        more.setColorFilter(color)
        filter.setColorFilter(color)
    }

    private fun setLightStatusBar() {
        if (requireContext().isDarkMode()) {
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
