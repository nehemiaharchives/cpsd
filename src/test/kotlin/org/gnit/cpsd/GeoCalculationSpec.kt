package org.gnit.cpsd

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object GeoCalculationSpec : Spek({
    describe("GeoCalculation") {
        it("calculates distance between 2 coordinates in meter") {
            val distanceFrom2Points = distanceJava(
                lat1 = 35.65851667125012,
                lon1 = 139.70133304595947,
                el1 = 0.0,
                lat2 = 35.65053127871581,
                lon2 = 139.69749212265015,
                el2 = 0.0
            )
            assertEquals(953.3412887144024, distanceFrom2Points)
        }

        it("calculates distance between 2 coordinates in meter") {
            val distanceFrom2Points = distanceKt(
                lat1 = 35.65851667125012,
                lon1 = 139.70133304595947,
                el1 = 0.0,
                lat2 = 35.65053127871581,
                lon2 = 139.69749212265015,
                el2 = 0.0
            )
            assertEquals(953.3412887144024, distanceFrom2Points)
        }

        it("converts latitudes from degrees to decimals/radians") {
            assertEquals(toDecimals(35.0), Math.toRadians(35.0))
        }

        it("converts latitudes from decimals/radians to degrees") {
            assertEquals(toDegrees(35.0), Math.toDegrees(35.0))
        }

        it("generates square coordinates around a coordinate") {
            val centerLat = 35.681208784564994
            val centerLng = 139.7676318883896
            val square = arrayOf(
                arrayOf(139.76663947105408, 35.680385235128604),
                arrayOf(139.76858139038083, 35.680385235128604),
                arrayOf(139.76858139038083, 35.6819713227605),
                arrayOf(139.76663947105408, 35.6819713227605),
                arrayOf(139.76663947105408, 35.680385235128604)
            )

            val generated = squareOf(centerLat, centerLng, 100.0)

            assertEquals(square, generated)
        }
    }
})
