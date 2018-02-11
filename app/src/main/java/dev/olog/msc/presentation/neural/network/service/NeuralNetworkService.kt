package dev.olog.msc.presentation.neural.network.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import com.crashlytics.android.Crashlytics
import dagger.android.DaggerService
import dev.olog.msc.FirebaseAnalytics
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.GetAllAlbumsForUtilsUseCase
import dev.olog.msc.presentation.utils.images.NeuralImages
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.isOreo
import dev.olog.msc.utils.k.extension.notificationManager
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class NeuralNetworkService : DaggerService() {

    companion object {
        const val NOTIFICATION_ID = 789
        const val TAG = "NeuralNetworkService"
        const val ACTION_STOP = TAG + ".ACTION_STOP"
        const val ACTION_START = TAG + ".ACTION_START"
        const val EXTRA_STYLE = TAG + ".EXTRA_STYLE"
        const val NOTIFICATION_CHANNEL_ID = "neural_network_id"
    }

    override fun onBind(intent: Intent?) : IBinder? = null

    private var builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

    @Inject lateinit var getAllAlbums: GetAllAlbumsForUtilsUseCase
    private var disposable: Disposable? = null
    private var count = 0
    private var size = 1

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()

        FirebaseAnalytics.trackNeuralStart()

        builder = builder.setContentTitle(getString(R.string.neural_service_title))
                .setContentText(getString(R.string.neural_service_subtitle))
                .setProgress(1, 0, true)
                .setDeleteIntent(deletePendingIntent())
//                .setContentIntent(deletePendingIntent())
                .setOngoing(true)
                .addAction(0, getString(R.string.neural_service_cancel), deletePendingIntent())
                .setSmallIcon(R.drawable.vd_bird_singing_24dp)

        val notification = builder.build()
        val importance = NotificationManager.IMPORTANCE_LOW
        if (isOreo()){
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getString(R.string.neural_notification_channel_title), importance)
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)

        }

        startForeground(NOTIFICATION_ID, notification)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun deletePendingIntent(): PendingIntent {
        return PendingIntent.getService(this, 0,
                Intent(this, this::class.java).setAction(ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun deleteAllChildsImages(){
        val list = listOf(
                "folder_neural", "playlist_neural", "artist_neural", "genre_neural"
        )
        for (s in list) {
            val folder = File("${applicationInfo.dataDir}${File.separator}$s")
            if(folder.exists()){
                folder.listFiles().forEach { it.delete() }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action

        when (action){
            ACTION_START -> {
                val styles = intent.getFloatArrayExtra(EXTRA_STYLE)
                start(styles)
            }
            ACTION_STOP -> stopSelf()
        }

        return Service.START_NOT_STICKY
    }

    private fun start(styles: FloatArray){
        disposable = getAllAlbums.execute()
                .firstOrError()
                .observeOn(Schedulers.io())
                .map {
                    size = it.size
                    notificationManager.notify(NOTIFICATION_ID, builder.setProgress(size, 0, false).build())
                    it
                }
                .flattenAsFlowable { it }
                .map {
                    try {
                        val bitmap = ImageUtils.getBitmapFromUriOrNull(applicationContext, Uri.parse(it.image), 768, 768)
                        makeFilteredImage(this, it.id, bitmap!!, styles)
                    } catch (ex: Exception){}
                }
                .doOnNext {
                    count++
                    notificationManager.notify(NOTIFICATION_ID, builder.setProgress(size, count, false).build())
                }
                .toList()
                .subscribe({
                    FirebaseAnalytics.trackNeuralSuccess(true)

                    deleteAllChildsImages()
                    contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
                    contentResolver.notifyChange(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null)
                    contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
                    contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
                    stopForeground(true)
                    stopSelf()
                }, {
                    Crashlytics.logException(it)
                    FirebaseAnalytics.trackNeuralSuccess(false)
                    it.printStackTrace()
                    stopForeground(true)
                    stopSelf()
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        disposable.unsubscribe()
    }

    /*
        album neural structure - albumId_progressive.webp
     */
    private fun makeFilteredImage(context: Context, albumId: Long, bitmap: Bitmap, styles: FloatArray){
        val result = NeuralImages.stylizeTensorFlow(context, bitmap, styles)

        val imageDirectory = ImagesFolderUtils.getImageFolderFor(context, "${ImagesFolderUtils.ALBUM}_neural")
        var progressive = System.currentTimeMillis()
        for (listFile in imageDirectory.listFiles()) {
            val name = listFile.name
            val indexOf = name.indexOf("_")
            if (indexOf != -1){
                val id = name.substring(0, indexOf)
                if (albumId == id.toLong()){
                    progressive = name.substring(indexOf + 1, name.indexOf(".webp")).toLong() + 1
                    listFile.delete()
                    break
                }
            }
        }

        val dest = File(imageDirectory, "${albumId}_$progressive.webp")
        val out = FileOutputStream(dest)
        result.compress(Bitmap.CompressFormat.WEBP, 90, out)
        out.close()
    }

}