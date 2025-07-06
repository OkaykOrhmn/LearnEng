package com.rhmn.learneng.data.config
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rhmn.learneng.data.model.Reading

@Dao
interface ReadingDao {
    @Insert
    suspend fun insert(reading: Reading)

    @Query("SELECT * FROM reading WHERE dayId = :dayId")
    suspend fun getByDayId(dayId: Int): List<Reading>

    @Query("SELECT * FROM reading WHERE id = :id")
    suspend fun getById(id: Int): Reading?
}