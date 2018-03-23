package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.utils.k.extension.configuration

class BaseSpanSizeLookup(
        context: Context,
        private val isPortrait: Boolean

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isBigTablet = smallestWidthDip >= 720

    override fun getSpanSize(position: Int): Int {
        val span = if (isPortrait) spanCount / 3 else spanCount / 4

        if (isBigTablet){
            return span + 1
        }

        return span
    }
}