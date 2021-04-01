package org.gnit.cpsd.crawler

import com.ibm.icu.text.Transliterator
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gnit.cpsd.jsonExists
import org.gnit.cpsd.loadJson
import org.gnit.cpsd.writeJson
import org.jsoup.Jsoup
import java.io.File
import java.util.*

val format = Json { prettyPrint = true }

@Serializable
data class Church(
    val churchName: String,
    val postalCode: String?,
    val address: String?,
    val phoneNumber: String?,
    //val faxNumber: String,
    val url: String?,
    val pastorName: String?,
    val subDivision: String?,
    val denomination: String
)

class LocalName(
    val name: String,
    val locale: Locale
)

fun String.ja() = LocalName(this, Locale.JAPANESE)
fun String.en() = LocalName(this, Locale.ENGLISH)

fun jaNames(vararg elements: String): List<LocalName> = elements.map { LocalName(it, Locale.JAPANESE) }

data class Denomination(
    val canonicalName: LocalName,
    val otherNames: List<LocalName>,
    val abbreviation: String
) {
    fun name() = canonicalName.name
    fun allNames() = otherNames.map { it.name }.plus(canonicalName).plus(abbreviation)
}

val denominations = listOf(
    Denomination("日本キリスト教会".ja(), jaNames("新日基", "新日キ").plus("Church of Christ in Japan".en()), "JCC"),
    Denomination(
        "日本基督教団".ja(),
        jaNames("日本キリスト教団", "日基教団", "日基").plus("United Church of Christ in Japan".en()),
        "UCCJ"
    ),
    Denomination("日本バプテスト連盟".ja(), jaNames("バプ連").plus("Japan Baptist Convention".en()), "JBC")
)

fun String.parseDenomination(): Denomination? = denominations.firstOrNull { it.allNames().contains(this) }

fun main() {
    denominations.map { it.abbreviation }.forEach {
        cache(it)
    }
}

fun cache(denominationAbbr: String): String {

    return if (jsonExists(denominationAbbr)) {
        loadJson(denominationAbbr)
    } else {
        val json = format.encodeToString(crawl(denominationAbbr))
        writeJson(denominationAbbr, json)
        json
    }
}

fun crawl(dnm: String) = when (dnm) {
    "JCC" -> crawlJCC()
    "UCCJ" -> crawlUCCJ()
    "JBC" -> crawlJBC()
    else -> emptyList()
}

//Japan Christian Church 日本キリスト教会
fun crawlJCC(): List<Church> {
    val document = Jsoup.parse(fetchCacheIfNotUrl("http://www.nikki-church.org/data.htm"))

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
                subDivision = presbytery,
                denomination = "日本キリスト教会"
            )
            if ("教会名" != church.churchName) churches.add(church)
        }
    }

    return churches
}

//United Church of Christ in Japan 日本基督教団
fun crawlUCCJ(): List<Church> {
    val document = Jsoup.parse(fetchCacheIfNotUrl("http://www.uccjshintokai.org/map/?pref=all"))

    return document.select(".category_1 .address-list .vcard").map { tr ->
        val name = tr.selectFirst("td.name").text().toFullWidth()
        val hpTd = tr.selectFirst("td.hp")
        val url: String? = if (hpTd.childrenSize() > 0) hpTd.selectFirst("a").attr("href") else null
        val addressTd = tr.selectFirst("td.address")
        val address: String? =
            if (addressTd.childrenSize() >= 2) addressTd.selectFirst("span.address").text().toFullWidth() else null

        Church(
            churchName = name,
            postalCode = null,
            address = address,
            phoneNumber = null,
            url = url,
            pastorName = null,
            subDivision = null,
            denomination = "日本基督教団"
        )
    }.filterNot { church -> church.churchName == "セムナン教会日本語礼拝" }
}

//Japan Baptist Convention 日本バプテスト連盟
fun crawlJBC(): List<Church> {
    val document = Jsoup.parse(fetchCacheIfNotUrl("https://www.bapren.jp/?page_id=216"))
    val trs = document.select(".post-type-church")

    val churches = mutableListOf<Church>()

    trs.forEach { tr ->
        val tds = tr.getElementsByTag("td")
        val name = tds[1].text().toFullWidth()

        if (name.contains("教会") || name.contains("伝道所") || name.contains("チャーチ") || name.contains("会堂") || name.contains(
                "チャペル"
            ) || name.contains("集会所")
        ) {
            val url = tds[1].getElementsByTag("a").first().attr("href")
            val church = fetchJBCChurch(url)
            churches.add(church)
        }
    }
    return churches
}

fun fetchJBCChurch(url: String): Church {

    val churchDocument = Jsoup.parse(fetchCacheIfNotUrl(url))

    val name = churchDocument.selectFirst("article header h1").text()

    val postalCodeAndAddress =
        churchDocument.select("article dl dt:contains(住所) + dd").first().text().split("&nbsp")

    val postalCode = if (postalCodeAndAddress.size == 2) {
        postalCodeAndAddress[0].trim()
    } else {
        null
    }

    val address = if (postalCodeAndAddress.size == 2) {
        postalCodeAndAddress[1]
    } else {
        null
    }

    val phoneNumber = churchDocument.select("article dl dt:contains(電話) + dd").first().text()

    val pastorName = churchDocument.select("article dl dt:contains(スタッフ) + dd").first().text()

    val localConvention = churchDocument.select("article dl dt:contains(連合) + dd").first().text()

    return Church(
        churchName = name,
        postalCode = postalCode,
        address = address,
        phoneNumber = phoneNumber,
        url = url,
        pastorName = pastorName,
        subDivision = localConvention,
        denomination = "日本バプテスト連盟"
    )
}

fun String.toHalfWidth() = Transliterator.getInstance("Fullwidth-Halfwidth").transliterate(this)
fun String.toFullWidth() = Transliterator.getInstance("Halfwidth-Fullwidth").transliterate(this)