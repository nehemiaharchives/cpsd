package org.gnit.cpsd

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * @param geoJsonFile For example, "src/main/resources/100m-mesh.geojson"
 * @return MeshPopulationPolygons.features
 */
fun meshPolygons(geoJsonFile: String): Array<MeshPolygon> {

    val inputStream = File(geoJsonFile).inputStream()
    val meshPopulation = Json.decodeFromStream<MeshPopulationPolygons>(inputStream)

    return meshPopulation.features
}

@OptIn(ExperimentalTime::class)
fun main() {
    val duration = measureTime {
        convertGeojsonToCsv()
    }
    print("Finished in ${duration.toDouble(DurationUnit.SECONDS)} s.")
}

const val meshCsvNeo4jAdminImport = "src/main/resources/mesh.csv"

fun convertGeojsonToCsv() {

    val geoJsonFile = "src/main/resources/100m-mesh.geojson"
    val totalLines = countLines(geoJsonFile)
    val meshPolygons = meshPolygons(geoJsonFile)

    println("iterating MeshPolygon")
    var meshCount = 0

    val path: Path = Paths.get(meshCsvNeo4jAdminImport)
    FileOutputStream(path.toFile(), true).bufferedWriter().use { writer ->
        writer.appendLine("id:ID,P2010TT:float,P2025TT:float,P2040TT:float,location:point{crs:WGS-84}")
    }

    val ids = mutableSetOf<String>()

    meshPolygons.forEach { meshPolygon ->
        val id = meshPolygon.id()
        if (ids.contains(id)) {
            println("id $id is duplicated, not included in the csv")
        } else {
            val csvLine = meshPolygon.toCsvLine()
            FileOutputStream(path.toFile(), true).bufferedWriter().use { writer ->
                writer.appendLine(csvLine)
            }
            ids.add(id)
        }

        meshCount++

        if (meshCount % 1000 == 0) {
            println(
                "recording $meshCount mesh points, " +
                        "wrote $meshCount lines of total lines: $totalLines, " +
                        "${(meshCount.toDouble() / totalLines) * 100} % writing done"
            )
        }
    }
}

