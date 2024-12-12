package ru.monke.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseProvider {
    lateinit var database: Database
}

fun countBuildings(cityId: Int): Int {
    return transaction(DatabaseProvider.database) {
        val result = exec("SELECT count_buildings_for_city($cityId)") { rs ->
            if (rs.next()) {
                rs.getInt(1)
            } else {
                0
            }
        }
        result ?: 0
    }
}