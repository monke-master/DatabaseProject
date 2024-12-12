package ru.monke.api

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import ru.monke.database.*
import java.io.File

fun Application.editRoutes(
    cityDatastore: CityDatastore,
    unitDatastore: UnitDatastore,
    buildingDatastore: BuildingDatastore,
    districtDatastore: DistrictDatastore
) {
    routing {
        route("/edit") {

            // Edit City Page
            get("/city/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val city = cityDatastore.read(id)
                    if (city != null) {
                        call.respondHtml(HttpStatusCode.OK) {
                            editCityPage(city)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "City not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid City ID")
                }
            }

            post("/city/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val multipart = call.receiveMultipart()
                    var name = ""
                    var playerId = 0
                    var population = 0

                    var photoData: ByteArray? = null

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "name" -> name = part.value
                                    "playerId" -> playerId = part.value.toIntOrNull() ?: 0
                                    "population" -> population = part.value.toIntOrNull() ?: 0
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "photo") {
                                    photoData = part.streamProvider().readBytes()
                                }
                            }
                            else -> {}
                        }
                        part.dispose()
                    }

                    val photoPath = if (photoData != null && photoData?.isNotEmpty() == true) {
                       savePhotoToFileSystem(photoData, name)
                    } else {
                        cityDatastore.read(id)?.photoPath ?: ""
                    }
                    cityDatastore.update(id, ExposedCity(name = name, playerId = playerId, photoPath = photoPath, population = population))
                    call.respondRedirect("/details/city/$id")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid City ID")
                }
            }

            get("/district/{id}") {
                val districtId = call.parameters["id"]?.toIntOrNull()
                if (districtId != null) {
                    val district = districtDatastore.read(districtId)
                    if (district != null) {
                        call.respondHtml {
                            editDistrictPage(district)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "District not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid District ID")
                }
            }

            post("/district/{id}") {
                val districtId = call.parameters["id"]?.toIntOrNull()
                if (districtId != null) {
                    val multipart = call.receiveMultipart()
                    val district = districtDatastore.read(districtId)
                    if (district != null) {
                        var name = district.name
                        var cityId = district.cityId
                        var productionCost = district.productionCost
                        var photoPath = district.photoPath

                        multipart.forEachPart { part ->
                            when (part) {
                                is PartData.FormItem -> {
                                    when (part.name) {
                                        "name" -> name = part.value
                                        "cityId" -> cityId = part.value.toIntOrNull() ?: cityId
                                        "productionCost" -> productionCost = part.value.toIntOrNull() ?: productionCost
                                    }
                                }

                                is PartData.FileItem -> {
                                    if (part.name == "photo") {
                                        val fileBytes = part.streamProvider().readBytes()
                                        if (fileBytes.isNotEmpty()) {
                                            photoPath = savePhotoToFileSystem(fileBytes, name)
                                        }
                                    }
                                }

                                else -> {}
                            }
                            part.dispose()
                        }

                        val updated = ExposedDistrict(
                            name = name,
                            cityId = cityId,
                            productionCost = productionCost,
                            photoPath = photoPath
                        )
                        districtDatastore.update(districtId, updated)

                        call.respondRedirect("/details/district/$districtId")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "District not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid District ID")
                }
            }
        }
    }

    routing {
        route("/edit") {
            // GET request to render the edit page for a building
            get("/building/{id}") {
                val buildingId = call.parameters["id"]?.toIntOrNull()
                if (buildingId != null) {
                    val building = buildingDatastore.read(buildingId)
                    if (building != null) {
                        call.respondHtml {
                            editBuildingPage(building)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Building not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Building ID")
                }
            }

            // POST request to update the building
            post("/building/{id}") {
                val buildingId = call.parameters["id"]?.toIntOrNull()
                if (buildingId != null) {
                    val multipart = call.receiveMultipart()
                    val building = buildingDatastore.read(buildingId)
                    if (building != null) {
                        var name = building.name
                        var districtId = building.districtId
                        var constructionCost = building.productionCost
                        var photoPath = building.photoPath
                        var defense = building.defense
                        var description = building.description
                        var food = building.food
                        var gold = building.gold
                        var production = building.production

                        multipart.forEachPart { part ->
                            when (part) {
                                is PartData.FormItem -> {
                                    when (part.name) {
                                        "name" -> name = part.value
                                        "districtId" -> districtId = part.value.toIntOrNull() ?: districtId
                                        "constructionCost" -> constructionCost = part.value.toIntOrNull() ?: constructionCost
                                        "defense" -> defense = part.value.toIntOrNull() ?: defense
                                        "description" -> description = part.value
                                        "food" -> food = part.value.toIntOrNull() ?: food
                                        "gold" -> gold = part.value.toIntOrNull() ?: gold
                                        "production" -> production = part.value.toIntOrNull() ?: production
                                    }
                                }

                                is PartData.FileItem -> {
                                    if (part.name == "photo") {
                                        val fileBytes = part.streamProvider().readBytes()
                                        if (fileBytes.isNotEmpty()) {
                                            photoPath = savePhotoToFileSystem(fileBytes, name)
                                        }
                                    }
                                }

                                else -> {}
                            }
                            part.dispose()
                        }

                        val updated = ExposedBuilding(
                            name = name,
                            districtId = districtId,
                            productionCost = constructionCost,
                            photoPath = photoPath,
                            defense = defense,
                            description = description,
                            food = food,
                            gold = gold,
                            production = production
                        )
                        buildingDatastore.update(buildingId, updated)

                        call.respondRedirect("/details/building/$buildingId")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Building not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Building ID")
                }
            }

            get("/unit/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val unit = unitDatastore.read(id)
                    if (unit != null) {
                        call.respondHtml(HttpStatusCode.OK) {
                            editUnitPage(unit)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Unit not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Unit ID")
                }
            }

            post("/unit/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val multipart = call.receiveMultipart()

                    var name = ""
                    var description = ""
                    var playerId = 0
                    var damage = 0
                    var health = 0
                    var movement = 0
                    var productionCost = 0
                    var salary = 0

                    var photoData: ByteArray? = null

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "name" -> name = part.value
                                    "description" -> description = part.value
                                    "playerId" -> playerId = part.value.toIntOrNull() ?: 0
                                    "damage" -> damage = part.value.toIntOrNull() ?: 0
                                    "health" -> health = part.value.toIntOrNull() ?: 0
                                    "movement" -> movement = part.value.toIntOrNull() ?: 0
                                    "productionCost" -> productionCost = part.value.toIntOrNull() ?: 0
                                    "salary" -> salary = part.value.toIntOrNull() ?: 0
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "photo") {
                                    photoData = part.streamProvider().readBytes()
                                }
                            }
                            else -> {}
                        }
                        part.dispose()
                    }

                    val photoPath = if (photoData != null && photoData?.isNotEmpty() == true) {
                        savePhotoToFileSystem(photoData, name)
                    } else {
                        unitDatastore.read(id)?.photoPath ?: ""
                    }

                    unitDatastore.update(
                        id,
                        ExposedUnit(
                            id = id,
                            playerId = playerId,
                            damage = damage,
                            health = health,
                            name = name,
                            description = description,
                            movement = movement,
                            productionCost = productionCost,
                            salary = salary,
                            photoPath = photoPath
                        )
                    )

                    call.respondRedirect("/details/unit/$id")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Unit ID")
                }
            }

        }
    }
}

