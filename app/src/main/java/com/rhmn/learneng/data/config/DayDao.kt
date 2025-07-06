package com.rhmn.learneng.data.config
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rhmn.learneng.data.model.DbDays
import com.rhmn.learneng.data.model.Dialogue

@Dao
interface DayDao {
    @Insert
    suspend fun insert(day: DbDays)

    @Query("SELECT * FROM day WHERE id = :id")
    suspend fun getDayById(id: Int): DbDays
    @Query("SELECT * FROM day")
    suspend fun getDays(): List<DbDays>

}