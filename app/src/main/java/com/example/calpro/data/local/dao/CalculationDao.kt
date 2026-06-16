package com.example.calpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.calpro.data.local.entity.CalculationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculations ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationEntity>>

    @Insert
    suspend fun insert(calculation: CalculationEntity)

    @Delete
    suspend fun delete(calculation: CalculationEntity)

    @Query("DELETE FROM calculations")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM calculations")
    suspend fun getCount(): Int

    @Query("DELETE FROM calculations WHERE id IN (SELECT id FROM calculations ORDER BY timestamp ASC LIMIT 1)")
    suspend fun deleteOldest()

    @Query("DELETE FROM calculations WHERE id NOT IN (SELECT id FROM calculations ORDER BY timestamp DESC LIMIT 50)")
    suspend fun keepOnlyLatest50()
}