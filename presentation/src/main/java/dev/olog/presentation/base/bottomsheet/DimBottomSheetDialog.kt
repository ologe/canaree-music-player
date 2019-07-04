package dev.olog.presentation.base.bottomsheet

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.olog.presentation.R
import dev.olog.shared.extensions.scrimBackground

class DimBottomSheetDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme) {

    override fun setContentView(view: View) {
        super.setContentView(view)
        val scrimColor = context.scrimBackground()
        window?.findViewById<View>(R.id.container)?.setBackgroundColor(scrimColor)
        window?.findViewById<View>(R.id.design_bottom_sheet)?.background = null
    }

}