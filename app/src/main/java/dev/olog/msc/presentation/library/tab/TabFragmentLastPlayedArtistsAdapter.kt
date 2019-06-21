package dev.olog.msc.presentation.library.tab

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

@PerFragment
class TabFragmentLastPlayedArtistsAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator

): AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun onViewRecycled(holder: DataBoundViewHolder) {
        holder.itemView.findViewById<View>(R.id.cover)?.let {
            GlideApp.with(holder.itemView).clear(it)
        }
        super.onViewRecycled(holder)
    }

}