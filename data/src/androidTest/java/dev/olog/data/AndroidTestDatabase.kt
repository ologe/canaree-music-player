package dev.olog.data

import androidx.test.platform.app.InstrumentationRegistry
import dev.olog.data.db.DatabaseFactory

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