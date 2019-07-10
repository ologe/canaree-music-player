package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.data.DataObserver
import dev.olog.shared.CustomScope
import dev.olog.shared.Permissions
import dev.olog.shared.extensions.assertBackground
import dev.olog.shared.utils.assertBackgroundThread
import dev.olog.shared.extensions.safeSend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.launch

internal abstract class BaseRepository<T, Param>(
    @ApplicationContext protected val context: Context
) : BaseGateway<T, Param>, CoroutineScope by CustomScope() {

    protected val contentResolver: ContentResolver = context.contentResolver

    protected val channel = ConflatedBroadcastChannel<List<T>>()

    init {
        firstQuery()
    }

    private fun firstQuery(){
        launch {
            assertBackgroundThread()
            // small delay to make subclass initialization
            delay(10)

            while (!Permissions.canReadStorage(context)){
                delay(300)
            }

            val contentUri = registerMainContentUri()

            contentResolver.registerContentObserver(
                contentUri.uri,
                contentUri.notifyForDescendants,
                DataObserver {
                    launch {
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
        return channel.asFlow().assertBackground()
    }

    protected fun <R> observeByParamInternal(
        contentUri: ContentUri,
        action: () -> R
    ): Flow<R> {

        val flow : Flow<R> =  flowViaChannel { channel ->

            if (!channel.isClosedForSend) {
                channel.offer(action())
            }

            val observer = DataObserver {
                launch {
                    if (!channel.isClosedForSend) {
                        channel.safeSend(action())
                    }
                }
            }

            contentResolver.registerContentObserver(
                contentUri.uri,
                contentUri.notifyForDescendants,
                observer
            )
            channel.invokeOnClose { contentResolver.unregisterContentObserver(observer) }
        }
        return flow.assertBackground()
    }

    protected abstract fun registerMainContentUri(): ContentUri
    protected abstract fun queryAll(): List<T>

}

data class ContentUri(
    val uri: Uri,
    val notifyForDescendants: Boolean
)