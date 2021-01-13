package org.gnit.cpsd

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object GeoCalculationSpec : Spek({
    describe("GeoCalculation") {

        it("calculates distance between 2 coordinates in meter") {
            val distanceBetween2Points = distanceOf(
                lat1 = 35.65851667125012,
                lng1 = 139.70133304595947,
                el1 = 0.0,
                lat2 = 35.65053127871581,
                lng2 = 139.69749212265015,
                el2 = 0.0
            )
            assertEquals(953.3412887144024, distanceBetween2Points)
        }

        it("generates square coordinates around a coordinate") {
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
    }
})