fun HTML.editCityPage(city: ExposedCity) {
    head {
        title { +"Edit City - ${city.name}" }
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css")
    }
    body {
        div(classes = "container mt-5") {
            h1(classes = "mb-4") { +"Edit City" }

            form(action = "/edit/city/${city.id}", method = FormMethod.post, encType = FormEncType.multipartFormData, classes = "row g-3") {
                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"City Name" }
                    input(classes = "form-control", type = InputType.text, name = "name") {
                        value = city.name
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Player ID" }
                    input(classes = "form-control", type = InputType.text, name = "playerId") {
                        value = city.playerId.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Population" }
                    input(classes = "form-control", type = InputType.text, name = "population") {
                        value = city.population.toString()
                    }
                }

                div(classes = "col-md-12") {
                    label(classes = "form-label") { +"City Photo" }
                    if (city.photoPath.isNotEmpty()) {
                        img(src = city.photoPath, alt = "City Photo", classes = "img-thumbnail mb-3") {
                            style = "max-width: 300px;"
                        }
                    }
                    input(classes = "form-control", type = InputType.file, name = "photo") {
                        accept = "image/*"
                    }
                }

                div(classes = "col-12") {
                    button(classes = "btn btn-primary", type = ButtonType.submit) { +"Save" }
                }
            }
        }
    }
}

fun HTML.editDistrictPage(district: ExposedDistrict) {
    head {
        title { +"Edit District - ${district.name}" }
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css")
    }
    body {
        div(classes = "container mt-5") {
            h1(classes = "mb-4") { +"Edit District" }

            form(action = "/edit/district/${district.id}", method = FormMethod.post, encType = FormEncType.multipartFormData, classes = "row g-3") {
                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"District Name" }
                    input(classes = "form-control", type = InputType.text, name = "name") {
                        value = district.name
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"City ID" }
                    input(classes = "form-control", type = InputType.text, name = "cityId") {
                        value = district.cityId.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Production Cost" }
                    input(classes = "form-control", type = InputType.text, name = "productionCost") {
                        value = district.productionCost.toString()
                    }
                }

                div(classes = "col-md-12") {
                    label(classes = "form-label") { +"District Photo" }
                    if (district.photoPath.isNotEmpty()) {
                        img(src = district.photoPath, alt = "District Photo", classes = "img-thumbnail mb-3") {
                            style = "max-width: 300px;"
                        }
                    }
                    input(classes = "form-control", type = InputType.file, name = "photo") {
                        accept = "image/*"
                    }
                }

                div(classes = "col-12") {
                    button(classes = "btn btn-primary", type = ButtonType.submit) { +"Save" }
                }
            }
        }
    }
}

