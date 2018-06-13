package com.tinashe.audioverse.data.database

import androidx.room.Database
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
        const val DATABASE_NAME = "audion_verse_db"
    }
}