package org.gnit.cpsd

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

val format = Json { prettyPrint = true }

// Basics
val crs84property = Properties(name = "urn:ogc:def:crs:OGC:1.3:CRS84")
val crs84 = Crs(type = "name", properties = crs84property)

@Serializable
class Properties(val name: String)

@Serializable
class Crs(val type: String, val properties: Properties)

@Serializable
class MultiPoint(val type: String, val crs: Crs, val coordinates: Array<Array<Double>>)

@Serializable
class LineString(val type: String, val crs: Crs, val coordinates: Array<Array<Double>>)

// Routes
@Serializable
class LineStringGeometry(val type: String, val coordinates: Array<Array<Double>>)

@Serializable
class RouteProperty(val distance: Double)

@Serializable
class Route(val type: String, val geometry: LineStringGeometry, val properties: RouteProperty)

@Serializable
class Routes(val type: String, val features: Array<Route>)

// Station Points
@Serializable
class StationProperty(val company: String, val line: String, val name: String, val passengers: Int)

@Serializable
class PointGeometry(val type: String, val coordinates: Array<Double>)

@Serializable
class StationPoint(val type: String, val geometry: PointGeometry, val properties: StationProperty)

@Serializable
class StationPoints(val type: String, val features: Array<StationPoint>)

// Station Polygons
@Serializable
class PolygonGeometry(val type: String, val coordinates: Array<Array<Array<Double>>>)

@Serializable
class StationPolygon(val type: String, val geometry: PolygonGeometry, val properties: StationProperty)

@Serializable
class StationPolygons(val type: String, val features: Array<StationPolygon>)

// Churches
@Serializable
class ChurchProperty(val name: String, val address: String, val catholic: Boolean)

@Serializable
class Church(val type: String, val geometry: PointGeometry, val properties: ChurchProperty)

@Serializable
class Churches(val type: String, val features: Array<Church>)

fun main() {
    printSingleLineString()
    printMultiPoint()
    printRoutes()
    printStationPoints()
    printStationPolygons()
    printChurches()
}

fun printChurches() {
    val c1 = Church(
        type = "Feature",
        geometry = PointGeometry(type = "Point", coordinates = arrayOf(139.6973877, 35.6504898)),
        properties = ChurchProperty(name = "東京バプテスト教会", address = "〒150-0035 東京都渋谷区鉢山町９−２", catholic = false)
    )

    val c2 = Church(
        type = "Feature",
        geometry = PointGeometry(type = "Point", coordinates = arrayOf(139.695138, 35.652867)),
        properties = ChurchProperty(name = "カトリック渋谷教会", address = "〒150-0036 東京都渋谷区南平台町１８−１３", catholic = true)
    )

    val churches = Churches(type = "FeatureCollection", features = arrayOf(c1, c2))

    println(format.encodeToString(churches))
}

val sp1 = StationProperty(company = "東京急行電鉄", line = "東横線", name = "代官山", passengers = 32148)
val sp2 = StationProperty(company = "東京地下鉄", line = "3号線銀座線", name = "渋谷", passengers = 224784)

fun printStationPoints() {

    val s1 = StationPoint(
        type = "Feature",
        geometry = PointGeometry(type = "Point", coordinates = arrayOf(139.70316, 35.64805)),
        properties = sp1
    )

    val s2 = StationPoint(
        type = "Feature",
        geometry = PointGeometry(type = "Point", coordinates = arrayOf(139.701968, 35.658869)),
        properties = sp2
    )

    val stationPoints = StationPoints(type = "FeatureCollection", features = arrayOf(s1, s2))

    println(format.encodeToString(stationPoints))
}

fun printStationPolygons() {
    val s1 = StationPolygon(
        type = "Feature",
        geometry = PolygonGeometry(type = "Polygon", arrayOf(squareOf(35.64805, 139.70316, 100.0))),
        properties = sp1
    )

    val s2 = StationPolygon(
        type = "Feature",
        geometry = PolygonGeometry(type = "Polygon", arrayOf(squareOf(35.658869, 139.701968, 100.0))),
        properties = sp2
    )

    val stationPolygons = StationPolygons(type = "FeatureCollection", features = arrayOf(s1, s2))

    println(format.encodeToString(stationPolygons))
}

fun printRoutes() {
    val f1 = Route(
        type = "Feature", geometry = LineStringGeometry(
            type = "LineString",
            coordinates = arrayOf(arrayOf(5.0, 0.0), arrayOf(7.0, 1.0))
        ), properties = RouteProperty(distance = 500.0)
    )

    val f2 = Route(
        type = "Feature", geometry = LineStringGeometry(
            type = "LineString",
            coordinates = arrayOf(arrayOf(3.0, 2.0), arrayOf(2.0, 1.0))
        ), properties = RouteProperty(distance = 600.0)
    )

    val fc = Routes(type = "FeatureCollection", features = arrayOf(f1, f2))
    println(format.encodeToString(fc))
}

fun printMultiPoint() {
    val mp = MultiPoint(type = "MultiPoint", crs = crs84, coordinates = arrayOf(arrayOf(0.0, 0.0), arrayOf(1.0, 1.0)))
    println(format.encodeToString(mp))
}

fun printSingleLineString() {
    val ls = LineString(type = "LineString", crs = crs84, coordinates = arrayOf(arrayOf(0.0, 0.0), arrayOf(1.0, 1.0)))
    println(format.encodeToString(ls))
}