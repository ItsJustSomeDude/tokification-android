package net.itsjustsomedude.tokens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class ClipboardHelper(private val ctx: Context) {
    val manager = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun getText(): String? {
        val clipData = manager.primaryClip

        if (clipData != null && clipData.itemCount > 0) {
            val item = clipData.getItemAt(0)
            val text = item.coerceToText(ctx).toString()

            return text
        } else {
            return null
        }
    }

    fun copyText(content: String, label: String = "Tokification Report") {
        val clip = ClipData.newPlainText(label, content)
        manager.setPrimaryClip(clip)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(ctx, "Report Copied!", Toast.LENGTH_SHORT).show()
            }
    }
}

