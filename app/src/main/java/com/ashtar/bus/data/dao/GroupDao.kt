package com.ashtar.bus.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ashtar.bus.model.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM `group` ORDER BY sort")
    fun getAll(): Flow<List<GroupEntity>>

    @Query("SELECT MAX(sort) FROM `group`")
    suspend fun getMaxSort(): Int

    @Insert
    suspend fun insert(item: GroupEntity): Long

    @Update
    suspend fun update(item: GroupEntity)

    @Query("UPDATE `group` SET sort = :sort WHERE id = :id")
    suspend fun updateSort(id: Int, sort: Int)

    @Delete
    suspend fun delete(item: GroupEntity)
}