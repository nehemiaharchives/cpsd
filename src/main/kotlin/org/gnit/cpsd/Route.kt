package org.gnit.cpsd

import kotlinx.serialization.encodeToString

fun main(){

    val driver = getDriver()
    val session = driver.session()

    val maxDistance = 500

    val minDistance = maxDistance - 500

    val stations = mutableListOf<Station>()
    val churches = mutableListOf<Church>()
    val routes = mutableListOf<Route>()

    val records = session.run("""
            MATCH (s:Station)-[r:ROUTE]->(c:Church)
            WHERE $minDistance < r.distance AND r.distance < $maxDistance AND s.passengers > 1000
            RETURN s.lng, s.lat, s.company, s.line, s.name, s.passengers, r.distance, c.lng, c.lat, c.name, c.address, c.catholic;
        """.trimIndent())

    val r = records.list().first()

    val stationLng = r.get("s.lng").asDouble()
    val stationLat = r.get("s.lat").asDouble()
    val stationCompany = r.get("s.company").asString()
    val stationLine = r.get("s.line").asString()
    val stationName = r.get("s.name").asString()
    val stationPassengers = r.get("s.passengers").asInt()
    val distance = r.get("r.distance").asDouble()
    val churchLng = r.get("c.lng").asDouble()
    val churchLat = r.get("c.lat").asDouble()
    val churchName = r.get("c.name").asString()
    val churchAddress = r.get("c.address").asString()
    val isCatholic = r.get("c.catholic").asBoolean()

    val station = Station(
        type = "Feature",
        geometry = PointGeometry(type = "Point", coordinates = arrayOf(stationLng, stationLat)),
        properties = StationProperty(company = stationCompany, line = stationLine, name = stationName, passengers = stationPassengers)
    )
    stations.add(station)

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
        properties = ChurchProperty(name = churchName, address = churchAddress, catholic = isCatholic)
    )
    churches.add(church)
    
    val churchGeoJson = format.encodeToString(Churches(type = "FeatureCollection", churches.toTypedArray()))
    val stationGeoJson = format.encodeToString(Stations(type = "FeatureCollection", stations.toTypedArray()))
    val routeGeoJson = format.encodeToString(Routes(type = "FeatureCollection", routes.toTypedArray()))

    println(routeGeoJson)

    /*distanceArray.forEach { maxDistance ->

        val minDistance = maxDistance - 500

        val records = session.run("""
            MATCH (s:Station)-[r:ROUTE]->(c:Church)
            WHERE $minDistance < r.distance AND r.distance < $maxDistance AND s.passengers > 1000
            RETURN s.lat, s.lng, s.company, s.line, s.name, s.passengers, r.distance, c.name, c.lat, c.lng, c.catholic;
        """.trimIndent())
    }

    println("here is records")

    */

    session.close()
    driver.close()
}