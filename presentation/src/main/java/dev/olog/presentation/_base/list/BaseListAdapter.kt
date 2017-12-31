package dev.olog.presentation._base.list

import android.arch.lifecycle.Lifecycle
import dev.olog.presentation._base.BaseModel

abstract class BaseListAdapter<Model: BaseModel> (
        lifecycle: Lifecycle

) : BaseAdapter<List<Model>, Model>(lifecycle, BaseListAdapterController())
