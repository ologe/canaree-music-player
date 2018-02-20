package dev.olog.msc.presentation.base.adp

import android.support.v7.util.DiffUtil

data class AdapterControllerResult(
        val wasEmpty: Boolean,
        val diffUtil: DiffUtil.DiffResult?
)