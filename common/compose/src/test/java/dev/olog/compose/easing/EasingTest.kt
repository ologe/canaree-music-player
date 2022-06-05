package dev.olog.compose.easing

import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.compose.animation.core.Easing
import dev.olog.compose.animation.AccelerateEasing
import dev.olog.compose.animation.BounceEasing
import dev.olog.compose.animation.DecelerateEasing
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EasingTest {

    companion object {
        private const val EPSYLON = 0.000001f
        private val factorsToTest = listOf(0f, .33f, .5f, .66f, .99f, 1f, 1.25f, 1.66f, 2f)
        private val valuesToTest = listOf(
            0f, 0.05f, .1f, .2f, .25f, .33f, .4f, .5f, .66f, .7f, .85f, .9f, .95f, .99f, 1f
        )
    }

    @Test
    fun `test accelerate`() {
        factorsToTest.forEach { factor ->
            testEasing(AccelerateEasing(factor), AccelerateInterpolator(factor))
        }
    }

    @Test
    fun `test decelerate`() {
        factorsToTest.forEach { factor ->
            testEasing(DecelerateEasing(factor), DecelerateInterpolator(factor))
        }
    }

    @Test
    fun `test bounce`() {
        testEasing(BounceEasing, BounceInterpolator())
    }

    private fun testEasing(
        easing: Easing,
        interpolator: Interpolator,
    ) {
        for (v in valuesToTest) {
            Assert.assertEquals(
                interpolator.getInterpolation(v),
                easing.transform(v),
                EPSYLON,
            )
        }
    }

}