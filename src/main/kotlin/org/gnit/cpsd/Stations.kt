package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val jsonFormatError = Json { encodeDefaults = true }

fun main(){
    val stationLines = jsonFormatError.decodeFromString<StationLines>(loadGeoJson("stations2019"))

    stationLines.features.forEach {
        println(it.toString())
    }
}