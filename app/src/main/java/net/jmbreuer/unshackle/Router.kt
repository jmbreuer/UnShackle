package net.jmbreuer.unshackle

import android.content.Context
import android.net.Uri
import android.util.Log
import net.jmbreuer.unshackle.emancipator.FacebookEmancipator

class Router(val context: Context) {
    private val emancipators = listOf(FacebookEmancipator(context))

    suspend fun handle(url: String): Uri? {
        Log.i("Router", "handle($url)")
        Log.i("Router", "${emancipators.size} emancipators registered")
        for (candidate in emancipators) {
            Log.i("Router", "trying candidate ${candidate.javaClass.name}")
            if (candidate.canHandle(url))
                try {
                    return candidate.getImage(url)
                } catch (t: Throwable) {
                    Log.e("Router", "threw up", t)
                }
        }
        return null
    }
}