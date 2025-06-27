package com.example.rayanshshmi

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Utils {
    private const val PREF_NAME = "word_time_pref"

    fun saveWordList(context: Context, list: List<String>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(list)
        prefs.edit().putString("word_list", json).apply()
    }

    fun loadWordList(context: Context): MutableList<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("word_list", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun saveTimeList(context: Context, key: String, list: List<TimeData>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(list)
        prefs.edit().putString(key, json).apply()
    }

    fun loadTimeList(context: Context, key: String): MutableList<TimeData> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<TimeData>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    // StepData list save/load
    fun saveStepList(context: Context, key: String, list: List<StepData>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(list)
        prefs.edit().putString(key, json).apply()
    }

    fun loadStepList(context: Context, key: String): MutableList<StepData> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<StepData>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}