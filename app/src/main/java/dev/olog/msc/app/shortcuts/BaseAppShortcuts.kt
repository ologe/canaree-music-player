package dev.olog.msc.app.shortcuts

import android.content.Context
import android.content.Intent
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.getBitmap
import dev.olog.msc.utils.k.extension.toast

abstract class BaseAppShortcuts(
        protected val context: Context

) : AppShortcuts {

    override fun addDetailShortcut(mediaId: MediaId, title: String, image: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            val intent = Intent(context, MainActivity::class.java)
            intent.action = AppConstants.SHORTCUT_DETAIL
            intent.putExtra(AppConstants.SHORTCUT_DETAIL_MEDIA_ID, mediaId.toString())

//            val img = when (mediaId.category){
//                MediaIdCategory.ARTISTS -> image
//                MediaIdCategory.ALBUMS,
//                MediaIdCategory.SONGS -> Uri.parse(image)
//                else -> Uri.fromFile(File(image))
//            }

            val id = if (mediaId.isFolder) mediaId.categoryValue.hashCode().toLong()
            else mediaId.categoryValue.toLong()

            val placeholder = CoverUtils.getGradient(context, id.toInt(), mediaId.category.ordinal)
            context.getBitmap(image, placeholder, 128, {
                val shortcut = ShortcutInfoCompat.Builder(context, title)
                        .setShortLabel(title)
                        .setIcon(IconCompat.createWithBitmap(it))
                        .setIntent(intent)
                        .build()

                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)

                onAddedSuccess(context)
            }, extend = {
                circleCrop()
            })

        } else {
            onAddedNotSupported(context)
        }
    }

    private fun onAddedSuccess(context: Context){
        context.toast(R.string.app_shortcut_added_to_home_screen)
    }

    private fun onAddedNotSupported(context: Context){
        context.toast(R.string.app_shortcut_add_to_home_screen_not_supported)
    }

}