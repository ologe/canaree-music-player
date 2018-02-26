package dev.olog.msc.presentation.base.adapter

import android.support.v7.util.DiffUtil

data class AdapterControllerResult(
        val wasEmpty: Boolean,
        val diffUtil: DiffUtil.DiffResult?
)