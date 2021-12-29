package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeoJsonTest {

    val mpp1 = MeshPopulationProperty(P2010TT = 2.0, P2025TT = 1.915344, P2040TT = 1.724868)
    val mpp1reach = MeshPopulationProperty(P2010TT = 2.0, P2025TT = 1.915344, P2040TT = 1.724868, reach = null)

    val mpgeo1 = MeshPolygonGeometry(
        type = "MultiPolygon",
        coordinates = arrayOf(
            arrayOf(
                arrayOf(
                    arrayOf(141.39, 42.69916666666666),
                    arrayOf(141.39, 42.7),
                    arrayOf(141.39125, 42.7),
                    arrayOf(141.39125, 42.69916666666666),
                    arrayOf(141.39, 42.69916666666666)
                )
            )
        )
    )
    val mp1 = MeshPolygon(type = "Feature", geometry = mpgeo1, properties = mpp1)
    val mp1reach = MeshPolygon(type = "Feature", geometry = mpgeo1, properties = mpp1reach)

    val mpp2 = MeshPopulationProperty(P2010TT = 0.516531, P2025TT = 0.494667, P2040TT = 0.445474)
    val mpp2reach = MeshPopulationProperty(P2010TT = 0.516531, P2025TT = 0.494667, P2040TT = 0.445474, reach = 1500)

    val mpgeo2 = MeshPolygonGeometry(
        type = "MultiPolygon",
        coordinates = arrayOf(
            arrayOf(
                arrayOf(
                    arrayOf(141.40375, 42.74166666666667),
                    arrayOf(141.40375, 42.7425),
                    arrayOf(141.405, 42.7425),
                    arrayOf(141.405, 42.74166666666667),
                    arrayOf(141.40375, 42.74166666666667)
                )
            )
        )
    )
    val mp2 = MeshPolygon(type = "Feature", geometry = mpgeo2, properties = mpp2)
    val mp2reach = MeshPolygon(type = "Feature", geometry = mpgeo2, properties = mpp2reach)

    val actual = MeshPopulationPolygons(
        type = "FeatureCollection",
        name = "100m-mesh",
        crs = crs84,
        features = arrayOf(mp1, mp2)
    )

    val actualReach = MeshPopulationPolygons(
        type = "FeatureCollection",
        name = "100m-mesh",
        crs = crs84,
        features = arrayOf(mp1reach, mp2reach)
    )

    @Test
    fun testEqualsMultiPolygonGeometry() {
        val first = MeshPolygonGeometry(
            type = "MultiPolygon",
            coordinates = arrayOf(
                arrayOf(
                    arrayOf(
                        arrayOf(141.40375, 42.74166666666667),
                        arrayOf(141.40375, 42.7425),
                        arrayOf(141.405, 42.7425),
                        arrayOf(141.405, 42.74166666666667),
                        arrayOf(141.40375, 42.74166666666667)
                    )
                )
            )
        )

        val second = MeshPolygonGeometry(
            type = "MultiPolygon",
            coordinates = arrayOf(
                arrayOf(
                    arrayOf(
                        arrayOf(141.40375, 42.74166666666667),
                        arrayOf(141.40375, 42.7425),
                        arrayOf(141.405, 42.7425),
                        arrayOf(141.405, 42.74166666666667),
                        arrayOf(141.40375, 42.74166666666667)
                    )
                )
            )
        )

        assertEquals(first, second)
    }

    @Test
    fun testCompareArray() {
        assertTrue(compareArray(arrayOf(1, 2, 3), arrayOf(1, 2, 3)))
        assertFalse(compareArray(arrayOf(1, 2, 3), arrayOf(1, 4, 3)))
    }

    @Test
    fun testReadingMultiPolygonGeometory() {
        val samplePloygonGeometry = """
            {
                "type": "MultiPolygon",
                "coordinates":
                [
                    [
                        [
                            [
                                141.40375,
                                42.74166666666667
                            ],
                            [
                                141.40375,
                                42.7425
                            ],
                            [
                                141.405,
                                42.7425
                            ],
                            [
                                141.405,
                                42.74166666666667
                            ],
                            [
                                141.40375,
                                42.74166666666667
                            ]
                        ]
                    ]
                ]
            }
        """.trimIndent()

        val actual = Json.decodeFromString<MeshPolygonGeometry>(samplePloygonGeometry)
        assertEquals(mpgeo2, actual)
    }

    @Test
    fun testParsing() {
        val expected = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygon)
        assertEquals(expected, actual)
    }

    @Test
    fun testParsingReach() {
        val expected = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygonWithReach)
        assertEquals(expected, actualReach)
    }
}