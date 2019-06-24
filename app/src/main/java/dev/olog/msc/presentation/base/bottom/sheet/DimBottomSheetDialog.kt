package dev.olog.msc.presentation.base.bottom.sheet

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.olog.msc.R
import dev.olog.shared.extensions.colorScrim

class DimBottomSheetDialog(context: Context, theme: Int)
    : BottomSheetDialog(context, theme) {

    override fun setContentView(view: View) {
        super.setContentView(view)
        val scrimColor = context.colorScrim()
        window?.findViewById<View>(R.id.container)?.setBackgroundColor(scrimColor)
        window?.findViewById<View>(R.id.design_bottom_sheet)?.background = null
    }

}