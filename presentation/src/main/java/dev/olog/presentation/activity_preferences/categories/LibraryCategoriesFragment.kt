package dev.olog.presentation.activity_preferences.categories

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import dev.olog.domain.entity.LibraryCategoryBehavior
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation._base.list.DataBoundViewHolder
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback
import dev.olog.shared.clearThenAdd
import dev.olog.shared.swap
import kotlinx.android.synthetic.main.dialog_list_multi_choice_item.view.*
import javax.inject.Inject

class LibraryCategoriesFragment : BaseDialogFragment() {

    companion object {
        const val TAG = "LibraryCategoriesFragment"

        fun newInstance(): LibraryCategoriesFragment {
            return LibraryCategoriesFragment()
        }
    }

    @Inject lateinit var presenter: LibraryCategoriesFragmentPresenter
    private lateinit var adapter: DraggableAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.dialog_list, null, false)

        val builder = AlertDialog.Builder(context)
                .setTitle("Library categories")
                .setView(view)
                .setNeutralButton("Reset", { _, _ ->
                    val defaultData = presenter.getDefaultDataSet()
                    adapter.updateDataSet(defaultData)
                })
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, { _, _ ->
                    presenter.setDataSet(adapter.data)
                    activity!!.setResult(Activity.RESULT_OK)
                })

        val list = view.findViewById<RecyclerView>(R.id.list)
        adapter = DraggableAdapter(presenter.getDataSet().toMutableList())
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)

        return builder.makeDialog()
    }

    class DraggableAdapter(val data: MutableList<LibraryCategoryBehavior>)
        : RecyclerView.Adapter<DataBoundViewHolder<*>>(),
            ItemTouchHelperAdapter {

        private val callback = ItemTouchHelperCallback(this, false)
        private val touchHelper = ItemTouchHelper(callback)

        override fun getItemCount(): Int = data.size

        override fun getItemViewType(position: Int): Int = R.layout.dialog_list_multi_choice_item

        override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
            holder.binding.setVariable(BR.item, data[position])
            holder.binding.executePendingBindings()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
            val viewHolder = DataBoundViewHolder(binding)
            initViewHolderListeners(viewHolder)
            return viewHolder
        }

        private fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>){
            viewHolder.itemView.findViewById<View>(R.id.dragHandle)?.setOnTouchListener { _, event ->
                if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(viewHolder)
                    true
                } else false
            }
            viewHolder.itemView.setOnClickListener {
                val item = data[viewHolder.adapterPosition]
                item.enabled = !item.enabled
                viewHolder.itemView.checkBox.isChecked = item.enabled
            }
        }

        fun updateDataSet(list: List<LibraryCategoryBehavior>){
            this.data.clearThenAdd(list)
            notifyDataSetChanged()
        }

        override fun onItemMove(from: Int, to: Int) {
            data.swap(from, to)
            data.forEachIndexed { index, item ->
                item.order = index
            }
        }

        override fun onItemDismiss(position: Int) {
            throw IllegalStateException("operation not supported")
        }

        override val draggableViewType = R.layout.dialog_list_multi_choice_item


    }



}