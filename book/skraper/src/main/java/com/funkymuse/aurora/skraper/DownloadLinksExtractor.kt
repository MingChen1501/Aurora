package com.funkymuse.aurora.skraper

import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.serverconstants.DEFAULT_API_TIMEOUT
import com.funkymuse.aurora.serverconstants.LIBGEN_LC
import com.funkymuse.aurora.serverconstants.LIBRARY_LOL
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */
class DownloadLinksExtractor @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {

    suspend fun extract(url: String): String? =
        when {
            url.contains(LIBRARY_LOL, true) -> extractLibgenLolDownloadLink(url)
            url.contains(LIBGEN_LC, true) -> extractLibgenLCDownloadLink(url)
            else -> null
        }


    private suspend fun getDocument(url: String): Document =
        withContext(dispatcher) {
            Jsoup.connect(url).apply {
                timeout(DEFAULT_API_TIMEOUT)
                url(url)
            }.get()
        }


    private suspend fun extractLibgenLCDownloadLink(url: String): String? {
        val document = getDocument(url)
        val link = document.select("a").firstOrNull()?.attr("href")
        link ?: return null
        return LIBGEN_LC + link
    }

    private suspend fun extractLibgenLolDownloadLink(url: String): String? {
        val document = getDocument(url)
        return document.getElementById("download")?.select("a")?.firstOrNull()?.attr("href")
    }


}