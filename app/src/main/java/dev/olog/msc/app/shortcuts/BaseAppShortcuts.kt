package dev.olog.msc.app.shortcuts

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.ImageUtils
import org.jetbrains.anko.toast
import java.io.File

abstract class BaseAppShortcuts(
        protected val context: Context

) : AppShortcuts {

    override fun addDetailShortcut(mediaId: MediaId, title: String, image: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            val intent = Intent(context, MainActivity::class.java)
            intent.action = AppConstants.SHORTCUT_DETAIL
            intent.putExtra(AppConstants.SHORTCUT_DETAIL_MEDIA_ID, mediaId.toString())

            val shortcut = ShortcutInfoCompat.Builder(context, title)
                    .setShortLabel(title)
                    .setIcon(IconCompat.createWithBitmap(getBitmap(context, mediaId, image)))
                    .setIntent(intent)
                    .build()

            ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)

            onAddedSuccess(context)
        } else {
            onAddedNotSupported(context)
        }
    }

    private fun getBitmap(context: Context, mediaId: MediaId, image: String): Bitmap {
        val id = if (mediaId.isFolder) mediaId.categoryValue.hashCode().toLong()
        else mediaId.categoryValue.toLong()

        val file = File(image)
        val uri = if (file.exists()){
            Uri.fromFile(file)
        } else {
            Uri.parse(image)
        }

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context, uri, id, 128, 128)
        return drawRounded(bitmap)
    }

    private fun drawRounded(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawARGB(0,0,0,0)
        val paint = Paint()
        paint.isAntiAlias = true
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }


    private fun onAddedSuccess(context: Context){
        context.toast(R.string.app_shortcut_added_to_home_screen)
    }

    private fun onAddedNotSupported(context: Context){
        context.toast(R.string.app_shortcut_add_to_home_screen_not_supported)
    }

}