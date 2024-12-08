package com.rashid.bstassignment.data.localDataSource.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromProblems(problems: List<Problem>): String {
        return gson.toJson(problems)
    }

    @TypeConverter
    fun toProblems(data: String): List<Problem> {
        val listType = object : TypeToken<List<Problem>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromDiabetes(diabetes: List<Diabete>): String {
        return gson.toJson(diabetes)
    }

    @TypeConverter
    fun toDiabetes(data: String): List<Diabete> {
        val listType = object : TypeToken<List<Diabete>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromAsthma(asthma: List<Map<String, Any>>): String {
        return gson.toJson(asthma)
    }

    @TypeConverter
    fun toAsthma(data: String): List<Map<String, Any>> {
        val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
        return gson.fromJson(data, listType)
    }
}
