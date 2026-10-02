package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.BeltLevel
import com.example.ui.components.BeltBadge
import com.example.ui.components.SafetyDisclaimerCard
import com.example.ui.theme.TkdBlue
import com.example.ui.theme.TkdCrimson
import com.example.ui.theme.TkdGold
import com.example.ui.theme.TkdSurface
import com.example.ui.theme.TkdSurfaceBorder
import com.example.ui.theme.TkdSurfaceVariant
import com.example.ui.theme.TkdTextMuted
import com.example.ui.theme.TkdTextPrimary
import com.example.ui.theme.TkdTextSecondary
import com.example.ui.viewmodel.TkdScreen
import com.example.ui.viewmodel.TkdViewModel

@Composable
fun DashboardScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  val coachMsg by viewModel.coachMessage.collectAsStateWithLifecycle()
  val isCoachLoading by viewModel.isCoachLoading.collectAsStateWithLifecycle()

  val currentProf = profile ?: return

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("dashboard_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Hero Dojang Banner & Player Card
    item {
      HeroDojangCard(
        profile = currentProf,
        onProfileClick = { viewModel.navigateTo(TkdScreen.PROFILE) }
      )
    }

    // 2. Training Stats Grid
    item {
      StatsSummaryRow(
        streakDays = currentProf.streakDays,
        trainingMins = currentProf.totalTrainingMinutes,
        techniquesCount = currentProf.techniquesLearnedCount,
        sparringWins = currentProf.sparringWins
      )
    }

    // 3. AI Coach Card (Master Kwan)
    item {
      AiCoachCard(
        coachMessage = coachMsg,
        isLoading = isCoachLoading,
        onAskAdvice = { viewModel.askAiCoach() },
        onOpenChat = { viewModel.navigateTo(TkdScreen.PROFILE) }
      )
    }

    // 4. Quick Training Pathways
    item {
      Text(
        text = "TRAINING PATHWAYS",
        color = TkdTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        PathwayActionCard(
          title = "Learn Curriculum",
          subtitle = "White → Black Belt",
          icon = Icons.AutoMirrored.Filled.MenuBook,
          accentColor = TkdCrimson,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.navigateTo(TkdScreen.LEARN) }
        )
        PathwayActionCard(
          title = "Practice Drill",
          subtitle = "Timing & Posture",
          icon = Icons.Default.FitnessCenter,
          accentColor = TkdBlue,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.startPracticeDrill() }
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        PathwayActionCard(
          title = "Sparring Arena",
          subtitle = "Olympic Rules AI",
          icon = Icons.Default.SportsKabaddi,
          accentColor = TkdGold,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.startSparring("Beginner Match") }
        )
        PathwayActionCard(
          title = "Pose Alignment",
          subtitle = "Stance Geometry",
          icon = Icons.Default.Visibility,
          accentColor = Color(0xFF00B0FF),
          modifier = Modifier.weight(1f),
          onClick = { viewModel.navigateTo(TkdScreen.POSE_SIMULATOR) }
        )
      }
    }

    // 5. Belt Progress Bar Card
    item {
      BeltProgressionCard(
        currentBelt = currentProf.currentBelt,
        currentXp = currentProf.currentXp,
        onViewProgress = { viewModel.navigateTo(TkdScreen.PROGRESS) }
      )
    }

    // 6. Safety & Responsible Learning Disclaimer
    item {
      SafetyDisclaimerCard(compact = true)
    }
  }
}

