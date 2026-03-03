package com.adriano.journey.data

import platform.Foundation.NSUserDefaults

class IosAppPreferences : AppPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (defaults.objectForKey(key) != null) {
            defaults.boolForKey(key)
        } else {
            defaultValue
        }
    }

    override fun setBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }
}
