package org.gnit.cpsd

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Sha1Test {

    /**
     * this function is intended to replace apoc.util.sha1([row.lat + row.lng])
     */
    @Test
    fun sha1Test(){
        val actual = "42.69958333333334141.390625,2.0".sha1()
        val expected = "3e0f1cdf434db3b509372ea8b1254c8147144ad1"
        assertEquals(expected, actual)
    }
}