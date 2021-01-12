package org.gnit.cpsd

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object GeoCalculationSpec: Spek({
    describe("GeoCalculation"){
        it("calculates distance between 2 coordinates in meter"){
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

        it("calculates distance between 2 coordinates in meter"){
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
    }
})
