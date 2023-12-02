/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.olog.shared.widgets.adapter

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import dev.olog.shared.swap
import java.util.Collections
import java.util.concurrent.Executor

class CustomAsyncListDiffer<T : Any>(
    private val updateCallback: ListUpdateCallback,
    private val config: AsyncDifferConfig<T>
) {

    private var list: MutableList<T>? = null

    var currentList = emptyList<T>()
        private set

    private var maxScheduledGeneration = 0

    fun submitList(newList: List<T>) {
        val runGeneration = ++maxScheduledGeneration
        if (newList === list) {
            return
        }

        // fast simple first insert
        if (list == null) {
            list = newList.toMutableList()
            currentList = Collections.unmodifiableList(newList)
            // notify last, after list is updated
            updateCallback.onInserted(0, newList.size)
            return
        }
        val oldList = requireNotNull(list)

        config.backgroundThreadExecutor.execute {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldList.size
                }

                override fun getNewListSize(): Int {
                    return newList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldList[oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return config.diffCallback.areItemsTheSame(oldItem, newItem)
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldItem = oldList[oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return config.diffCallback.areContentsTheSame(oldItem, newItem)
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                    val oldItem = oldList[oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return config.diffCallback.getChangePayload(oldItem, newItem)
                }
            })
            mainThreadExecutor.execute {
                if (maxScheduledGeneration == runGeneration) {
                    latchList(newList, result)
                }
            }
        }
    }

    fun move(from: Int, to: Int) {
        val list = this.list ?: return
        list.swap(from, to)
        updateCallback.onMoved(from, to)
    }

    fun remove(position: Int) {
        val list = this.list ?: return
        list.removeAt(position)
        updateCallback.onRemoved(position, 1)
    }

    private fun latchList(
        newList: List<T>,
        diffResult: DiffUtil.DiffResult,
    ) {
        list = newList.toMutableList()
        // notify last, after list is updated
        currentList = Collections.unmodifiableList(newList)
        diffResult.dispatchUpdatesTo(updateCallback)
    }

    companion object {
        private val mainThreadExecutor: Executor = MainThreadExecutor()
    }
}

private class MainThreadExecutor : Executor {
    val mHandler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
        mHandler.post(command)
    }
}