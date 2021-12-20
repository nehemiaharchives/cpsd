package org.gnit.cpsd

import kotlin.math.floor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeoCalculationTest {

    @Test
    fun testSquareOf() {

        //calculates distance between 2 coordinates in meter
        val distanceBetween2Points = distanceOf(
            lat1 = 35.65851667125012,
            lng1 = 139.70133304595947,
            el1 = 0.0,
            lat2 = 35.65053127871581,
            lng2 = 139.69749212265015,
            el2 = 0.0
        )
        assertEquals(953.3412887144024, distanceBetween2Points)

        //generates square coordinates around a coordinate
        arrayOf(
            arrayOf(46.0, 148.0), // North East of Japan
            arrayOf(35.0, 138.0), // Center of Japan
            arrayOf(23.0, 123.0)  // South West of Japan
        ).forEach { coordinate ->
            val lat = coordinate[0]
            val lng = coordinate[1]
            val sq = squareOf(lat, lng, 100.0)

            val lng1 = sq[0][0]
            val lat1 = sq[0][1]

            val lng2 = sq[1][0]
            val lat2 = sq[1][1]

            val lng3 = sq[2][0]
            val lat3 = sq[2][1]

            val width = distanceOf(lat1, lat2, lng1, lng2, 0.0, 0.0)
            val height = distanceOf(lat2, lat3, lng2, lng3, 0.0, 0.0)

            assertTrue { 99.9 < width && width < 100.2 }
            assertTrue { 99.9 < height && height < 100.2 }

        }
    }

    @Test
    fun testCenterOf() {
        val actual = centerOf(latNE = 42.6, lngNE = 140.0, latSW = 42.7, lngSW = 141.0)
        val expected = arrayOf(42.65, 140.5)
    }

    @Test
    fun testSquareAndCenterOf(){

        val latNE = 42.69916666666666
        val lngNE = 141.39
        val latSW = 42.7
        val lngSW = 141.39125

        val center = centerOf(latNE = latNE, lngNE = lngNE, latSW = latSW, lngSW = lngSW)

        val square = squareOf(lat = center[0], lng = center[1], 100.0)

        assertEquals(lngNE.floor3Digits(), square[0][0].floor3Digits())
        assertEquals(latNE.floor3Digits(), square[0][1].floor3Digits())
        assertEquals(lngSW.floor3Digits(), square[2][0].floor3Digits())
        assertEquals(latSW.floor3Digits(), square[2][1].floor3Digits())
    }
}

inline fun Double.floor3Digits() = floor(this * 1000.0) / 1000.0