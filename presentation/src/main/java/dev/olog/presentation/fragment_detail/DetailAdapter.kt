package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.music_service.MusicController
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import io.reactivex.BackpressureStrategy
import javax.inject.Inject

class DetailAdapter @Inject constructor(
        @ApplicationContext context: Context,
        @FragmentLifecycle lifecycle: Lifecycle,
        mediaId: String,
        private val recentSongsAdapter: DetailRecentlyAddedAdapter,
        private val mostPlayedAdapter: DetailMostPlayedAdapter,
        private val navigator: Navigator,
        private val musicController: MusicController,
        private val viewModel: DetailFragmentViewModel

) : RecyclerView.Adapter<DataBoundViewHolder<*>>() {

    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    private val dataController = DetailDataController(context, this, MediaIdHelper.mapCategoryToSource(mediaId))
    private val recycled = RecyclerView.RecycledViewPool()

    init {
        lifecycle.addObserver(dataController)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    private fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int){
        when (viewType) {
            R.layout.item_most_played_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                val layoutManager = GridLayoutManager(viewHolder.itemView.context,
                        5, GridLayoutManager.HORIZONTAL, false)
                layoutManager.isItemPrefetchEnabled = true
                layoutManager.initialPrefetchItemCount = 10
                list.layoutManager = layoutManager
                list.adapter = mostPlayedAdapter
                list.recycledViewPool = recycled

                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(list)
            }
            R.layout.item_recent_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                val layoutManager = GridLayoutManager(viewHolder.itemView.context,
                        5, GridLayoutManager.HORIZONTAL, false)
                layoutManager.isItemPrefetchEnabled = true
                layoutManager.initialPrefetchItemCount = 10
                list.layoutManager = layoutManager
                list.adapter = recentSongsAdapter
                list.recycledViewPool = recycled

                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(list)
            }
            R.layout.item_tab_song -> {
                viewHolder.itemView.setOnClickListener {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val item = dataController[position]
                        musicController.playFromMediaId(item.mediaId)
                        viewModel.addToMostPlayed(item.mediaId)
                                .subscribe()
                    }
                }
            }
            R.layout.item_detail_album -> {
                viewHolder.itemView.setOnClickListener {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val item = dataController[position]
                        navigator.toDetailActivity(item.mediaId)
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder<*>) {
        when (holder.itemViewType) {
            R.layout.item_most_played_horizontal_list -> {
                val list = holder.itemView as RecyclerView
                val layoutManager = list.layoutManager as GridLayoutManager
                (list.adapter as BaseAdapter).onDataChanged()
                        .takeUntil(RxView.detaches(holder.itemView).toFlowable(BackpressureStrategy.LATEST))
                        .subscribe({
                            layoutManager.spanCount = if (it.size < 5) it.size else 5
                        }, Throwable::printStackTrace)
            }

        }
    }

    override fun getItemCount(): Int = dataController.getSize()

    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        bind(holder.binding, dataController[position], position)
        holder.binding.executePendingBindings()
    }

    private fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source,  source)
        binding.setVariable(BR.position, position)
    }

    override fun getItemViewType(position: Int): Int = dataController[position].type

    fun getItem(position: Int): DisplayableItem = dataController[position]

    fun onItemChanged(item: DisplayableItem){
        dataController.publisher.onNext(DetailDataType.HEADER.to(listOf(item)))
    }

    fun onSongListChanged(list: List<DisplayableItem>){
        dataController.publisher.onNext(DetailDataType.SONGS.to(list))
    }

    fun onAlbumListChanged(list: List<DisplayableItem>){
        dataController.publisher.onNext(DetailDataType.ALBUMS.to(list))
    }

    fun onMostPlayedChanged(list: List<DisplayableItem>){
        dataController.publisher.onNext(DetailDataType.MOST_PLAYED.to(list))
    }

    fun onRecentlyAddedChanged(list: List<DisplayableItem>){
        dataController.publisher.onNext(DetailDataType.RECENT.to(list))
    }

    fun onArtistInDataChanged(list: List<DisplayableItem>){
        dataController.publisher.onNext(DetailDataType.ARTISTS_IN.to(list))
    }

}