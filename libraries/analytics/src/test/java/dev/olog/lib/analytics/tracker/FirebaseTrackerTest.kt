package dev.olog.lib.analytics.tracker

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.lib.analytics.tracker.FirebaseTracker.Companion.MAX_SIZE_ALLOWED
import dev.olog.domain.MediaIdCategory
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import dev.olog.test.shared.schedulers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FirebaseTrackerTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val firebase = mock<FirebaseAnalytics>()
    private val sut = FirebaseTracker(firebase, coroutineRule.schedulers)

    @Test
    fun `track screen success`() = coroutineRule.runBlockingTest {
        val screenName = "test"
        val bundle = bundleOf("arg" to 1)

        sut.trackScreen(screenName, bundle)

        verify(firebase).logEvent(screenName, bundle)
    }

    @Test
    fun `track screen, null bundle, success`() = coroutineRule.runBlockingTest {
        val screenName = "test"
        val bundle: Bundle? = null

        sut.trackScreen(screenName, bundle)

        verify(firebase).logEvent(screenName, bundle)
    }

    @Test
    fun `track screen, screen name too long, success`() = coroutineRule.runBlockingTest {
        val screenName = (0..MAX_SIZE_ALLOWED * 2).joinToString("")
        val bundle: Bundle? = null

        sut.trackScreen(screenName, bundle)

        verify(firebase).logEvent(screenName.take(MAX_SIZE_ALLOWED), bundle)
    }

    @Test
    fun `track service event, no args, success`() = coroutineRule.runBlockingTest {
        val event = "test"

        sut.trackServiceEvent(event)

        // unfortunately bundle don't implement equals
        verify(firebase).logEvent(eq(event), any())
    }

    @Test
    fun `track service event, with args, success`() = coroutineRule.runBlockingTest {
        val event = "test"
        val argument = MediaIdCategory.PODCASTS_PLAYLIST

        sut.trackServiceEvent(event, argument)

        // unfortunately bundle don't implement equals
        verify(firebase).logEvent(eq(event), any())
    }

    @Test
    fun `track service event, event name too long, success`() = coroutineRule.runBlockingTest {
        val event = (0..MAX_SIZE_ALLOWED * 2).joinToString("")

        sut.trackServiceEvent(event)

        // unfortunately bundle don't implement equals
        verify(firebase).logEvent(eq(event.take(MAX_SIZE_ALLOWED)), any())
    }

}