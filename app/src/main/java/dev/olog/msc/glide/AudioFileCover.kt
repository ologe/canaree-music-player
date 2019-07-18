package dev.olog.msc.glide

import android.media.MediaMetadataRetriever
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import org.jaudiotagger.audio.AudioFileIO
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

data class AudioFileCover(
        val filePath: String
)

class AudioFileCoverLoader : ModelLoader<AudioFileCover, InputStream> {

    override fun buildLoadData(model: AudioFileCover, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(model), AudioFileCoverFetcher(model))
    }

    override fun handles(model: AudioFileCover): Boolean = true

    class Factory : ModelLoaderFactory<AudioFileCover, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<AudioFileCover, InputStream> {
            return AudioFileCoverLoader()
        }

        override fun teardown() {
        }
    }

}

class AudioFileCoverFetcher(
        private val model: AudioFileCover

) : DataFetcher<InputStream> {

    private var stream: InputStream? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(model.filePath)
            val picture = retriever.embeddedPicture ?: AudioFileIO.read(File(model.filePath))
                    .tagOrCreateAndSetDefault.firstArtwork.binaryData
            stream = ByteArrayInputStream(picture)
            callback.onDataReady(stream)
        } catch (ex: Exception){
            callback.onLoadFailed(ex)
        } finally {
            stream?.close()
            retriever.release()
        }
    }

    override fun cleanup() {
        try {
            stream?.close()
        } catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    override fun cancel() {

    }

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
}