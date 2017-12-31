package dev.olog.presentation._base.list

import android.arch.lifecycle.Lifecycle
import dev.olog.presentation._base.BaseModel

abstract class BaseMapAdapter<E: Enum<E>, Model : BaseModel> (
        lifecycle: Lifecycle,
        enums: Array<E>

) : BaseAdapter<MutableMap<E, MutableList<Model>>, Model>(lifecycle, BaseMapAdapterController(enums))
