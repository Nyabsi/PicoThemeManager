package cc.sovellus.picothememanager.extension

import android.content.SharedPreferences
import androidx.core.content.edit

internal var SharedPreferences.lastUsedTheme: String
    get() = getString("lastTheme", "") ?: ""
    set(it) = edit { putString("lastTheme", it) }