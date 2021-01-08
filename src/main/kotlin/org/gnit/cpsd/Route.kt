package org.gnit.cpsd

fun main(){

    val driver = getDriver()
    val session = driver.session()

    val query = """
        MATCH (s:Station)-[r:ROUTE]->(c:Church)
        WHERE r.distance < 3000 AND s.passengers > 1000
        RETURN s.lat, s.lng, s.company, s.line, s.name, s.passengers, r.distance, c.name, c.lat, c.lng, c.catholic;        
    """.trimIndent()

    val records = session.run(query).list()

    println("here is records")

    session.close()
    driver.close()
}