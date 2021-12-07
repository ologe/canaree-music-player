package dev.olog.data

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.olog.data.dagger.SortAdapter

class TestDatabase private constructor() {

    companion object {
        operator fun invoke(): Database {
            val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            Database.Schema.create(driver)
            return Database(
                driver = driver,
                sortAdapter = SortAdapter
            )
        }
    }

}