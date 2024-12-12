package ru.monke.api

import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import ru.monke.database.*
import java.io.File

fun Application.creatingRoutes(
    cityDatastore: CityDatastore,
    unitDatastore: UnitDatastore,
    buildingDatastore: BuildingDatastore,
    districtDatastore: DistrictDatastore
) {
    routing {
        get("/create_entity/{type}") {
            val entityType = call.parameters["type"] ?: return@get call.respondText(
                "Entity type is required", status = HttpStatusCode.BadRequest
            )

            when (entityType) {
                "city" -> call.respondRedirect("/create_city")
                "unit" -> call.respondRedirect("/create_unit")
                "building" -> call.respondRedirect("/create_building")
                "district" -> call.respondRedirect("/create_district")
                else -> return@get call.respondText(
                    "Unknown entity type", status = HttpStatusCode.BadRequest
                )
            }
        }
        get("/create_city") {
            call.respondHtml {
                head {
                    title { +"Create City" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Create New City" }

                        form(action = "/create_city", method = FormMethod.post, encType = FormEncType.multipartFormData) {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Player ID:" }
                                textInput(name = "playerId") {
                                    classes = setOf("form-control")
                                    placeholder = "Enter Player ID"
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"City Name:" }
                                textInput(name = "name") {
                                    classes = setOf("form-control")
                                    placeholder = "Enter City Name"
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Population:" }
                                numberInput(name = "population") {
                                    classes = setOf("form-control")
                                    placeholder = "Enter Population"
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"City Photo:" }
                                fileInput(name = "photo") {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary mt-3") {
                                +"Create City"
                            }
                        }
                    }
                }
            }
        }

        get("/create_district") {
            call.respondHtml {
                head {
                    title { +"Create District" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Create New District" }

                        form(
                            action = "/create_district",
                            method = FormMethod.post,
                            encType = FormEncType.multipartFormData
                        ) {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"City ID:" }
                                textInput(name = "cityId") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Название:" }
                                textInput(name = "name") {
                                    classes = setOf("form-control")
                                    placeholder = "Введите название района"
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production Cost:" }
                                numberInput(name = "productionCost") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Photo:" }
                                fileInput(name = "photo") {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary mt-3") {
                                +"Create District"
                            }
                        }
                    }
                }
            }
        }

        get("/create_building") {
            call.respondHtml {
                head {
                    title { +"Create Building" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Create New Building" }

                        form(action = "/create_building", method = FormMethod.post, encType = FormEncType.multipartFormData) {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"District ID:" }
                                textInput(name = "districtId") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Building Name:" }
                                textInput(name = "name") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production Cost:" }
                                numberInput(name = "productionCost") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production:" }
                                numberInput(name = "production") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Food:" }
                                numberInput(name = "food") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Gold:" }
                                numberInput(name = "gold") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Defense:" }
                                numberInput(name = "defense") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Description:" }
                                textArea {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Photo Upload:" }
                                fileInput(name = "photo") {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary mt-3") {
                                +"Create Building"
                            }
                        }
                    }
                }
            }
        }

        get("/create_unit") {
            call.respondHtml {
                head {
                    title { +"Create Unit" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Create New Unit" }

                        form(action = "/create_unit", method = FormMethod.post, encType = FormEncType.multipartFormData) {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Player ID:" }
                                textInput(name = "playerId") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Unit Name:" }
                                textInput(name = "name") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Damage:" }
                                numberInput(name = "damage") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Health:" }
                                numberInput(name = "health") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Movement:" }
                                numberInput(name = "movement") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production Cost:" }
                                numberInput(name = "productionCost") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Содержание:" }
                                numberInput(name = "salary") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Description:" }
                                textArea {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Photo Upload:" }
                                fileInput(name = "photo") {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary mt-3") {
                                +"Create Unit"
                            }
                        }
                    }
                }
            }
        }

        post("/create_city") {
            val multipart = call.receiveMultipart()
            var playerId: String? = null
            var name: String? = null
            var population: Int? = null
            var photoData: ByteArray? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "playerId" -> playerId = part.value
                            "name" -> name = part.value
                            "population" -> population = part.value.toIntOrNull()
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "photo") {
                            photoData = part.streamProvider().readBytes()
                        }
                    }
                    else -> Unit
                }
                part.dispose()
            }

            if (playerId == null || name == null || population == null) {
                return@post call.respondText("All fields are required", status = HttpStatusCode.BadRequest)
            }

            try {
                val photoPath = savePhotoToFileSystem(photoData, name!!)
                cityDatastore.create(ExposedCity(playerId = playerId!!.toInt(), name =  name!!, population = population!!, photoPath = photoPath))

                call.respondRedirect("/entities/city")
            } catch (e: Exception) {
                call.respondText("Failed to create city: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }

        post("/create_district") {
            val multipart = call.receiveMultipart()
            var cityId: Int? = null
            var name: String? = null
            var productionCost: Int? = null
            var photoData: ByteArray? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "cityId" -> cityId = part.value.toIntOrNull()
                            "name" -> name = part.value
                            "productionCost" -> productionCost = part.value.toIntOrNull()
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "photo") {
                            photoData = part.streamProvider().readBytes()
                        }
                    }
                    else -> Unit
                }
                part.dispose()
            }

            if (cityId == null || name == null || productionCost == null) {
                return@post call.respondText("All fields are required", status = HttpStatusCode.BadRequest)
            }

            try {
                val photoPath = savePhotoToFileSystem(photoData, name!!)
                districtDatastore.create(
                    ExposedDistrict(
                        cityId = cityId!!,
                        name = name!!,
                        productionCost = productionCost!!,
                        photoPath = photoPath
                    )
                )

                call.respondRedirect("/entities/district")
            } catch (e: Exception) {
                call.respondText("Failed to create district: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }



        post("/create_building") {
            val multipart = call.receiveMultipart()
            var districtId: Int? = null
            var cityId: Int? = null
            var name: String? = null
            var productionCost: Int? = null
            var production: Int? = null
            var food: Int? = null
            var gold: Int? = null
            var defense: Int? = null
            var description: String? = ""
            var photoBytes: ByteArray? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "districtId" -> districtId = part.value.toIntOrNull()
                            "cityId" -> cityId = part.value.toIntOrNull()
                            "name" -> name = part.value
                            "productionCost" -> productionCost = part.value.toIntOrNull()
                            "production" -> production = part.value.toIntOrNull()
                            "food" -> food = part.value.toIntOrNull()
                            "gold" -> gold = part.value.toIntOrNull()
                            "defense" -> defense = part.value.toIntOrNull()
                            "description" -> description = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "photo") {
                            photoBytes = part.streamProvider().readBytes()
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (districtId == null || cityId == null || name.isNullOrBlank() || productionCost == null || production == null || food == null || gold == null || defense == null) {
                return@post call.respondText("All fields are required and must be valid", status = HttpStatusCode.BadRequest)
            }

            if (productionCost!! < 0 || production!! < 0 || food!! < 0 || gold!! < 0 || defense!! < 0) {
                return@post call.respondText("Values cannot be negative", status = HttpStatusCode.BadRequest)
            }

            try {
                // Save photo (e.g., save to database or file system)
                val photoPath = savePhotoToFileSystem(photoBytes, name!!)

                buildingDatastore.create(
                    ExposedBuilding(
                        districtId = districtId!!.toInt(),
                        name = name!!,
                        productionCost = productionCost!!,
                        production = production!!,
                        food = food!!,
                        gold = gold!!,
                        defense = defense!!,
                        description = description!!.trim(),
                        photoPath = photoPath
                    )
                )
                call.respondRedirect("/entities/building")
            } catch (e: Exception) {
                call.respondText("Failed to create building: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }


        post("/create_unit") {
            val multipart = call.receiveMultipart()
            var playerId: Int? = null
            var name: String? = null
            var damage: Int? = null
            var health: Int? = null
            var movement: Int? = null
            var productionCost: Int? = null
            var salary: Int? = null
            var description: String? = ""
            var photoBytes: ByteArray? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "playerId" -> playerId = part.value.toIntOrNull()
                            "name" -> name = part.value
                            "damage" -> damage = part.value.toIntOrNull()
                            "health" -> health = part.value.toIntOrNull()
                            "movement" -> movement = part.value.toIntOrNull()
                            "productionCost" -> productionCost = part.value.toIntOrNull()
                            "salary" -> salary = part.value.toIntOrNull()
                            "description" -> description = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "photo") {
                            photoBytes = part.streamProvider().readBytes()
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (playerId == null || name.isNullOrBlank() || damage == null || health == null || movement == null || productionCost == null || salary == null) {
                return@post call.respondText("All fields are required and must be valid", status = HttpStatusCode.BadRequest)
            }

            if (damage!! < 0 || health!! < 0 || movement!! < 0 || productionCost!! < 0 || salary!! < 0) {
                return@post call.respondText("Values cannot be negative", status = HttpStatusCode.BadRequest)
            }

            try {
                val photoPath = savePhotoToFileSystem(photoBytes, name!!)

                unitDatastore.create(
                    ExposedUnit(
                        playerId = playerId!!,
                        name = name!!,
                        damage = damage!!,
                        health = health!!,
                        movement = movement!!,
                        productionCost = productionCost!!,
                        salary = salary!!,
                        description = description!!.trim(),
                        photoPath = photoPath
                    )
                )
                call.respondRedirect("/entities/unit")
            } catch (e: Exception) {
                call.respondText("Failed to create unit: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }


    }


}
