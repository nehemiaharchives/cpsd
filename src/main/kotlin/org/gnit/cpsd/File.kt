package org.gnit.cpsd

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