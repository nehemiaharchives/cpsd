package org.gnit.cpsd

import kotlin.math.*

/**
 * [Reference](https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude)
 *
 * Calculate distance between two points in latitude and longitude taking
 * into account height difference. If you are not interested in height
 * difference pass 0.0. Uses Haversine method as its base.
 *
 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
 * el2 End altitude in meters
 * @returns Distance in Meters
 */
fun distanceKt(
    lat1: Double, lat2: Double, lon1: Double,
    lon2: Double, el1: Double, el2: Double
): Double {
    val R = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var distance = R * c * 1000 // convert to meters
    val height = el1 - el2
    distance = distance.pow(2.0) + height.pow(2.0)
    return sqrt(distance)
}

fun distanceJava(
    lat1: Double, lat2: Double, lon1: Double,
    lon2: Double, el1: Double, el2: Double
): Double {
    val R = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    var distance = R * c * 1000 // convert to meters
    val height = el1 - el2
    distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)
    return Math.sqrt(distance)
}

// ref: https://stackoverflow.com/questions/13861616/drawing-a-square-around-a-lat-long-point
fun squareCoordinate(lat: Double, lon: Double){
    //TODO to be implemented
}
