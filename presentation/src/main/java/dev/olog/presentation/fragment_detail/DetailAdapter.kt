package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.Header
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@PerFragment
class DetailAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaId: String,
        private val horizontalAdapter: DetailHorizontalAdapter,
        private val viewModel: DetailFragmentViewModel

) : BaseAdapter(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        if (viewType == R.layout.item_horizontal_list){
            val horizontalList = viewHolder.itemView as RecyclerView
            horizontalList.adapter = horizontalAdapter
            horizontalList.layoutManager = LinearLayoutManager(horizontalList.context,
                    LinearLayoutManager.HORIZONTAL, false)

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(horizontalList)
        }
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder<*>) {
        if (holder.itemViewType == R.layout.item_horizontal_list) {
            val list = holder.binding.root.findViewById<RecyclerView>(R.id.list)

            RxRecyclerView.scrollEvents(list)
                    .takeUntil(RxView.detaches(holder.itemView))
                    .map {
                        (list.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    }
                    .filter { pos -> pos >= 0 && pos < horizontalAdapter.itemCount }
                    .distinctUntilChanged()
                    .map { horizontalAdapter.getItem(it) }
                    .subscribe { viewModel.onMediaItemChanged(it!!) }

            horizontalAdapter.onDataChanged()
                    .takeUntil(RxView.detaches(holder.itemView).toFlowable(BackpressureStrategy.LATEST))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val itemPosition = horizontalAdapter.getItemPosition(mediaId)
                        list.scrollToPosition(itemPosition)
                    }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.position, position)
    }

    override fun provideStaticHeaders(): List<Header> {
        val header = Header(R.layout.item_horizontal_list)
        return listOf(header)
    }

}