fun HTML.editBuildingPage(building: ExposedBuilding) {
    head {
        title { +"Edit Building - ${building.name}" }
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css")
    }
    body {
        div(classes = "container mt-5") {
            h1(classes = "mb-4") { +"Edit Building" }

            form(action = "/edit/building/${building.id}", method = FormMethod.post, encType = FormEncType.multipartFormData, classes = "row g-3") {
                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Building Name" }
                    input(classes = "form-control", type = InputType.text, name = "name") {
                        value = building.name
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"District ID" }
                    input(classes = "form-control", type = InputType.text, name = "districtId") {
                        value = building.districtId.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Construction Cost" }
                    input(classes = "form-control", type = InputType.text, name = "constructionCost") {
                        value = building.productionCost.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Defense" }
                    input(classes = "form-control", type = InputType.text, name = "defense") {
                        value = building.defense.toString()
                    }
                }

                div(classes = "col-md-12") {
                    label(classes = "form-label") { +"Description" }
                    textArea(classes = "form-control") {
                        +building.description
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Food" }
                    input(classes = "form-control", type = InputType.text, name = "food") {
                        value = building.food.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Gold" }
                    input(classes = "form-control", type = InputType.text, name = "gold") {
                        value = building.gold.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Production" }
                    input(classes = "form-control", type = InputType.text, name = "production") {
                        value = building.production.toString()
                    }
                }

                div(classes = "col-md-12") {
                    label(classes = "form-label") { +"Building Photo" }
                    if (building.photoPath.isNotEmpty()) {
                        img(src = building.photoPath, alt = "Building Photo", classes = "img-thumbnail mb-3") {
                            style = "max-width: 300px;"
                        }
                    }
                    input(classes = "form-control", type = InputType.file, name = "photo") {
                        accept = "image/*"
                    }
                }

                div(classes = "col-12") {
                    button(classes = "btn btn-primary", type = ButtonType.submit) { +"Save" }
                }
            }
        }
    }
}

fun HTML.editUnitPage(unit: ExposedUnit) {
    head {
        title { +"Edit Unit - ${unit.name}" }
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css")
    }
    body {
        div(classes = "container mt-5") {
            h1(classes = "mb-4") { +"Edit Unit" }

            form(
                action = "/edit/unit/${unit.id}",
                method = FormMethod.post,
                encType = FormEncType.multipartFormData,
                classes = "row g-3"
            ) {
                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Unit Name" }
                    input(classes = "form-control", type = InputType.text, name = "name") {
                        value = unit.name
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Description" }
                    input(classes = "form-control", type = InputType.text, name = "description") {
                        value = unit.description
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Player ID" }
                    input(classes = "form-control", type = InputType.text, name = "playerId") {
                        value = unit.playerId.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Damage" }
                    input(classes = "form-control", type = InputType.text, name = "damage") {
                        value = unit.damage.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Health" }
                    input(classes = "form-control", type = InputType.text, name = "health") {
                        value = unit.health.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Movement" }
                    input(classes = "form-control", type = InputType.text, name = "movement") {
                        value = unit.movement.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Production Cost" }
                    input(classes = "form-control", type = InputType.text, name = "productionCost") {
                        value = unit.productionCost.toString()
                    }
                }

                div(classes = "col-md-6") {
                    label(classes = "form-label") { +"Salary" }
                    input(classes = "form-control", type = InputType.text, name = "salary") {
                        value = unit.salary.toString()
                    }
                }

                div(classes = "col-md-12") {
                    label(classes = "form-label") { +"Unit Photo" }
                    if (unit.photoPath.isNotEmpty()) {
                        img(src = unit.photoPath, alt = "Unit Photo", classes = "img-thumbnail mb-3") {
                            style = "max-width: 300px;"
                        }
                    }
                    input(classes = "form-control", type = InputType.file, name = "photo") {
                        accept = "image/*"
                    }
                }

                div(classes = "col-12") {
                    button(classes = "btn btn-primary", type = ButtonType.submit) { +"Save" }
                }
            }
        }
    }
}


// Continue similarly for Building and District pages

