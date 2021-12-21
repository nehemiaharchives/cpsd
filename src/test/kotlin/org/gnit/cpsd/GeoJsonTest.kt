package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.*

class GeoJsonTest {

    val mpp1 = MeshPopulationProperty(P2010TT = 2.0, P2025TT = 1.915344, P2040TT = 1.724868)
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

    val mpp2 = MeshPopulationProperty(P2010TT = 0.516531, P2025TT = 0.494667, P2040TT = 0.445474)
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

    val actual = MeshPopulationPolygons(
        type = "FeatureCollection",
        name = "100m-mesh",
        crs = crs84,
        features = arrayOf(mp1, mp2)
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
    fun testCompareArray(){
        assertTrue(compareArray(arrayOf(1,2,3), arrayOf(1,2,3)))
        assertFalse(compareArray(arrayOf(1,2,3), arrayOf(1,4,3)))
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
    fun testPrinting() {
        val string = format.encodeToJsonElement(actual)
        println(string)
    }

    @Test
    fun testParsing() {
        val expected = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygon)
        assertEquals(expected, actual)
    }
}