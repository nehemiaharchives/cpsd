package org.gnit.cpsd

import kotlinx.serialization.encodeToString

fun main() {
    val driver = getDriver()
    val session = driver.session()

    val minPassengers = 1000

    distanceArray.forEach { maxDistance ->

        val minDistance = 0

        val stationPoints = mutableListOf<StationPoint>()
        val stationPolygons = mutableListOf<StationPolygon>()

        val stationsRecord = session.run(
            """
            MATCH (s:Station) WHERE s.passengers > $minPassengers RETURN s;
        """.trimIndent()
        ).list()
        val total = stationsRecord.size

        val stationsWithChurchRecord = session.run(
            """
            MATCH (s:Station)-[r:ROUTE]->(c:Church)
            WHERE $minDistance < r.distance AND r.distance < $maxDistance AND s.passengers > $minPassengers
            RETURN DISTINCT s;
        """.trimIndent()
        ).list()
        val withChurch = stationsWithChurchRecord.size

        stationsRecord.removeAll(stationsWithChurchRecord)
        val withoutChurch = stationsRecord.size

        stationsRecord.forEach { r ->
            val n = r.get("s").asNode()

            val stationId = n.get("id").asString()
            val stationLng = n.get("lng").asDouble()
            val stationLat = n.get("lat").asDouble()
            val stationCompany = n.get("company").asString()
            val stationLine = n.get("line").asString()
            val stationName = n.get("name").asString()
            val stationPassengers = n.get(("passengers")).asInt()

            val stationProperty = StationProperty(
                stationId = stationId,
                company = stationCompany,
                line = stationLine,
                name = stationName,
                passengers = stationPassengers
            )

            val stationPoint = StationPoint(
                type = "Feature",
                geometry = PointGeometry(type = "Point", coordinates = arrayOf(stationLng, stationLat)),
                properties = stationProperty
            )
            stationPoints.add(stationPoint)

            val stationPolygon = StationPolygon(
                type = "Feature",
                geometry = PolygonGeometry(
                    type = "Polygon",
                    coordinates = arrayOf(squareOf(stationLat, stationLng, 100.0))
                ),
                properties = stationProperty
            )
            stationPolygons.add(stationPolygon)
        }

        val stationPointGeoJson =
            format.encodeToString(StationPoints(type = "FeatureCollection", stationPoints.toTypedArray()))
        val stationPolygonGeoJson =
            format.encodeToString(StationPolygons(type = "FeatureCollection", stationPolygons.toTypedArray()))

        //something like:
        //segment 0 - 500 has 4307 stations, 2074 with church, 2233 without church
        //segment 0 - 1000 has 4307 stations, 3271 with church, 1036 without church
        //segment 0 - 1500 has 4307 stations, 3667 with church, 640 without church
        //segment 0 - 2000 has 4307 stations, 3874 with church, 433 without church
        //segment 0 - 2500 has 4307 stations, 3986 with church, 321 without church
        //segment 0 - 3000 has 4307 stations, 4064 with church, 243 without church
        println("segment $minDistance - $maxDistance has $total stations, $withChurch with church, $withoutChurch without church")
        writeJson("$maxDistance-station-point-without-church", stationPointGeoJson)
        writeJson("$maxDistance-station-polygon-without-church", stationPolygonGeoJson)
    }

    session.close()
    driver.close()
}