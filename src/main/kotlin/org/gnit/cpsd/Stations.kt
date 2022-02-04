package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val jsonFormatError = Json { encodeDefaults = true }

fun main() {
    val stationLinesGeojson = jsonFormatError.decodeFromString<StationLines>(loadGeoJson("stations2019"))

    val deDup = mutableSetOf<StationLine>()
    val noPassengers = mutableListOf<StationLine>()

    val stations = stationLinesGeojson.features

    stations.forEach { station ->

        if(station.hasNoPassengerData()){
            noPassengers.add(station)
        }

        val key = station.longName()
        if (deDup.map { it.longName() }.contains(key)) {
            val found = deDup.first { it.longName() == key }
            /*println("found duplicate!")
            println(found)
            println(station)*/

            if(found.hasNoPassengerData() && !station.hasNoPassengerData()){
                deDup.removeIf { it.longName() == key }
                deDup.add(station)
            }
        }else {
            deDup.add(station)
        }

    }

    println("geojson features: ${stations.size}")
    println("stations without data: ${noPassengers.size}")
    println("stations without duplicates: ${deDup.size}")
}