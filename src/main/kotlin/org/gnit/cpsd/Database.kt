package org.gnit.cpsd

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import java.lang.RuntimeException
import java.util.Properties

fun getDriver(): Driver {
    val props = Properties()
    Thread.currentThread().contextClassLoader.getResourceAsStream("db.properties")
        .use { resourceStream -> props.load(resourceStream) }

    return GraphDatabase.driver(
        "bolt://localhost:7687",
        AuthTokens.basic(props.getProperty("username"), props.getProperty("password"))
    ) ?: throw RuntimeException("Driver is null")
}