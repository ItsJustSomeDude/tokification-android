package net.itsjustsomedude.tokens

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast

class ClipboardHelper(private val ctx: Context) {
    fun getUrl(): String? {
        val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip

        if (clipData != null && clipData.itemCount > 0) {
            val item = clipData.getItemAt(0)

            // Check if the clipboard data is a plain text or URI
            if (clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST) == true) {
                val url = item.uri?.toString() ?: return null
                // Handle the URL
                return url
            } else if (clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
                val url = item.text?.toString() ?: return null
                // Handle the URL text
                return url
            } else {
                // println("Clipboard does not contain a URL")
                return null
            }
        } else {
            println("Clipboard is empty")
            return null
        }
    }

    fun copyText(content: String, label: String = "Tokification Report") {
        val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            Toast.makeText(ctx, "Report Copied!", Toast.LENGTH_SHORT).show()
    }
}

