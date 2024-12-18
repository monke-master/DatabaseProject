package ru.monke.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import ru.monke.database.*

const val DEFAULT_LIMIT = 3

fun Application.entityRoutes(
    cityDatastore: CityDatastore,
    unitDatastore: UnitDatastore,
    buildingDatastore: BuildingDatastore,
    districtDatastore: DistrictDatastore
) {
    routing {
        get("/entities/{type}") {
            val entityType = call.parameters["type"] ?: return@get call.respondText(
                "Entity type is required", status = HttpStatusCode.BadRequest
            )

            val filterParams = call.request.queryParameters
            val page = filterParams["page"]?.toIntOrNull() ?: 1

            val entities = when (entityType) {
                "city" -> {
                    val minPopulation = filterParams["minPopulation"]?.toIntOrNull()
                    val name = filterParams["name"]

                    val offset = (page - 1) * DEFAULT_LIMIT

                    cityDatastore.getAllCities(minPopulation, name, offset = offset.toLong())
                }
                "unit" -> {
                    val playerId = filterParams["playerId"]?.toIntOrNull()
                    val minDamage = filterParams["minDamage"]?.toIntOrNull()
                    val minHealth = filterParams["minHealth"]?.toIntOrNull()
                    val minMovement = filterParams["minMovement"]?.toIntOrNull()

                    val offset = (page - 1) * DEFAULT_LIMIT

                    unitDatastore.getAllUnits(
                        playerId = playerId,
                        minDamage = minDamage,
                        minMovement = minMovement,
                        minHealth = minHealth,
                        offset = offset.toLong()
                    )
                }
                "building" -> {
                    val districtId = filterParams["districtId"]?.toIntOrNull()
                    val production = filterParams["production"]?.toIntOrNull()
                    val defense = filterParams["defense"]?.toIntOrNull()

                    val offset = (page - 1) * DEFAULT_LIMIT

                    buildingDatastore.getAllBuildings(
                        districtId = districtId,
                        minProduction = production,
                        minDefense = defense,
                        offset = offset.toLong()
                    )
                }
                "district" -> {
                    val productionCost = filterParams["productionCost"]?.toIntOrNull()
                    val cityId = filterParams["cityId"]?.toIntOrNull()

                    val offset = (page - 1) * DEFAULT_LIMIT

                    districtDatastore.getAllDistricts(
                        minProductionCost = productionCost,
                        cityId = cityId,
                        offset = offset.toLong()
                    )
                }
                else -> return@get call.respondText(
                    "Unknown entity type", status = HttpStatusCode.BadRequest
                )
            }

            call.respondHtml {
                entityListPage(entityType, entities, filterParams, page)
            }
        }
    }
}

