package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.BaseGateway2
import dev.olog.data.DataObserver
import dev.olog.shared.CustomScope
import dev.olog.shared.assertBackgroundThread
import dev.olog.shared.safeSend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

internal abstract class BaseRepository<T, Param>(
    @ApplicationContext protected val context: Context
) : BaseGateway2<T, Param>, CoroutineScope by CustomScope() {

    protected val contentResolver: ContentResolver = context.contentResolver

    protected val channel = ConflatedBroadcastChannel<List<T>>()

    init {
        launch {
            // small delay to make subclass initialization
            delay(10)

            val contentUri = registerMainContentUri()

            contentResolver.registerContentObserver(
                contentUri.uri,
                contentUri.notifyForDescendants,
                DataObserver {
                    launch {
                        assertBackgroundThread()
                        channel.safeSend(queryAll())
                    }
                }
            )
            channel.safeSend(queryAll())
        }
    }

    override fun getAll(): List<T> {
        assertBackgroundThread()
        return channel.valueOrNull
            ?: queryAll() // fallback to normal query if channel never emitted
    }

    override fun observeAll(): Flow<List<T>> {
        assertBackgroundThread()
        return channel.asFlow()
    }

    protected fun <R> observeByParamInternal(
        contentUri: ContentUri,
        action: () -> R
    ): Flow<R> {
        assertBackgroundThread()

        return channelFlow {

            assertBackgroundThread()
            if (!isClosedForSend) {
                safeSend(action())
            }

            val observer = DataObserver {
                launch {
                    if (!isClosedForSend) {
                        safeSend(action())
                    }
                }
            }

            contentResolver.registerContentObserver(
                contentUri.uri,
                contentUri.notifyForDescendants,
                observer
            )
            invokeOnClose { contentResolver.unregisterContentObserver(observer) }
        }
    }

    protected abstract fun registerMainContentUri(): ContentUri
    protected abstract fun queryAll(): List<T>

}

data class ContentUri(
    val uri: Uri,
    val notifyForDescendants: Boolean
)