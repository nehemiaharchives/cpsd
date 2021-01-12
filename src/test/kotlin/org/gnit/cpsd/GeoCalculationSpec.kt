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
            val centerLat = 35.64805
            val centerLng = 139.70316
            val square = arrayOf(
                arrayOf(139.70259342920963, 35.64758959761317),
                arrayOf(139.70372657079034, 35.64758959761317),
                arrayOf(139.70372657079034, 35.64851040238682),
                arrayOf(139.70259342920963, 35.64851040238682),
                arrayOf(139.70259342920963, 35.64758959761317)
            )

            val generated = squareOf(centerLat, centerLng, 100.0)

            (0..4).forEach { i ->
                (0..1).forEach { j ->
                    assertEquals(square[i][j], generated[i][j])
                }
            }
        }
    }
})
