package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.local.ChallengeQuest
import com.example.data.local.PlayerProfile
import com.example.data.local.PracticeRecord
import com.example.data.local.SparringRecord
import com.example.data.local.TkdDatabase
import com.example.data.model.BeltLevel
import com.example.data.model.Technique
import com.example.data.model.TechniqueCurriculum
import com.example.data.remote.GeminiCoachService
import com.example.data.repository.TkdRepository
import com.example.ui.components.FighterAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class TkdScreen {
  DASHBOARD,
  LEARN,
  TECHNIQUE_DETAIL,
  PRACTICE,
  POSE_SIMULATOR,
  SPARRING,
  CHALLENGES,
  PROGRESS,
  PROFILE,
  SETTINGS
}

data class SparringUiState(
  val isActive: Boolean = false,
  val isGameOver: Boolean = false,
  val opponentName: String = "Trainee Min-ho",
  val difficulty: String = "Beginner Match",
  val roundSecondsRemaining: Int = 45,
  val roundNumber: Int = 1,
  val maxRounds: Int = 2,
  val playerScore: Int = 0,
  val opponentScore: Int = 0,
  val playerStamina: Float = 1.0f,
  val opponentStamina: Float = 1.0f,
  val playerGuard: Float = 1.0f,
  val opponentGuard: Float = 1.0f,
  val playerAction: FighterAction = FighterAction.IDLE_STANCE,
  val opponentAction: FighterAction = FighterAction.IDLE_STANCE,
  val combatDistance: Float = 1.2f, // 0.8f close, 1.2f striking range, 1.8f out of range
  val cleanAttacks: Int = 0,
  val blocksCount: Int = 0,
  val gamjeomCount: Int = 0,
  val lastCombatLog: String = "Fight with discipline! Touch gloves.",
  val postMatchFeedback: String = ""
)

data class PracticeUiState(
  val isActive: Boolean = false,
  val currentTechnique: Technique = TechniqueCurriculum.allTechniques[2], // Default Front Kick
  val currentTargetHeight: String = "Momtong (Body)",
  val timingWindowActive: Boolean = false,
  val score: Int = 0,
  val totalReps: Int = 0,
  val correctReps: Int = 0,
  val currentStreak: Int = 0,
  val playerAction: FighterAction = FighterAction.IDLE_STANCE,
  val liveFeedback: String = "Assume Fighting Stance (Gyeorugi Junbi)",
  val feedbackPositive: Boolean = true,
  val isCompleted: Boolean = false,
  val finalAccuracy: Int = 0,
  val finalTimingScore: Int = 0,
  val finalTechniqueScore: Int = 0,
  val mistakeLog: List<String> = emptyList()
)

data class PoseAnalyzerState(
  val selectedTechnique: Technique = TechniqueCurriculum.allTechniques[2],
  val headAligned: Boolean = true,
  val guardUp: Boolean = true,
  val kneeChamberAngle: Float = 85f, // degrees
  val supportingFootPivot: Float = 45f, // degrees
  val torsoStability: Float = 90f,
  val statusNote: String = "Good preliminary posture! Guard hands defending chin."
)

class TkdViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TkdRepository
  private val coachService = GeminiCoachService()
  val soundManager = SoundManager()

  init {
    val database = TkdDatabase.getDatabase(application)
    repository = TkdRepository(database.tkdDao())
    viewModelScope.launch {
      repository.initializeIfEmpty()
    }
  }

  val profile: StateFlow<PlayerProfile?> = repository.playerProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val practiceRecords: StateFlow<List<PracticeRecord>> = repository.practiceRecords
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val sparringRecords: StateFlow<List<SparringRecord>> = repository.sparringRecords
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val quests: StateFlow<List<ChallengeQuest>> = repository.quests
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Navigation
  private val _currentScreen = MutableStateFlow(TkdScreen.DASHBOARD)
  val currentScreen: StateFlow<TkdScreen> = _currentScreen.asStateFlow()

  private val _selectedTechnique = MutableStateFlow<Technique>(TechniqueCurriculum.allTechniques[2])
  val selectedTechnique: StateFlow<Technique> = _selectedTechnique.asStateFlow()

  // AI Coach state
  private val _coachMessage = MutableStateFlow<String>("Welcome to the Dojang! Focus on timing, balance, and guard discipline.")
  val coachMessage: StateFlow<String> = _coachMessage.asStateFlow()

  private val _isCoachLoading = MutableStateFlow(false)
  val isCoachLoading: StateFlow<Boolean> = _isCoachLoading.asStateFlow()

  // Practice state
  private val _practiceState = MutableStateFlow(PracticeUiState())
  val practiceState: StateFlow<PracticeUiState> = _practiceState.asStateFlow()
  private var practiceJob: Job? = null

  // Sparring state
  private val _sparringState = MutableStateFlow(SparringUiState())
  val sparringState: StateFlow<SparringUiState> = _sparringState.asStateFlow()
  private var sparringJob: Job? = null

  // Pose analyzer state
  private val _poseState = MutableStateFlow(PoseAnalyzerState())
  val poseState: StateFlow<PoseAnalyzerState> = _poseState.asStateFlow()

  fun navigateTo(screen: TkdScreen) {
    if (screen != TkdScreen.PRACTICE) stopPracticeDrill()
    if (screen != TkdScreen.SPARRING) endSparringMatch(save = false)
    _currentScreen.value = screen
  }

  fun openTechniqueDetail(technique: Technique) {
    _selectedTechnique.value = technique
    _currentScreen.value = TkdScreen.TECHNIQUE_DETAIL
  }

  // --- PRACTICE SIMULATOR LOGIC ---
  fun startPracticeDrill(technique: Technique = _selectedTechnique.value) {
    _selectedTechnique.value = technique
    _practiceState.value = PracticeUiState(
      isActive = true,
      currentTechnique = technique,
      score = 0,
      totalReps = 0,
      correctReps = 0,
      currentStreak = 0,
      liveFeedback = "Ready! Touch prompt to start drill.",
      feedbackPositive = true,
      isCompleted = false,
      mistakeLog = emptyList()
    )
    _currentScreen.value = TkdScreen.PRACTICE
    soundManager.playGong()
    launchPracticeTargetCycle()
  }

  private fun launchPracticeTargetCycle() {
    practiceJob?.cancel()
    practiceJob = viewModelScope.launch {
      while (_practiceState.value.isActive && _practiceState.value.totalReps < 10) {
        delay(1200)
        if (!_practiceState.value.isActive) break

        val isHead = Random.nextBoolean()
        _practiceState.value = _practiceState.value.copy(
          currentTargetHeight = if (isHead) "Olgul (Head Level)" else "Momtong (Body Level)",
          timingWindowActive = true,
          liveFeedback = "STRIKE NOW! Snap kick with high knee chamber!",
          feedbackPositive = true
        )

        // Timing window open for 1500ms
        delay(1500)
        if (_practiceState.value.timingWindowActive) {
          // Missed timing
          recordPracticeAttempt(timedOut = true)
        }
      }

      if (_practiceState.value.isActive && _practiceState.value.totalReps >= 10) {
        completePracticeDrill()
      }
    }
  }

  fun executePracticeAction(action: FighterAction) {
    if (!_practiceState.value.isActive || _practiceState.value.isCompleted) return

    val current = _practiceState.value
    _practiceState.value = current.copy(playerAction = action)

    when (action) {
      FighterAction.FRONT_KICK, FighterAction.ROUNDHOUSE_KICK, FighterAction.SIDE_KICK, FighterAction.AXE_KICK -> {
        soundManager.playKickSound()
      }
      FighterAction.PUNCH -> soundManager.playImpactSound()
      FighterAction.HIGH_BLOCK, FighterAction.LOW_BLOCK -> soundManager.playBlockSound()
      else -> {}
    }

    if (current.timingWindowActive) {
      _practiceState.value = _practiceState.value.copy(timingWindowActive = false)
      val techniqueMatches = matchesExpectedAction(action, current.currentTechnique)
      if (techniqueMatches) {
        val newCorrect = current.correctReps + 1
        val newTotal = current.totalReps + 1
        val newScore = current.score + 100 + (current.currentStreak * 15)
        soundManager.playKihapSound()
        _practiceState.value = _practiceState.value.copy(
          totalReps = newTotal,
          correctReps = newCorrect,
          currentStreak = current.currentStreak + 1,
          score = newScore,
          liveFeedback = "✅ Excellent chamber, crisp snap, and recovery!",
          feedbackPositive = true
        )
      } else {
        val newTotal = current.totalReps + 1
        val mistake = "Wrong technique choice for ${current.currentTechnique.name}"
        _practiceState.value = _practiceState.value.copy(
          totalReps = newTotal,
          currentStreak = 0,
          liveFeedback = "⚠️ Execute the designated ${current.currentTechnique.name}!",
          feedbackPositive = false,
          mistakeLog = current.mistakeLog + mistake
        )
      }
    } else {
      // Struck too early or out of tempo
      _practiceState.value = _practiceState.value.copy(
        liveFeedback = "⚠️ Off-tempo! Wait for the pad signal before kicking.",
        feedbackPositive = false
      )
    }

    // Reset fighter action back to stance after 400ms
    viewModelScope.launch {
      delay(450)
      _practiceState.value = _practiceState.value.copy(playerAction = FighterAction.IDLE_STANCE)
    }
  }

  private fun recordPracticeAttempt(timedOut: Boolean) {
    val current = _practiceState.value
    val newTotal = current.totalReps + 1
    val mistake = if (timedOut) "Missed timing sweet-spot window" else "Unbalanced recovery"
    _practiceState.value = current.copy(
      totalReps = newTotal,
      currentStreak = 0,
      timingWindowActive = false,
      liveFeedback = "❌ Too slow! Explode directly from your stance.",
      feedbackPositive = false,
      mistakeLog = current.mistakeLog + mistake
    )
  }

  private fun matchesExpectedAction(action: FighterAction, tech: Technique): Boolean {
    return when (tech.id) {
      "front-kick" -> action == FighterAction.FRONT_KICK
      "roundhouse-kick" -> action == FighterAction.ROUNDHOUSE_KICK
      "side-kick" -> action == FighterAction.SIDE_KICK
      "axe-kick" -> action == FighterAction.AXE_KICK
      "basic-punch" -> action == FighterAction.PUNCH
      "basic-blocks" -> action == FighterAction.HIGH_BLOCK || action == FighterAction.LOW_BLOCK
      else -> action != FighterAction.IDLE_STANCE
    }
  }

  private fun completePracticeDrill() {
    practiceJob?.cancel()
    val current = _practiceState.value
    val accuracy = if (current.totalReps > 0) (current.correctReps * 100) / current.totalReps else 0
    val timingScore = (accuracy * 0.95f + Random.nextInt(5)).toInt().coerceIn(0, 100)
    val techScore = (accuracy * 0.9f + Random.nextInt(10)).toInt().coerceIn(0, 100)
    val xp = (current.score / 5).coerceAtLeast(60)

    soundManager.playSuccessFanfare()

    _practiceState.value = current.copy(
      isActive = false,
      isCompleted = true,
      finalAccuracy = accuracy,
      finalTimingScore = timingScore,
      finalTechniqueScore = techScore,
      liveFeedback = "Drill Complete! Review your technical assessment."
    )

    viewModelScope.launch {
      repository.recordPracticeSession(
        techniqueId = current.currentTechnique.id,
        techniqueName = current.currentTechnique.name,
        accuracy = accuracy,
        timing = timingScore,
        techniqueScore = techScore,
        mistakes = current.mistakeLog.distinct().joinToString(", ").ifEmpty { "Clean execution" },
        xp = xp
      )
    }
  }

  fun stopPracticeDrill() {
    practiceJob?.cancel()
    _practiceState.value = _practiceState.value.copy(isActive = false)
  }

  // --- SPARRING SIMULATOR LOGIC ---
  fun startSparring(difficulty: String = "Beginner Match") {
    val oppName = when (difficulty) {
      "Training Sparring" -> "Senior Student Jin"
      "Beginner Match" -> "Trainee Min-ho (Yellow Belt)"
      "Intermediate Match" -> "Challenger Tae-hyun (Blue Belt)"
      else -> "Champion Ji-hoon (Black Belt)"
    }

    _sparringState.value = SparringUiState(
      isActive = true,
      isGameOver = false,
      opponentName = oppName,
      difficulty = difficulty,
      roundSecondsRemaining = 45,
      roundNumber = 1,
      maxRounds = 2,
      playerScore = 0,
      opponentScore = 0,
      playerStamina = 1.0f,
      opponentStamina = 1.0f,
      playerGuard = 1.0f,
      opponentGuard = 1.0f,
      playerAction = FighterAction.IDLE_STANCE,
      opponentAction = FighterAction.IDLE_STANCE,
      combatDistance = 1.2f,
      cleanAttacks = 0,
      blocksCount = 0,
      gamjeomCount = 0,
      lastCombatLog = "Charyeot, Gyeongnye! Shi-jak (Begin)!"
    )
    _currentScreen.value = TkdScreen.SPARRING
    soundManager.playGong()
    launchSparringAiLoop()
  }

  private fun launchSparringAiLoop() {
    sparringJob?.cancel()
    sparringJob = viewModelScope.launch {
      while (_sparringState.value.isActive && !_sparringState.value.isGameOver) {
        delay(1000)
        val s = _sparringState.value
        if (!s.isActive || s.isGameOver) break

        val newTime = s.roundSecondsRemaining - 1
        if (newTime <= 0) {
          if (s.roundNumber < s.maxRounds) {
            soundManager.playGong()
            _sparringState.value = s.copy(
              roundNumber = s.roundNumber + 1,
              roundSecondsRemaining = 45,
              lastCombatLog = "Round ${s.roundNumber + 1}! Keep hands high, reset stance!"
            )
          } else {
            endSparringMatch(save = true)
            break
          }
        } else {
          // Passive stamina recovery
          val pStamina = (s.playerStamina + 0.05f).coerceAtMost(1f)
          val oStamina = (s.opponentStamina + 0.05f).coerceAtMost(1f)
          val pGuard = (s.playerGuard + 0.08f).coerceAtMost(1f)
          val oGuard = (s.opponentGuard + 0.08f).coerceAtMost(1f)

          _sparringState.value = s.copy(
            roundSecondsRemaining = newTime,
            playerStamina = pStamina,
            opponentStamina = oStamina,
            playerGuard = pGuard,
            opponentGuard = oGuard
          )

          // AI decision logic
          executeAiSparringTurn()
        }
      }
    }
  }

  private fun executeAiSparringTurn() {
    val s = _sparringState.value
    val aiAggressiveness = when (s.difficulty) {
      "Training Sparring" -> 0.15f
      "Beginner Match" -> 0.25f
      "Intermediate Match" -> 0.40f
      else -> 0.60f
    }

    if (Random.nextFloat() < aiAggressiveness) {
      val attackType = when (Random.nextInt(4)) {
        0 -> FighterAction.ROUNDHOUSE_KICK
        1 -> FighterAction.FRONT_KICK
        2 -> FighterAction.AXE_KICK
        else -> FighterAction.PUNCH
      }

      _sparringState.value = _sparringState.value.copy(
        opponentAction = attackType,
        opponentStamina = (s.opponentStamina - 0.15f).coerceAtLeast(0.1f)
      )

      // Evaluate whether player blocked or got hit
      if (s.playerAction == FighterAction.HIGH_BLOCK || s.playerAction == FighterAction.LOW_BLOCK) {
        soundManager.playBlockSound()
        _sparringState.value = _sparringState.value.copy(
          blocksCount = s.blocksCount + 1,
          lastCombatLog = "Blocked! Solid guard defused the opponent's strike!"
        )
      } else {
        // Points to opponent
        val pts = if (attackType == FighterAction.AXE_KICK) 3 else 2
        soundManager.playImpactSound()
        _sparringState.value = _sparringState.value.copy(
          opponentScore = s.opponentScore + pts,
          playerAction = FighterAction.HIT_REACTION,
          playerGuard = (s.playerGuard - 0.25f).coerceAtLeast(0f),
          lastCombatLog = "Opponent scored +$pts! Guard dropped during defense."
        )
      }

      // Reset AI stance
      viewModelScope.launch {
        delay(400)
        _sparringState.value = _sparringState.value.copy(opponentAction = FighterAction.IDLE_STANCE)
      }
    }
  }

  fun playerSparringAction(action: FighterAction) {
    if (!_sparringState.value.isActive || _sparringState.value.isGameOver) return
    val s = _sparringState.value
    if (s.playerStamina < 0.12f) {
      _sparringState.value = s.copy(lastCombatLog = "Low Stamina! Breathe and recover your guard.")
      return
    }

    _sparringState.value = s.copy(
      playerAction = action,
      playerStamina = (s.playerStamina - 0.15f).coerceAtLeast(0f)
    )

    when (action) {
      FighterAction.ROUNDHOUSE_KICK, FighterAction.FRONT_KICK, FighterAction.SIDE_KICK, FighterAction.AXE_KICK -> {
        soundManager.playKickSound()
        evaluatePlayerAttack(action)
      }
      FighterAction.PUNCH -> {
        soundManager.playImpactSound()
        evaluatePlayerAttack(action)
      }
      FighterAction.HIGH_BLOCK, FighterAction.LOW_BLOCK -> {
        soundManager.playBlockSound()
        _sparringState.value = _sparringState.value.copy(
          playerGuard = 1.0f,
          lastCombatLog = "Iron Guard locked! Protected against counter attacks."
        )
      }
      else -> {}
    }

    viewModelScope.launch {
      delay(420)
      if (_sparringState.value.isActive) {
        _sparringState.value = _sparringState.value.copy(playerAction = FighterAction.IDLE_STANCE)
      }
    }
  }

  private fun evaluatePlayerAttack(action: FighterAction) {
    val s = _sparringState.value
    val aiBlocks = Random.nextFloat() < (if (s.difficulty == "Beginner Match") 0.25f else 0.55f)

    if (aiBlocks) {
      soundManager.playBlockSound()
      _sparringState.value = s.copy(
        opponentAction = FighterAction.HIGH_BLOCK,
        lastCombatLog = "Opponent blocked the strike with reinforced guard!"
      )
      viewModelScope.launch {
        delay(350)
        _sparringState.value = _sparringState.value.copy(opponentAction = FighterAction.IDLE_STANCE)
      }
    } else {
      // Clean strike scores!
      soundManager.playKihapSound()
      val pts = when (action) {
        FighterAction.AXE_KICK -> 3 // Head kick
        FighterAction.ROUNDHOUSE_KICK -> 2 // Trunk kick
        FighterAction.FRONT_KICK -> 2
        FighterAction.SIDE_KICK -> 2
        FighterAction.PUNCH -> 1
        else -> 1
      }
      val newPlayerScore = s.playerScore + pts
      _sparringState.value = s.copy(
        playerScore = newPlayerScore,
        cleanAttacks = s.cleanAttacks + 1,
        opponentAction = FighterAction.HIT_REACTION,
        lastCombatLog = "CLEAN STRIKE! +$pts points to Blue!"
      )
      viewModelScope.launch {
        delay(400)
        _sparringState.value = _sparringState.value.copy(opponentAction = FighterAction.IDLE_STANCE)
      }
    }
  }

  fun stepDistance(forward: Boolean) {
    val s = _sparringState.value
    val newDist = if (forward) (s.combatDistance - 0.25f).coerceAtLeast(0.8f) else (s.combatDistance + 0.25f).coerceAtMost(1.8f)
    _sparringState.value = s.copy(
      combatDistance = newDist,
      lastCombatLog = if (forward) "Closed distance to striking pocket" else "Stepped back to safe perimeter"
    )
  }

  private fun endSparringMatch(save: Boolean) {
    sparringJob?.cancel()
    val s = _sparringState.value
    val isWin = s.playerScore > s.opponentScore
    val xp = (s.playerScore * 25 + s.cleanAttacks * 20 + if (isWin) 150 else 50).coerceAtLeast(50)

    val feedback = if (isWin) {
      "Victory! You controlled distance and capitalized on clean kicking angles. Keep your hands up when planting after kicks."
    } else {
      "Good effort! Work on your defensive blocks and avoid overextending into the opponent's counter range."
    }

    _sparringState.value = s.copy(
      isActive = false,
      isGameOver = true,
      postMatchFeedback = feedback
    )

    if (save) {
      soundManager.playSuccessFanfare()
      viewModelScope.launch {
        repository.recordSparringMatch(
          opponentName = s.opponentName,
          difficulty = s.difficulty,
          playerScore = s.playerScore,
          opponentScore = s.opponentScore,
          isWin = isWin,
          cleanAttacks = s.cleanAttacks,
          blocksCount = s.blocksCount,
          feedback = feedback,
          xp = xp
        )
      }
    }
  }

  // --- POSE ANALYZER SIMULATION ---
  fun updatePoseJoint(angle: Float, type: String) {
    val cur = _poseState.value
    when (type) {
      "knee" -> _poseState.value = cur.copy(kneeChamberAngle = angle)
      "foot" -> _poseState.value = cur.copy(supportingFootPivot = angle)
      "guard" -> _poseState.value = cur.copy(guardUp = angle > 50f)
      "torso" -> _poseState.value = cur.copy(torsoStability = angle)
    }
    evaluatePose()
  }

  private fun evaluatePose() {
    val p = _poseState.value
    val notes = mutableListOf<String>()

    if (p.kneeChamberAngle < 75f) {
      notes.add("Knee chamber is too low; pull knee higher to solar plexus level.")
    }
    if (p.supportingFootPivot < 40f && p.selectedTechnique.id == "roundhouse-kick") {
      notes.add("Pivot supporting heel more (aim for 90°-120°) to open hips.")
    }
    if (!p.guardUp) {
      notes.add("Hands dropped below chest. Raise fists to protect jawline.")
    }
    if (notes.isEmpty()) {
      _poseState.value = p.copy(statusNote = "✅ Excellent body alignment! Balanced, guarded, and primed.")
    } else {
      _poseState.value = p.copy(statusNote = "⚠️ " + notes.joinToString(" "))
    }
  }

  fun setPoseTechnique(tech: Technique) {
    _poseState.value = _poseState.value.copy(selectedTechnique = tech)
    evaluatePose()
  }

  // --- AI COACH CONSULTATION ---
  fun askAiCoach(question: String? = null) {
    val prof = profile.value ?: return
    val practices = practiceRecords.value
    val sparring = sparringRecords.value

    _isCoachLoading.value = true
    viewModelScope.launch {
      val advice = coachService.getCoachingAdvice(prof, practices, sparring, question)
      _coachMessage.value = advice
      _isCoachLoading.value = false
    }
  }

  // --- PROFILE & QUESTS ---
  fun promoteBelt(belt: BeltLevel) {
    viewModelScope.launch {
      val success = repository.promoteBelt(belt)
      if (success) {
        soundManager.playSuccessFanfare()
        _coachMessage.value = "Kukkiwon Congratulates You! You have achieved ${belt.rankTitle} (${belt.hangul}). With greater rank comes greater humility and discipline."
      }
    }
  }

  fun claimQuest(quest: ChallengeQuest) {
    viewModelScope.launch {
      repository.claimQuest(quest.id)
      soundManager.playSuccessFanfare()
    }
  }

  fun updatePlayerName(newName: String) {
    viewModelScope.launch {
      repository.updatePlayerName(newName)
    }
  }

  fun resetAllProgress() {
    viewModelScope.launch {
      repository.resetProgress()
    }
  }

  override fun onCleared() {
    super.onCleared()
    soundManager.release()
  }
}
