//package dev.olog.msc.presentation.widget
//
//import android.content.Context
//import android.graphics.Color
//import android.support.v4.content.ContextCompat
//import android.support.v7.widget.AppCompatTextView
//import android.text.Spannable
//import android.text.SpannableString
//import android.text.style.BackgroundColorSpan
//import android.text.style.ForegroundColorSpan
//import android.util.AttributeSet
//import dev.olog.msc.R
//import dev.olog.msc.constants.AppConstants
//
//class AdaptiveTextView @JvmOverloads constructor(
//        context: Context,
//        attrs: AttributeSet? = null
//
//): AppCompatTextView(context, attrs) {
//
//    private var adaptiveTextColor: Int = ContextCompat.getColor(context, R.color.text_color_primary)
//    private var adaptiveBackgroundColor: Int = ContextCompat.getColor(context, R.color.background)
//
//
//    fun updateColors(textColor: Int, backgroundColor: Int){
//        this.adaptiveTextColor = textColor
//        this.adaptiveBackgroundColor = backgroundColor
//        setTextColor(this.adaptiveTextColor)
//        text = createFlatAdaptiveString(text)
//    }
//
//    override fun setText(text: CharSequence?, type: BufferType?) {
//        if (text == null){
//            super.setText(text, type)
//            return
//        }
//
//        if (AppConstants.THEME.isFlat()){
//            if (AppConstants.IS_ADAPTIVE_COLOR){
//                super.setText(createFlatAdaptiveString(text), type)
//            } else {
//                super.setText(createFlatString(text), type)
//            }
//
//        } else {
//            super.setText(text, type)
//        }
//    }
//
//    private fun createFlatString(text: CharSequence): Spannable {
//        val spannable = SpannableString(text)
//        spannable.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.dark_grey)), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        return spannable
//    }
//
//    private fun createFlatAdaptiveString(text: CharSequence): Spannable {
//        val spannable = SpannableString(text)
//        spannable.setSpan(BackgroundColorSpan(adaptiveBackgroundColor), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        return spannable
//    }
//
//}