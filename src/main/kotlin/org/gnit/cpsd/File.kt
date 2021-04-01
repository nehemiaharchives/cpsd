package org.gnit.cpsd

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
