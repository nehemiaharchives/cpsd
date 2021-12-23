package org.gnit.cpsd

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import java.util.Properties

/**
 * This function reads db.properties file to obtain username and password of Neo4j Database.
 * db.properties example:
 * username=xxx
 * password=xxx
 */
fun getDriver(): Driver {
    val props = Properties()
    Thread.currentThread().contextClassLoader.getResourceAsStream("db.properties")
        .use { resourceStream -> props.load(resourceStream) }

    return GraphDatabase.driver(
        "bolt://localhost:7687",
        AuthTokens.basic(props.getProperty("username"), props.getProperty("password"))
    ) ?: throw RuntimeException("Driver is null")
}

/**
 * Database connection test
 */
fun main() {
    val driver = getDriver()
    val session = driver.session()
    val churches = session.run("MATCH (c:Church) RETURN c;")
    println(churches.list().size)
    session.close()
    driver.close()
}
