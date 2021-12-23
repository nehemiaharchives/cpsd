package org.gnit.cpsd

import java.io.*
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    writeRouteCsv("cv3", "e9b874fd963b70ad5c8df1e7a87b0c0fb7b23f73", 456307.0)
}

fun writeRouteCsv(stationId: String, churchId: String, distance: Double) {

    val path: Path = Paths.get("src/main/resources/routes.csv")
    try {
        FileOutputStream(path.toFile(), true).bufferedWriter().use {
            writer -> writer.appendLine("$stationId,$churchId,$distance")
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

fun jsonExists(name: String) = Paths.get("src/main/resources/$name.json").toFile().exists()

fun writeJson(name: String, json: String) {

    val path: Path = Paths.get("src/main/resources/$name.json")
    try {
        FileOutputStream(path.toFile(), true).bufferedWriter().use {
                writer -> writer.appendLine(json)
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

fun loadJson(jsonName: String): String = File("src/main/resources/$jsonName.json").readText()

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