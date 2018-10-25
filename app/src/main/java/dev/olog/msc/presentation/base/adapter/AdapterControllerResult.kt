package dev.olog.msc.presentation.base.adapter

import androidx.recyclerview.widget.DiffUtil

data class AdapterControllerResult(
        val wasEmpty: Boolean,
        val diffUtil: DiffUtil.DiffResult?
)