package org.gnit.cpsd

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.FileOutputStream
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val duration = measureTime {
        fromDbToHash()
        val meshPolygons = meshPolygons(meshGeoJsonFromQgisExport)
        addReachData(meshPolygons)
        fromHashToPolygon(meshPolygons)
        fromHashToPoint(meshPolygons)
    }
    print("Finished in ${duration.toDouble(DurationUnit.SECONDS)} s.")
}

val reachByDistance = mutableMapOf<String, Int>()

fun fromDbToHash() {

    distanceArray.sortedDescending().forEach { reach ->

        val driver = getDriver()
        val session = driver.session()
        println("getting mesh ids within $reach meters")
        val query = "MATCH (c:Church)-[r:Reach]->(m:Mesh) WHERE r.distance < $reach RETURN DISTINCT m.id"
        session.run(query).stream().forEach { record ->
            val id = record.get("m.id").asString()
            reachByDistance[id] = reach
        }
        session.close()
        driver.close()

    }

    val count = reachByDistance.map { it.toPair() }.groupingBy { it.second }.eachCount().toString()
    println("total hash size: ${reachByDistance.size}. counts: $count")
}

private fun addReachData(meshPolygons: Array<MeshPolygon>) {
    println("iterating MeshPolygon")

    meshPolygons.forEach { meshPolygon ->
        val id = meshPolygon.id()
        if (reachByDistance.containsKey(id)) {
            val found = reachByDistance[id]
            meshPolygon.properties.reach = found
            println("reach of mesh: $id set to $found")
        }
    }
}

const val meshGeoJsonFromQgisExport = "src/main/resources/100m-mesh.geojson"

const val meshGeoJsonPolygonsWithReach = "src/main/resources/100m-mesh-polygons-reach/100m-mesh-polygons-reach.geojson"

@OptIn(ExperimentalSerializationApi::class)
fun fromHashToPolygon(meshPolygons: Array<MeshPolygon>) {

    println("writing modified geojson to file")
    FileOutputStream(File(meshGeoJsonPolygonsWithReach)).use { output ->
        Json.encodeToStream(MeshPopulationPolygons(features = meshPolygons), output)
    }
}

private val jsonFormatter = Json { encodeDefaults = true }

const val meshGeoJsonPointsWithReach = "src/main/resources/100m-mesh-points-reach/100m-mesh-points-reach.geojson"

@OptIn(ExperimentalSerializationApi::class)
fun fromHashToPoint(meshPolygons: Array<MeshPolygon>) {

    println("converting polygons to points")
    val meshPointArray = meshPolygons.map { it.toMeshPoint() }.toTypedArray()

    println("writing modified geojson to file")
    FileOutputStream(File(meshGeoJsonPointsWithReach)).use { output ->
        jsonFormatter.encodeToStream(MeshPoints(features = meshPointArray), output)
    }
}
