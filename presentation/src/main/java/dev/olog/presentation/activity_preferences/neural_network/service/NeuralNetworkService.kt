package dev.olog.presentation.activity_preferences.neural_network.service

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import dagger.android.DaggerService
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.presentation.R
import dev.olog.shared_android.Constants
import dev.olog.shared_android.extension.notificationManager
import dev.olog.shared_android.neural.NeuralImages
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class NeuralNetworkService : DaggerService() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var builder = NotificationCompat.Builder(this, "id")

    @Inject lateinit var getAllSongsUseCase: GetAllSongsUseCase
    private var disposable: Disposable? = null
    private var count = 0
    private var size = 1

    override fun onCreate() {
        super.onCreate()
        builder = builder.setContentTitle("Stylize")
                .setContentText("")
                .setProgress(1, 0, true)
                .setSmallIcon(R.drawable.vd_bird_singing_24dp)

        notificationManager.notify(789, builder.build())

        disposable = getAllSongsUseCase.execute()
                .subscribeOn(Schedulers.computation())
                .doOnNext {
                    size = it.size
                    notificationManager.notify(789, builder.setProgress(size, 0, false).build())
                }
                .firstOrError()
                .map { it.asSequence()
                        .filter { it.album != Constants.UNKNOWN_ALBUM }
                        .distinctBy { it.albumId }
                        .toList()
                }.flattenAsFlowable { it }
                .parallel()
                .runOn(Schedulers.computation())
                .map {
                    runBlocking {
                        try {
                            val ctx = this@NeuralNetworkService
                            val bitmap = MediaStore.Images.Media.getBitmap(ctx.contentResolver, Uri.parse(it.image))
                            makeFilteredImage(ctx, it.albumId, bitmap).await()
                        } catch (ex: Exception){}
                    }
                }
                .sequential()
                .doOnNext {
                    count++
                    notificationManager.notify(789, builder.setProgress(size, count, false).build())
                }
                .toList()
                .subscribe({
                    notificationManager.cancel(789)
                    stopSelf()
                }, {
                    it.printStackTrace()
                    notificationManager.cancel(789)
                    stopSelf()
                })
    }

    private fun makeFilteredImage(context: Context, albumId: Long, bitmap: Bitmap) : Deferred<Bitmap> = async {
        val result = NeuralImages.stylizeTensorFlow(context, bitmap)

        val parentFile = File("${context.applicationInfo.dataDir}${File.separator}album_neural")
        parentFile.mkdirs()
        val dest = File(parentFile, albumId.toString())
        val out = FileOutputStream(dest)
        result.compress(Bitmap.CompressFormat.WEBP, 85, out)
        out.close()

        result
    }

}