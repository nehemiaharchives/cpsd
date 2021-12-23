package org.gnit.cpsd

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ImportTest {

    @Test
    fun convertChurchCsvLine() {
        val inputRow = mapOf(
            "url" to "https://www.google.com/maps?cid=6646597370070891755",
            "catholic" to "FALSE",
            "lat" to "35.6504898",
            "lng" to "139.6973877",
            "address" to "〒150-0035 東京都渋谷区鉢山町９−２",
            "name" to "東京バプテスト教会",
        )

        val expected = listOf(
            "efd92e8aa759cda7aab9a1def465219a7341c86e",
            "東京バプテスト教会",
            "〒150-0035 東京都渋谷区鉢山町９−２",
            "FALSE",
            "\"{latitude:35.6504898, longitude:139.6973877}\""
        )

        val actual = convertChurchCsvLine(inputRow)

        assertEquals(expected, actual)
    }
}