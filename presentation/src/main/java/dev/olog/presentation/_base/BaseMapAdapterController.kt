package dev.olog.presentation._base

import android.arch.lifecycle.LifecycleObserver
import dev.olog.presentation.fragment_detail.DetailDataType
import io.reactivex.processors.PublishProcessor

class BaseMapAdapterController<E : Enum<E>, Model>(
        private val adapter: BaseMapAdapter<E, Model>

) : LifecycleObserver{

    private val publisher = PublishProcessor.create<MutableMap<E, MutableList<Model>>>()

    private val originalDataSet : MutableMap<E, MutableList<Model>> = mutableMapOf()

    init {
        Enum


        for (enumValue in enumValues) {

        }

        for (value in DetailDataType.values()) {
            originalDataSet.put(value, mutableListOf())
        }
    }

}