package dev.olog.presentation.tab.layoutmanager

import android.content.Context
import dev.olog.shared.android.extensions.configuration

class BaseSpanSizeLookup(
        context: Context

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        var span = 3

        if (isTablet) {
            span++
        }

        return spanCount / span
    }
}