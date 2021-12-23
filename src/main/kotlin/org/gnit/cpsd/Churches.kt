package org.gnit.cpsd

import kotlinx.serialization.encodeToString

fun main() {
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
