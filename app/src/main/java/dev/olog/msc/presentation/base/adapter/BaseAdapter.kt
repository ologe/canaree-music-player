//package dev.olog.msc.presentation.base.adapter
//
//import android.arch.lifecycle.Lifecycle
//import android.content.Context
//import android.databinding.DataBindingUtil
//import android.databinding.ViewDataBinding
//import android.support.v7.widget.RecyclerView
//import android.support.v7.widget.helper.ItemTouchHelper
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import dev.olog.msc.presentation.base.BaseModel
//import dev.olog.msc.presentation.base.adp.DataBoundViewHolder
//import io.reactivex.Flowable
//
//abstract class BaseAdapter <DataType, Model: BaseModel>(
//        context: Context?,
//        lifecycle: Lifecycle,
//        protected val dataController: AdapterController<DataType, Model>
//
//) : RecyclerView.Adapter<DataBoundViewHolder>() {
//
//    protected open val touchCallbackConfig = TouchCallbackConfig()
//    var onDataChangedListener : OnDataChangedListener? = null
//
//    init {
//        lifecycle.addObserver(dataController)
//        dataController.setAdapter(this)
//    }
//
//    private val draggableBehavior by lazy { if (touchCallbackConfig.canDrag) {
//        TouchBehaviorImpl(context, dataController, touchCallbackConfig, touchCallbackConfig.canSwipe)
//    } else null }
//
//    fun touchHelper() : ItemTouchHelper? = draggableBehavior?.touchHelper
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
//        val viewHolder = DataBoundViewHolder(binding)
//        initViewHolderListeners(viewHolder, viewType)
//        return viewHolder
//    }
//
//    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)
//
//    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
//        bind(holder.binding, dataController[position], position)
//        holder.binding.executePendingBindings()
//    }
//
//    protected abstract fun bind(binding: ViewDataBinding, item: Model, position: Int)
//
//    override fun getItemCount(): Int = dataController.getSize()
//
//    override fun getItemViewType(position: Int) = dataController[position].type
//
//    open fun hasGranularUpdate() : Boolean = true
//
//    fun getItemAt(position: Int): Model = dataController[position]
//
//    fun getItemPositionByPredicate(predicate: (Model) -> Boolean) : Int{
//        return dataController.getItemPositionByPredicate(predicate)
//    }
//
//    fun onDataChanged() : Flowable<DataType> = dataController.onDataChanged()
//
//    fun updateDataSet(dataSet: DataType){
//        dataController.onNext(dataSet)
//    }
//
//    internal open fun areContentTheSameExtension(oldItemPosition: Int, newItemPosition: Int, oldItem: Model, newItem: Model) = true
//
//}