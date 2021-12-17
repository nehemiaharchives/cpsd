package org.gnit.cpsd

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gnit.cpsd.crawler.Church
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

object DataCheck : Spek({
    describe("JCC") {
        it("matches address") {
            val jcc = Json.decodeFromString<List<Church>>(string = loadJson("JCC"))
            val jccAddressList = jcc.map { "〒${it.postalCode} ${it.address}" }

            val actual: List<List<String>> = csvReader().readAll(File("data/japan/churches.csv"))
            val actualAddressList = actual.map { it[4] }

            var contains = 0

            jccAddressList.forEach { jccAddress ->
                actualAddressList.forEach { actualAddress ->
                    if (actualAddress.contains(jccAddress)){
                        println()
                        contains++
                    }
                }
            }

            println(contains)
        }
    }
})
