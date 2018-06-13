package com.tinashe.audioverse.data.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: T)

    @Delete
    fun delete(item: T)
}