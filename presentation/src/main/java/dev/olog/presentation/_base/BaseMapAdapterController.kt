package dev.olog.presentation._base

import android.widget.BaseAdapter
import dev.olog.presentation.fragment_detail.DetailDataType
import io.reactivex.processors.PublishProcessor
import kotlin.reflect.KClass

class BaseMapAdapterController<E : Enum<E>, Model>(
        private val adapter: BaseAdapter
) {

    private val publisher = PublishProcessor.create<MutableMap<E, MutableList<Model>>>()

    private val originalDataSet : MutableMap<E, MutableList<Model>> = mutableMapOf()

    init {

        E::class.enumValues()


        for (enumValue in enumValues) {

        }

        for (value in DetailDataType.values()) {
            originalDataSet.put(value, mutableListOf())
        }
    }

}

fun <T: Enum<T>> KClass<T>.enumValues(): Array<T> = java.enumConstants