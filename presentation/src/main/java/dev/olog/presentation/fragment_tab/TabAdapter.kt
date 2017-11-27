package dev.olog.presentation.fragment_tab

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.Header
import dev.olog.presentation.music_service.MusicController
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.setOnClickListener
import javax.inject.Inject

class TabAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val source: Int,
        private val musicController: MusicController

) : BaseAdapter(lifecycle) {



    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(getDataSet(), { item ->
            if (item.isPlayable){
                musicController.playFromMediaId(item.mediaId)
            } else {
                navigator.toDetailActivity(item.mediaId, viewHolder.adapterPosition)
            }

        })
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source, source)
        binding.setVariable(BR.position, position)
    }

    override fun provideStaticHeaders(): List<Header> {
        if (source == TabViewPagerAdapter.SONG){
            return listOf(Header(R.layout.item_shuffle))
        }
        return super.provideStaticHeaders()
    }

}