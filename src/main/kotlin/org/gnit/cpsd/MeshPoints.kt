package org.gnit.cpsd

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val jsonFormatter = Json { encodeDefaults = true }


/**
 * 1. download original data from
 *  [ref](https://nlftp.mlit.go.jp/ksj/gml/datalist/KsjTmplt-mesh500.html) and
 *  [ref](https://nlftp.mlit.go.jp/ksj/old/datalist/old_KsjTmplt-mesh1000-17.html)
 * 2. import in QGIS
 * 3. export as geojson files
 * 4. rename properties from POP20xx to P20xxTT
 * 5. run main()
 */
fun main() {
    val meshSize = 500 //or 1000m-mesh.geojson
    val geoJsonFile = "src/main/resources/${meshSize}m-mesh.geojson"

    println("parsing mesh polygon geojson file")

    val meshPolygons = meshPolygons(geoJsonFile)

    val meshPointList = mutableListOf<MeshPoint>()

    println("converting mesh polygon to mesh point")

    meshPolygons.forEach { meshPolygon ->

        val meshPoint = MeshPoint(
            type = "Feature", geometry = PointGeometry(
                type = "Point",
                coordinates = meshPolygon.center().reversedArray()
            ),
            properties = MeshPointProperty(1000)
        )

        meshPointList.add(meshPoint)
    }

    val meshPoints = MeshPoints(
        type = "FeatureCollection",
        name = "${meshSize}m-mesh-points",
        crs = crs84,
        features = meshPointList.toTypedArray()
    )

    println("starting to write mesh points json file")

    writeJson("${meshSize}m-mesh-points", jsonFormatter.encodeToString(meshPoints))
}
