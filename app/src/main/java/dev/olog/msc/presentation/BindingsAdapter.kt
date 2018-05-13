package dev.olog.msc.presentation

import android.databinding.BindingAdapter
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.app.GlideApp
import dev.olog.msc.glide.AudioFileCover
import dev.olog.msc.presentation.library.folder.tree.DisplayableFile
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.special.thanks.SpecialThanksModel
import dev.olog.msc.presentation.utils.images.RippleTarget
import dev.olog.msc.presentation.widget.QuickActionView
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.img.ImagesFolderUtils
import kotlin.math.absoluteValue

object BindingsAdapter {

    private const val OVERRIDE_SMALL = 150
    private const val OVERRIDE_MID = 300
    private const val OVERRIDE_BIG = 600

    @JvmStatic
    @BindingAdapter("fileTrackLoader")
    fun loadFile(view: ImageView, item: DisplayableFile){
        val context = view.context
        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(AudioFileCover(item.path!!))
                .override(OVERRIDE_SMALL)
                .placeholder(CoverUtils.getGradient(context, MediaId.songId(item.path.hashCode().toLong())))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }

    @JvmStatic
    @BindingAdapter("fileDirLoader")
    fun loadDirImage(view: ImageView, item: DisplayableFile){
        val path = item.path ?: ""
        val displayableItem = DisplayableItem(0, MediaId.folderId(path),
                "", "", ImagesFolderUtils.forFolder(view.context, path))
        loadImageImpl(view, displayableItem, OVERRIDE_SMALL)
    }

    @JvmStatic
    private fun loadImageImpl(
            view: ImageView,
            item: DisplayableItem,
            override: Int,
            priority: Priority = Priority.HIGH){

        val mediaId = item.mediaId
        val context = view.context

        GlideApp.with(context).clear(view)

        val load: Any = if (ImagesFolderUtils.isChoosedImage(item.image)){
            item.image
        } else item

        GlideApp.with(context)
                .load(load)
                .override(override)
                .priority(priority)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(RippleTarget(view, mediaId.isLeaf))
    }

    @BindingAdapter("albumsArtistImage")
    @JvmStatic
    fun loadAlbumsArtistImage(view: ImageView, item: DisplayableItem){
        if (!item.mediaId.isAlbum) return

        val artistId = item.mediaId.categoryValue.toLong().absoluteValue
        val artistMediaId = MediaId.artistId(artistId)

        val load: Any = if (ImagesFolderUtils.isChoosedImage(item.image)){
            item.image
        } else item.copy(mediaId = artistMediaId)

        GlideApp.with(view.context).clear(view)

        GlideApp.with(view.context)
                .load(load)
                .override(50)
                .placeholder(CoverUtils.getGradient(view.context, artistMediaId))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }

    @BindingAdapter("backgroundDrawable")
    @JvmStatic
    fun loadBackgroundDrawable(view: ImageView, item: DisplayableItem){
        view.background = CoverUtils.onlyGradient(view.context, item.mediaId.resolveId.toInt())
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
        GlideApp.with(view)
                .load(ContextCompat.getDrawable(view.context, item.image))
                .into(view)
    }

    @BindingAdapter("setBoldIfTrue")
    @JvmStatic
    fun setBoldIfTrue(view: TextView, setBold: Boolean){
        val style = if (setBold) Typeface.BOLD else Typeface.NORMAL
        view.setTypeface(null, style)
    }

    @BindingAdapter("quickActionItem")
    @JvmStatic
    fun quickActionItem(view: QuickActionView, item: DisplayableItem){
        view.setId(item.mediaId)
    }

}