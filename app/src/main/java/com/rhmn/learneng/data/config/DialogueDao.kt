package com.rhmn.learneng.data.config
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rhmn.learneng.data.model.Dialogue

@Dao
interface DialogueDao {
    @Insert
    suspend fun insert(dialogue: Dialogue):Long

    @Query("SELECT * FROM dialogue WHERE dayId = :dayId")
    suspend fun getByDayId(dayId: Int): List<Dialogue>

    @Query("SELECT * FROM dialogue WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<Dialogue>
}