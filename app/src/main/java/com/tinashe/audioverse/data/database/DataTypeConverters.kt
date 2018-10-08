package com.tinashe.audioverse.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tinashe.audioverse.data.model.MediaFile
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Series
import com.tinashe.audioverse.data.model.Sponsor
import java.util.*


class DataTypeConverters {

    private val gson: Gson = Gson()

    @TypeConverter
    fun longToDate(time: Long): Date {
        val date = Calendar.getInstance()
        date.timeInMillis = time

        return date.time
    }

    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun sponsorsToJson(sponsors: List<Sponsor>): String {
        return gson.toJson(sponsors)
    }

    @TypeConverter
    fun jsonToSponsors(jsonString: String): List<Sponsor> {
        val type = object : TypeToken<List<Sponsor>>() {
        }.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun mediaFilesToJson(mediaFiles: List<MediaFile>): String {
        return gson.toJson(mediaFiles)
    }

    @TypeConverter
    fun jsonToMediaFiles(jsonString: String): List<MediaFile> {
        val type = object : TypeToken<List<MediaFile>>() {
        }.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun presentersToJson(presenters: List<Presenter>): String {
        return gson.toJson(presenters)
    }

    @TypeConverter
    fun jsonToPresenters(jsonString: String): List<Presenter> {
        val type = object : TypeToken<List<Presenter>>() {
        }.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun seriesToJson(series: List<Series>): String {
        return gson.toJson(series)
    }

    @TypeConverter
    fun jsonToSeries(jsonString: String): List<Series> {
        val type = object : TypeToken<List<Series>>() {
        }.type

        return gson.fromJson(jsonString, type)
    }

}