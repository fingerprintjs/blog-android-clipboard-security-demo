package com.fingerprintjs.android.clipboard_reader


import android.annotation.SuppressLint
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter.ClipboardItem
import java.lang.StringBuilder


interface ApplicationPreferences {
    fun getClipboardHistory(): List<ClipboardItem>
    fun setClipboardHistory(history: List<ClipboardItem>)
}

class ApplicationPreferencesImpl(context: Context) : ApplicationPreferences {

    private val preferences = createPreferences(context)

    override fun getClipboardHistory(): List<ClipboardItem> {
        synchronized(this) {
            return preferences.getStringSet(CLIPBOARD_HISTORY_KEY, emptySet())?.mapNotNull {
                ClipboardItem(
                    it.split(TIMESTAMP_VALUE_DELIMITER)[0].toLong(),
                    it.split(TIMESTAMP_VALUE_DELIMITER)[1]
                )
            } ?: emptyList()
        }
    }

    @SuppressLint("ApplySharedPref")
    override fun setClipboardHistory(history: List<ClipboardItem>) {
        synchronized(this) {
            val sb = StringBuilder()
            preferences
                .edit()
                .clear()
                .commit()
            preferences
                .edit()
                .putStringSet(
                    CLIPBOARD_HISTORY_KEY,
                    history.map { "${it.unixTimestamp},${it.value}" }.toSet()
                )
                .apply()
        }
    }

    private fun createPreferences(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            PREFERENCES_FILENAME,
            MasterKey(context),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
}

private const val PREFERENCES_FILENAME = "fpjs_prefs"
private const val CLIPBOARD_HISTORY_KEY = "clipboard_history"
private const val TIMESTAMP_VALUE_DELIMITER = ","