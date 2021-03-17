package org.gnit.cpsd.crawler

import com.ibm.icu.text.Transliterator
import org.jsoup.Jsoup

data class Church(
    val churchName: String,
    val postalCode: String?,
    val address: String?,
    val phoneNumber: String?,
    //val faxNumber: String,
    val url: String?,
    val pastorName: String?,
    val presbytery: String?,
    val denomination: String
)

fun main() {
    //crawlJCC().forEach { println(it) }
    crawlUCCJ().forEach { println(it) }
}

//Japan Christian Church 日本キリスト教会
fun crawlJCC(): List<Church> {
    val document = Jsoup.connect("http://www.nikki-church.org/data.htm").get()
    val table = document.getElementsByAttributeValue("style", "border-collapse:collapse;").first()

    val churches = mutableListOf<Church>()

    var presbytery: String? = null

    table.getElementsByTag("tr").forEach { tr ->

        val tds = tr.getElementsByTag("td")

        //教会名	郵便番号	住所	電話番号	ＦＡＸ 番号	牧師名

        if (tds.size == 1) {
            presbytery = tds.first().getElementsByTag("b").text()
        } else {
            var pastorName: String? = null
            val td5 = tds[5].text().toFullWidth()
            if (td5.contains("（")) {
                pastorName = td5.split("（")[0]
            } else if (td5.trim().isEmpty()) {
                pastorName = null
            } else {
                pastorName = td5
            }

            val church = Church(
                churchName = tds[0].text().toFullWidth(),
                postalCode = tds[1].text().toHalfWidth(),
                address = tds[2].text().toFullWidth(),
                phoneNumber = tds[3].text().toHalfWidth(),
                //faxNumber = tds[4].text(),
                url = null,
                pastorName = pastorName,
                presbytery = presbytery,
                denomination = "日本キリスト教会"
            )
            churches.add(church)
        }
    }

    return churches
}

//United Church of Christ in Japan 日本基督教団
fun crawlUCCJ(): List<Church> {
    val document = Jsoup.connect("http://www.uccjshintokai.org/map/?pref=all").get()

    return document.select(".category_1 .address-list .vcard") .map { tr ->
        val name = tr.selectFirst("td.name").text().toFullWidth()
        val hpTd = tr.selectFirst("td.hp")
        val url: String? = if (hpTd.childrenSize() > 0) hpTd.selectFirst("a").attr("href") else null
        val addressTd = tr.selectFirst("td.address")
        val address: String? = if (addressTd.childrenSize() >= 2) addressTd.selectFirst("span.address").text().toFullWidth() else null

        Church(
            churchName = name,
            postalCode = null,
            address = address,
            phoneNumber = null,
            url = url,
            pastorName = null,
            presbytery = null,
            denomination = "日本基督教団"
        )
    }.filterNot { church -> church.churchName == "セムナン教会日本語礼拝" }
}

fun String.toHalfWidth() = Transliterator.getInstance("Fullwidth-Halfwidth").transliterate(this)
fun String.toFullWidth() = Transliterator.getInstance("Halfwidth-Fullwidth").transliterate(this)