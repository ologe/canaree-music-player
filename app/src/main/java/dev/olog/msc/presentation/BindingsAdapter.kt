package dev.olog.msc.presentation

import android.databinding.BindingAdapter
import android.graphics.Typeface
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.special.thanks.SpecialThanksModel
import dev.olog.msc.presentation.utils.images.RippleTarget
import dev.olog.msc.presentation.widget.QuickActionView
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.img.CoverUtils
import java.io.File
import kotlin.math.absoluteValue

object BindingsAdapter {

    private const val OVERRIDE_SMALL = 150
    private const val OVERRIDE_MID = 300
    private const val OVERRIDE_BIG = 600

    @JvmStatic
    private fun loadImageImpl(
            view: ImageView,
            item: DisplayableItem?,
            override: Int,
            priority: Priority = Priority.HIGH){

        item ?: return

        val mediaId = item.mediaId
        val context = view.context

        GlideApp.with(context).clear(view)

        val source = resolveSource(mediaId)
        val id = resolveId(mediaId)
        val image = resolveUri(item.image)

        var request = GlideApp.with(context)
                .load(image)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(override)
                .priority(priority)
                .placeholder(CoverUtils.getGradient(context, id, source))

        if (!mediaId.isLeaf){
            request = request.transition(DrawableTransitionOptions.withCrossFade())
        }

        request.into(RippleTarget(view, mediaId.isLeaf))
    }

    @BindingAdapter("albumsArtistImage")
    @JvmStatic
    fun loadAlbumsArtistImage(view: ImageView, item: DisplayableItem){
        if (!item.mediaId.isAlbum) return

        val artistId = item.mediaId.categoryValue.toInt().absoluteValue

        GlideApp.with(view.context).clear(view)

        GlideApp.with(view.context)
                .load(item.image)
                .circleCrop()
                .override(50)
                .skipMemoryCache(true)
                .placeholder(CoverUtils.getGradient(view.context, artistId, MediaIdCategory.ARTISTS.ordinal))
                .into(view)
    }

    @BindingAdapter("imageSong")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, OVERRIDE_SMALL)
    }

    @BindingAdapter("imageAlbum")
    @JvmStatic
    fun loadAlbumImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, OVERRIDE_MID, Priority.HIGH)
    }

    @BindingAdapter("imageBigAlbum")
    @JvmStatic
    fun loadBigAlbumImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, OVERRIDE_BIG, Priority.IMMEDIATE)
    }

    @BindingAdapter("imageSpecialThanks")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: SpecialThanksModel) {
        val context = view.context

        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(Uri.EMPTY)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(OVERRIDE_SMALL)
                .placeholder(ContextCompat.getDrawable(view.context, item.image))
                .into(view)
    }

    @BindingAdapter("setBoldIfTrue")
    @JvmStatic
    fun setBoldIfTrue(view: TextView, setBold: Boolean){
        val style = if (setBold) Typeface.BOLD else Typeface.NORMAL
        view.setTypeface(null, style)
    }

    @JvmStatic
    private fun resolveSource(mediaId: MediaId): Int {
        if (mediaId.isLeaf){
            return MediaIdCategory.SONGS.ordinal
        }
        return mediaId.category.ordinal
    }

    @JvmStatic
    private fun resolveUri(imageAsString: String): Uri {
        val file = File(imageAsString)
        return if (file.exists()){
            Uri.fromFile(file)
        } else {
            Uri.parse(imageAsString)
        }
    }

    @JvmStatic
    fun resolveId(mediaId: MediaId): Int {
        if (mediaId.isLeaf){
            return mediaId.leaf!!.toInt()
        }
        if (mediaId.isFolder){
            return mediaId.categoryValue.hashCode()
        }
        return mediaId.categoryValue.toInt()
    }

    @BindingAdapter("quickActionItem")
    @JvmStatic
    fun quickActionItem(view: QuickActionView, item: DisplayableItem){
        view.setId(item.mediaId)
    }

}