package ru.monke

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import ru.monke.api.authRoutesWithBootstrap
import ru.monke.api.detailsRoutes
import ru.monke.api.entityRoutes
import ru.monke.api.entitySelectionRoutes
import ru.monke.database.*
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres
import ru.yandex.qatools.embed.postgresql.util.SocketUtil

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    val database = connectToPostgres()
    dropDatabases(database)

    val playerDatastore = PlayerDatastore(database)
    val cityDatastore = CityDatastore(database)
    val buildingDatastore = BuildingDatastore(database)
    val districtDatastore = DistrictDatastore(database)
    val unitDatastore = UnitDatastore(database)

    authRoutesWithBootstrap(playerDatastore)
    entitySelectionRoutes()
    runBlocking {
        fillMockData(playerDatastore, cityDatastore, districtDatastore, buildingDatastore, unitDatastore)
    }

    entityRoutes(
        cityDatastore = cityDatastore,
        buildingDatastore = buildingDatastore,
        districtDatastore = districtDatastore,
        unitDatastore = unitDatastore
    )

    detailsRoutes(
        cityDatastore = cityDatastore,
        buildingDatastore = buildingDatastore,
        districtDatastore = districtDatastore,
        unitDatastore = unitDatastore
    )
}

fun connectToPostgres(): Database {
    val url = "jdbc:postgresql://localhost:5432/gamedb"
    val user = "admin"
    val password = "admin"

    return Database.connect(
        url = url,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    ).also {
        println("Connected to PostgreSQL!")
    }
}

private suspend fun fillMockData(
    playerDatastore: PlayerDatastore,
    cityDatastore: CityDatastore,
    districtDatastore: DistrictDatastore,
    buildingDatastore: BuildingDatastore,
    unitDatastore: UnitDatastore
) {
    val player1 = ExposedPlayer(
        login = "mahno_batko",
        password = "password",
        isAdmin = true
    )
    val id = playerDatastore.create(player1)
    val city1 = ExposedCity(
        name = "Moscow",
        playerId = id,
        population = 10000
    )
    val city2 = ExposedCity(
        name = "Domodedovo",
        playerId = id,
        population = 304003
    )
    val city3 = ExposedCity(
        name = "Париж",
        playerId = id,
        population = 1
    )
    val city4 = ExposedCity(
        name = "Суп-Хрустальный",
        playerId = id,
        population = 12343
    )
    val id1 = cityDatastore.create(city1)
    val id2 = cityDatastore.create(city2)
    val id3 = cityDatastore.create(city3)
    val id4 = cityDatastore.create(city4)

    val district1 = ExposedDistrict(
        name = "Кампус",
        cityId = id1,
        productionCost = 800
    )
    val district2 = ExposedDistrict(
        name = "Военный лагерь",
        cityId = id1,
        productionCost = 1800
    )

    val districtId1 = districtDatastore.create(district1)
    val districtId2 = districtDatastore.create(district2)

    val building1 = ExposedBuilding(
        cityId = id1,
        districtId = districtId1,
        name = "Библиоетка",
        defense = 8,
        description = "Для умненьких студентиков",
        food = 1,
        gold = 10,
        productionCost = 1900,
        production = 200
    )
    buildingDatastore.create(building1)

    val unit1 = ExposedUnit(
        playerId = id,
        damage = 80,
        name = "Солдадерос",
        description = "Солдатки из Мексики",
        health = 100,
        movement = 90,
        productionCost = 800,
        salary = 10
    )
    unitDatastore.create(unit1)
}
