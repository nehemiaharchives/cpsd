package org.gnit.cpsd.crawler

import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.IllegalCharsetNameException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

private val client: OkHttpClient = OkHttpClient.Builder()
    .cache(
        Cache(
            directory = File("cache"),
            // $0.05 worth of phone storage in 2020
            maxSize = 500L * 1024L * 1024L // 500 MiB
        )
    ).build()

fun fetchCacheIfNotUrl(url: String): String {

    val request: Request = Request.Builder()
        .cacheControl(
            CacheControl.Builder()
                .maxAge(30, TimeUnit.DAYS)
                .build()
        )
        .url(url)
        .build()

    val response = client.newCall(request).execute()

    if (!response.isSuccessful) throw RuntimeException("http request failed for $url")

    response.use {

        val bytes = it.body!!.bytes()

        val doc = Jsoup.parse(String(bytes))

        // look for <meta http-equiv="Content-Type" content="text/html;charset=gb2312"> or HTML5 <meta charset="gb2312">
        val metaElements: Elements = doc.select("meta[http-equiv=content-type], meta[charset]")
        var foundCharset: String? = null // if not found, will keep utf-8 as best attempt

        for (meta in metaElements) {
            if (meta.hasAttr("http-equiv")) foundCharset = getCharsetFromContentType(meta.attr("content"))
            if (foundCharset == null && meta.hasAttr("charset")) foundCharset = meta.attr("charset")
            if (foundCharset != null) break
        }

        return if (Charsets.UTF_8.toString() != foundCharset) {
            String(bytes, Charset.forName(foundCharset))
        }else{
            String(bytes)
        }
    }
}

fun getCharsetFromContentType(contentType: String?): String? {
    if (contentType == null) return null
    val m = Pattern.compile("(?i)\\bcharset=\\s*(?:[\"'])?([^\\s,;\"']*)").matcher(contentType)
    if (m.find()) {
        var charset = m.group(1).trim { it <= ' ' }
        charset = charset.replace("charset=", "")
        return validateCharset(charset)
    }
    return null
}

private fun validateCharset(cs: String): String? {
    var cs: String? = cs
    if (cs == null || cs.length == 0) return null
    cs = cs.trim { it <= ' ' }.replace("[\"']".toRegex(), "")
    try {
        if (Charset.isSupported(cs)) return cs
        cs = cs.toUpperCase(Locale.ENGLISH)
        if (Charset.isSupported(cs)) return cs
    } catch (e: IllegalCharsetNameException) {
        // if our this charset matching fails.... we just take the default
    }
    return null
}