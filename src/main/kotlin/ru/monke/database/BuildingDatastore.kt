package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedBuilding(
    val id: Int = 0,
    val districtId: Int,
    val cityId: Int,
    val name: String,
    val description: String,
    val production: Int,
    val productionCost: Int,
    val food: Int,
    val gold: Int,
    val defense: Int,
    val photoPath: String
)

class BuildingDatastore(database: Database) {

    object Buildings : IntIdTable("Building") {
        val districtId = reference("district_id", DistrictDatastore.Districts, onDelete = ReferenceOption.CASCADE)
        val cityId = reference("city_id", CityDatastore.Cities, onDelete = ReferenceOption.CASCADE)
        val production = integer("production")
        val productionCost = integer("production_cost")
        val food = integer("food")
        val gold = integer("gold")
        val name = varchar("name", 200)
        val description = varchar("description", 2000)
        val defense = integer("defense")
        val photoPath = varchar("photo_path", 2000)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Buildings)
        }
    }

    suspend fun create(building: ExposedBuilding): Int = dbQuery {
        Buildings.insertIgnore {
            it[districtId] = building.districtId
            it[cityId] = building.cityId
            it[production] = building.production
            it[productionCost] = building.productionCost
            it[food] = building.food
            it[gold] = building.gold
            it[name] = building.name
            it[description] = building.description
            it[defense] = building.defense
            it[photoPath] = building.photoPath
        }[Buildings.id].value
    }

    suspend fun getAllBuildings(): List<ExposedBuilding> = dbQuery {
        Buildings.selectAll().map {
            ExposedBuilding(
                id = it[Buildings.id].value,
                districtId = it[Buildings.districtId].value,
                cityId = it[Buildings.cityId].value,
                production = it[Buildings.production],
                productionCost = it[Buildings.productionCost],
                food = it[Buildings.food],
                gold = it[Buildings.gold],
                name = it[Buildings.name],
                description = it[Buildings.description],
                defense = it[Buildings.defense],
                photoPath = it[Buildings.photoPath]
            )
        }
    }

    suspend fun read(id: Int): ExposedBuilding? = dbQuery {
        Buildings.selectAll()
            .where { Buildings.id eq id}
            .map { ExposedBuilding(
                id = id,
                districtId = it[Buildings.districtId].value,
                cityId = it[Buildings.cityId].value,
                production = it[Buildings.production],
                productionCost = it[Buildings.productionCost],
                food = it[Buildings.food],
                gold = it[Buildings.gold],
                name = it[Buildings.name],
                description = it[Buildings.description],
                defense = it[Buildings.defense],
                photoPath = it[Buildings.photoPath]
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
                it[photoPath] = building.photoPath
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Buildings.deleteWhere { Buildings.id.eq(id) }
        }
    }
}