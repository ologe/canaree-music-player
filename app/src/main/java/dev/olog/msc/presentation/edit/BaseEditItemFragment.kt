package dev.olog.msc.presentation.edit

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.ctx
import dev.olog.msc.utils.k.extension.makeDialog

private const val RESULT_LOAD_IMAGE = 12346

abstract class BaseEditItemFragment : BaseFragment() {

    private var progressDialog: ProgressDialog? = null

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        hideLoader()
    }

    protected fun loadLocalImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK){
            data?.data?.let { onLocalImageLoaded(it) }
        }
    }

    protected fun showImageChooser(items: Array<String>, listener: (DialogInterface, Int) -> Unit){
        AlertDialog.Builder(ctx)
                .setItems(items, listener)
                .makeDialog()
    }

    protected fun setImage(string: String, itemId: Int){
        val background = view!!.findViewById<ImageView>(R.id.backgroundCover)
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(background)
        GlideApp.with(ctx).clear(image)

        val builder = GlideApp.with(ctx)
                .load(string)
                .error(GlideApp.with(ctx)
                        .load(Uri.parse(string))
                        .placeholder(CoverUtils.getGradient(ctx, itemId))
                        .override(500)
                ).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(500)
                .priority(Priority.IMMEDIATE)

        builder.into(image)
        builder.into(background)
    }

    protected fun showLoader(@StringRes resId: Int){
        showLoader(getString(resId))
    }

    protected fun showLoader(message: String){
        progressDialog = ProgressDialog.show(context, "", message, true)
        progressDialog?.setCancelable(true)
        progressDialog?.setCanceledOnTouchOutside(true)
        progressDialog?.setOnCancelListener {
            onLoaderCancelled()
            progressDialog?.setOnCancelListener(null)
        }
    }

    protected fun hideLoader(){
        progressDialog?.dismiss()
        progressDialog = null
    }

    abstract fun onLoaderCancelled()
    abstract fun onLocalImageLoaded(uri: Uri)

}