fun HTML.entityListPage(entityType: String, entities: List<Any>, filterParams: Parameters, currentPage: Int) {
    head {
        title { +"Entity List - $entityType" }
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
    }

    body {
        div(classes = "container my-4") {
            h1(classes = "mb-4") { +"List of ${entityType.capitalize()}" }

            div(classes = "mb-3") {
                when (entityType) {
                    "city" -> {
                        val minPopulation = filterParams["minPopulation"]?.toIntOrNull()
                        val name = filterParams["name"]
                        form(method = FormMethod.get, action = "/entities/city") {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"City Name:" }
                                textInput(name = "name") {
                                    placeholder = "Enter City Name"
                                    classes = setOf("form-control")
                                    name?.let { value = name }
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Min Population:" }
                                numberInput(name = "minPopulation") {
                                    placeholder = "Enter Min Population"
                                    classes = setOf("form-control")
                                    minPopulation?.let { value = minPopulation.toString() }
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary") { +"Фильтровать записи" }
                        }
                    }
                    "unit" -> {
                        val playerId = filterParams["playerId"]?.toIntOrNull()
                        val minDamage = filterParams["minDamage"]?.toIntOrNull()
                        val minHealth = filterParams["minHealth"]?.toIntOrNull()
                        val minMovement = filterParams["minMovement"]?.toIntOrNull()

                        form(method = FormMethod.get, action = "/entities/unit") {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Название юнита:" }
                                textInput(name = "name") {
                                    placeholder = "Введите название юнита"
                                    classes = setOf("form-control")
                                    playerId?.let {
                                        value = playerId.toString()
                                    }
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Минимальный урон:" }
                                numberInput(name = "minDamage") {
                                    placeholder = "Введите Минимальный урон"
                                    classes = setOf("form-control")
                                    minDamage?.let {
                                        value = minDamage.toString()
                                    }
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Минимальное здоровье:" }
                                numberInput(name = "minHealth") {
                                    placeholder = "Введите Минимальное здоровье"
                                    classes = setOf("form-control")
                                    minHealth?.let {
                                        value = minHealth.toString()
                                    }
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Минимальное перемещение:" }
                                numberInput(name = "minDamage") {
                                    placeholder = "Введите Минимальное перемещение"
                                    classes = setOf("form-control")
                                    minMovement?.let {
                                        value = minMovement.toString()
                                    }
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary") { +"Фильтровать записи" }
                        }
                    }
                    "building" -> {
                        val districtId = filterParams["districtId"]?.toIntOrNull()
                        val production = filterParams["production"]?.toIntOrNull()
                        val defense = filterParams["defense"]?.toIntOrNull()

                        form(method = FormMethod.get, action = "/entities/building") {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"District ID:" }
                                numberInput(name = "districtId") {
                                    placeholder = "Enter District ID"
                                    classes = setOf("form-control")

                                    districtId?.let {
                                        value = districtId.toString()
                                    }
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Мин. производство:" }
                                numberInput(name = "production") {
                                    placeholder = "Введите мин. производство"
                                    classes = setOf("form-control")

                                    production?.let {
                                        value = production.toString()
                                    }
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Defense:" }
                                numberInput(name = "defense") {
                                    placeholder = "Enter Defense Value"
                                    classes = setOf("form-control")

                                    defense?.let {
                                        value = defense.toString()
                                    }
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary") { +"Фильтровать записи" }
                        }
                    }
                    "district" -> {
                        val productionCost = filterParams["productionCost"]?.toIntOrNull()
                        val cityId = filterParams["cityId"]?.toIntOrNull()
                        form(method = FormMethod.get, action = "/entities/district") {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"City ID:" }
                                numberInput(name = "cityId") {
                                    placeholder = "Enter City ID"
                                    classes = setOf("form-control")
                                    cityId?.let {
                                        value = cityId.toString()
                                    }
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production Cost:" }
                                numberInput(name = "productionCost") {
                                    placeholder = "Enter Production Cost"
                                    classes = setOf("form-control")
                                    productionCost?.let {
                                        value = productionCost.toString()
                                    }
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary") { +"Фильтровать записи" }
                        }
                    }
                }
            }

            div(classes = "row row-cols-1 row-cols-md-3 g-4") {
                entities.forEach { entity ->
                    div(classes = "col") {
                        div(classes = "card h-100 text-center") {
                            img(
                                classes = "card-img-top mx-auto d-block",
                                src = getPhotoPath(entity),
                                alt = "$entityType Image"
                            ) {
                                attributes["style"] = "width: 40%; height: auto;"
                            }
                            // Display entity fields dynamically
                            div(classes = "card-body") {
                                h5(classes = "card-title") {
                                    when (entity) {
                                        is ExposedCity -> +entity.name
                                        is ExposedUnit -> +entity.name
                                        is ExposedBuilding -> +entity.name
                                        is ExposedDistrict -> +"District #${entity.name}"
                                    }
                                }
                                p(classes = "card-text") {
                                    when (entity) {
                                        is ExposedCity -> +"Population: ${entity.population}"
                                        is ExposedUnit -> +"Damage: ${entity.damage}, Health: ${entity.health}"
                                        is ExposedBuilding -> +"Production: ${entity.production}, Defense: ${entity.defense}"
                                        is ExposedDistrict -> +"Production Cost: ${entity.productionCost}"
                                    }
                                }
                                a(href = "/details/$entityType/${getId(entity)}", classes = "btn btn-primary") {
                                    +"View Details"
                                }
                            }
                        }
                    }
                }
            }

            when (entityType) {
                "city" -> {
                    val minPopulation = filterParams["minPopulation"]?.toIntOrNull() ?: 0
                    val name = filterParams["name"] ?: ""

                    div {
                        if (currentPage > 1) {
                            a(href = "/entities/city?page=${currentPage - 1}&limit=$DEFAULT_LIMIT&minPopulation=$minPopulation&name=$name") { +"Пред. страница" }
                        }
                        if (entities.size == DEFAULT_LIMIT) {
                            a(href = "/entities/city?page=${currentPage + 1}&limit=$DEFAULT_LIMIT&minPopulation=$minPopulation&name=$name") { +"След. страница" }
                        }
                    }
                }

                "unit" -> {
                    val playerId = filterParams["playerId"]?.toIntOrNull()
                    val minDamage = filterParams["minDamage"]?.toIntOrNull() ?: 0
                    val minHealth = filterParams["minHealth"]?.toIntOrNull() ?: 0
                    val minMovement = filterParams["minMovement"]?.toIntOrNull() ?: 0

                    var prevHref = "/entities/unit?page=${currentPage - 1}&limit=$DEFAULT_LIMIT&minDamage=$minDamage&minHealth=$minHealth&minMovement=$minMovement"
                    var lastHref = "/entities/unit?page=${currentPage + 1}&limit=$DEFAULT_LIMIT&minDamage=$minDamage&minHealth=$minHealth&minMovement=$minMovement"

                    playerId?.let {
                        prevHref = "$prevHref&playerId=$playerId"
                        lastHref = "$lastHref&playerId=$playerId"
                    }

                    div {
                        if (currentPage > 1) {
                            a(href = prevHref) { +"Пред. страница" }
                        }
                        if (entities.size == DEFAULT_LIMIT) {
                            a(href = lastHref) { +"След. страница" }
                        }
                    }

                }

                "building" -> {
                    val districtId = filterParams["districtId"]?.toIntOrNull()
                    val production = filterParams["production"]?.toIntOrNull()
                    val defense = filterParams["defense"]?.toIntOrNull()


                    var prevHref = "/entities/building?page=${currentPage - 1}&limit=$DEFAULT_LIMIT&production=$production&defense=$defense"
                    var lastHref = "/entities/building?page=${currentPage + 1}&limit=$DEFAULT_LIMIT&production=$production&defense=$defense"

                    districtId?.let {
                        prevHref = "$prevHref&districtId=$districtId"
                        lastHref = "$lastHref&districtId=$districtId"
                    }

                    div {
                        if (currentPage > 1) {
                            a(href = prevHref) { +"Пред. страница" }
                        }
                        if (entities.size == DEFAULT_LIMIT) {
                            a(href = lastHref) { +"След. страница" }
                        }
                    }
                }

                "district" -> {
                    val productionCost = filterParams["productionCost"]?.toIntOrNull()
                    val cityId = filterParams["cityId"]?.toIntOrNull()

                    var prevHref = "/entities/district?page=${currentPage - 1}&limit=$DEFAULT_LIMIT&productionCost=$productionCost"
                    var lastHref = "/entities/district?page=${currentPage + 1}&limit=$DEFAULT_LIMIT&productionCost=$productionCost"

                    cityId?.let {
                        prevHref = "$prevHref&cityId=$cityId"
                        lastHref = "$lastHref&cityId=$cityId"
                    }

                    div {
                        if (currentPage > 1) {
                            a(href = prevHref) { +"Пред. страница" }
                        }
                        if (entities.size == DEFAULT_LIMIT) {
                            a(href = lastHref) { +"След. страница" }
                        }
                    }
                }
            }
        }
    }
}


fun getPhotoPath(entity: Any): String = when (entity) {
    is ExposedCity -> entity.photoPath
    is ExposedUnit -> entity.photoPath
    is ExposedBuilding -> entity.photoPath
    is ExposedDistrict -> entity.photoPath
    else -> throw IllegalArgumentException("Unknown entity type")
}

fun getId(entity: Any): Int = when (entity) {
    is ExposedCity -> entity.id
    is ExposedUnit -> entity.id
    is ExposedBuilding -> entity.id
    is ExposedDistrict -> entity.id
    else -> throw IllegalArgumentException("Unknown entity type")
}
