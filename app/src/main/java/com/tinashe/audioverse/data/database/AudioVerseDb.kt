package com.tinashe.audioverse.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tinashe.audioverse.data.database.dao.PresentersDao
import com.tinashe.audioverse.data.database.dao.RecordingsDao
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording

@Database(entities = [(Recording::class), (Presenter::class)], version = 1, exportSchema = false)
@TypeConverters(DataTypeConverters::class)
abstract class AudioVerseDb : RoomDatabase() {

    abstract fun recordingsDao(): RecordingsDao

    abstract fun presentersDao(): PresentersDao

    companion object {
        private const val DATABASE_NAME = "audio_verse_db"

        fun create(context: Context): AudioVerseDb = Room.databaseBuilder(context,
                AudioVerseDb::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}