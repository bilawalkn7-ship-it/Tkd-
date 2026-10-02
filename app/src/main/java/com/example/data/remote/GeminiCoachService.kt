package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.PlayerProfile
import com.example.data.local.PracticeRecord
import com.example.data.local.SparringRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiCoachService {

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .build()

  suspend fun getCoachingAdvice(
    profile: PlayerProfile,
    recentPractices: List<PracticeRecord>,
    recentSparring: List<SparringRecord>,
    userQuestion: String? = null
  ): String = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Throwable) {
      ""
    }

    val isKeyConfigured = apiKey.isNotBlank() &&
      !apiKey.equals("MY_GEMINI_API_KEY", ignoreCase = true) &&
      !apiKey.startsWith("YOUR_")

    if (isKeyConfigured) {
      try {
        val prompt = buildPrompt(profile, recentPractices, recentSparring, userQuestion)
        val response = callGeminiRest(apiKey, prompt)
        if (response.isNotBlank()) {
          return@withContext response
        }
      } catch (e: Exception) {
        Log.w("GeminiCoach", "Gemini API call failed, falling back to local coach: ${e.message}")
      }
    }

    // Local Rule-Based Taekwondo Coach Engine
    return@withContext generateLocalCoachAdvice(profile, recentPractices, recentSparring, userQuestion)
  }

  private fun buildPrompt(
    profile: PlayerProfile,
    recentPractices: List<PracticeRecord>,
    recentSparring: List<SparringRecord>,
    userQuestion: String?
  ): String {
    val practicesSummary = recentPractices.take(5).joinToString("; ") {
      "${it.techniqueName}: Acc ${it.accuracyPercent}%, Timing ${it.timingScorePercent}% (Mistakes: ${it.mistakesSummary})"
    }.ifEmpty { "No recent practice records recorded yet." }

    val sparringSummary = recentSparring.take(3).joinToString("; ") {
      "vs ${it.opponentName} (${if (it.isWin) "Won" else "Lost"} ${it.playerScore}-${it.opponentScore}, Clean: ${it.cleanAttacks}, Blocks: ${it.successfulBlocks})"
    }.ifEmpty { "No sparring matches recorded yet." }

    val questionText = if (!userQuestion.isNullOrBlank()) {
      "The student has this specific question: \"$userQuestion\""
    } else {
      "Provide a concise training evaluation, celebrating progress, highlighting 1-2 specific technical improvements (guard, knee chamber, supporting foot pivot), and prescribing next drills."
    }

    return """
      You are Master Kwan, an educational AI Taekwondo Coach in a martial arts training app.
      DISCLAIMER: Always remember this is an educational training aid and does not substitute for certified human martial arts instruction.
      
      Student Profile:
      - Name: ${profile.name}
      - Current Belt: ${profile.currentBelt.rankTitle} (${profile.currentBelt.hangul})
      - XP: ${profile.currentXp} | Streak: ${profile.streakDays} days | Total Training: ${profile.totalTrainingMinutes} mins
      - Sparring Record: ${profile.sparringWins}W - ${profile.sparringLosses}L
      
      Recent Practice History:
      $practicesSummary
      
      Recent Sparring Performance:
      $sparringSummary
      
      Task:
      $questionText
      
      Guidelines:
      - Respond in warm, disciplined, inspiring martial arts tone (using authentic Korean terminology like Chagi, Makgi, Gyeorugi, Kihap).
      - Keep response focused, encouraging, and under 130 words.
      - Emphasize discipline, safety, patience, and proper technique over fighting or violence.
    """.trimIndent()
  }

  private fun callGeminiRest(apiKey: String, prompt: String): String {
    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

    val jsonRequest = JSONObject().apply {
      put("contents", JSONArray().apply {
        put(JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply {
              put("text", prompt)
            })
          })
        })
      })
    }

    val request = Request.Builder()
      .url(url)
      .post(jsonRequest.toString().toRequestBody("application/json".toMediaType()))
      .build()

    val response = okHttpClient.newCall(request).execute()
    if (!response.isSuccessful) {
      Log.w("GeminiCoach", "HTTP code: ${response.code}")
      return ""
    }

    val responseBody = response.body?.string() ?: return ""
    val root = JSONObject(responseBody)
    val candidates = root.optJSONArray("candidates") ?: return ""
    if (candidates.length() == 0) return ""

    val first = candidates.getJSONObject(0)
    val content = first.optJSONObject("content") ?: return ""
    val parts = content.optJSONArray("parts") ?: return ""
    if (parts.length() == 0) return ""

    return parts.getJSONObject(0).optString("text", "")
  }

  private fun generateLocalCoachAdvice(
    profile: PlayerProfile,
    recentPractices: List<PracticeRecord>,
    recentSparring: List<SparringRecord>,
    userQuestion: String?
  ): String {
    if (!userQuestion.isNullOrBlank()) {
      val q = userQuestion.lowercase()
      return when {
        q.contains("roundhouse") || q.contains("dollyo") ->
          "For Dollyo Chagi (Roundhouse kick), power comes from your base pivot! Turn your supporting heel 180° toward the target and snap your hip completely over. Keep your rear hand glued to your chin to prevent counters."
        q.contains("front kick") || q.contains("ap chagi") ->
          "In Ap Chagi, knee height dictates kick height. Lift your knee tight to your chest before snapping the lower leg like a whip. Ensure toes are curled back to strike with Ap Chuk (ball of the foot)."
        q.contains("side kick") || q.contains("yeop") ->
          "Yeop Chagi requires a straight piston thrust. Align your shoulder, hip, and striking heel in a laser line. Pull your toes back so only the solid heel blade (Balnal) makes contact."
        q.contains("guard") || q.contains("defense") ->
          "Guard discipline separates beginners from champions! After every kick, your hands must instantly return to defend your chin and temple. Never drop your guard to balance."
        q.contains("belt") || q.contains("grading") ->
          "To graduate to your next belt (${profile.currentBelt.nextBelt?.rankTitle ?: "Black Belt"}), complete your required curriculum lessons, maintain high practice accuracy, and earn ${profile.currentBelt.nextBelt?.requiredXp ?: 4000} XP!"
        else ->
          "Patience and repetition are the heart of Taekwondo! Focus on smooth knee chambering, proper breathing (Kihap on impact), and instant return to Gyeorugi Junbi (fighting stance). Train consistently each day!"
      }
    }

    // Default performance analysis
    val lastPractice = recentPractices.firstOrNull()
    return if (lastPractice != null) {
      if (lastPractice.accuracyPercent < 75) {
        "Pil-Seung! On your recent ${lastPractice.techniqueName} drill, accuracy was ${lastPractice.accuracyPercent}%. Focus on slowing down: chamber your knee first, lock your core, and snap smoothly instead of rushing power."
      } else {
        "Excellent discipline! Your ${lastPractice.techniqueName} showed sharp form (${lastPractice.accuracyPercent}% accuracy, ${lastPractice.timingScorePercent}% timing). Continue honing your guard recovery before testing in Sparring mode!"
      }
    } else {
      "Welcome to the Dojang, ${profile.name}! As a ${profile.currentBelt.rankTitle}, your first duty is mastering the fighting stance (Gyeorugi Junbi) and front snap kick (Ap Chagi). Begin your first drill in the Learn tab!"
    }
  }
}
