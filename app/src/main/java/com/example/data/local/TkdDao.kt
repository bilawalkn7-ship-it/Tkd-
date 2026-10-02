package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TkdDao {
  @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
  fun getPlayerProfile(): Flow<PlayerProfile?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: PlayerProfile)

  // Practice records
  @Query("SELECT * FROM practice_records ORDER BY timestamp DESC")
  fun getAllPracticeRecords(): Flow<List<PracticeRecord>>

  @Query("SELECT * FROM practice_records WHERE techniqueId = :techniqueId ORDER BY timestamp DESC")
  fun getPracticeRecordsForTechnique(techniqueId: String): Flow<List<PracticeRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPracticeRecord(record: PracticeRecord)

  // Sparring records
  @Query("SELECT * FROM sparring_records ORDER BY timestamp DESC")
  fun getAllSparringRecords(): Flow<List<SparringRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSparringRecord(record: SparringRecord)

  // Challenge quests
  @Query("SELECT * FROM challenge_quests")
  fun getAllQuests(): Flow<List<ChallengeQuest>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateQuests(quests: List<ChallengeQuest>)

  @Update
  suspend fun updateQuest(quest: ChallengeQuest)
}
