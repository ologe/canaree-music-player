package dev.olog.presentation.utils

import android.content.Context
import android.content.res.Configuration
import org.jetbrains.anko.configuration

val Context.isPortrait: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_PORTRAIT