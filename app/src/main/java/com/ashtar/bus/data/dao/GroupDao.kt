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
    @Query("SELECT * FROM `group` ORDER BY id")
    fun getAll(): Flow<List<GroupEntity>>

    @Insert
    suspend fun insert(item: GroupEntity): Long

    @Update
    suspend fun update(item: GroupEntity)

    @Delete
    suspend fun delete(item: GroupEntity)
}