@Composable
private fun HeroDojangCard(
  profile: com.example.data.local.PlayerProfile,
  onProfileClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(170.dp)
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, TkdSurfaceBorder, RoundedCornerShape(16.dp))
      .clickable(onClick = onProfileClick)
  ) {
    // Background Dojang Image
    Image(
      painter = painterResource(R.drawable.tkd_hero_dojang_1790968402300),
      contentDescription = "Taekwondo Dojang",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Dark Gradient Overlay for readability
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.88f),
              Color.Black.copy(alpha = 0.55f),
              Color.Transparent
            )
          )
        )
    )

    // Content
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Avatar
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .border(2.dp, TkdCrimson, CircleShape)
      ) {
        Image(
          painter = painterResource(R.drawable.tkd_fighter_avatar_1790968416564),
          contentDescription = "Player Avatar",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = profile.name,
          color = TkdTextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        BeltBadge(belt = profile.currentBelt)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "태권도 수련 · ${profile.currentXp} XP",
          color = TkdGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = "View Profile",
        tint = TkdTextMuted,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
private fun StatsSummaryRow(
  streakDays: Int,
  trainingMins: Int,
  techniquesCount: Int,
  sparringWins: Int
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    StatItemCard(
      title = "Streak",
      value = "$streakDays Days",
      icon = Icons.Default.LocalFireDepartment,
      tint = Color(0xFFFF7043),
      modifier = Modifier.weight(1f)
    )
    StatItemCard(
      title = "Training",
      value = "${trainingMins}m",
      icon = Icons.Default.Timer,
      tint = TkdBlue,
      modifier = Modifier.weight(1f)
    )
    StatItemCard(
      title = "Curriculum",
      value = "$techniquesCount Techs",
      icon = Icons.AutoMirrored.Filled.MenuBook,
      tint = TkdGold,
      modifier = Modifier.weight(1f)
    )
    StatItemCard(
      title = "Sparring",
      value = "$sparringWins Wins",
      icon = Icons.Default.MilitaryTech,
      tint = TkdCrimson,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun StatItemCard(
  title: String,
  value: String,
  icon: ImageVector,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = TkdSurfaceVariant.copy(alpha = 0.7f)),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, color = TkdTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Text(text = title, color = TkdTextMuted, fontSize = 10.sp)
    }
  }
}

@Composable
private fun AiCoachCard(
  coachMessage: String,
  isLoading: Boolean,
  onAskAdvice: () -> Unit,
  onOpenChat: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = TkdSurface),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TkdBlue.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI Coach",
            tint = TkdBlue,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Master Kwan (AI Assistant)",
            color = TkdTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = "Educational Guide",
          color = TkdTextMuted,
          fontSize = 10.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      if (isLoading) {
        Row(
          modifier = Modifier.padding(vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TkdBlue, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Analyzing your recent technique data...", color = TkdTextSecondary, fontSize = 12.sp)
        }
      } else {
        Text(
          text = "\"$coachMessage\"",
          color = TkdTextPrimary.copy(alpha = 0.95f),
          fontSize = 12.sp,
          lineHeight = 18.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
          onClick = onAskAdvice,
          colors = ButtonDefaults.buttonColors(containerColor = TkdSurfaceVariant),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Text(text = "🔄 Refresh Advice", color = TkdTextPrimary, fontSize = 11.sp)
        }
        Button(
          onClick = onOpenChat,
          colors = ButtonDefaults.buttonColors(containerColor = TkdBlue.copy(alpha = 0.2f)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Text(text = "💬 Ask Question", color = TkdBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun PathwayActionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick),
    colors = CardDefaults.cardColors(containerColor = TkdSurface),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(text = title, color = TkdTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      Text(text = subtitle, color = TkdTextSecondary, fontSize = 11.sp)
    }
  }
}

@Composable
private fun BeltProgressionCard(
  currentBelt: BeltLevel,
  currentXp: Int,
  onViewProgress: () -> Unit
) {
  val nextBelt = currentBelt.nextBelt
  val targetXp = nextBelt?.requiredXp ?: currentBelt.requiredXp
  val prevXp = currentBelt.requiredXp
  val progress = if (nextBelt != null) {
    ((currentXp - prevXp).toFloat() / (targetXp - prevXp).toFloat()).coerceIn(0f, 1f)
  } else 1f

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onViewProgress),
    colors = CardDefaults.cardColors(containerColor = TkdSurface),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "Belt Progression", color = TkdTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(
          text = if (nextBelt != null) "Next: ${nextBelt.rankTitle}" else "Max Rank (1st Dan)",
          color = TkdGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = TkdCrimson,
        trackColor = TkdSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = "$currentXp XP", color = TkdTextSecondary, fontSize = 11.sp)
        Text(
          text = if (nextBelt != null) "${targetXp - currentXp} XP to promotion" else "Mastery Achieved",
          color = TkdTextMuted,
          fontSize = 11.sp
        )
      }
    }
  }
}
