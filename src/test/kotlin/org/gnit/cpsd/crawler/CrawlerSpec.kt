package org.gnit.cpsd.crawler

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object CrawlerSpec : Spek({
    describe("Denomination"){
        it("parses from name"){
            assertEquals("日本基督教団", "日本キリスト教団".parseDenomination()!!.name())
        }
    }
})
