package dev.olog.msc.presentation.base.adp

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable

abstract class AbsAdapter<Model : BaseModel>(
        lifecycle: Lifecycle,
        protected val controller : AdapterDataController<Model> = BaseAdapterDataController()

) : RecyclerView.Adapter<DataBoundViewHolder>(), DefaultLifecycleObserver, TouchableAdapter {

    private var dataDisposable : Disposable? = null
    var touchHelper: ItemTouchHelper? = null

    var beforeDataChanged : ((List<*>) -> Unit)? = null
    var afterDataChanged : ((List<*>) -> Unit)? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        beforeDataChanged = null
        afterDataChanged = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        bind(holder.binding, controller.getItem(position), position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: ViewDataBinding, item: Model, position: Int)

    override fun getItemCount(): Int = controller.getSize()

    override fun getItemViewType(position: Int) = controller.getItem(position).type

    override fun onResume(owner: LifecycleOwner) {
        dataDisposable = controller.handleNewData(extendAreItemTheSame)
                .subscribe({ (wasEmpty, callback) ->
                    if (wasEmpty || !hasGranularUpdate() || callback == null){
                        notifyDataSetChanged()
                    } else {
                        callback.dispatchUpdatesTo(this)
                    }

                    afterDataChanged?.invoke(controller.getAll())
                })
    }

    override fun onPause(owner: LifecycleOwner) {
        dataDisposable.unsubscribe()
    }

    fun updateDataSet(data: Any){
        beforeDataChanged?.invoke(controller.getAll())
        controller.update(data)
    }

    fun elementAt(position: Int): Model {
        return controller.getAll()[position]
    }

    fun indexOf(predicate : (Model) -> Boolean): Int {
        return controller.getAll().indexOfFirst(predicate)
    }

    fun find(predicate: (Model) -> Boolean): Model? {
        return controller.getAll().find(predicate)
    }

    fun first(predicate: (Model) -> Boolean): Model {
        return controller.getAll().first(predicate)
    }

    protected open fun hasGranularUpdate() : Boolean = true

    protected open val extendAreItemTheSame: ((Int, Int, Model, Model) -> Boolean)? = null

    protected open val onDragAction: ((from: Int, to: Int) -> Any)? = null

    protected open val onSwipeAction : ((position: Int) -> Any)? = null

    override fun onMoved(from: Int, to: Int) {
        controller.swap(from, to)
        notifyItemMoved(from, to)
        // drag action must be defined
        onDragAction!!.invoke(from, to)
    }

    override fun onSwiped(position: Int) {
        controller.remove(position)
        notifyItemRemoved(position)
        // swipe action must be defined
        onSwipeAction!!.invoke(position)
    }

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean? = null

}