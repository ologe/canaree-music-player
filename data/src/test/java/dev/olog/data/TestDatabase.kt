package dev.olog.data

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

class TestDatabase private constructor() {

    companion object {
        operator fun invoke(): Database {
            val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            for (pragma in DatabaseFactory.pragmas) {
                driver.execute(null, pragma, 0)
            }
            Database.Schema.create(driver)
            return DatabaseFactory.create(driver)
        }
    }

}