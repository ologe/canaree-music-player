package dev.olog.msc.presentation.base

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import dev.olog.msc.utils.k.extension.makeDialog
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.toast

abstract class BaseDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val application = activity!!.application

        val builder = AlertDialog.Builder(context)
                .setTitle(title(application))
                .setMessage(message(application))
                .setNegativeButton(negativeButtonMessage(application), null)
                .setPositiveButton(positiveButtonMessage(application), { dialog, which ->
                    positiveAction(dialog, which)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete { application.toast(successMessage(application)) }
                            .doOnError { application.toast(failMessage(application)) }
                            .subscribe()
                })

        return builder.makeDialog()
    }

    protected abstract fun title(context: Context): CharSequence
    protected abstract fun message(context: Context): CharSequence

    protected abstract fun negativeButtonMessage(context: Context): Int
    protected abstract fun positiveButtonMessage(context: Context): Int

    protected abstract fun successMessage(context: Context): CharSequence
    protected abstract fun failMessage(context: Context): CharSequence

    protected abstract fun positiveAction(dialogInterface: DialogInterface, which: Int) : Completable

}