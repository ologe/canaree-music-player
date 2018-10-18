package dev.olog.msc.presentation.base.bottom.sheet

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.dip
import dev.olog.msc.utils.k.extension.isLandscape

class DimBottomSheetDialog(context: Context, theme: Int)
    : BottomSheetDialog(context, theme) {

    private var behavior: BottomSheetBehavior<FrameLayout>? = null

    private fun getScrimColor(): Int {
        if (AppTheme.isWhiteTheme()) {
            return 0x88FFFFFF.toInt()
        }
        return 0xAA232323.toInt()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        window?.findViewById<View>(R.id.container)?.setBackgroundColor(getScrimColor())
        window?.findViewById<View>(R.id.design_bottom_sheet)?.background = null

        if (context.isLandscape){
            val bottomSheet = window?.findViewById(R.id.design_bottom_sheet) as FrameLayout
            behavior = BottomSheetBehavior.from(bottomSheet)
            behavior?.peekHeight = context.dip(300)
        }
    }

}