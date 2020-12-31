package dev.olog.test.shared

import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance

// TODO create some lint rule to enforce this
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class StatelessSutTest {

    @AfterEach
    open fun teardown() {
        clearAllMocks()
    }

}