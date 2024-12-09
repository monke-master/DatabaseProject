package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedCity(
    val id: Int = 0,
    val playerId: Int,
    val name: String,
    val population: Int
)

class CityDatastore(database: Database) {

    object Cities : IntIdTable("City") {
        val playerId = reference("player_id", PlayerDatastore.Players)
        val name = varchar("name", 200)
        val population = integer("population")
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
                ) }
                .singleOrNull()
        }
    }

    suspend fun getAllCities(): List<ExposedCity> = dbQuery {
        Cities.selectAll().map {
            ExposedCity(
                id = it[Cities.id].value,
                playerId = it[Cities.playerId].value,
                name = it[Cities.name],
                population = it[Cities.population]
            )
        }
    }

    suspend fun update(id: Int, city: ExposedCity) {
        dbQuery {
            Cities.update({ Cities.id eq id}) {
                it[playerId] = city.playerId
                it[name] = city.name
                it[population] = city.population
            }
        }
    }


    suspend fun delete(id: Int) {
        dbQuery {
            Cities.deleteWhere { Cities.id.eq(id) }
        }
    }

}