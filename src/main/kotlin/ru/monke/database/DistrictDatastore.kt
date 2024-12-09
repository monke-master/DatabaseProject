package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


@Serializable
data class ExposedDistrict(
    val id: Int = 0,
    val cityId: Int,
    val name: String,
    val productionCost: Int,
)

class DistrictDatastore(database: Database) {

    object Districts : IntIdTable("District") {
        val cityId = reference("city_id", CityDatastore.Cities)
        val name = varchar("name", 200)
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
            it[name] = district.name
            it[productionCost] = district.productionCost
        }[Districts.id].value
    }

    suspend fun read(id: Int): ExposedDistrict? {
        return dbQuery {
            Districts.selectAll()
                .where { Districts.id eq id}
                .map { ExposedDistrict(
                    id = id,
                    cityId = it[Districts.cityId].value,
                    name = it[Districts.name],
                    productionCost = it[Districts.productionCost],
                ) }
                .singleOrNull()
        }
    }

    suspend fun getAllDistricts(): List<ExposedDistrict> = dbQuery {
        Districts.selectAll().map {
            ExposedDistrict(
                id = it[Districts.id].value,
                cityId = it[Districts.cityId].value,
                productionCost = it[Districts.productionCost],
                name = it[Districts.name],
            )
        }
    }

    suspend fun update(id: Int, district: ExposedDistrict) {
        dbQuery {
            Districts.update({ Districts.id eq id}) {
                it[cityId] = district.cityId
                it[name] = district.name
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