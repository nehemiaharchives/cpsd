package org.gnit.cpsd

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

val format = Json { prettyPrint = true }

// Basics
val crs84property = Properties(name = "urn:ogc:def:crs:OGC:1.3:CRS84")
val crs84 = Crs(type = "name", properties = crs84property)

@Serializable
data class Properties(val name: String)

@Serializable
data class Crs(val type: String, val properties: Properties)

@Serializable
class MultiPoint(val type: String, val crs: Crs, val coordinates: Array<Array<Double>>)

@Serializable
class LineString(val type: String, val crs: Crs, val coordinates: Array<Array<Double>>)

@Serializable
class LineStringGeometry(val type: String, val coordinates: Array<Array<Double>>)

@Serializable
class PolygonGeometry(val type: String, val coordinates: Array<Array<Array<Double>>>)

@Serializable
data class MultiLineStringGeometry(val type: String = "MultiLineString", val coordinates: Array<Array<Array<Double>>>)

// Routes
@Serializable
class RouteProperty(val distance: Double)

@Serializable
class Route(val type: String, val geometry: LineStringGeometry, val properties: RouteProperty)

@Serializable
class Routes(val type: String, val features: Array<Route>)

// Station Points
@Serializable
class StationProperty(
    val stationId: String,
    val company: String,
    val line: String,
    val name: String,
    val passengers: Int
)

@Serializable
class PointGeometry(val type: String = "Point", val coordinates: Array<Double>) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is PointGeometry -> {
                this.type == other.type &&
                        this.coordinates[0] == other.coordinates[0] &&
                        this.coordinates[1] == other.coordinates[1]
            }
            else -> false
        }
    }

    override fun toString(): String {
        return "type:${this.type}, coordinates:${coordinates.joinToString()}"
    }
}

@Serializable
class StationPoint(val type: String = "Feature", val geometry: PointGeometry, val properties: StationProperty)

@Serializable
class StationPoints(val type: String, val features: Array<StationPoint>)

// Station Polygons
@Serializable
class StationPolygon(val type: String, val geometry: PolygonGeometry, val properties: StationProperty)

@Serializable
class StationPolygons(val type: String, val features: Array<StationPolygon>)

// Station Lines
@Serializable
data class StationLineProperty(
    @SerialName("S12_001") val station: String,
    @SerialName("S12_002") val railroadCompany: String,
    @SerialName("S12_003") val railroad: String,
    @SerialName("S12_009") val passenger2011: Int,
    @SerialName("S12_013") val passenger2012: Int,
    @SerialName("S12_017") val passenger2013: Int,
    @SerialName("S12_021") val passenger2014: Int,
    @SerialName("S12_025") val passenger2015: Int,
    @SerialName("S12_029") val passenger2016: Int,
    @SerialName("S12_033") val passenger2017: Int,
    @SerialName("S12_037") val passenger2018: Int,
    @SerialName("S12_041") val passenger2019: Int
)

@Serializable
data class StationLine(
    val type: String = "Feature",
    val properties: StationLineProperty,
    val geometry: MultiLineStringGeometry
)

/**
 * 1. download daily passengers of stations data, (named S12-{YY}_GML.zip) from
 * [National Land Information Division, National Spatial Planning and Regional Policy Bureau, MLIT of Japan](https://nlftp.mlit.go.jp/ksj/gml/datalist/KsjTmplt-S12-v2_7.html)
 * 2. import S12-{YY}_NumberOfPassengers.geojson into QGIS with option to convert crs to 84
 * 3. export by only selecting fields required for [StationLineProperty]
 */
@Serializable
class StationLines(
    val type: String = "FeatureCollection",
    val name: String,
    val crs: Crs = crs84,
    val features: Array<StationLine>
)

// Churches
@Serializable
class ChurchProperty(val churchId: String, val name: String, val address: String, val catholic: Boolean)

@Serializable
class Church(val type: String, val geometry: PointGeometry, val properties: ChurchProperty)

@Serializable
class Churches(val type: String, val features: Array<Church>)

// 100 meter mesh Polygons
@Serializable
data class MeshPopulationProperty(
    val P2010TT: Double,
    val P2025TT: Double,
    val P2040TT: Double,
    var reach: Int? = null
)

@Serializable
data class MeshPolygonGeometry(val type: String, val coordinates: Array<Array<Array<Array<Double>>>>) {

    fun cord(i: Int) = this.coordinates[0][0][i]

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is MeshPolygonGeometry -> {
                this.type == other.type &&
                        this.cord(0)[0] == other.cord(0)[0] &&
                        this.cord(1)[0] == other.cord(1)[0] &&
                        this.cord(2)[0] == other.cord(2)[0] &&
                        this.cord(3)[0] == other.cord(3)[0] &&
                        this.cord(4)[0] == other.cord(4)[0] &&

                        this.cord(0)[1] == other.cord(0)[1] &&
                        this.cord(1)[1] == other.cord(1)[1] &&
                        this.cord(2)[1] == other.cord(2)[1] &&
                        this.cord(3)[1] == other.cord(3)[1] &&
                        this.cord(4)[1] == other.cord(4)[1]

            }
            else -> false
        }
    }
}

