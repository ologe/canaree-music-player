package dev.olog.data

import androidx.test.platform.app.InstrumentationRegistry
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dev.olog.data.dagger.SortAdapter
import java.util.*

class AndroidTestDatabase private constructor() {

    companion object {
        operator fun invoke(): Database {
            val schema = Database.Schema
            val driver = AndroidSqliteDriver(
                schema = schema,
                context = InstrumentationRegistry.getInstrumentation().context,
                name = null, // in memory database, see SupportSQLiteOpenHelper.Configuration
            )
            return Database(
                driver = driver,
                sortAdapter = SortAdapter
            )
        }
    }

}