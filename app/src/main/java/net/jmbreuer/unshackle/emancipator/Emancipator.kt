package net.jmbreuer.unshackle.emancipator

import android.net.Uri

interface Emancipator {
    fun canHandle(url: String): Boolean
    suspend fun getDescription(url: String): String?
    suspend fun getImage(url: String): Uri?
}