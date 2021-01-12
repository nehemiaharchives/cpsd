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

/**
 * [Reference](https://stackoverflow.com/questions/13861616/drawing-a-square-around-a-lat-long-point)
 * @param lat latitude of center coordinate for square polygon
 * @param lon longitude of center coordinate for square polygon
 * @param side length of the side of square in meters
 * @return array of 5 coordinates to draw square polygon with GeoJson, order of corner is SW->SE->NE->NW->SW
 */
fun squareOf(lat: Double, lon: Double, side: Double): Array<Array<Double>> {
    val ec = 40075 // equatorial circumference of Earth

    val latInDecimals = Math.toRadians(lat)
    val longInDecimals = Math.toRadians(lon)

    // Calculating length of longitude at that point of latitude
    val lineOfLat = cos(lon) * ec

    // Calculating change in longitude for square of side 'side'
    val changeInLat = (side / 10000) * (360.0 / lineOfLat)

    // Converting changes into radians
    val changeInLatRad = Math.toRadians(changeInLat)

    // longitudinal_length = latitudinal_length / cos(latitude)
    val changeInLongRad = changeInLatRad / cos(latInDecimals)

    val nLat = changeInLatRad / 2
    val nLong = changeInLongRad / 2

    // Getting square coordinates in degrees
    val lat1 = Math.toDegrees(latInDecimals - nLat)
    val lng1 = Math.toDegrees(longInDecimals - nLong)

    val lat2 = Math.toDegrees(latInDecimals - nLat)
    val lng2 = Math.toDegrees(longInDecimals + nLong)

    val lat3 = Math.toDegrees(latInDecimals + nLat)
    val lng3 = Math.toDegrees(longInDecimals + nLong)

    val lat4 = Math.toDegrees(latInDecimals + nLat)
    val lng4 = Math.toDegrees(longInDecimals - nLong)

    return arrayOf(
        arrayOf(lng1, lat1),
        arrayOf(lng2, lat2),
        arrayOf(lng3, lat3),
        arrayOf(lng4, lat4),
        arrayOf(lng1, lat1)
    )
}

fun toDecimals(latitude: Double): Double {
    val latInDecimals = (Math.PI / 180) * latitude
    return latInDecimals
}

fun toDegrees(latitudeInDecimals: Double): Double {
    val latitude = latitudeInDecimals * (180 / Math.PI)
    return latitude
}