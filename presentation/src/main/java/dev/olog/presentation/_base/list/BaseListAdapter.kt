package dev.olog.presentation._base.list

import android.arch.lifecycle.Lifecycle
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.presentation._base.BaseModel
import io.reactivex.Flowable

abstract class BaseListAdapter<Model: BaseModel> (
        lifecycle: Lifecycle

) : RecyclerView.Adapter<DataBoundViewHolder<*>>() {

    protected open val touchCallbackConfig = TouchCallbackConfig()

    var onDataChangedListener : OnDataChangedListener? = null

    protected val dataController = BaseListAdapterController(this)

    private val draggableBehavior by lazy { if (touchCallbackConfig.canDrag) {
        TouchBehaviorImpl(dataController, touchCallbackConfig)
    } else null }

    fun touchHelper() : ItemTouchHelper? = draggableBehavior?.touchHelper

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

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int)

    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        bind(holder.binding, dataController[position], position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: ViewDataBinding, item: Model, position: Int)

    fun updateDataSet(dataSet: List<Model>) {
        dataController.onNext(dataSet)
    }

    override fun getItemCount(): Int = dataController.getSize()

    override fun getItemViewType(position: Int) = dataController[position].type

    internal fun getDataSet(): List<Model> = dataController.getDataSet()

    internal fun getItem(position: Int): Model = dataController[position]

    fun onDataChanged() : Flowable<List<Model>> = dataController.onDataChanged()

    open val hasGranularUpdate : Boolean = true

    internal open fun areContentTheSameExtension(oldItemPosition: Int, newItemPosition: Int, oldItem: Model, newItem: Model) = true

}
