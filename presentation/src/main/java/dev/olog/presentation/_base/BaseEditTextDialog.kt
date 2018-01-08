package dev.olog.presentation._base

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.animation.AnimationUtils
import dev.olog.presentation.R
import dev.olog.presentation.utils.ImeUtils
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.shared.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.layout_edit_text.view.*
import java.util.concurrent.TimeUnit

abstract class BaseEditTextDialog : BaseDialogFragment() {

    private var showKeyboardDisposable : Disposable? = null

    protected abstract fun provideDialogTitle(): Int

    protected open fun provideNegativeMessage() : Int = R.string.popup_negative_cancel

    protected open fun providePositiveMessage() : Int = R.string.popup_positive_ok

    @StringRes
    protected abstract fun provideErrorMessageForEmptyString() : Int

    @StringRes
    protected abstract fun provideErrorMessageForInvalidString(string: String) : Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(provideDialogTitle())
                .setView(R.layout.layout_edit_text)
                .setNegativeButton(provideNegativeMessage(), null)
                .setPositiveButton(providePositiveMessage(), null)

        val dialog = builder.makeDialog()
        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)

        editText.setText(provideStartEditTextValue())

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val editTextString = editText.text.toString()

                    if (editTextString.isBlank()){
                        showError(editTextLayout, provideErrorMessageForEmptyString())
                    } else if (!isStringValid(editTextString)){
                        showError(editTextLayout, provideErrorMessageForInvalidString(editTextString))
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
        view!!.clear.setOnClickListener { view!!.editText.setText("") }
    }

    override fun onPause() {
        super.onPause()
        view!!.clear.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        showKeyboardDisposable.unsubscribe()
    }

    protected abstract fun isStringValid(string: String): Boolean

    protected open fun provideStartEditTextValue(): String = ""

}