package dev.olog.msc.presentation.base.adapter

import android.support.v7.util.DiffUtil

class AdapterData<Model>(
        val data: List<Model>,
        val diffUtil: DiffUtil.DiffResult?
)