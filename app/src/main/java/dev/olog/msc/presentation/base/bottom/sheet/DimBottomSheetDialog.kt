package dev.olog.msc.presentation.base.bottom.sheet

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.dip
import dev.olog.msc.utils.k.extension.isLandscape
import dev.olog.shared.colorScrim

class DimBottomSheetDialog(context: Context, theme: Int)
    : BottomSheetDialog(context, theme) {

    override fun setContentView(view: View) {
        super.setContentView(view)
        val scrimColor = context.colorScrim()
        window?.findViewById<View>(R.id.container)?.setBackgroundColor(scrimColor)
        window?.findViewById<View>(R.id.design_bottom_sheet)?.background = null

        if (context.isLandscape){
            behavior.peekHeight = context.dip(300)
        }
    }

}