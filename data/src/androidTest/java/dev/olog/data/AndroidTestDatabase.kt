package dev.olog.data

import androidx.test.platform.app.InstrumentationRegistry

class AndroidTestDatabase private constructor() {

    companion object {
        operator fun invoke(): Database {
            return DatabaseFactory.createAndroid(
                context = InstrumentationRegistry.getInstrumentation().context,
                inMemory = true,
            )
        }
    }

}