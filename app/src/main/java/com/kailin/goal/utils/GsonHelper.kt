package com.kailin.goal.utils

import com.google.gson.Gson
import java.lang.reflect.Type

class GsonHelper private constructor() {

    val gson = Gson()

    fun <T> fromJson(json: String, type: Type): T = gson.fromJson(json, type)

    companion object {
        val instance: GsonHelper by lazy { GsonHelper() }
    }
}