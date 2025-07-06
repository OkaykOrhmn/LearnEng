package com.rhmn.learneng.data.config
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rhmn.learneng.data.model.Listening

@Dao
interface ListeningDao {
    @Insert
    suspend fun insert(listening: Listening)

    @Query("SELECT * FROM listening WHERE dayId = :dayId")
    suspend fun getByDayId(dayId: Int): List<Listening>

    @Query("SELECT * FROM listening WHERE id = :id")
    suspend fun getById(id: Int): Listening?
}