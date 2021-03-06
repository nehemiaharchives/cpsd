package org.gnit.cpsd

import kotlinx.serialization.encodeToString

fun main(){

    val driver = getDriver()
    val session = driver.session()

    distanceArray.forEach { maxDistance ->

        val stationPoints = mutableListOf<StationPoint>()
        val stationPolygons = mutableListOf<StationPolygon>()
        val churches = mutableListOf<Church>()
        val routes = mutableListOf<Route>()

        val records = session.run("""
            MATCH (s:Station)-[r:ROUTE]->(c:Church)
            WHERE 0 < r.distance AND r.distance < $maxDistance AND s.passengers > 1000
            RETURN s.id, s.lng, s.lat, s.company, s.line, s.name, s.passengers, r.distance, c.id, c.lng, c.lat, c.name, c.address, c.catholic;
        """.trimIndent())

        records.list().forEach { r ->
            val stationId = r.get("s.id").asString()
            val stationLng = r.get("s.lng").asDouble()
            val stationLat = r.get("s.lat").asDouble()
            val stationCompany = r.get("s.company").asString()
            val stationLine = r.get("s.line").asString()
            val stationName = r.get("s.name").asString()
            val stationPassengers = r.get("s.passengers").asInt()
            val distance = r.get("r.distance").asDouble()
            val churchId = r.get("c.id").asString()
            val churchLng = r.get("c.lng").asDouble()
            val churchLat = r.get("c.lat").asDouble()
            val churchName = r.get("c.name").asString()
            val churchAddress = r.get("c.address").asString()
            val isCatholic = r.get("c.catholic").asBoolean()

            val stationProperty = StationProperty(stationId = stationId, company = stationCompany, line = stationLine, name = stationName, passengers = stationPassengers)

            val stationPoint = StationPoint(
                type = "Feature",
                geometry = PointGeometry(type = "Point", coordinates = arrayOf(stationLng, stationLat)),
                properties = stationProperty
            )
            stationPoints.add(stationPoint)

            val stationPolygon = StationPolygon(
                type = "Feature",
                geometry = PolygonGeometry(type = "Polygon", coordinates = arrayOf(squareOf(stationLat, stationLng, 100.0))),
                properties = stationProperty
            )
            stationPolygons.add(stationPolygon)

            val route = Route(
                type = "Feature", geometry = LineStringGeometry(
                    type = "LineString",
                    coordinates = arrayOf(arrayOf(stationLng, stationLat), arrayOf(churchLng, churchLat))
                ), properties = RouteProperty(distance = distance)
            )
            routes.add(route)

            val church = Church(
                type = "Feature",
                geometry = PointGeometry(type = "Point", coordinates = arrayOf(churchLng, churchLat)),
                properties = ChurchProperty(churchId = churchId, name = churchName, address = churchAddress, catholic = isCatholic)
            )
            churches.add(church)
        }

        val churchGeoJson = format.encodeToString(Churches(type = "FeatureCollection", churches.distinctBy { it.properties.churchId } .toTypedArray()))
        val stationPointGeoJson = format.encodeToString(StationPoints(type = "FeatureCollection", stationPoints.distinctBy { it.properties.stationId } .toTypedArray()))
        val stationPolygonGeoJson = format.encodeToString(StationPolygons(type = "FeatureCollection", stationPolygons.distinctBy { it.properties.stationId } .toTypedArray()))
        val routeGeoJson = format.encodeToString(Routes(type = "FeatureCollection", routes.toTypedArray()))

        val segment = "$maxDistance"

        writeJson("$segment-route", routeGeoJson)
        writeJson("$segment-church", churchGeoJson)
        writeJson("$segment-station-point-with-church", stationPointGeoJson)
        writeJson("$segment-station-polygon-with-church", stationPolygonGeoJson)
    }

    session.close()
    driver.close()
}