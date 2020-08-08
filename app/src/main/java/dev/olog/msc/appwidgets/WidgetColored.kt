package dev.olog.msc.appwidgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.domain.MediaId.Companion.SONGS_CATEGORY
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.image.loader.getCachedBitmap
import dev.olog.msc.R
import dev.olog.feature.presentation.base.palette.ImageProcessor
import dev.olog.shared.coroutines.autoDisposeJob
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

private const val IMAGE_SIZE = 300

class WidgetColored : BaseWidget() {

    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface WidgetEntryPoint {
        fun schedulers(): Schedulers
        fun musicPrefs(): MusicPreferencesGateway
    }

    private var job by autoDisposeJob()

    private fun schedulers(context: Context): Schedulers {
        return EntryPoints.get(context, WidgetEntryPoint::class.java).schedulers()
    }

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray, remoteViews: RemoteViews?) {
        job = GlobalScope.launch(schedulers(context).main) {
            val bitmap = withContext(schedulers(context).io){
                context.getCachedBitmap(SONGS_CATEGORY.playableItem(metadata.id), IMAGE_SIZE)
            } ?: return@launch
            yield()
            val remote = remoteViews ?: RemoteViews(context.packageName, layoutId)
            remote.setTextViewText(R.id.title, metadata.title)
            remote.setTextViewText(R.id.subtitle, metadata.subtitle)

            colorize(context, remote, bitmap)

            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remote)
        }
    }

    private fun colorize(context: Context, remoteViews: RemoteViews, bitmap: Bitmap){
        val result = ImageProcessor(
            context
        ).processImage(bitmap)
        remoteViews.setImageViewBitmap(R.id.cover, result.bitmap)

        updateTextColor(remoteViews, result)

        remoteViews.setInt(R.id.background, "setBackgroundColor", result.background)

        setMediaButtonColors(remoteViews, result.primaryTextColor)
    }

    override val layoutId : Int = R.layout.widget_colored
}