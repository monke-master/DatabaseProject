package ru.monke.api

import ru.monke.database.ExposedPlayer

object UserSession {
    var currentUser: ExposedPlayer? = null
}