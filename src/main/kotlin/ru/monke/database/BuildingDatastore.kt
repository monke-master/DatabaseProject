package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedBuilding(
    val districtId: Int,
    val cityId: Int,
    val name: String,
    val description: String,
    val production: Int,
    val productionCost: Int,
    val food: Int,
    val gold: Int,
    val defense: Int,
)

class BuildingDatastore(database: Database) {

    object Buildings : IntIdTable("Building") {
        val districtId = reference("district_id", DistrictDatabase.Districts)
        val cityId = reference("city_id", CityDatastore.Cities)
        val production = integer("production")
        val productionCost = integer("production_cost")
        val food = integer("food")
        val gold = integer("gold")
        val name = varchar("name", 200)
        val description = varchar("description", 2000)
        val defense = integer("defense")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Buildings)
        }
    }

    suspend fun create(building: ExposedBuilding): Int = dbQuery {
        Buildings.insert {
            it[districtId] = building.districtId
            it[cityId] = building.cityId
            it[production] = building.production
            it[productionCost] = building.productionCost
            it[food] = building.food
            it[gold] = building.gold
            it[name] = building.name
            it[description] = building.description
            it[defense] = building.defense
        }[Buildings.id].value
    }

    suspend fun read(id: Int): ExposedBuilding? = dbQuery {
        Buildings.selectAll()
            .where { Buildings.id eq id}
            .map { ExposedBuilding(
                districtId = it[Buildings.districtId].value,
                cityId = it[Buildings.cityId].value,
                production = it[Buildings.production],
                productionCost = it[Buildings.productionCost],
                food = it[Buildings.food],
                gold = it[Buildings.gold],
                name = it[Buildings.name],
                description = it[Buildings.description],
                defense = it[Buildings.defense]
            ) }
            .singleOrNull()
    }

    suspend fun update(id: Int, building: ExposedBuilding) {
        dbQuery {
            Buildings.update({ Buildings.id eq id}) {
                it[districtId] = building.districtId
                it[cityId] = building.cityId
                it[production] = building.production
                it[productionCost] = building.productionCost
                it[food] = building.food
                it[gold] = building.gold
                it[name] = building.name
                it[description] = description
                it[defense] = defense
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Buildings.deleteWhere { Buildings.id.eq(id) }
        }
    }
}