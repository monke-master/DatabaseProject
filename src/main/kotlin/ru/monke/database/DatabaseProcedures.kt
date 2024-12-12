package ru.monke.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun createWarDistrict(cityId: Long, database: Database) {
    val procedure = """
        CREATE OR REPLACE FUNCTION create_default_district(city_id INT)
        RETURNS VOID AS ${'$'}${'$'}
        BEGIN
            INSERT INTO District (city_id, name, production_cost, photo_path)
            VALUES (city_id, 'Военный лагерь', 800, '/static/war_district.png');
        END;
        ${'$'}${'$'} LANGUAGE plpgsql;
    """.trimIndent()

    transaction(database) {
        try {
            exec(procedure)
            exec("SELECT create_default_district($cityId)") {}
            println("Stored procedure executed successfully.")
        } catch (ex: Exception) {
            println("Failed to execute stored procedure: ${ex.message}")
        }
    }
}