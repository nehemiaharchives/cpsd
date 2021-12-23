package org.gnit.cpsd


import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.serialization.encodeToString
import java.io.File

fun main() {
    //generateChurchPointGeoJson()
    covertChurchCsvNeo4jAdminImport()
    //convertChurchesCsvReachDetection()
}

fun generateChurchPointGeoJson() {
    val driver = getDriver()
    val session = driver.session()
    val records = session.run("MATCH (c:Church) RETURN c.id, c.lng, c.lat, c.name, c.address, c.catholic;").list()

    val churches = mutableListOf<Church>()

    records.forEach { r ->
        val churchId = r.get("c.id").asString()
        val churchLng = r.get("c.lng").asDouble()
        val churchLat = r.get("c.lat").asDouble()
        val churchName = r.get("c.name").asString()
        val churchAddress = r.get("c.address").asString()
        val isCatholic = r.get("c.catholic").asBoolean()

        val church = Church(
            type = "Feature",
            geometry = PointGeometry(type = "Point", coordinates = arrayOf(churchLng, churchLat)),
            properties = ChurchProperty(
                churchId = churchId,
                name = churchName,
                address = churchAddress,
                catholic = isCatholic
            )
        )
        churches.add(church)
    }

    val churchGeoJson = format.encodeToString(Churches(type = "FeatureCollection", churches.toTypedArray()))

    writeJson("all-church", churchGeoJson)

    session.close()
    driver.close()
}

/**
 * This method converts church csv data for neo4j-admin import operation.
 *
 * input csv:
 * url,catholic,lat,lng,address,name
 * https://www.google.com/maps?cid=6646597370070891755,FALSE,35.6504898,139.6973877,〒150-0035 東京都渋谷区鉢山町９−２,東京バプテスト教会
 *
 * output csv:
 * :ID,name,address,catholic,location:point{crs:WGS-84}
 * efd92e8aa759cda7aab9a1def465219a7341c86e,東京バプテスト教会,〒150-0035 東京都渋谷区鉢山町９−２,FALSE,"{latitude:35.6971514, longitude:139.4038426}"
 */
fun convertChurchCsvLine(inputRow: Map<String, String>): List<String> = listOf(
    inputRow["url"]!!.sha1(),
    inputRow["name"]!!,
    inputRow["address"]!!,
    inputRow["catholic"]!!,
    "{latitude:${inputRow["lat"]!!},longitude:${inputRow["lng"]!!}}"
)

const val churchCsvNeo4jAdminImport = "src/main/resources/churches.csv"

fun covertChurchCsvNeo4jAdminImport() {

    val touch = File(churchCsvNeo4jAdminImport)
    touch.createNewFile()

    val urls = mutableSetOf<String>()

    csvWriter().open(churchCsvNeo4jAdminImport) {
        writeRow(listOf("id:ID", "name", "address", "catholic", "location:point{crs:WGS-84}"))

        csvReader().open("data/japan/churches.csv") {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                val url = row["url"]!!
                if(!urls.contains(url)) {
                    val converted = convertChurchCsvLine(row)
                    writeRow(converted)
                    println(converted)
                    urls.add(url)
                }
            }
        }
    }
}

const val churchCsvReachDetection = "src/main/resources/churches-rd.csv"

fun convertChurchReachDetection(inputRow: Map<String, String>): List<String> = listOf(
    inputRow["url"]!!.sha1(),
    inputRow["lat"]!!,
    inputRow["lng"]!!
)

fun convertChurchesCsvReachDetection() {
    val touch = File(churchCsvReachDetection)
    touch.createNewFile()

    val urls = mutableSetOf<String>()

    csvWriter().open(churchCsvReachDetection) {
        csvReader().open("data/japan/churches.csv") {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                val url = row["url"]!!
                if(!urls.contains(url)){
                    val converted = convertChurchReachDetection(row)
                    writeRow(converted)
                    println(converted)
                    urls.add(url)
                }
            }
        }
    }
}
