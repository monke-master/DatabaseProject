package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import ru.monke.api.DEFAULT_LIMIT
import ru.monke.database.CityDatastore.Cities

@Serializable
data class ExposedCity(
    val id: Int = 0,
    val playerId: Int,
    val name: String,
    val population: Int,
    val photoPath: String
)

class CityDatastore(database: Database) {

    object Cities : IntIdTable("City") {
        val playerId = reference("player_id", PlayerDatastore.Players, onDelete = ReferenceOption.CASCADE)
        val name = varchar("name", 200)
        val population = integer("population")
        val photoPath = varchar("photo_path", 2000)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Cities)
        }
    }

    suspend fun create(city: ExposedCity): Int = dbQuery {
        Cities.insert {
            it[playerId] = city.playerId
            it[name] = city.name
            it[population] = city.population
            it[photoPath] = city.photoPath
        }[Cities.id].value
    }

    suspend fun read(id: Int): ExposedCity? {
        return dbQuery {
            Cities.selectAll()
                .where { Cities.id eq id}
                .map { ExposedCity(
                    id = id,
                    playerId = it[Cities.playerId].value,
                    name = it[Cities.name],
                    population = it[Cities.population],
                    photoPath = it[Cities.photoPath]
                ) }
                .singleOrNull()
        }
    }

    suspend fun getAllCities(
        minPopulation: Int? = null,
        name: String? = null,
        offset: Long = 0,
        limit: Int = DEFAULT_LIMIT
    ): List<ExposedCity> = dbQuery {
        // Start constructing the query
        val query = Cities.selectAll().where {
            // Combine conditions dynamically
            val condition = mutableListOf<Op<Boolean>>()

            if (minPopulation != null) {
                condition += (Cities.population greaterEq minPopulation)
            }
            if (name != null) {
                condition += (Cities.name like "%$name%")
            }

            // If no filters are provided, select everything
            if (condition.isNotEmpty()) {
                condition.reduce { acc, c -> acc and c }
            } else {
                Op.TRUE // No filters, select everything
            }
        }
            .limit(limit, offset)

        query.map {
            ExposedCity(
                id = it[Cities.id].value,
                playerId = it[Cities.playerId].value,
                name = it[Cities.name],
                population = it[Cities.population],
                photoPath = it[Cities.photoPath]
            )
        }
    }

    suspend fun update(id: Int, city: ExposedCity) {
        dbQuery {
            Cities.update({ Cities.id eq id}) {
                it[playerId] = city.playerId
                it[name] = city.name
                it[population] = city.population
                it[photoPath] = city.photoPath
            }
        }
    }


    suspend fun delete(id: Int) {
        dbQuery {
            Cities.deleteWhere { Cities.id.eq(id) }
        }
    }

}