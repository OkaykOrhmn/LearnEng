package com.rhmn.learneng.data.config
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rhmn.learneng.data.model.Pronunciation

@Dao
interface PronunciationDao {
    @Insert
    suspend fun insert(pronunciation: Pronunciation)

    @Query("SELECT * FROM pronunciation WHERE dayId = :dayId")
    suspend fun getByDayId(dayId: Int): List<Pronunciation>

    @Query("SELECT * FROM pronunciation WHERE id = :id")
    suspend fun getById(id: Int): Pronunciation?
}