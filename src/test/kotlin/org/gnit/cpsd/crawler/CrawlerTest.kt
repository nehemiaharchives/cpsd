package org.gnit.cpsd.crawler

import kotlin.test.Test
import kotlin.test.assertEquals

class CrawlerTest {

    @Test
    fun testDenomination() {
        //parses from name
        assertEquals("日本基督教団", "日本キリスト教団".parseDenomination()!!.name())
    }
}
