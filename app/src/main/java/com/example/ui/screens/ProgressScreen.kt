package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BeltLevel
import com.example.ui.components.BeltBadge
import com.example.ui.components.SafetyDisclaimerCard
import com.example.ui.theme.TkdBlue
import com.example.ui.theme.TkdCrimson
import com.example.ui.theme.TkdGold
import com.example.ui.theme.TkdSuccess
import com.example.ui.theme.TkdSurface
import com.example.ui.theme.TkdSurfaceBorder
import com.example.ui.theme.TkdSurfaceVariant
import com.example.ui.theme.TkdTextMuted
import com.example.ui.theme.TkdTextPrimary
import com.example.ui.theme.TkdTextSecondary
import com.example.ui.viewmodel.TkdViewModel

@Composable
fun ProgressScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  val practiceRecords by viewModel.practiceRecords.collectAsStateWithLifecycle()
  val sparringRecords by viewModel.sparringRecords.collectAsStateWithLifecycle()

  val currentProf = profile ?: return

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("progress_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Column {
        Text(text = "수련 성과 분석 · Performance Analytics", color = TkdGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Taekwondo Mastery & Belt Grading", color = TkdTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
      }
    }

    // 1. Belt Progression Ladder & Grading Exam
    item {
      BeltGradingCard(
        currentBelt = currentProf.currentBelt,
        currentXp = currentProf.currentXp,
        onPromote = { nextBelt -> viewModel.promoteBelt(nextBelt) }
      )
    }

    // 2. Technique Accuracy Breakdown Chart
    item {
      TechniqueAccuracyChartCard(practiceRecords = practiceRecords)
    }

    // 3. Sparring Win/Loss & Defensive Combat Statistics
    item {
      SparringAnalyticsCard(
        wins = currentProf.sparringWins,
        losses = currentProf.sparringLosses,
        sparringRecords = sparringRecords
      )
    }

    // 4. Martial Arts Achievement Badges
    item {
      AchievementBadgesCard(
        profile = currentProf,
        practicesCount = practiceRecords.size
      )
    }

    // 5. Educational Disclaimer
    item {
      SafetyDisclaimerCard(compact = true)
    }
  }
}

@Composable
private fun BeltGradingCard(
  currentBelt: BeltLevel,
  currentXp: Int,
  onPromote: (BeltLevel) -> Unit
) {
  val nextBelt = currentBelt.nextBelt
  val isEligibleForPromotion = nextBelt != null && currentXp >= nextBelt.requiredXp

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = TkdSurface),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TkdGold.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "CURRENT RANK", color = TkdTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          BeltBadge(belt = currentBelt)
        }
        if (nextBelt != null) {
          Column(horizontalAlignment = Alignment.End) {
            Text(text = "NEXT EXAMINATION", color = TkdTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            BeltBadge(belt = nextBelt)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      if (nextBelt != null) {
        val targetXp = nextBelt.requiredXp
        val progress = (currentXp.toFloat() / targetXp.toFloat()).coerceIn(0f, 1f)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Grading Eligibility", color = TkdTextSecondary, fontSize = 12.sp)
          Text(text = "$currentXp / $targetXp XP", color = TkdGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = TkdGold,
          trackColor = TkdSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isEligibleForPromotion) {
          Button(
            onClick = { onPromote(nextBelt) },
            colors = ButtonDefaults.buttonColors(containerColor = TkdGold),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Promote to ${nextBelt.rankTitle}!", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(TkdSurfaceVariant)
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Earn ${targetXp - currentXp} more XP to take the ${nextBelt.rankTitle} Promotion Exam.",
              color = TkdTextMuted,
              fontSize = 11.sp
            )
          }
        }
      } else {
        Text(
          text = "Black Belt (1st Dan) Achieved. Mastery of fundamentals is the lifelong path.",
          color = TkdGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

@Composable
private fun TechniqueAccuracyChartCard(
  practiceRecords: List<com.example.data.local.PracticeRecord>
) {
  // Compute average accuracies or provide default realistic benchmark
  val frontKickAcc = practiceRecords.filter { it.techniqueId == "front-kick" }.map { it.accuracyPercent }.average().let { if (it.isNaN()) 87 else it.toInt() }
  val roundhouseAcc = practiceRecords.filter { it.techniqueId == "roundhouse-kick" }.map { it.accuracyPercent }.average().let { if (it.isNaN()) 74 else it.toInt() }
  val sideKickAcc = practiceRecords.filter { it.techniqueId == "side-kick" }.map { it.accuracyPercent }.average().let { if (it.isNaN()) 69 else it.toInt() }
  val blocksAcc = practiceRecords.filter { it.techniqueId == "basic-blocks" }.map { it.accuracyPercent }.average().let { if (it.isNaN()) 82 else it.toInt() }

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = TkdSurface),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TkdCrimson, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "TECHNIQUE ACCURACY BENCHMARK", color = TkdTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(12.dp))

      AccuracyBarRow("Front Kick (Ap Chagi)", frontKickAcc, TkdCrimson)
      Spacer(modifier = Modifier.height(8.dp))
      AccuracyBarRow("Roundhouse Kick (Dollyo Chagi)", roundhouseAcc, TkdBlue)
      Spacer(modifier = Modifier.height(8.dp))
      AccuracyBarRow("Side Kick (Yeop Chagi)", sideKickAcc, TkdGold)
      Spacer(modifier = Modifier.height(8.dp))
      AccuracyBarRow("Core Defense Blocks (Makgi)", blocksAcc, Color(0xFF26A69A))
    }
  }
}

