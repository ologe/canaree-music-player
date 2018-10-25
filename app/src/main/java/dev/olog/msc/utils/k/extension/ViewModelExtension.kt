package dev.olog.msc.utils.k.extension

import android.content.Context
import androidx.lifecycle.AndroidViewModel

val AndroidViewModel.context : Context
    get() = getApplication()