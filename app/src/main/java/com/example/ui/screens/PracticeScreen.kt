package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AnimatedFighterCanvas
import com.example.ui.components.FighterAction
import com.example.ui.theme.BeltBlack
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
import com.example.ui.theme.TkdWarning
import com.example.ui.viewmodel.TkdScreen
import com.example.ui.viewmodel.TkdViewModel

@Composable
fun PracticeScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val practiceState by viewModel.practiceState.collectAsStateWithLifecycle()

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("practice_screen")
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(TkdSurface)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { viewModel.navigateTo(TkdScreen.LEARN) }) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Exit Drill",
            tint = TkdTextPrimary
          )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column {
          Text(
            text = "Interactive Practice: ${practiceState.currentTechnique.name}",
            color = TkdTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${practiceState.currentTechnique.hangul} · Target Reps: ${practiceState.totalReps}/10",
            color = TkdGold,
            fontSize = 11.sp
          )
        }
      }

      // Streak badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(TkdCrimson.copy(alpha = 0.2f))
          .border(1.dp, TkdCrimson, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Streak: ${practiceState.currentStreak}x",
          color = TkdCrimson,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Interactive Dojang Arena Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color(0xFF13141F), Color(0xFF1A1C2B), Color(0xFF0F1017))
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      // Background Target Mitt Indicator
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp)
      ) {
        Card(
          colors = CardDefaults.cardColors(
            containerColor = if (practiceState.timingWindowActive) TkdCrimson.copy(alpha = 0.3f) else TkdSurfaceVariant
          ),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (practiceState.timingWindowActive) TkdCrimson else TkdSurfaceBorder
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(if (practiceState.timingWindowActive) TkdCrimson else TkdTextMuted)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (practiceState.timingWindowActive) "STRIKE TARGET NOW: ${practiceState.currentTargetHeight}" else "PREPARE STANCE...",
              color = if (practiceState.timingWindowActive) Color.White else TkdTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Animated Fighter
      AnimatedFighterCanvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(280.dp),
        action = practiceState.playerAction,
        facingRight = true,
        hoguColor = TkdBlue,
        beltColor = BeltBlack,
        showEnergyTrail = true
      )

      // Live Educational Feedback Banner
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(
            containerColor = if (practiceState.feedbackPositive) TkdSurface else Color(0xFF2A1517)
          ),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (practiceState.feedbackPositive) TkdSuccess.copy(alpha = 0.5f) else TkdWarning
          )
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (practiceState.feedbackPositive) Icons.Default.CheckCircle else Icons.Default.Warning,
              contentDescription = null,
              tint = if (practiceState.feedbackPositive) TkdSuccess else TkdWarning,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = practiceState.liveFeedback,
              color = TkdTextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    // Action Control Pad
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(TkdSurface)
        .padding(14.dp)
    ) {
      Text(
        text = "TECHNIQUE CONTROLS · EXECUTE WITH PRECISION",
        color = TkdTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(10.dp))

      // Row 1: Primary Kicks
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        PracticeActionButton(
          label = "Front Kick",
          korean = "앞차기",
          color = TkdCrimson,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.executePracticeAction(FighterAction.FRONT_KICK) }
        )
        PracticeActionButton(
          label = "Roundhouse",
          korean = "돌려차기",
          color = TkdBlue,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.executePracticeAction(FighterAction.ROUNDHOUSE_KICK) }
        )
        PracticeActionButton(
          label = "Side Kick",
          korean = "옆차기",
          color = TkdGold,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.executePracticeAction(FighterAction.SIDE_KICK) }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row 2: Secondary strikes & blocks
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        PracticeActionButton(
          label = "Axe Kick",
          korean = "내려차기",
          color = Color(0xFFAB47BC),
          modifier = Modifier.weight(1f),
          onClick = { viewModel.executePracticeAction(FighterAction.AXE_KICK) }
        )
        PracticeActionButton(
          label = "Punch",
          korean = "지르기",
          color = Color(0xFFFF7043),
          modifier = Modifier.weight(1f),
          onClick = { viewModel.executePracticeAction(FighterAction.PUNCH) }
        )
        PracticeActionButton(
          label = "Block / Guard",
          korean = "막기",
          color = Color(0xFF26A69A),
          modifier = Modifier.weight(1f),
          onClick = { viewModel.executePracticeAction(FighterAction.HIGH_BLOCK) }
        )
      }
    }
  }

  // Drill Completion Modal
  if (practiceState.isCompleted) {
    AlertDialog(
      onDismissRequest = { },
      containerColor = TkdSurface,
      shape = RoundedCornerShape(16.dp),
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TkdSuccess, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Drill Completed!", color = TkdTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Technical Evaluation for ${practiceState.currentTechnique.name} (${practiceState.currentTechnique.hangul}):",
            color = TkdTextSecondary,
            fontSize = 12.sp
          )

          // Score meters
          ScoreMeterRow(label = "Form Accuracy", score = practiceState.finalAccuracy, color = TkdCrimson)
          ScoreMeterRow(label = "Reaction Timing", score = practiceState.finalTimingScore, color = TkdBlue)
          ScoreMeterRow(label = "Technique Grade", score = practiceState.finalTechniqueScore, color = TkdGold)

          // Mistakes detected
          if (practiceState.mistakeLog.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Mistakes Noted:", color = Color(0xFFEF5350), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            practiceState.mistakeLog.distinct().take(2).forEach { mistake ->
              Text(text = "• $mistake", color = TkdTextMuted, fontSize = 11.sp)
            }
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(TkdGold.copy(alpha = 0.15f))
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "+${(practiceState.score / 5).coerceAtLeast(60)} XP Earned · Streak Kept!",
              color = TkdGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { viewModel.startPracticeDrill(practiceState.currentTechnique) },
          colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson)
        ) {
          Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Practice Again")
        }
      },
      dismissButton = {
        Button(
          onClick = { viewModel.startSparring("Beginner Match") },
          colors = ButtonDefaults.buttonColors(containerColor = TkdBlue)
        ) {
          Icon(imageVector = Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Sparring Test")
        }
      }
    )
  }
}

@Composable
private fun PracticeActionButton(
  label: String,
  korean: String,
  color: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.18f)),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f)),
    shape = RoundedCornerShape(10.dp),
    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
    modifier = modifier.height(52.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = label, color = TkdTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Text(text = korean, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
  }
}

@Composable
private fun ScoreMeterRow(
  label: String,
  score: Int,
  color: Color
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, color = TkdTextSecondary, fontSize = 11.sp)
      Text(text = "$score%", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { score / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = TkdSurfaceVariant
    )
  }
}
