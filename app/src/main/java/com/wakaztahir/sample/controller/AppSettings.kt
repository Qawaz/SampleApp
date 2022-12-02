package com.wakaztahir.sample.controller

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettings @Inject constructor(@ApplicationContext private val context: Context) {

    val prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

    enum class ThemeType { Light, Dark, System }

    fun getThemeType(): ThemeType {
        return ThemeType.valueOf(
            prefs.getString("theme_type", ThemeType.System.name) ?: ThemeType.System.name
        )
    }

    fun setThemeType(type: ThemeType) {
        prefs.edit(true) { putString("theme_type", type.name) }
    }

    fun getIsFirstTime(): Boolean = prefs.getBoolean("is_first_time", true)

    fun setIsFirstTime(set: Boolean) = prefs.edit(true) { putBoolean("is_first_time", set) }

    fun getLastTimeRatedApp() : Long {
        return prefs.getLong("last_time_rated",0)
    }

    fun setLastTimeRatedApp(time : Long){
        return prefs.edit(true){  putLong("last_time_rated",time) }
    }

    fun getIsProMember(): Boolean {
        return if (prefs.getLong("last_pro_set", 0) > System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 5) {
            prefs.getBoolean("is_pro_member", false)
        }else {
            false
        }
    }

    fun setIsProMember(pro: Boolean) {
        prefs.edit(true) { putBoolean("is_pro_member", pro) }
        prefs.edit(true){ putLong("last_pro_set",System.currentTimeMillis()) }
    }

}