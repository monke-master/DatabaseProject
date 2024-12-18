package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.monke.api.DEFAULT_LIMIT
import kotlin.math.min

@Serializable
data class ExposedBuilding(
    val id: Int = 0,
    val districtId: Int,
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

    suspend fun getAllBuildings(
        districtId: Int? = null,
        minProduction: Int? = null,
        minDefense: Int? = null,
        limit: Int = DEFAULT_LIMIT,
        offset: Long = 0
    ): List<ExposedBuilding> = dbQuery {
        // Build dynamic conditions
        val queryConditions = mutableListOf<Op<Boolean>>()

        if (districtId != null) {
            queryConditions += (Buildings.districtId eq districtId)
        }
        if (minProduction != null) {
            queryConditions += (Buildings.production greaterEq minProduction)
        }
        if (minDefense != null) {
            queryConditions += (Buildings.defense greaterEq minDefense)
        }

        // Combine all conditions using AND
        val combinedCondition = if (queryConditions.isNotEmpty()) {
            queryConditions.reduce { acc, condition -> acc and condition }
        } else {
            Op.TRUE  // No filters applied, select everything
        }

        // Execute the query with filters and pagination
        Buildings.selectAll().where { combinedCondition }
            .limit(limit, offset)
            .map {
                ExposedBuilding(
                    id = it[Buildings.id].value,
                    districtId = it[Buildings.districtId].value,
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