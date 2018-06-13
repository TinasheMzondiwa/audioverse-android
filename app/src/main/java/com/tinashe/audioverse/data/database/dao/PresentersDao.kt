package com.tinashe.audioverse.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tinashe.audioverse.data.model.Presenter
import io.reactivex.Flowable

@Dao
interface PresentersDao : BaseDao<Presenter> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(presenters: List<Presenter>)

    @Query("SELECT * FROM presenters ORDER BY displayName")
    fun listAll(): Flowable<List<Presenter>>

    @Query("SELECT * FROM presenters ORDER BY displayName")
    fun listAllDirect(): List<Presenter>

    @Query("SELECT * FROM presenters ORDER BY displayName")
    fun presentersByName(): DataSource.Factory<Int, Presenter>
}