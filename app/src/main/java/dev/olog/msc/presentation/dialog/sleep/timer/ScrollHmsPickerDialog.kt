package dev.olog.msc.presentation.dialog.sleep.timer

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseDialogFragment
import dev.olog.msc.presentation.theme.AppTheme

open class ScrollHmsPickerDialog : BaseDialogFragment() {
    interface HmsPickHandler {
        fun onHmsPick(reference: Int, hours: Int, minutes: Int, seconds: Int)
    }

    var reference: Int = -1
    var autoStep: Boolean = false
    @ColorRes
    var colorNormal: Int = android.R.color.darker_gray
    @ColorRes
    var colorSelected: Int = if (AppTheme.isDarkTheme()) R.color.accent_secondary else R.color.accent
    @ColorRes
    var colorBackground: Int = if (AppTheme.isDarkTheme()) R.color.dark_dialog_background else android.R.color.white
    var dismissListener: DialogInterface.OnDismissListener? = null
    var pickListener: ScrollHmsPickerDialog.HmsPickHandler? = null

    protected lateinit var hmsPicker: ScrollHmsPicker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.picker_fragment, container, false)
        hmsPicker = view.findViewById<ScrollHmsPicker>(R.id.hms_picker).also { picker ->
            picker.setAutoStep(autoStep)
            picker.setColorNormal(colorNormal)
            picker.setColorSelected(colorSelected)
        }
        val textColor = ContextCompat.getColor(view.context, colorSelected)
        view.findViewById<Button>(R.id.button_cancel).apply {
            setTextColor(textColor)
            setOnClickListener { dismiss() }
        }
        view.findViewById<Button>(R.id.button_ok).apply {
            setTextColor(textColor)
        }
        dialog?.run {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(dialog)
    }

}