package dev.olog.msc.presentation.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.R
import dev.olog.presentation.model.BaseModel
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.msc.utils.k.extension.logStackStace
import dev.olog.shared.extensions.toast
import dev.olog.shared.extensions.unsubscribe
import dev.olog.presentation.base.DataBoundViewHolder
import io.reactivex.disposables.Disposable

abstract class AbsAdapter<Model : BaseModel>(
        lifecycle: Lifecycle,
        protected val controller : AdapterDataController<Model> = BaseAdapterDataController()

) : androidx.recyclerview.widget.RecyclerView.Adapter<DataBoundViewHolder>(), DefaultLifecycleObserver,
    TouchableAdapter {

    private var dataDisposable : Disposable? = null
    var touchHelper: ItemTouchHelper? = null

    private var afterDataChanged : ((List<*>) -> Unit)? = null

    private val firstEmission = AdapterFirstEmission(lifecycle)

    fun setAfterDataChanged(func : ((List<*>) -> Unit)?, skipInitialValue: Boolean = true){
        this.afterDataChanged = func
        if (!skipInitialValue){
            afterDataChanged?.invoke(controller.getAll())
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
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
        controller.getItem(position)?.let { model ->
            bind(holder.binding, model, position)
            holder.binding.executePendingBindings()
        }
    }

    protected abstract fun bind(binding: ViewDataBinding, item: Model, position: Int)

    override fun getItemCount(): Int = controller.getSize()

    override fun getItemViewType(position: Int) = controller.getItem(position)?.type ?: -1

    override fun onStart(owner: LifecycleOwner) {
        controller.resumeObservingData(true)
        dataDisposable = controller.handleNewData(extendAreItemTheSame)
                .subscribe({ (wasEmpty, callback) ->

                    if (wasEmpty || !hasGranularUpdate() || callback == null){
                        notifyDataSetChanged()
                    } else {
                        callback.dispatchUpdatesTo(this)
                    }

                    firstEmission.emitIfFirst()
                    afterDataChanged?.invoke(controller.getAll())
                }, Throwable::logStackStace)
    }

    override fun onStop(owner: LifecycleOwner) {
        dataDisposable.unsubscribe()
    }

    fun onFirstEmission(func: () -> Unit){
        firstEmission.setAction(func)
    }

    fun updateDataSet(data: Any){
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

    protected open val onSwipeLeftAction : ((position: Int) -> Any)? = null
    protected open val onSwipeRightAction : ((position: Int) -> Any)? = null

    /*
        Relative position is calculated from first interactive item,
        because there are items that isn't
     */
    override fun onMoved(from: Int, to: Int) {
        val positionPivot = indexOf { canInteractWithViewHolder(it.type)!! }
        val relativeFrom = from - positionPivot
        val relativeTo = to - positionPivot

        controller.swap(from, to)
        notifyItemMoved(from, to)
        // drag action must be defined
        onDragAction!!.invoke(relativeFrom, relativeTo)
    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        val context = viewHolder.itemView.context

        onSwipeLeftAction?.invoke(position)
        notifyItemChanged(position)
        context.toast(R.string.common_added_to_play_next)
    }

    /*
        Relative position is calculated from first interactive item,
        because there are items that isn't
     */
    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        controller.pauseObservingData()
        val positionPivot = indexOf { canInteractWithViewHolder(it.type)!! }
        val relativePosition = position - positionPivot

        controller.remove(position)
        notifyItemRemoved(position)
        // swipe action must be defined
        onSwipeRightAction?.invoke(relativePosition)
        controller.resumeObservingData(false)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? = null

    override fun onClearView() {
        controller.resumeObservingData(false)
    }
}