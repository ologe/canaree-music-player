package dev.olog.msc.presentation.base

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.View
import android.view.animation.AnimationUtils
import dev.olog.msc.R
import dev.olog.msc.presentation.utils.ImeUtils
import dev.olog.msc.utils.k.extension.makeDialog
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

abstract class BaseEditTextDialog : BaseDialogFragment() {

    private var showKeyboardDisposable : Disposable? = null

    private lateinit var clearButton : View
    private lateinit var editText : TextInputEditText

    protected abstract fun provideDialogTitle(): Int

    protected open fun provideNegativeMessage() : Int = R.string.popup_negative_cancel

    protected open fun providePositiveMessage() : Int = R.string.popup_positive_ok

    @StringRes
    protected abstract fun provideErrorMessageForBlankForm() : Int

    @StringRes
    protected abstract fun provideErrorMessageForInvalidForm(string: String) : Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(provideDialogTitle())
                .setView(R.layout.layout_edit_text)
                .setNegativeButton(provideNegativeMessage(), null)
                .setPositiveButton(providePositiveMessage(), null)

        val dialog = builder.makeDialog()
        editText = dialog.findViewById(R.id.editText)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)
        clearButton = dialog.findViewById(R.id.clear)

        editText.setText(provideStartEditTextValue())

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val editTextString = editText.text.toString()

                    if (editTextString.isBlank()){
                        showError(editTextLayout, provideErrorMessageForBlankForm())
                    } else if (!isStringValid(editTextString)){
                        showError(editTextLayout, provideErrorMessageForInvalidForm(editTextString))
                    } else {
                        onValidData(editTextString)
                        dismiss()
                    }
                }

        showKeyboardDisposable = Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ ImeUtils.showIme(editText) }, Throwable::printStackTrace)

        return dialog
    }

    private fun showError(editTextLayout: TextInputLayout, @StringRes errorStringId: Int){
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        editTextLayout.startAnimation(shake)
        editTextLayout.error = getString(errorStringId)
        editTextLayout.isErrorEnabled = true
        editTextLayout.postDelayed({
            try {
                editTextLayout.isErrorEnabled = false
            } catch (ignored: Exception){}
        }, 2000)
    }

    abstract fun onValidData(string: String)

    override fun onResume() {
        super.onResume()
        clearButton.setOnClickListener { editText.setText("") }
    }

    override fun onPause() {
        super.onPause()
        clearButton.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        showKeyboardDisposable.unsubscribe()
    }

    protected abstract fun isStringValid(string: String): Boolean

    protected open fun provideStartEditTextValue(): String = ""

}