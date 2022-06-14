package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val jsonFormatError = Json { encodeDefaults = true }

fun main() {
    val stationLinesGeojson = jsonFormatError.decodeFromString<StationLines>(loadGeoJson("stations2019"))

    val deDup = mutableSetOf<StationLine>()
    val noPassengers = mutableListOf<StationLine>()

    val stations = stationLinesGeojson.features

    stations.forEach { station ->

        if (station.hasNoPassengerData()) {
            noPassengers.add(station)
        }

        val key = station.longName()
        if (deDup.map { it.longName() }.contains(key)) {
            val found = deDup.first { it.longName() == key }
            /*println("found duplicate!")
            println(found)
            println(station)*/

            if (found.hasNoPassengerData() && !station.hasNoPassengerData()) {
                //println("removing ${deDup.first { it.longName() == key }}")
                deDup.removeIf { it.longName() == key }
                //println("adding   $station")
                deDup.add(station)
            }
        } else {
            deDup.add(station)
        }

    }

    println("geojson features: ${stations.size}")
    println("stations without data: ${noPassengers.size}")
    println("stations without duplicates: ${deDup.size}")

    val withData = deDup.filter { !it.hasNoPassengerData() }
    println("stations with data(!hasNoPassengerData): ${withData.size}")
    val withLatest = deDup.filter { it.latestPassengers() > 0 }
    println("stations with data(latestPassengers)   : ${withLatest.size}")

    val lessThan50 = deDup.filter { it.latestPassengers() < 50 }
    println("stations less than 50: ${lessThan50.size}")

    val moreThan50 = deDup.filter { it.latestPassengers() >= 50 }
    println("stations more than 50: ${moreThan50.size}")

    val lessThan25 = deDup.filter { it.latestPassengers() < 25 }
    println("stations less than 25: ${lessThan25.size}")

    val moreThan25 = deDup.filter { it.latestPassengers() >= 25 }
    println("stations more than 25: ${moreThan25.size}")



    writeJson("stationLines", jsonFormatError.encodeToString(StationLines(name = "station2019", features = deDup.toTypedArray())))
}