package com.margelo.nitro.rnstroketext

import android.content.Context
import android.graphics.Typeface
import com.facebook.react.views.text.ReactFontManager
import java.io.IOException

object FontUtil {

    @JvmStatic
    fun getFont(context: Context, fontFamily: String): Typeface? {
        return getFontFromAssets(context, fontFamily)
            ?: getFontFromReactFontManager(context, fontFamily)
    }

    private fun getFontFromAssets(context: Context, fontFamily: String): Typeface? {
        val fontPath = findFontFile(context, "fonts/", fontFamily)
        return fontPath?.let {
            Typeface.createFromAsset(context.assets, it)
        }
    }

    private fun getFontFromReactFontManager(
        context: Context,
        fontFamily: String
    ): Typeface? {
        return ReactFontManager.getInstance()
            .getTypeface(fontFamily, Typeface.NORMAL, context.assets)
    }

    private fun findFontFile(
        context: Context,
        folderPath: String,
        fontName: String
    ): String? {
        return try {
            context.assets.list(folderPath)
                ?.firstOrNull { file ->
                    file.startsWith(fontName) &&
                        (file.endsWith(".ttf") || file.endsWith(".otf"))
                }
                ?.let { "$folderPath$it" }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
