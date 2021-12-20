package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * [Reference](https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java)
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

fun main(){

    val geoJsonFile = "src/main/resources/100m-mesh.geojson"
    val totalLines = countLines(geoJsonFile)

    println("started to read line")
    val lines = Files.lines(Paths.get(geoJsonFile))
    val sb = StringBuilder()
    var lineCount = 0;
    lines.forEach { line ->
        sb.append(line)
        lineCount++

        if (lineCount % 1000 == 0){
            val progress = lineCount * 100 / totalLines
            println("$progress% of lines got in StringBuffer")
        }
    }

    println("decoding string to MeshPopulationPolygons object")
    val jsonString = sb.toString()
    val meshPopulation = Json.decodeFromString<MeshPopulationPolygons>(jsonString)

    println("iterating MeshPolygon")
    var meshCount = 0
    val path: Path = Paths.get("src/main/resources/100m-mesh.csv")
    FileOutputStream(path.toFile(), true).bufferedWriter().use { writer ->
        writer.appendLine("lat,lng,P2010TT,P2025TT,P2040TT")
    }

    meshPopulation.features.forEach { meshPolygon ->
        val csvLine = meshPolygon.toCsvLine()

        FileOutputStream(path.toFile(), true).bufferedWriter().use { writer ->
            writer.appendLine(csvLine)
        }

        meshCount++

        if (meshCount % 1000 == 0){
            println(
                "recording $meshCount mesh points, " +
                        "wrote $meshCount lines of total lines: $totalLines, " +
                        "${(meshCount.toDouble() / totalLines) * 100} % writing done"
            )
        }
    }
}
