package ru.monke.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun dropDatabases(database: Database) {
    transaction(database) {
        SchemaUtils.drop(BuildingDatastore.Buildings)
        SchemaUtils.drop(DistrictDatastore.Districts)
        SchemaUtils.drop(CityDatastore.Cities)
        SchemaUtils.drop(UnitDatastore.Units)
        SchemaUtils.drop(PlayerDatastore.Players)
    }
}