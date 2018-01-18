package dev.olog.presentation._base.list

import android.arch.lifecycle.Lifecycle
import android.content.Context
import dev.olog.presentation._base.BaseModel

abstract class BaseMapAdapter<E: Enum<E>, Model : BaseModel> (
        lifecycle: Lifecycle,
        enums: Array<E>,
        context: Context? = null

) : BaseAdapter<MutableMap<E, MutableList<Model>>, Model>(context, lifecycle, BaseMapAdapterController(enums))
