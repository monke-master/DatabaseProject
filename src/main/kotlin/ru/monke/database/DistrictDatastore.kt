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
    val photoPath: String
)

class DistrictDatastore(database: Database) {

    object Districts : IntIdTable("District") {
        val cityId = reference("city_id", CityDatastore.Cities, onDelete = ReferenceOption.CASCADE)
        val name = varchar("name", 200)
        val productionCost = integer("production_cost")
        val photoPath = varchar("photo_path", 2000)
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
            it[photoPath] = district.photoPath
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
                    photoPath = it[Districts.photoPath]
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
                photoPath = it[Districts.photoPath]
            )
        }
    }

    suspend fun update(id: Int, district: ExposedDistrict) {
        dbQuery {
            Districts.update({ Districts.id eq id}) {
                it[cityId] = district.cityId
                it[name] = district.name
                it[productionCost] = district.productionCost
                it[photoPath] = district.photoPath
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Districts.deleteWhere { Districts.id.eq(id) }
        }
    }
}