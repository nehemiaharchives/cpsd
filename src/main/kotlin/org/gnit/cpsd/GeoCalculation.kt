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
fun distanceOf(
    lat1: Double, lat2: Double, lng1: Double,
    lng2: Double, el1: Double, el2: Double
): Double {
    val R = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lng2 - lng1)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var distance = R * c * 1000 // convert to meters
    val height = el1 - el2
    distance = distance.pow(2.0) + height.pow(2.0)
    return sqrt(distance)
}

/**
 * [Reference](https://gis.stackexchange.com/questions/15545/calculating-coordinates-of-square-x-miles-from-center-point)
 * @param lat latitude of center coordinate for square polygon
 * @param lng longitude of center coordinate for square polygon
 * @param side length of the side of square in meters
 * @return array of 5 coordinates to draw square polygon with GeoJson, order of corner is SW->SE->NE->NW->SW
 */
fun squareOf(lat: Double, lng: Double, side: Double): Array<Array<Double>> {

    val dLat = side / (111 * 1000) // Latitudinal or north-south distance in degrees
    val dLng = dLat / cos(Math.toRadians(lat)) // Longitudinal or east-west distance in degrees

    val nLat = dLat / 2
    val nLng = dLng / 2

    val lat1 = lat - nLat
    val lng1 = lng - nLng

    val lat2 = lat - nLat
    val lng2 = lng + nLng

    val lat3 = lat + nLat
    val lng3 = lng + nLng

    val lat4 = lat + nLat
    val lng4 = lng - nLng

    return arrayOf(
        arrayOf(lng1, lat1),
        arrayOf(lng2, lat2),
        arrayOf(lng3, lat3),
        arrayOf(lng4, lat4),
        arrayOf(lng1, lat1)
    )
}

/**
 * @param latNE latitude of first geo polygon coordinate which is north-east corner
 * @param lngNE longitude of first geo polygon coordinate which is north-east corner
 * @param latSW latitude of third geo polygon coordinate which is south-west corner
 * @param lngSW longitude of third geo polygon coordinate which is south-west corner
 * @return array of Double representing (latitude, longitude) of center of the square
 */
fun centerOf(latNE: Double, lngNE: Double, latSW: Double, lngSW: Double): Array<Double> = arrayOf(
    (latNE + latSW) * 0.5, (lngNE + lngSW) * 0.5
)
