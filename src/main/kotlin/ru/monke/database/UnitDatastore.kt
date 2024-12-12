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
data class ExposedUnit(
    val id: Int = 0,
    val playerId: Int,
    val damage: Int,
    val health: Int,
    val name: String,
    val description: String,
    val movement: Int,
    val productionCost: Int,
    val salary: Int,
    val photoPath: String
)

class UnitDatastore(database: Database) {

    object Units : IntIdTable("Unit") {
        val playerId = reference("player_id", PlayerDatastore.Players, onDelete = ReferenceOption.CASCADE)
        val damage = integer("damage")
        val health = integer("health")
        val name = varchar("name", 20)
        val description = varchar("description", 2000)
        val movement = integer("movement")
        val productionCost = integer("production_cost")
        val salary = integer("salary")
        val photoPath = varchar("photo_path", 2000)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Units)
        }
    }

    suspend fun create(unit: ExposedUnit): Int = dbQuery {
        Units.insert {
            it[playerId] = unit.playerId
            it[damage] = unit.damage
            it[health] = unit.health
            it[name] = unit.name
            it[description] = unit.description
            it[movement] = unit.movement
            it[productionCost] = unit.productionCost
            it[salary] = unit.salary
            it[photoPath] = unit.photoPath
        }[Units.id].value
    }

    suspend fun read(id: Int): ExposedUnit? = dbQuery {
        Units.selectAll()
            .where { Units.id eq id}
            .map { ExposedUnit(
                id = id,
                playerId = it[Units.playerId].value,
                damage = it[Units.damage],
                name = it[Units.name],
                health = it[Units.health],
                description = it[Units.description],
                movement = it[Units.movement],
                productionCost = it[Units.productionCost],
                salary = it[Units.salary],
                photoPath = it[Units.photoPath]
            ) }
            .singleOrNull()
    }

    suspend fun getAllUnits(
        playerId: Int? = null,
        minDamage: Int? = null,
        minHealth: Int? = null,
        minMovement: Int? = null,
        offset: Long = 0,
        limit: Int = DEFAULT_LIMIT
    ): List<ExposedUnit> = dbQuery {
        // Build dynamic conditions
        val queryConditions = mutableListOf<Op<Boolean>>()

        if (playerId != null) {
            queryConditions += (Units.playerId eq playerId)
        }
        if (minHealth != null) {
            queryConditions += (Units.health greaterEq minHealth)
        }
        if (minDamage != null) {
            queryConditions += (Units.damage greaterEq minDamage)
        }
        if (minMovement != null) {
            queryConditions += (Units.movement greaterEq minMovement)
        }

        // Combine all conditions using AND
        val combinedCondition = if (queryConditions.isNotEmpty()) {
            queryConditions.reduce { acc, condition -> acc and condition }
        } else {
            Op.TRUE  // No filters applied, select everything
        }

        // Execute query with filters and pagination
        Units.selectAll().where { combinedCondition }
            .limit(limit, offset)
            .map {
                ExposedUnit(
                    id = it[Units.id].value,
                    playerId = it[Units.playerId].value,
                    damage = it[Units.damage],
                    name = it[Units.name],
                    health = it[Units.health],
                    movement = it[Units.movement],
                    productionCost = it[Units.productionCost],
                    salary = it[Units.salary],
                    description = it[Units.description],
                    photoPath = it[Units.photoPath]
                )
            }
    }


    suspend fun update(id: Int, unit: ExposedUnit) {
        dbQuery {
            Units.update({ Units.id eq id}) {
                it[playerId] = unit.playerId
                it[damage] = unit.damage
                it[health] = unit.health
                it[name] = unit.name
                it[description] = description
                it[movement] = unit.movement
                it[productionCost] = unit.productionCost
                it[salary] = unit.salary
                it[photoPath] = unit.photoPath
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Units.deleteWhere { Units.id.eq(id) }
        }
    }



}