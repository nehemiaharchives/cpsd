package org.gnit.cpsd

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class MeshPolygonTest {

    val polygons = Json.decodeFromString<MeshPopulationPolygons>(sampleMeshPolygon)
    val meshPolygon = polygons.features.first()

    @Test
    fun processMeshPolygonTest() {

        val geometry = meshPolygon.geometry
        (0..4).forEach { i ->
            val cord = geometry.cord(i)
        }

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
        val expected = "8b694a805003a22563e0ebd88f43f3bc905277ac,42.69958333333334,141.390625,2.0,1.915344,1.724868"
        val actual = meshPolygon.toCsvLine()
        assertEquals(expected, actual)
    }

    @Test
    fun withinWalkingDistanceTest() {
        val meshPolygons = meshPolygons("src/test/resources/100m-mesh-test.geojson")

        val churches = arrayOf(
            arrayOf(42.82253112262299, 141.6507933230139), // 千歳栄光教会 https://goo.gl/maps/1eZ3BuAdZFGaDRFD8
            arrayOf(42.82471380353792, 141.65151819288073), // カトリック千歳教会 https://goo.gl/maps/MomnTgJYc7qFovtc9
            arrayOf(42.833877959592705, 141.64281174290457), // 千歳福音キリスト教会 https://goo.gl/maps/Vqtidby1DRbeA6yo6
            arrayOf(42.830775472211144, 141.67897320174316), // 千歳クリスチャンセンター https://goo.gl/maps/XsjQP1bi59FXEgUZ7
            arrayOf(42.85053661476408, 141.66597655875736), // 千歳ライトチャペル https://goo.gl/maps/917QKqJeVMY8qQCu6
        )

        // maybe attempt to simulate reached and unreached grouping
    }

    //@Test
    /**
     * For generating test csv file to import db to check performance.
     * It took 20 min to CREATE RELATIONSHIP between 9500 Church and 100000 Mesh with less than 3000 m distance.
     * Created 473639 relationships out of 949,990,500 calculations.
     */
    fun headCopyPolygonsCsv() {
        val maxLineCount = 100000
        val csvFile = "src/main/resources/100m-mesh.csv"

        val lines = Files.lines(Paths.get(csvFile))
        var lineCount = 0

        val destinationFile = "src/test/resources/100m-mesh-test.csv"

        for (line in lines) {

            FileOutputStream(Paths.get(destinationFile).toFile(), true).bufferedWriter().use { writer ->
                writer.appendLine(line)
            }

            lineCount++
            if (lineCount == maxLineCount) {
                println("line count reached $maxLineCount, exiting")
                break
            }
        }
        assertEquals(maxLineCount, countLines(destinationFile))
    }
}