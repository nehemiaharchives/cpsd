package org.gnit.cpsd

fun main(){
    countTotalRoutesByDistance()
    countRoutesByDistanceSegment()
}

// ref: https://survey.gov-online.go.jp/h21/h21-aruite/images/z05.gif
val distanceArray = arrayOf(500, 1000, 1500, 2000, 2500, 3000)

fun countTotalRoutesByDistance(){
    val driver = getDriver()
    val session = driver.session()

    val totalRoutesByMaxDistance = hashMapOf<Int, Int>()

    distanceArray.forEach { maxDistance ->
        val count = session.run("""
            MATCH (s:Station)-[r:ROUTE]->(c:Church)
            WHERE r.distance < $maxDistance AND s.passengers > 1000
            RETURN count(r) as count;
        """.trimIndent()).single().get("count").asInt()

        totalRoutesByMaxDistance[maxDistance] = count
    }

    // something like {2000=27376, 500=2683, 2500=39609, 1000=8740, 3000=53678, 1500=17140}
    println(totalRoutesByMaxDistance)

    session.close()
    driver.close()
}

fun countRoutesByDistanceSegment(){
    val driver = getDriver()
    val session = driver.session()

    val routesByDistanceSegments = hashMapOf<Int, Int>()

    distanceArray.forEach { maxDistance ->

        val minDistance = maxDistance - 500

        val count = session.run("""
            MATCH (s:Station)-[r:ROUTE]->(c:Church)
            WHERE $minDistance < r.distance AND r.distance < $maxDistance AND s.passengers > 1000
            RETURN count(r) as count;
        """.trimIndent()).single().get("count").asInt()

        routesByDistanceSegments[maxDistance] = count
    }

    // something like {2000=10236, 500=2683, 2500=12233, 1000=6057, 3000=14069, 1500=8400}
    println(routesByDistanceSegments)

    session.close()
    driver.close()
}
