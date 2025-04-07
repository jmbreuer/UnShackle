package net.jmbreuer.unshackle.emancipator

import android.content.Context
import android.net.Uri
import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

class FacebookEmancipator(context: Context) : HTTPEmancipator(context) {
    override fun canHandle(url: String): Boolean {
        Log.i("FacebookEmancipator", "canHandle($url)")
        return url.startsWith("https://www.facebook.com/share/")
    }

    override suspend fun getDescription(url: String): String? {
        if (!canHandle(url))
            return null

        val doc = getDoc(url);
        val descriptionElement = doc.select("meta[property='og:description']")

        return descriptionElement.attr("content");
    }

    override suspend fun getImage(url: String): Uri? {
        Log.i("FacebookEmancipator", "getImage($url)")

        if (!canHandle(url))
            return null

        val doc = getDoc(url);
        val imageElement = doc.select("meta[property='og:image']")

        return saveUriForSharing(Uri.parse(imageElement.attr("content")))
    }
}