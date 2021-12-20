package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MeshPolygonTest {

    val polygons = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygon)
    val meshPolygon = polygons.features.first()

    @Test
    fun processMeshPolygonTest(){

        println(meshPolygon.properties)

        val geometry = meshPolygon.geometry
        (0..4).forEach { i ->
            val cord = geometry.cord(i)
            //println("${cord.first()}, ${cord.last()}")
        }

        //print csv line with following info: lat, lng, P2010TT, P2025TT, P2040TT
        val latNE = geometry.cord(0)[1]
        val lngNE = geometry.cord(0)[0]
        val latSW = geometry.cord(2)[1]
        val lngSW = geometry.cord(2)[0]

        val center = centerOf(latNE = latNE, lngNE = lngNE, latSW = latSW, lngSW = lngSW)
        println(center.joinToString())

        assertEquals(center[0].floor3Digits(), 42.699) //lat center
        assertEquals(center[1].floor3Digits(), 141.390) //lng center
    }

    @Test
    fun toCsvLineTest() {
        val actual = meshPolygon.toCsvLine()
        val expected = "42.69958333333334,141.390625,2.0,1.915344,1.724868"

        assertEquals(expected, actual)
    }
}