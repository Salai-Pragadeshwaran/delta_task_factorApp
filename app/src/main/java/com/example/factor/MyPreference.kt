package com.example.factor

import android.content.Context


class MyPreference(context: Context) {


    val PREFERENCE_NAME = "SharedPreferenceExample"
    val PREFERENCE_LOGIN_COUNT = "HighScoreCount"

    val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getHSCount(): Int {
        return preference.getInt(PREFERENCE_LOGIN_COUNT, 0)
    }

    fun setHSCount(count: Int) {
        val editor = preference.edit()
        editor.putInt(PREFERENCE_LOGIN_COUNT, count)
        editor.apply()
    }

}