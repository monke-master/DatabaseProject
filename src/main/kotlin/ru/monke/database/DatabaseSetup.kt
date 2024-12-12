package ru.monke.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun setupDatabase(database: Database) {
    val sqlScript = """
        CREATE OR REPLACE FUNCTION create_default_district()
        RETURNS TRIGGER AS $$
        BEGIN
            INSERT INTO District (city_id, name, production_cost, photo_path)
            VALUES (NEW.id, 'Центр Города', 0, '/static/district.png');
            RETURN NEW;
        END;
        $$ LANGUAGE plpgsql;
    
        CREATE TRIGGER after_city_insert
        AFTER INSERT ON City
        FOR EACH ROW
        EXECUTE FUNCTION create_default_district();
    """
    transaction(database) {
        exec(sqlScript)
    }

    val countFunc = """
        CREATE OR REPLACE FUNCTION count_buildings_for_city(p_city_id INT)
        RETURNS INTEGER AS ${'$'}${'$'}
        BEGIN
            RETURN (
                SELECT COUNT(*)
                FROM Building b
                JOIN District d ON b.district_id = d.id
                WHERE d.city_id = p_city_id
            );
        END;
        ${'$'}${'$'} LANGUAGE plpgsql;
    """.trimIndent()

    transaction(database) {
        exec(countFunc)
    }
}
