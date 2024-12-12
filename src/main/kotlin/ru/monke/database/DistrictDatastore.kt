package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.monke.api.DEFAULT_LIMIT
import ru.monke.database.DistrictDatastore.Districts


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

    suspend fun getAllDistricts(
        minProductionCost: Int? = null,
        cityId: Int? = null,
        offset: Long = 0,
        limit: Int = DEFAULT_LIMIT
    ): List<ExposedDistrict> = dbQuery {
        var a = buildList {
            if (minProductionCost != null) this.add(Districts.productionCost greaterEq minProductionCost)
            if (cityId != null) this.add(Districts.cityId eq cityId)
        }

        if (a.isEmpty()) return@dbQuery Districts.selectAll()
            .limit(limit, offset)
            .map {
                ExposedDistrict(
                    id = it[Districts.id].value,
                    cityId = it[Districts.cityId].value,
                    productionCost = it[Districts.productionCost],
                    name = it[Districts.name],
                    photoPath = it[Districts.photoPath]
                )
            }
        return@dbQuery Districts.selectAll().where {a.reduce { acc, condition -> acc and condition } }
            .limit(limit, offset)
            .map {
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