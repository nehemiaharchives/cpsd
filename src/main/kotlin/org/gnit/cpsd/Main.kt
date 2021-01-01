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

    val stations = session.run("MATCH (s:Station) RETURN s;").list()

    val churches = session.run("MATCH (c:Church) RETURN c;").list()

    // DuplicateOption.values().forEach { findDuplicates(churches, it) }

    val totalRoutes = stations.size * churches.size

    var searchCount: Int = 1
    var recordCount: Int = 1

    stations.forEach { s ->
        churches.forEach { c ->
            val station = s.get("s").asNode()
            val church = c.get("c").asNode()
            val distance = distance(station, church)
            if (distance < 10 * 1000) {
                println(
                    "recording $recordCount routes, " +
                            "searched $searchCount routes of total routes: $totalRoutes, " +
                            "${(searchCount.toDouble() / totalRoutes) * 100} % search done"
                )
                val query = query(station, church, distance)
                session.run(query)
                recordCount++
            }
            searchCount++
        }
    }

    session.close()
    driver.close()
}

enum class DuplicateOption { NAME, ADDRESS, LAT_LNG, URL }

private fun findDuplicates(churches: MutableList<Record>, duplicateOption: DuplicateOption) {
    churches.groupBy { it.get("c").asNode().get("url") }
    println(churches.map {
        val church = it.get("c").asNode()
        when (duplicateOption) {
            DuplicateOption.NAME -> church.get("name").toString()
            DuplicateOption.ADDRESS -> church.get("address").toString()
            DuplicateOption.LAT_LNG -> church.get("lat").toString() + church.get("lng").toString()
            DuplicateOption.URL -> church.get("url").toString()
        }
    }.groupingBy { it }.eachCount().filter { it.value > 1 })
}

fun query(station: org.neo4j.driver.types.Node, church: org.neo4j.driver.types.Node, distance: Double): String {
    val id = station.get("id").asString()
    val url = church.get("url").asString()

    return """
        MATCH (c:Church), (s:Station)
        WHERE s.id = '$id' AND c.url = '$url'
        CREATE (s)-[r:ROUTE {distance: toFloat($distance)} ]->(c)
        RETURN type(r)
    """.trimIndent()
}

fun distance(station: org.neo4j.driver.types.Node, church: org.neo4j.driver.types.Node): Double {
    val lat1 = station.get("lat").asDouble()
    val lng1 = station.get("lng").asDouble()
    val lat2 = church.get("lat").asDouble()
    val lng2 = church.get("lng").asDouble()
    val distance = distance(lat1 = lat1, lat2 = lat2, lon1 = lng1, lon2 = lng2, el1 = 1.0, el2 = 1.0)

    //if(distance < 10 * 1000)
    //println("distance from station ${station.get("name")} to church ${church.get("name")} is ${(distance / 1000).toInt()} km")

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