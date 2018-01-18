package dev.olog.presentation._base.list

import android.arch.lifecycle.Lifecycle
import android.content.Context
import dev.olog.presentation._base.BaseModel

abstract class BaseListAdapter<Model: BaseModel> (
        lifecycle: Lifecycle,
        context: Context? = null

) : BaseAdapter<List<Model>, Model>(context, lifecycle, BaseListAdapterController())
