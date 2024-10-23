package net.itsjustsomedude.tokens.network

import android.content.Context
import android.content.pm.PackageManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

class UpdateChecker(
    private val context: Context,
    private val client: HttpClient
) {
    suspend fun isNewVersionAvailable(): Result<Boolean> {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val currentVersion = info.versionName
//        val currentVersion = BuildConfig.VERSION_NAME
//        val currentVersion = "0.0.1"

        return try {
            val response: HttpResponse = client.get(
                "https://api.github.com/repos/$REPO/releases/latest"
            )
            if (response.status.value in 200..299) {
                val latestTag = response.body<ReleaseResponse>().tag_name
                val isNewer = latestTag.isNewerThan(currentVersion.toString())
                Result.success(isNewer)
            } else {
                Result.failure(Exception("Unexpected response: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val REPO = "ItsJustSomeDude/tokification-android"
    }
}

@Serializable
data class ReleaseResponse(
    val tag_name: String // Maps to "tag_name" in the JSON response
)

private fun String.isNewerThan(other: String): Boolean {
    val thisParts = this.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
    val otherParts = other.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }

    // Compare each part of the version (major, minor, patch)
    for (i in 0 until maxOf(thisParts.size, otherParts.size)) {
        val thisPart = thisParts.getOrNull(i) ?: 0
        val otherPart = otherParts.getOrNull(i) ?: 0
        if (thisPart != otherPart) {
            return thisPart > otherPart
        }
    }
    return false // Return false if versions are identical
}