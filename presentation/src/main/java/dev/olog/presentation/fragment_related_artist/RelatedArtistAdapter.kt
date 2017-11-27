package dev.olog.presentation.fragment_related_artist

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation._base.BaseAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.setOnClickListener
import javax.inject.Inject

class RelatedArtistAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator

): BaseAdapter(lifecycle) {


    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(getDataSet(), { item ->
            navigator.toDetailActivity(item.mediaId, viewHolder.adapterPosition)
        })
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source, TabViewPagerAdapter.ARTIST)
        binding.setVariable(BR.position, position)
    }
}