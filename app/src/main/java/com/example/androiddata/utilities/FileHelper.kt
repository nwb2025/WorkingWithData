package com.example.androiddata.utilities

import android.app.Application
import android.content.Context
import java.io.File

class FileHelper {
    companion object {
        fun getTextFromResources(context: Context, resourceId: Int): String {
            return context.resources.openRawResource(resourceId).use {
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }

        fun getTextFromAssets(context: Context, fileName: String): String {
            return context.assets.open(fileName).use {
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }

        // Save and read to external Storage
        // external files visible to user
        fun saveTextToExternalStorage(app: Application, json: String?) {
            val file = File(app.getExternalFilesDir("monsters"), "monsters.json")
            file.writeText(json ?: "", Charsets.UTF_8)
        }

        fun readTextFromExternalStorage(app: Application): String? {
            val file = File(app.getExternalFilesDir("monsters"), "monsters.json")
            return if (file.exists()) {
                file.readText()
            } else null
        }

        // Save and Read to/from internal storage - cache dirs
        // internal storages aren't visible to user
        fun readTextFromCache(app: Application):String? {
            val file = File(app.cacheDir, "monsters.json")
            return if (file.exists()) {
                file.readText()
            } else null
        }

        fun saveTextToCache(app: Application, json: String?) {
            val file = File(app.cacheDir, "monsters.json")
            file.writeText(json ?: "", Charsets.UTF_8)
        }




    }
}