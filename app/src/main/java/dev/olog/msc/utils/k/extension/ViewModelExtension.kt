package dev.olog.msc.utils.k.extension

import android.arch.lifecycle.AndroidViewModel
import android.content.Context

val AndroidViewModel.context : Context
    get() = getApplication()