package dev.olog.ui

import androidx.fragment.app.FragmentActivity
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.ScrollType

fun interface ScrollHelperFactory {

    fun create(
        activity: FragmentActivity,
        scrollType: ScrollType,
    ): ScrollHelper

}