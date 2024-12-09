package ru.monke.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedPlayer(
    val login: String,
    val password: String,
    val isAdmin: Boolean
)

class PlayerDatastore(database: Database) {

    object Players : IntIdTable("Player") {
        val login = varchar("login", 100).uniqueIndex()
        val password = varchar("password", 100)
        val isAdmin = bool("is_admin")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Players)
        }
    }

    suspend fun create(player: ExposedPlayer): Int = dbQuery {
        Players.insert {
            it[login] = player.login
            it[password] = player.password
            it[isAdmin] = player.isAdmin
        }[Players.id].value
    }

    suspend fun read(id: Int): ExposedPlayer? {
        return dbQuery {
            Players.selectAll()
                .where { Players.id eq id}
                .map { ExposedPlayer(
                    login = it[Players.login],
                    password = it[Players.password],
                    isAdmin = it[Players.isAdmin],
                ) }
                .singleOrNull()
        }
    }

    suspend fun read(login: String): ExposedPlayer? {
        return dbQuery {
            Players.selectAll()
                .where { Players.login eq login}
                .map { ExposedPlayer(
                    login = it[Players.login],
                    password = it[Players.password],
                    isAdmin = it[Players.isAdmin],
                ) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, player: ExposedPlayer) {
        dbQuery {
            Players.update({ Players.id eq id}) {
                it[login] = player.login
                it[password] = player.password
                it[isAdmin] = player.isAdmin
            }
        }
    }


    suspend fun delete(id: Int) {
        dbQuery {
            Players.deleteWhere { Players.id.eq(id) }
        }
    }


}