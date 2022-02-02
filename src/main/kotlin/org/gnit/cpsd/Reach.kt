package org.gnit.cpsd

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private fun formatInterval(l: Long): String {
    val hr: Long = TimeUnit.MILLISECONDS.toHours(l)
    val min: Long = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr))
    val sec: Long = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min))

    return String.format("%02d:%02d:%02d", hr, min, sec)
}

@OptIn(ExperimentalTime::class)
fun main() {
    val duration = measureTime {
        reachedAreas(
            meshCsvNeo4jAdminImport = "src/main/resources/mesh.csv",
            reachCsvNeo4jImport = "src/main/resources/reach.csv"
        )
    }
    print("Finished in ${duration.toDouble(DurationUnit.SECONDS)} s.")
}

class ChurchRD(val id: String, val lat: Double, val lng: Double)

/** church-rd.csv e.g.
 * :ID,lat,lng
 * efd92e8aa759cda7aab9a1def465219a7341c86e,35.6504898,139.6973877"
 */
inline fun List<String>.toChurchDR() = ChurchRD(this[0], this[1].toDouble(), this[2].toDouble())

class MeshRD(val id: String, val lat: Double, val lng: Double)

/**
 * mesh.csv e.g.
 * id:ID,lat:float,lng:float,P2010TT:float,P2025TT:float,P2040TT:float
 * 8b694a805003a22563e0ebd88f43f3bc905277ac,42.69958333333334,141.390625,2.0,1.915344,1.724868
 */
inline fun List<String>.toMeshRD() = MeshRD(this[0], this[1].toDouble(), this[2].toDouble())

/**
 * Generates following reach.csv for neo4j admin import operation:
 * church id, distance, area id
 * :START_ID,distance:float,:END_ID
 * efd92e8aa759cda7aab9a1def465219a7341c86e,797000.1,316a6f63c159915d6e3e4ca104b2ed8aaf7bcaae
 */
fun reachedAreas(meshCsvNeo4jAdminImport: String, reachCsvNeo4jImport: String) {
    val begin = System.currentTimeMillis()

    val churches = csvReader().readAll(File(churchCsvReachDetection)).map { it.toChurchDR() }.toTypedArray()
    println("finished reading church csv, start reading mesh csv")

    val meshRD = csvReader().readAll(File(meshCsvNeo4jAdminImport))
    val mesh = meshRD.subList(1, meshRD.lastIndex).map { it.toMeshRD() }.toTypedArray()
    println("finished reading mesh csv, start reach detection")

    var calcCount = 0
    val totalLines = countLines(meshCsvNeo4jAdminImport)
    var distance: Double

    val touch = File(reachCsvNeo4jImport)
    touch.createNewFile()

    csvWriter().open(reachCsvNeo4jImport) {
        writeRow(listOf(":START_ID", "distance:float", ":END_ID"))

        mesh.asSequence().forEach { area ->
            churches.asSequence().forEach { church ->
                distance = distanceOf(church.lat, area.lat, church.lng, area.lng, 1.0, 1.0)
                if (distance < 3000.5) {
                    writeRow(listOf(church.id, distance, area.id))
                }
            }

            calcCount++
            if (calcCount % 1000 == 0) {
                val elapsed = System.currentTimeMillis() - begin
                val progress = formatInterval(elapsed)
                val estimation = formatInterval((elapsed * totalLines) / calcCount)
                println("calc progressed $calcCount lines of total $totalLines. ${calcCount * 100 / totalLines}% job done. $progress passed since start. estimated to finish in $estimation.")
            }
        }
    }
}
