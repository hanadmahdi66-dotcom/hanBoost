package com.example.data

import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hanboost_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NICKNAME = "user_nickname"
        private const val KEY_IS_SIGNED_UP = "is_signed_up"
        private const val KEY_PLAN = "user_subscription_plan" // "Free", "Pro", "Ultimate"
        private const val KEY_SELECTED_RESOLUTION = "selected_resolution" // "720P", "1488P", "4K"
        private const val KEY_FPS_LIMIT = "selected_fps_limit" // 60, 90, 120, 144
        private const val KEY_CPU_BOOST = "cpu_boost_enabled"
        private const val KEY_RAM_BOOST = "ram_boost_enabled"
        private const val KEY_STABLE_FPS = "stable_fps_enabled"
    }

    var nickname: String
        get() = prefs.getString(KEY_NICKNAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NICKNAME, value).apply()

    var isSignedUp: Boolean
        get() = prefs.getBoolean(KEY_IS_SIGNED_UP, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_SIGNED_UP, value).apply()

    var subscriptionPlan: String
        get() = prefs.getString(KEY_PLAN, "Free") ?: "Free"
        set(value) = prefs.edit().putString(KEY_PLAN, value).apply()

    var selectedResolution: String
        get() = prefs.getString(KEY_SELECTED_RESOLUTION, "720P") ?: "720P"
        set(value) = prefs.edit().putString(KEY_SELECTED_RESOLUTION, value).apply()

    var pendingPlanName: String
        get() = prefs.getString("pending_plan_name", "") ?: ""
        set(value) = prefs.edit().putString("pending_plan_name", value).apply()

    var pendingPlanTimestamp: Long
        get() = prefs.getLong("pending_plan_timestamp", 0L)
        set(value) = prefs.edit().putLong("pending_plan_timestamp", value).apply()

    var pendingPlanNameInput: String
        get() = prefs.getString("pending_plan_name_input", "") ?: ""
        set(value) = prefs.edit().putString("pending_plan_name_input", value).apply()

    var pendingPlanNumberInput: String
        get() = prefs.getString("pending_plan_number_input", "") ?: ""
        set(value) = prefs.edit().putString("pending_plan_number_input", value).apply()

    var selectedFpsLimit: Int
        get() = prefs.getInt(KEY_FPS_LIMIT, 60)
        set(value) = prefs.edit().putInt(KEY_FPS_LIMIT, value).apply()

    var isCpuBoostEnabled: Boolean
        get() = prefs.getBoolean(KEY_CPU_BOOST, true)
        set(value) = prefs.edit().putBoolean(KEY_CPU_BOOST, value).apply()

    var isRamBoostEnabled: Boolean
        get() = prefs.getBoolean(KEY_RAM_BOOST, true)
        set(value) = prefs.edit().putBoolean(KEY_RAM_BOOST, value).apply()

    var isStableFpsEnabled: Boolean
        get() = prefs.getBoolean(KEY_STABLE_FPS, true)
        set(value) = prefs.edit().putBoolean(KEY_STABLE_FPS, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