@Serializable
data class MeshPolygon(val type: String, val geometry: MeshPolygonGeometry, val properties: MeshPopulationProperty) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is MeshPolygon -> {
                this.type == other.type &&
                        this.properties == other.properties &&
                        this.geometry == other.geometry
            }
            else -> false
        }
    }

    fun center(): Array<Double> {
        val latNE = geometry.cord(0)[1]
        val lngNE = geometry.cord(0)[0]
        val latSW = geometry.cord(2)[1]
        val lngSW = geometry.cord(2)[0]

        return centerOf(latNE = latNE, lngNE = lngNE, latSW = latSW, lngSW = lngSW)
    }

    fun id(): String {
        val latCenter = center()[0]
        val lngCenter = center()[1]
        return "$latCenter$lngCenter".sha1()
    }

    fun toNeo4jAdminImportCsvLine(): String {
        val latCenter = center()[0]
        val lngCenter = center()[1]
        val id = id()
        val p = properties
        // e.g. 316a6f63c159915d6e3e4ca104b2ed8aaf7bcaae,10.1,11.1,12.1,"{latitude:42.7, longitude:141.39}"
        return "$id,${p.P2010TT},${p.P2025TT},${p.P2040TT},\"{latitude:$latCenter,longitude:$lngCenter}\""
    }

    fun toMeshPoint() = MeshPoint(
        type = "Feature",
        geometry = PointGeometry(type = "Point", coordinates = this.center().reversedArray()),
        properties = MeshPointProperty(reach = this.properties.reach)
    )
}

@Serializable
data class MeshPopulationPolygons(
    val type: String = "FeatureCollection",
    val name: String = "100m-mesh-polygons",
    val crs: Crs = crs84,
    val features: Array<MeshPolygon>
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is MeshPopulationPolygons -> {
                this.type == other.type &&
                        this.name == other.name &&
                        this.crs == other.crs &&
                        compareArray(this.features, other.features)
            }
            else -> false
        }
    }
}

// 100 meter mesh Points

@Serializable
data class MeshPointProperty(val reach: Int?)

@Serializable
data class MeshPoint(
    val type: String = "Feature",
    val geometry: PointGeometry,
    val properties: MeshPointProperty
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is MeshPoint -> {
                this.type == other.type &&
                        this.geometry == other.geometry &&
                        this.properties == other.properties
            }
            else -> false
        }
    }
}

@Serializable
data class MeshPoints(
    val type: String = "FeatureCollection",
    val name: String = "100m-mesh-points",
    val crs: Crs = crs84,
    val features: Array<MeshPoint>
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is MeshPoints -> {
                this.type == other.type &&
                        this.name == other.name &&
                        this.crs == other.crs &&
                        compareArray(this.features, other.features)
            }
            else -> false
        }
    }
}

fun <T> compareArray(first: Array<T>, second: Array<T>): Boolean {
    return if (first.size == second.size) {
        var matches = true

        for ((index, f) in first.withIndex()) {
            if (f != second[index]) {
                matches = false
                break
            }
        }

        matches
    } else {
        false
    }
}

fun main() {
    printStationPoints()
}

fun main2() {
    printSingleLineString()
    printMultiPoint()
    printRoutes()
    printStationPoints()
    printStationPolygons()
    printChurches()
    printStationPolygons()
}

fun printChurches() {
    val c1 = Church(
        type = "Feature",
        geometry = PointGeometry(type = "Point", coordinates = arrayOf(139.6973877, 35.6504898)),
        properties = ChurchProperty(
            churchId = "tbc",
            name = "東京バプテスト教会",
            address = "〒150-0035 東京都渋谷区鉢山町９−２",
            catholic = false
        )
    )

    val c2 = Church(
        type = "Feature",
        geometry = PointGeometry(type = "Point", coordinates = arrayOf(139.695138, 35.652867)),
        properties = ChurchProperty(
            churchId = "csc",
            name = "カトリック渋谷教会",
            address = "〒150-0036 東京都渋谷区南平台町１８−１３",
            catholic = true
        )
    )

    val churches = Churches(type = "FeatureCollection", features = arrayOf(c1, c2))

    println(format.encodeToString(churches))
}

val sp1 = StationProperty(stationId = "s1", company = "東京急行電鉄", line = "東横線", name = "代官山", passengers = 32148)
val sp2 = StationProperty(stationId = "s2", company = "東京地下鉄", line = "3号線銀座線", name = "渋谷", passengers = 224784)

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
