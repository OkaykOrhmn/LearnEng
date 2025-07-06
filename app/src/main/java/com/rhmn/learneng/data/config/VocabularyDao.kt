package com.rhmn.learneng.data.config
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rhmn.learneng.data.model.Vocabulary

@Dao
interface VocabularyDao {
    @Insert
    suspend fun insert(vocabulary: Vocabulary)

    @Query("SELECT * FROM vocabularies WHERE dayId = :dayId")
    suspend fun getByDayId(dayId: Int): List<Vocabulary>

    @Query("SELECT * FROM vocabularies WHERE id = :id")
    suspend fun getById(id: Int): Vocabulary?
}