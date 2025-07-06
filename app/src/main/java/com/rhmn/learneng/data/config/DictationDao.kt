package com.rhmn.learneng.data.config
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rhmn.learneng.data.model.Dictation

@Dao
interface DictationDao {
    @Insert
    suspend fun insert(dictation: Dictation)

    @Query("SELECT * FROM dictation WHERE dayId = :dayId")
    suspend fun getByDayId(dayId: Int): List<Dictation>

    @Query("SELECT * FROM dictation WHERE id = :id")
    suspend fun getById(id: Int): Dictation?
}