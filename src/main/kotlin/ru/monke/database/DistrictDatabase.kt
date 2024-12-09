package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


@Serializable
data class ExposedDistrict(
    val cityId: Int,
    val productionCost: Int,
)

class DistrictDatabase(database: Database) {

    object Districts : IntIdTable("District") {
        val cityId = reference("city_id", CityDatastore.Cities)
        val productionCost = integer("production_cost")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Districts)
        }
    }

    suspend fun create(district: ExposedDistrict): Int = dbQuery {
        Districts.insert {
            it[cityId] = district.cityId
            it[productionCost] = district.productionCost
        }[Districts.id].value
    }

    suspend fun read(id: Int): ExposedDistrict? {
        return dbQuery {
            Districts.selectAll()
                .where { Districts.id eq id}
                .map { ExposedDistrict(
                    cityId = it[Districts.cityId].value,
                    productionCost = it[Districts.productionCost],
                ) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, district: ExposedDistrict) {
        dbQuery {
            Districts.update({ Districts.id eq id}) {
                it[cityId] = district.cityId
                it[productionCost] = district.productionCost
            }
        }
    }


    suspend fun delete(id: Int) {
        dbQuery {
            Districts.deleteWhere { Districts.id.eq(id) }
        }
    }
}