package dev.olog.presentation.sleeptimer

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import dev.olog.presentation.R

open class ScrollHmsPickerDialog : DialogFragment() {
    interface HmsPickHandler {
        fun onHmsPick(reference: Int, hours: Int, minutes: Int, seconds: Int)
    }

    var reference: Int = -1
    var autoStep: Boolean = false
    @ColorRes
    var colorNormal: Int = android.R.color.darker_gray
    @ColorRes
    var colorSelected: Int = R.color.defaultColorAccent
    @ColorRes
    var colorBackground: Int = android.R.color.white
    var dismissListener: DialogInterface.OnDismissListener? = null
    var pickListener: HmsPickHandler? = null

    protected lateinit var hmsPicker: ScrollHmsPicker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_picker, container, false)
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(dialog)
    }

}