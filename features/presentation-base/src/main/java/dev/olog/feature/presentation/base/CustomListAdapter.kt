package dev.olog.feature.presentation.base

import androidx.recyclerview.widget.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private val backedListField = AsyncListDiffer::class.java
    .getDeclaredField("mList")
    .also { it.isAccessible = true }

/**
 * [ListAdapter] copy that exposes internal mList
 */
abstract class CustomListAdapter<T: Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {

    private val mDiffer = AsyncListDiffer(
        AdapterListUpdateCallback(this),
        AsyncDifferConfig.Builder(diffCallback).build()
    )

    @Suppress("UNCHECKED_CAST")
    protected val backedList: MutableList<T>
        get() = backedListField.get(mDiffer) as? MutableList<T> ?: mutableListOf()

    private val mListener = AsyncListDiffer.ListListener<T> { previousList, currentList ->
        onCurrentListChanged(previousList, currentList)
    }

    init {
        mDiffer.addListListener(mListener)
    }

    fun submitList(list: List<T>?) {
        mDiffer.submitList(list)
    }

    fun submitList(
        list: List<T>?,
        commitCallback: Runnable?
    ) {
        mDiffer.submitList(list, commitCallback)
    }

    fun getItem(position: Int): T {
        return mDiffer.currentList[position]
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    val currentList: List<T>
        get() = mDiffer.currentList

    open fun onCurrentListChanged(
        previousList: List<T>,
        currentList: List<T>
    ) {
        
    }

    suspend fun suspendSubmitList(list: List<T>) = suspendCancellableCoroutine<Unit> {
        submitList(list, Runnable {
            it.resume(Unit)
        })
    }

}