@Composable
private fun AccuracyBarRow(
  label: String,
  percent: Int,
  color: Color
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, color = TkdTextSecondary, fontSize = 12.sp)
      Text(text = "$percent%", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { percent / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(7.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = color,
      trackColor = TkdSurfaceVariant
    )
  }
}

@Composable
private fun SparringAnalyticsCard(
  wins: Int,
  losses: Int,
  sparringRecords: List<com.example.data.local.SparringRecord>
) {
  val total = wins + losses
  val winRate = if (total > 0) (wins * 100) / total else 0

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = TkdSurface),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = null, tint = TkdBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "SPARRING COMBAT RECORD", color = TkdTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        AnalyticsItem("Matches", "$total")
        AnalyticsItem("Wins", "$wins", TkdSuccess)
        AnalyticsItem("Losses", "$losses", Color(0xFFEF5350))
        AnalyticsItem("Win Rate", "$winRate%", TkdGold)
      }
    }
  }
}

@Composable
private fun AnalyticsItem(title: String, value: String, valueColor: Color = TkdTextPrimary) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = value, color = valueColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Text(text = title, color = TkdTextMuted, fontSize = 11.sp)
  }
}

@Composable
private fun AchievementBadgesCard(
  profile: com.example.data.local.PlayerProfile,
  practicesCount: Int
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = TkdSurface),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = "DOJANG ACHIEVEMENTS & BADGES", color = TkdTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        BadgeIconItem("First Kick", "🥋", unlocked = practicesCount >= 1)
        BadgeIconItem("Iron Guard", "🛡️", unlocked = profile.sparringWins >= 1 || practicesCount >= 2)
        BadgeIconItem("Dedication", "🔥", unlocked = profile.streakDays >= 1)
        BadgeIconItem("Sparring Win", "⚔️", unlocked = profile.sparringWins >= 1)
        BadgeIconItem("Black Belt Path", "🥋", unlocked = profile.currentBelt != BeltLevel.WHITE)
      }
    }
  }
}

@Composable
private fun BadgeIconItem(name: String, icon: String, unlocked: Boolean) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(if (unlocked) TkdGold.copy(alpha = 0.2f) else TkdSurfaceVariant)
        .border(1.dp, if (unlocked) TkdGold else TkdSurfaceBorder, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      if (unlocked) {
        Text(text = icon, fontSize = 20.sp)
      } else {
        Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = TkdTextMuted, modifier = Modifier.size(18.dp))
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = name, color = if (unlocked) TkdTextPrimary else TkdTextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
  }
}
