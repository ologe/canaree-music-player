package dev.olog.msc.presentation.base.adapter

import android.arch.lifecycle.Lifecycle
import android.content.Context
import dev.olog.msc.presentation.base.BaseModel

abstract class BaseListAdapter<Model: BaseModel> (
        lifecycle: Lifecycle,
        context: Context? = null

) : BaseAdapter<List<Model>, Model>(context, lifecycle, BaseListAdapterController())
