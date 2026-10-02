package com.example.data.repository

import com.example.data.local.ChallengeQuest
import com.example.data.local.PlayerProfile
import com.example.data.local.PracticeRecord
import com.example.data.local.SparringRecord
import com.example.data.local.TkdDao
import com.example.data.model.BeltLevel
import com.example.data.model.TechniqueCurriculum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class TkdRepository(private val dao: TkdDao) {

  val playerProfile: Flow<PlayerProfile?> = dao.getPlayerProfile()
  val practiceRecords: Flow<List<PracticeRecord>> = dao.getAllPracticeRecords()
  val sparringRecords: Flow<List<SparringRecord>> = dao.getAllSparringRecords()
  val quests: Flow<List<ChallengeQuest>> = dao.getAllQuests()

  suspend fun initializeIfEmpty() {
    val existingProfile = dao.getPlayerProfile().firstOrNull()
    if (existingProfile == null) {
      dao.insertOrUpdateProfile(
        PlayerProfile(
          id = 1,
          name = "Dojang Student",
          avatarKey = "tkd_fighter_avatar",
          currentBelt = BeltLevel.WHITE,
          currentXp = 100,
          streakDays = 1,
          totalTrainingMinutes = 15,
          techniquesLearnedCount = 2,
          sparringWins = 0,
          sparringLosses = 0,
          lastTrainingDateMs = System.currentTimeMillis()
        )
      )
    }

    val existingQuests = dao.getAllQuests().firstOrNull()
    if (existingQuests.isNullOrEmpty()) {
      dao.insertOrUpdateQuests(getDefaultQuests())
    }
  }

  suspend fun addXpAndTraining(xpEarned: Int, minutesSpent: Int) {
    val current = dao.getPlayerProfile().firstOrNull() ?: PlayerProfile()
    val now = System.currentTimeMillis()
    val isNewDay = (now - current.lastTrainingDateMs) > TimeUnit.HOURS.toMillis(20)
    val newStreak = if (isNewDay) current.streakDays + 1 else current.streakDays

    val updatedXp = current.currentXp + xpEarned
    // Auto check if belt can promote or be eligible
    val updatedProfile = current.copy(
      currentXp = updatedXp,
      streakDays = newStreak,
      totalTrainingMinutes = current.totalTrainingMinutes + minutesSpent,
      lastTrainingDateMs = now
    )
    dao.insertOrUpdateProfile(updatedProfile)
  }

  suspend fun updateTechniquesLearnedCount(count: Int) {
    val current = dao.getPlayerProfile().firstOrNull() ?: return
    if (count > current.techniquesLearnedCount) {
      dao.insertOrUpdateProfile(current.copy(techniquesLearnedCount = count))
    }
  }

  suspend fun recordPracticeSession(
    techniqueId: String,
    techniqueName: String,
    accuracy: Int,
    timing: Int,
    techniqueScore: Int,
    mistakes: String,
    xp: Int
  ) {
    val record = PracticeRecord(
      techniqueId = techniqueId,
      techniqueName = techniqueName,
      accuracyPercent = accuracy,
      timingScorePercent = timing,
      techniqueScorePercent = techniqueScore,
      mistakesSummary = mistakes,
      xpEarned = xp
    )
    dao.insertPracticeRecord(record)
    addXpAndTraining(xpEarned = xp, minutesSpent = 3)
    updateQuestProgress("daily_kick_drill", 1)
  }

  suspend fun recordSparringMatch(
    opponentName: String,
    difficulty: String,
    playerScore: Int,
    opponentScore: Int,
    isWin: Boolean,
    cleanAttacks: Int,
    blocksCount: Int,
    feedback: String,
    xp: Int
  ) {
    val record = SparringRecord(
      opponentName = opponentName,
      difficulty = difficulty,
      playerScore = playerScore,
      opponentScore = opponentScore,
      isWin = isWin,
      cleanAttacks = cleanAttacks,
      successfulBlocks = blocksCount,
      feedbackSummary = feedback,
      xpEarned = xp
    )
    dao.insertSparringRecord(record)

    val current = dao.getPlayerProfile().firstOrNull() ?: PlayerProfile()
    val newWins = if (isWin) current.sparringWins + 1 else current.sparringWins
    val newLosses = if (!isWin) current.sparringLosses + 1 else current.sparringLosses
    dao.insertOrUpdateProfile(current.copy(sparringWins = newWins, sparringLosses = newLosses))

    addXpAndTraining(xpEarned = xp, minutesSpent = 5)
    updateQuestProgress("daily_sparring_match", 1)
    if (blocksCount > 0) {
      updateQuestProgress("daily_block_defense", blocksCount)
    }
  }

  suspend fun promoteBelt(targetBelt: BeltLevel): Boolean {
    val current = dao.getPlayerProfile().firstOrNull() ?: return false
    if (current.currentXp >= targetBelt.requiredXp) {
      dao.insertOrUpdateProfile(current.copy(currentBelt = targetBelt))
      return true
    }
    return false
  }

  suspend fun updatePlayerName(newName: String) {
    val current = dao.getPlayerProfile().firstOrNull() ?: return
    dao.insertOrUpdateProfile(current.copy(name = newName.trim()))
  }

  suspend fun resetProgress() {
    dao.insertOrUpdateProfile(
      PlayerProfile(
        id = 1,
        name = "Dojang Student",
        avatarKey = "tkd_fighter_avatar",
        currentBelt = BeltLevel.WHITE,
        currentXp = 0,
        streakDays = 1,
        totalTrainingMinutes = 0,
        techniquesLearnedCount = 1,
        sparringWins = 0,
        sparringLosses = 0,
        lastTrainingDateMs = System.currentTimeMillis()
      )
    )
    dao.insertOrUpdateQuests(getDefaultQuests())
  }

  suspend fun updateQuestProgress(questId: String, increment: Int) {
    val quests = dao.getAllQuests().firstOrNull() ?: return
    val quest = quests.find { it.id == questId } ?: return
    if (quest.isCompleted) return

    val newProgress = (quest.currentProgress + increment).coerceAtMost(quest.targetCount)
    val isNowCompleted = newProgress >= quest.targetCount
    val updated = quest.copy(currentProgress = newProgress, isCompleted = isNowCompleted)
    dao.updateQuest(updated)

    if (isNowCompleted) {
      addXpAndTraining(quest.rewardXp, 1)
    }
  }

  suspend fun claimQuest(questId: String) {
    val quests = dao.getAllQuests().firstOrNull() ?: return
    val quest = quests.find { it.id == questId } ?: return
    if (!quest.isCompleted) {
      dao.updateQuest(quest.copy(currentProgress = quest.targetCount, isCompleted = true))
      addXpAndTraining(quest.rewardXp, 1)
    }
  }

  private fun getDefaultQuests(): List<ChallengeQuest> = listOf(
    ChallengeQuest(
      id = "daily_stance_practice",
      title = "Perfect Stance Drill",
      description = "Hold a disciplined fighting stance with guard hands up for 60 seconds.",
      targetCount = 1,
      currentProgress = 0,
      isCompleted = false,
      rewardXp = 100,
      isWeekly = false
    ),
    ChallengeQuest(
      id = "daily_kick_drill",
      title = "Execute 15 Technique Drills",
      description = "Complete 15 successful kick or strike repetitions with 80%+ accuracy.",
      targetCount = 15,
      currentProgress = 3,
      isCompleted = false,
      rewardXp = 150,
      isWeekly = false
    ),
    ChallengeQuest(
      id = "daily_block_defense",
      title = "Solid Iron Guard",
      description = "Successfully block 5 incoming strikes in Practice or Sparring mode.",
      targetCount = 5,
      currentProgress = 1,
      isCompleted = false,
      rewardXp = 120,
      isWeekly = false
    ),
    ChallengeQuest(
      id = "daily_sparring_match",
      title = "Complete 1 Sparring Match",
      description = "Finish a full 2-round controlled sparring bout against an AI partner.",
      targetCount = 1,
      currentProgress = 0,
      isCompleted = false,
      rewardXp = 200,
      isWeekly = false
    ),
    ChallengeQuest(
      id = "weekly_master_curriculum",
      title = "Curriculum Advancement",
      description = "Learn and complete practice drills for at least 3 distinct techniques this week.",
      targetCount = 3,
      currentProgress = 1,
      isCompleted = false,
      rewardXp = 400,
      isWeekly = true
    ),
    ChallengeQuest(
      id = "weekly_sparring_victory",
      title = "Dojang Champion",
      description = "Win 3 sparring matches with clean technique and minimal Gam-jeom penalties.",
      targetCount = 3,
      currentProgress = 0,
      isCompleted = false,
      rewardXp = 500,
      isWeekly = true
    ),
    ChallengeQuest(
      id = "weekly_streak_keeper",
      title = "Indomitable Spirit",
      description = "Maintain your Taekwondo training discipline for 3 consecutive days.",
      targetCount = 3,
      currentProgress = 1,
      isCompleted = false,
      rewardXp = 350,
      isWeekly = true
    )
  )
}
