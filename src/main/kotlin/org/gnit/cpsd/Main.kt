package org.gnit.cpsd

import org.neo4j.driver.*
import java.util.Properties

// cpsd: church planting strategy database
fun main() {
    val props = Properties()
    Thread.currentThread().contextClassLoader.getResourceAsStream("db.properties")
        .use { resourceStream -> props.load(resourceStream) }

    val driver = GraphDatabase.driver(
        "bolt://localhost:7687",
        AuthTokens.basic(props.getProperty("username"), props.getProperty("password"))
    )
    val session = driver.session()

    val stations = session.run("MATCH (s:Station) RETURN s LIMIT 10;").list()

    val churches = session.run("MATCH (c:Church) RETURN c LIMIT 10;").list()

    stations.forEach { s ->
        churches.forEach { c ->
            val station = s.get("s").asNode()
            val church = c.get("c").asNode()
            distance(station, church)
        }
    }

    session.close()
    driver.close()
}

fun distance(station: org.neo4j.driver.types.Node, church: org.neo4j.driver.types.Node): Double {
    val lat1 = station.get("lat").asDouble()
    val lng1 = station.get("lng").asDouble()
    val lat2 = church.get("lat").asDouble()
    val lng2 = church.get("lng").asDouble()
    val distance = distance(lat1 = lat1, lat2 = lat2, lon1 = lng1, lon2 = lng2, el1 = 1.0, el2 = 1.0)

    if(distance < 500 * 1000)
        println("distance from station ${station.get("name")} to church ${church.get("name")} is ${(distance / 1000).toInt()} km")

    return distance
}

/**
 * Calculate distance between two points in latitude and longitude taking
 * into account height difference. If you are not interested in height
 * difference pass 0.0. Uses Haversine method as its base.
 *
 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
 * el2 End altitude in meters
 * @returns Distance in Meters
 */
fun distance(
    lat1: Double, lat2: Double, lon1: Double,
    lon2: Double, el1: Double, el2: Double
): Double {
    val R = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    var distance = R * c * 1000 // convert to meters
    val height = el1 - el2
    distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)
    return Math.sqrt(distance)
}