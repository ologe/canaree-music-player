package dev.olog.lib.image.provider.fetcher

import android.media.MediaMetadataRetriever
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.lib.image.provider.model.AudioFileCover
import org.jaudiotagger.audio.AudioFileIO
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

class AudioFileCoverFetcher(
        private val model: AudioFileCover

) : DataFetcher<InputStream> {

    private var stream: InputStream? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(model.path)
            val picture = retriever.embeddedPicture ?: AudioFileIO.read(File(model.path))
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