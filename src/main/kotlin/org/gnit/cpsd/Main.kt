package org.gnit.cpsd

import org.neo4j.driver.*
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties
import kotlin.math.*

// cpsd: church planting strategy database
fun main() {

    val driver = getDriver();

    val session = driver.session()

    val stations = session.run("MATCH (s:Station) RETURN s;").list()

    val churches = session.run("MATCH (c:Church) RETURN c;").list()

    // DuplicateOption.values().forEach { findDuplicates(churches, it) }

    val totalRoutes = stations.size * churches.size

    var searchCount: Int = 1
    var recordCount: Int = 1

    val path: Path = Paths.get("src/main/resources/routes.csv")

    try {

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

                    val stationId = station.get("id").asString()
                    val churchId = church.get("id").asString()

                    FileOutputStream(path.toFile(), true).bufferedWriter().use { writer ->
                        writer.appendLine("$stationId,$churchId,$distance")
                    }

                    /*val query = query(station, church, distance)
                    session.run(query)*/

                    recordCount++
                }
                searchCount++
            }
        }

    } catch (ex: IOException) {
        ex.printStackTrace()
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
    val stationId = station.get("id").asString()
    val churchId = church.get("id").asString()

    return """
        MATCH (c:Church), (s:Station)
        WHERE s.id = '$stationId' AND c.url = '$churchId'
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
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var distance = R * c * 1000 // convert to meters
    val height = el1 - el2
    distance = distance.pow(2.0) + height.pow(2.0)
    return sqrt(distance)
}

// ref: https://stackoverflow.com/questions/13861616/drawing-a-square-around-a-lat-long-point
fun squareCoordinate(lat: Double, lon: Double){
    //TODO to be implemented
}