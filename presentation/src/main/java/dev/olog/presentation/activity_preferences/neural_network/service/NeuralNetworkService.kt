package dev.olog.presentation.activity_preferences.neural_network.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import dagger.android.DaggerService
import dev.olog.domain.interactor.GetAllSongsForImagesUseCase
import dev.olog.presentation.R
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.Constants
import dev.olog.shared_android.ImagesFolderUtils
import dev.olog.shared_android.extension.notificationManager
import dev.olog.shared_android.neural.NeuralImages
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class NeuralNetworkService : DaggerService() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var builder = NotificationCompat.Builder(this, "id")

    @Inject lateinit var getAllSongsUseCase: GetAllSongsForImagesUseCase
    private var disposable: Disposable? = null
    private var count = 0
    private var size = 1

    override fun onCreate() {
        super.onCreate()
        builder = builder.setContentTitle("Stylize")
                .setContentText("")
                .setProgress(1, 0, true)
                .setDeleteIntent(PendingIntent.getService(this, 0,
                        Intent(this, this::class.java).setAction("stop"),
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.drawable.vd_bird_singing_24dp)

        notificationManager.notify(789, builder.build())

        disposable = getAllSongsUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe { deleteAll() }
                .map {
                    val result = it.asSequence()
                            .filter { it.album != Constants.UNKNOWN_ALBUM }
                            .distinctBy { it.albumId }
                            .toList()

                    size = result.size
                    notificationManager.notify(789, builder.setProgress(size, 0, false).build())

                    result
                }
                .flattenAsFlowable { it }
                .map {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(it.image))
                        makeFilteredImage(this, it.albumId, bitmap)
                    } catch (ex: Exception){}
                }
                .doOnNext {
                    count++
                    notificationManager.notify(789, builder.setProgress(size, count, false).build())
                }
                .toList()
                .subscribe({
                    contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
                    contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
                    contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
                    notificationManager.cancel(789)
                    stopSelf()
                }, {
                    it.printStackTrace()
                    notificationManager.cancel(789)
                    stopSelf()
                })
    }

    private fun deleteAll(){
        val list = listOf(
                "folder_neural", "playlist_neural", "album_neural", "artist_neural", "genre_neural"
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

        if (action == "stop"){
            stopSelf()
        }

        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.unsubscribe()
        notificationManager.cancel(789)
    }

    private fun makeFilteredImage(context: Context, albumId: Long, bitmap: Bitmap){
        val result = NeuralImages.stylizeTensorFlow(context, bitmap)

        val parentFile = ImagesFolderUtils.getImageFolderFor(context, "${ImagesFolderUtils.ALBUM}_neural")
        val dest = File(parentFile, "$albumId")
        val out = FileOutputStream(dest)
        result.compress(Bitmap.CompressFormat.WEBP, 85, out)
        out.close()
    }

}