package dev.olog.msc.presentation.base.adapter

import androidx.recyclerview.widget.DiffUtil

class AdapterData<Model>(
        val data: List<Model>,
        val diffUtil: DiffUtil.DiffResult?
)