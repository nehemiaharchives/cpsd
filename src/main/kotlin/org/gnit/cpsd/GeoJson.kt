package org.gnit.cpsd

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
class Properties(val name: String)

@Serializable
class Crs(val type: String, val properties: Properties)

@Serializable
class MultiPoint(val type: String, val crs: Crs, val coordinates: Array<Array<Double>>)

@Serializable
class LineString(val type: String, val crs: Crs, val coordinates: Array<Array<Double>>)

@Serializable
class Geometry(val type: String, val coordinates: Array<Array<Double>>)

@Serializable
class RouteProperty(val distance: String)

@Serializable
class Route(val type: String, val geometry: Geometry, val properties: RouteProperty)

@Serializable
class FeatureCollection(val type: String, val features: Array<Route>)

val crs84property = Properties(name = "urn:ogc:def:crs:OGC:1.3:CRS84")
val crs84 = Crs(type = "name", properties = crs84property)
val format = Json { prettyPrint = true }

fun main() {
    printSingleLineString()
    printMultiPoint()
    printRoutes()
}

fun printRoutes(){
    val f1 = Route(type = "Feature", geometry = Geometry(type = "LineString", coordinates = arrayOf(arrayOf(5.0, 0.0), arrayOf(7.0, 1.0))), properties = RouteProperty(distance = "line1"))
    val f2 = Route(type = "Feature", geometry = Geometry(type = "LineString", coordinates = arrayOf(arrayOf(3.0, 2.0), arrayOf(2.0, 1.0))), properties = RouteProperty(distance = "line2"))

    val fc = FeatureCollection(type = "FeatureCollection", features = arrayOf(f1, f2))
    println(format.encodeToString(fc))
}

fun printMultiPoint(){
    val mp = MultiPoint(type = "MultiPoint", crs = crs84, coordinates = arrayOf(arrayOf(0.0, 0.0), arrayOf(1.0, 1.0)))
    println(format.encodeToString(mp))
}

fun printSingleLineString(){
    val ls = LineString(type = "LineString", crs = crs84, coordinates = arrayOf(arrayOf(0.0, 0.0), arrayOf(1.0, 1.0)))
    println(format.encodeToString(ls))
}