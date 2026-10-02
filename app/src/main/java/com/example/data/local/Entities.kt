package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BeltLevel

@Entity(tableName = "player_profile")
data class PlayerProfile(
  @PrimaryKey val id: Int = 1,
  val name: String = "Taekwondo Trainee",
  val avatarKey: String = "tkd_fighter_avatar",
  val currentBelt: BeltLevel = BeltLevel.WHITE,
  val currentXp: Int = 0,
  val streakDays: Int = 1,
  val totalTrainingMinutes: Int = 12,
  val techniquesLearnedCount: Int = 1,
  val sparringWins: Int = 0,
  val sparringLosses: Int = 0,
  val lastTrainingDateMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "practice_records")
data class PracticeRecord(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val techniqueId: String,
  val techniqueName: String,
  val accuracyPercent: Int,
  val timingScorePercent: Int,
  val techniqueScorePercent: Int,
  val mistakesSummary: String,
  val xpEarned: Int,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sparring_records")
data class SparringRecord(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val opponentName: String,
  val difficulty: String,
  val playerScore: Int,
  val opponentScore: Int,
  val isWin: Boolean,
  val cleanAttacks: Int,
  val successfulBlocks: Int,
  val feedbackSummary: String,
  val xpEarned: Int,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "challenge_quests")
data class ChallengeQuest(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val targetCount: Int,
  val currentProgress: Int,
  val isCompleted: Boolean,
  val rewardXp: Int,
  val isWeekly: Boolean
)
