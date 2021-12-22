package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * [Reference](https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java)
 * @param For example, "src/main/resources/100m-mesh.geojson"
 */
fun countLines(fileName: String): Int {
    val inputStream: InputStream = BufferedInputStream(FileInputStream(fileName))
    return inputStream.use { inputStream ->
        val c = ByteArray(1024)
        var readChars = inputStream.read(c)
        if (readChars == -1) {
            // bail out if nothing to read
            return 0
        }

        // make it easy for the optimizer to tune this loop
        var count = 0
        while (readChars == 1024) {
            var i = 0
            while (i < 1024) {
                if (c[i++] == '\n'.code.toByte()) {
                    ++count
                }
            }
            readChars = inputStream.read(c)
        }

        // count remaining characters
        while (readChars != -1) {
            for (i in 0 until readChars) {
                if (c[i] == '\n'.code.toByte()) {
                    ++count
                }
            }
            readChars = inputStream.read(c)
        }
        if (count == 0) 1 else count
    }
}

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
fun main(){
    val duration = measureTime {
        convertGeojsonToCsv()
    }
    print("Finished in ${duration.toDouble(DurationUnit.SECONDS)} s.")
}

fun convertGeojsonToCsv() {

    val geoJsonFile = "src/main/resources/100m-mesh.geojson"
    val totalLines = countLines(geoJsonFile)
    val meshPolygons = meshPolygons(geoJsonFile)

    println("iterating MeshPolygon")
    var meshCount = 0
    val path: Path = Paths.get("src/main/resources/100m-mesh.csv")
    FileOutputStream(path.toFile(), true).bufferedWriter().use { writer ->
        writer.appendLine("id:ID,lat:float,lng:float,P2010TT:float,P2025TT:float,P2040TT:float")
    }

    val ids = mutableSetOf<String>()

    meshPolygons.forEach { meshPolygon ->
        val id = meshPolygon.id()
        if(ids.contains(id)){
            println("id $id is duplicated, not included in the csv")
        }else{
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

fun main2() {
    val driver = getDriver();
    val session = driver.session()

    val churches = session.run("MATCH (c:Church) RETURN c;").list()

    println(churches.size)

    val geoJsonFile = "src/main/resources/100m-mesh.geojson"
    val totalLines = countLines(geoJsonFile)
    val meshPolygons = meshPolygons(geoJsonFile)

    // calculate reached or unreached ?

    session.close()
    driver.close()
}
