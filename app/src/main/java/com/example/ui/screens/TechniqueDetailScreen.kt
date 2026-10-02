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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Technique
import com.example.ui.components.AnimatedFighterCanvas
import com.example.ui.components.BeltBadge
import com.example.ui.components.FighterAction
import com.example.ui.theme.BeltBlack
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
import kotlinx.coroutines.delay

@Composable
fun TechniqueDetailScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val technique by viewModel.selectedTechnique.collectAsStateWithLifecycle()

  var isPlayingDemo by remember { mutableStateOf(true) }
  var demoProgress by remember { mutableFloatStateOf(0f) }

  // Cycle animation through steps
  LaunchedEffect(isPlayingDemo) {
    while (isPlayingDemo) {
      delay(40)
      demoProgress = (demoProgress + 0.02f) % 1.0f
    }
  }

  val currentAction = remember(demoProgress, technique) {
    val phase = demoProgress
    when {
      phase < 0.20f -> FighterAction.IDLE_STANCE
      phase < 0.45f -> FighterAction.CHAMBER
      phase < 0.75f -> when (technique.id) {
        "roundhouse-kick" -> FighterAction.ROUNDHOUSE_KICK
        "side-kick" -> FighterAction.SIDE_KICK
        "axe-kick" -> FighterAction.AXE_KICK
        "basic-punch" -> FighterAction.PUNCH
        "basic-blocks" -> FighterAction.HIGH_BLOCK
        else -> FighterAction.FRONT_KICK
      }
      phase < 0.90f -> FighterAction.CHAMBER
      else -> FighterAction.IDLE_STANCE
    }
  }

  val phaseDescription = remember(currentAction) {
    when (currentAction) {
      FighterAction.IDLE_STANCE -> "Phase 1: Fighting Stance (Gyeorugi Junbi) & Guard"
      FighterAction.CHAMBER -> "Phase 2: Knee Lift & Chambering (Power Prep)"
      FighterAction.FRONT_KICK -> "Phase 3: Snapping Extension · Strike with Ap Chuk"
      FighterAction.ROUNDHOUSE_KICK -> "Phase 3: Hip Turnover & Instep Whip Strike"
      FighterAction.SIDE_KICK -> "Phase 3: Linear Piston Thrust with Foot Blade"
      FighterAction.AXE_KICK -> "Phase 3: High Heel Descending Drop"
      FighterAction.PUNCH -> "Phase 3: Direct Knuckle Thrust & Forearm Pronation"
      FighterAction.HIGH_BLOCK -> "Phase 3: Upward Deflection Forearm Wall"
      else -> "Phase 4: Retraction & Recovery to Guard"
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("technique_detail_screen")
  ) {
    // Header Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(TkdSurface)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { viewModel.navigateTo(TkdScreen.LEARN) }) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back to Curriculum",
          tint = TkdTextPrimary
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = technique.name, color = TkdTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = technique.hangul, color = TkdGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = "${technique.koreanName} · ${technique.category.displayName}", color = TkdTextSecondary, fontSize = 11.sp)
      }
      BeltBadge(belt = technique.beltRequired, showHangul = false)
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Interactive Demonstration Box
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "ANIMATED TECHNIQUE DEMO", color = TkdTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { isPlayingDemo = !isPlayingDemo },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = if (isPlayingDemo) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause Demo",
                    tint = TkdGold
                  )
                }
              }
            }

            // Canvas character box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TkdSurfaceVariant.copy(alpha = 0.5f)),
              contentAlignment = Alignment.Center
            ) {
              AnimatedFighterCanvas(
                action = currentAction,
                facingRight = true,
                hoguColor = TkdBlue,
                beltColor = BeltBlack,
                actionProgress = demoProgress
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Phase Scrubber
            Slider(
              value = demoProgress,
              onValueChange = {
                isPlayingDemo = false
                demoProgress = it
              },
              colors = SliderDefaults.colors(
                thumbColor = TkdCrimson,
                activeTrackColor = TkdCrimson,
                inactiveTrackColor = TkdSurfaceBorder
              ),
              modifier = Modifier.fillMaxWidth()
            )

            // Current phase label
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TkdSurfaceVariant)
                .padding(vertical = 6.dp, horizontal = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = phaseDescription,
                color = TkdGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      // 2. Action CTA Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = { viewModel.startPracticeDrill(technique) },
            colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1.2f)
              .height(46.dp)
          ) {
            Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Start Practice Drill", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              viewModel.setPoseTechnique(technique)
              viewModel.navigateTo(TkdScreen.POSE_SIMULATOR)
            },
            colors = ButtonDefaults.buttonColors(containerColor = TkdBlue.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
          ) {
            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = TkdBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Pose Alignment", color = TkdBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 3. Purpose & Target Area
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "TACTICAL OBJECTIVE & TARGET", color = TkdTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = technique.purpose, color = TkdTextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TkdCrimson.copy(alpha = 0.15f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(text = "Target: ${technique.targetArea}", color = TkdCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TkdGold.copy(alpha = 0.15f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(text = "+${technique.xpReward} XP Reward", color = TkdGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 4. Step-by-Step Breakdown
      item {
        Text(
          text = "EXECUTION CHECKPOINTS",
          color = TkdTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      items(technique.steps) { step ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(TkdCrimson),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${step.stepNumber}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(text = step.title, color = TkdTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(4.dp))
              Text(text = step.instruction, color = TkdTextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
              Spacer(modifier = Modifier.height(6.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(TkdSurfaceVariant)
                  .padding(6.dp)
              ) {
                Text(text = "Key: ${step.keyPoint}", color = TkdGold, fontSize = 11.sp)
              }
            }
          }
        }
      }

      // 5. Common Mistakes
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.3f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "COMMON MISTAKES TO AVOID", color = Color(0xFFEF5350), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            technique.commonMistakes.forEach { mistake ->
              Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                Text(text = "•", color = Color(0xFFEF5350), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = mistake, color = TkdTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
              }
            }
          }
        }
      }

      // 6. Safety Tips
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdGold.copy(alpha = 0.3f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = TkdGold, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "SAFETY & WARM-UP ADVICE", color = TkdGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            technique.safetyTips.forEach { tip ->
              Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                Text(text = "✓", color = TkdGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = tip, color = TkdTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
              }
            }
          }
        }
      }

      // 7. Ask AI Coach about this technique
      item {
        Button(
          onClick = {
            viewModel.askAiCoach("How can I master ${technique.name} (${technique.koreanName}) effectively and avoid common mistakes?")
            viewModel.navigateTo(TkdScreen.PROFILE)
          },
          colors = ButtonDefaults.buttonColors(containerColor = TkdSurfaceVariant),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
        ) {
          Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = TkdBlue, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Ask AI Coach About ${technique.name}", color = TkdTextPrimary, fontSize = 12.sp)
        }
      }
    }
  }
}
