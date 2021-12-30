package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MeshPolygonTest {

    val polygons = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygon)
    val meshPolygon = polygons.features.first()

    @Test
    fun processMeshPolygonTest() {

        val geometry = meshPolygon.geometry

        //print csv line with following info: lat, lng, P2010TT, P2025TT, P2040TT
        val latNE = geometry.cord(0)[1]
        val lngNE = geometry.cord(0)[0]
        val latSW = geometry.cord(2)[1]
        val lngSW = geometry.cord(2)[0]

        val center = centerOf(latNE = latNE, lngNE = lngNE, latSW = latSW, lngSW = lngSW)

        assertEquals(center[0].floor3Digits(), 42.699) //lat center
        assertEquals(center[1].floor3Digits(), 141.390) //lng center
    }

    @Test
    fun testCsvLineConversion() {
        val expected = """
                8b694a805003a22563e0ebd88f43f3bc905277ac,2.0,1.915344,1.724868,"{latitude:42.69958333333334,longitude:141.390625}"
            """.trimIndent()
        val actual = meshPolygon.toNeo4jAdminImportCsvLine()
        assertEquals(expected, actual)
    }

    @Test
    fun readModifyWriteGeoJsonTest() {
        val actual = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygon)

        val first = actual.features[0]
        val second = actual.features[1]

        first.properties.reach = null
        second.properties.reach = 1500

        val expected = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygonWithReach)
        assertEquals(expected, actual)
    }

    @Test
    fun convertPolygonToPointTest() {
        val polygons = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygonWithReach)
        val pointArray = polygons.features.map { it.toMeshPoint() }.toTypedArray()
        val actual = MeshPoints(type = "FeatureCollection", features = pointArray)

        val encodedActual = Json { encodeDefaults = true }.encodeToString(actual)
        assertTrue(encodedActual.contains("Point"))

        val expected = Json.decodeFromString<MeshPoints>(sampleMeshPointsWithReach)
        assertEquals(expected, actual)
    }
}