package dev.olog.shared.extension

import androidx.core.content.edit
import dev.olog.flow.test.observer.test
import dev.olog.shared.TestSharedPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SharedPreferenceExtensionTest {

    private val prefs = TestSharedPreferences()

    @Test
    fun `observeKey should return default value when preference is never set`() = runTest {
        prefs.observeKey("key", "default")
            .test(this) {
                assertValues("default")
            }
    }

    @Test
    fun `observeKey should return default value when preference is set to null`() = runTest {
        prefs.edit {
            putString("key", null)
        }

        prefs.observeKey("key", "default")
            .test(this) {
                assertValues("default")
            }
    }

    @Test
    fun `observeKey should return last preference value when preference is set`() = runTest {
        prefs.edit {
            putString("key", "value")
        }

        prefs.observeKey("key", "default")
            .test(this) {
                assertValues("value")
            }
    }

    @Test
    fun `observeKey should return last preference and all pushed value`() = runTest {
        prefs.edit {
            putString("key", "value")
        }

        prefs.observeKey("key", "default")
            .test(this) {
                assertValues("value", "value1", "value2")
            }

        launch {
            delay(1000)
            prefs.edit {
                putString("key", "value1")
            }

            delay(1000)
            prefs.edit {
                putString("key", "value2")
            }

            delay(1000)
            prefs.edit {
                putString("key2", "value3")
            }
        }
    }

    @Test
    fun `observeKey should dispose listeners on completion`() = runTest {
        val job = prefs.observeKey("key", "default")
            .test(this) {
                assertValues("default")
            }

        launch {
            delay(1000)
            Assert.assertEquals(1, prefs.listeners.size)

            job.cancel()
            delay(10) // give some time to handle cancellation
            Assert.assertEquals(0, prefs.listeners.size)
        }
    }

}