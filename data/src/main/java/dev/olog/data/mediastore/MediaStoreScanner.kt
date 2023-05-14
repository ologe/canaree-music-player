package dev.olog.data.mediastore

import android.content.Context
import android.media.MediaScannerConnection
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

// TODO check it works
// TODO needed?
class MediaStoreScanner @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun scanFilePath(
        path: String
    ) = suspendCancellableCoroutine { continuation ->
        MediaScannerConnection.scanFile(
            context,
            arrayOf(path),
            null,
        ) { _, uri ->
            if (uri == null) {
                Log.e("MediaStoreScanner", "File not scanned $path")
            }
            continuation.resume(Unit)
        }
    }

}