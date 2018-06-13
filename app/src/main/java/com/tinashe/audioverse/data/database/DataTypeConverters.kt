package com.tinashe.audioverse.data.database

import androidx.annotation.Nullable
import androidx.room.TypeConverter
import timber.log.Timber
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tinashe.audioverse.data.model.MediaFile
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Series
import com.tinashe.audioverse.data.model.Sponsor


class DataTypeConverters {

    private val lock = Any()

    private var gson: Gson? = null

    private fun getGson(): Gson {
        synchronized(lock) {
            if (gson == null) {
                gson = Gson()
            }

            return gson as Gson
        }
    }

    @TypeConverter
    fun stringToIntList(data: String?): List<Int>? {
        return if (data == null) {
            Collections.emptyList()
        } else splitToIntList(data)
    }

    @TypeConverter
    fun intListToString(ints: List<Int>): String? {
        return joinIntoString(ints)
    }

    /**
     * Splits a comma separated list of integers to integer list.
     *
     *
     * If an input is malformed, it is omitted from the result.
     *
     * @param input Comma separated list of integers.
     * @return A List containing the integers or null if the input is null.
     */
    @Nullable
    fun splitToIntList(@Nullable input: String?): List<Int>? {
        if (input == null) {
            return null
        }
        val result = mutableListOf<Int>()
        val tokenizer = StringTokenizer(input, ",")
        while (tokenizer.hasMoreElements()) {
            val item = tokenizer.nextToken()
            try {
                result.add(Integer.parseInt(item))
            } catch (ex: NumberFormatException) {
                Timber.e("ROOM", "Malformed integer list", ex)
            }

        }
        return result
    }

    /**
     * Joins the given list of integers into a comma separated list.
     *
     * @param input The list of integers.
     * @return Comma separated string composed of integers in the list. If the list is null, return
     * value is null.
     */
    @Nullable
    fun joinIntoString(@Nullable input: List<Int>?): String? {
        if (input == null) {
            return null
        }

        val size = input.size
        if (size == 0) {
            return ""
        }
        val sb = StringBuilder()
        for (i in 0 until size) {
            sb.append(Integer.toString(input[i]))
            if (i < size - 1) {
                sb.append(",")
            }
        }
        return sb.toString()
    }


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
    fun sponsorsToJson(sponsors: List<Sponsor>) : String {
        return getGson().toJson(sponsors)
    }

    @TypeConverter
    fun jsonToSponsors(jsonString: String): List<Sponsor> {
        val type = object : TypeToken<List<Sponsor>>() {
        }.type

        return getGson().fromJson(jsonString, type)
    }

    @TypeConverter
    fun mediaFilesToJson(mediaFiles: List<MediaFile>) : String {
        return getGson().toJson(mediaFiles)
    }

    @TypeConverter
    fun jsonToMediaFiles(jsonString: String): List<MediaFile> {
        val type = object : TypeToken<List<MediaFile>>() {
        }.type

        return getGson().fromJson(jsonString, type)
    }

    @TypeConverter
    fun presentersToJson(presenters: List<Presenter>) : String {
        return getGson().toJson(presenters)
    }

    @TypeConverter
    fun jsonToPresenters(jsonString: String): List<Presenter> {
        val type = object : TypeToken<List<Presenter>>() {
        }.type

        return getGson().fromJson(jsonString, type)
    }

    @TypeConverter
    fun seriesToJson(series: List<Series>) : String {
        return getGson().toJson(series)
    }

    @TypeConverter
    fun jsonToSeries(jsonString: String): List<Series> {
        val type = object : TypeToken<List<Series>>() {
        }.type

        return getGson().fromJson(jsonString, type)
    }

}