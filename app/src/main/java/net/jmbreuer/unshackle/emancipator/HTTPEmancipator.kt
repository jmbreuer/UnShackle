package net.jmbreuer.unshackle.emancipator

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.DisposableEffect
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

abstract class HTTPEmancipator(var context: Context) : Emancipator {

    protected suspend fun getDoc(url: String): Document {
        return CoroutineScope(Dispatchers.IO).async() {
            Jsoup.connect(url)
                .userAgent("Mozilla") // /5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.3
                .get()
        }.await()
    }

    protected suspend fun saveUriForSharing(uri: Uri) : Uri {
        return CoroutineScope(Dispatchers.IO).async() {
            val cacheDir = Paths.get(context.cacheDir.path, "images")
            cacheDir.createDirectories()
            val fileName = cacheDir.resolve(uri.lastPathSegment)
            fileName.deleteIfExists()
            URL(uri.toString()).openStream().use { Files.copy(it, fileName) }
            FileProvider.getUriForFile(context, "net.jmbreuer.unshackle.fileprovider", fileName.toFile())
        }.await()
    }

}