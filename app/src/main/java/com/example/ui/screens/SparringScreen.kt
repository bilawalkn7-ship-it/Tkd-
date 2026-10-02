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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.SafetyDisclaimerCard
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
import com.example.ui.viewmodel.TkdScreen
import com.example.ui.viewmodel.TkdViewModel

@Composable
fun SparringScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val sparringState by viewModel.sparringState.collectAsStateWithLifecycle()
  var selectedDifficulty by remember { mutableStateOf("Beginner Match") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("sparring_screen")
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(TkdSurface)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { viewModel.navigateTo(TkdScreen.DASHBOARD) }) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Exit Sparring",
          tint = TkdTextPrimary
        )
      }
      Spacer(modifier = Modifier.width(4.dp))
      Column {
        Text(
          text = "Controlled Sparring (Gyeorugi)",
          color = TkdTextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Olympic-Inspired Electronic Scoring Simulation",
          color = TkdGold,
          fontSize = 11.sp
        )
      }
    }

    if (!sparringState.isActive && !sparringState.isGameOver) {
      // Match Lobby & Mode Selection
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdCrimson.copy(alpha = 0.4f))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "SELECT SPARRING OPPONENT & LEVEL",
              color = TkdTextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            val modes = listOf("Training Sparring", "Beginner Match", "Intermediate Match", "Advanced Match")
            modes.forEach { mode ->
              val isSelected = selectedDifficulty == mode
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) TkdCrimson.copy(alpha = 0.2f) else TkdSurfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) TkdCrimson else TkdSurfaceBorder
                )
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text(text = mode, color = TkdTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                      text = when (mode) {
                        "Training Sparring" -> "Slow attacks · AI cues player to counter"
                        "Beginner Match" -> "Predictable yellow belt AI partner"
                        "Intermediate Match" -> "Blocks, counters, and tactical footwork"
                        else -> "High-speed combos, feints, and angle trapping"
                      },
                      color = TkdTextMuted,
                      fontSize = 11.sp
                    )
                  }
                  Button(
                    onClick = {
                      selectedDifficulty = mode
                      viewModel.startSparring(mode)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                  ) {
                    Text(text = "Fight", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        // Scoring rules note
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "OLYMPIC POINT SCORING SYSTEM", color = TkdGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "• Punch to trunk Hogu: 1 Point\n• Basic kick to trunk Hogu (Front/Roundhouse): 2 Points\n• Turning / Head kick (Axe): 3 Points\n• Dropping guard causes counter damage and penalty", color = TkdTextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
          }
        }

        SafetyDisclaimerCard(compact = true)
      }
    } else {
      // ACTIVE SPARRING MATCH SCREEN
      Column(modifier = Modifier.fillMaxSize()) {
        // Electronic Scoreboard
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF10121A)),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdGold.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            // Round & Timer
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ROUND ${sparringState.roundNumber}/${sparringState.maxRounds}",
                color = TkdGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${sparringState.roundSecondsRemaining}s",
                color = if (sparringState.roundSecondsRemaining <= 10) TkdCrimson else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = sparringState.difficulty,
                color = TkdTextMuted,
                fontSize = 11.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Blue (Player) vs Red (Opponent) Score Display
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Player Blue
              Column(horizontalAlignment = Alignment.Start) {
                Text(text = "BLUE (청)", color = TkdBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "${sparringState.playerScore}", color = TkdBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Stamina", color = TkdTextMuted, fontSize = 9.sp)
                LinearProgressIndicator(
                  progress = { sparringState.playerStamina },
                  modifier = Modifier
                    .width(90.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = TkdBlue,
                  trackColor = TkdSurfaceVariant
                )
              }

              // VS center
              Text(text = "VS", color = TkdTextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)

              // Opponent Red
              Column(horizontalAlignment = Alignment.End) {
                Text(text = "RED (홍)", color = TkdCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "${sparringState.opponentScore}", color = TkdCrimson, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Stamina", color = TkdTextMuted, fontSize = 9.sp)
                LinearProgressIndicator(
                  progress = { sparringState.opponentStamina },
                  modifier = Modifier
                    .width(90.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = TkdCrimson,
                  trackColor = TkdSurfaceVariant
                )
              }
            }
          }
        }

        // Live Dojang Canvas Arena with Two Fighters
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(
              Brush.verticalGradient(
                colors = listOf(Color(0xFF141624), Color(0xFF1F2236), Color(0xFF0F1017))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Player Fighter (Blue)
            AnimatedFighterCanvas(
              modifier = Modifier
                .width(130.dp)
                .height(220.dp),
              action = sparringState.playerAction,
              facingRight = true,
              hoguColor = TkdBlue,
              beltColor = BeltBlack
            )

            // AI Opponent Fighter (Red)
            AnimatedFighterCanvas(
              modifier = Modifier
                .width(130.dp)
                .height(220.dp),
              action = sparringState.opponentAction,
              facingRight = false,
              hoguColor = TkdCrimson,
              beltColor = BeltBlack
            )
          }

          // Live Combat Log
          Box(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .fillMaxWidth()
              .padding(12.dp)
          ) {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = TkdSurface.copy(alpha = 0.9f)),
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
            ) {
              Text(
                text = sparringState.lastCombatLog,
                color = TkdTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              )
            }
          }
        }

        // Sparring Control Panel
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(TkdSurface)
            .padding(12.dp)
        ) {
          // Footwork Movement Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { viewModel.stepDistance(forward = true) },
              colors = ButtonDefaults.buttonColors(containerColor = TkdSurfaceVariant),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
            ) {
              Text(text = "Slide Step In ⏩", color = TkdTextPrimary, fontSize = 11.sp)
            }
            Button(
              onClick = { viewModel.stepDistance(forward = false) },
              colors = ButtonDefaults.buttonColors(containerColor = TkdSurfaceVariant),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
            ) {
              Text(text = "Slide Step Out ⏪", color = TkdTextPrimary, fontSize = 11.sp)
            }
            Button(
              onClick = { viewModel.playerSparringAction(FighterAction.HIGH_BLOCK) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
            ) {
              Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "Guard", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Attack Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { viewModel.playerSparringAction(FighterAction.ROUNDHOUSE_KICK) },
              colors = ButtonDefaults.buttonColors(containerColor = TkdBlue),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
            ) {
              Text(text = "Roundhouse (2pt)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
              onClick = { viewModel.playerSparringAction(FighterAction.AXE_KICK) },
              colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
            ) {
              Text(text = "Axe Kick (3pt)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
              onClick = { viewModel.playerSparringAction(FighterAction.PUNCH) },
              colors = ButtonDefaults.buttonColors(containerColor = TkdGold),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(0.8f)
                .height(44.dp)
            ) {
              Text(text = "Punch (1pt)", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Post-Match Educational Feedback Modal
  if (sparringState.isGameOver) {
    val isWin = sparringState.playerScore > sparringState.opponentScore
    AlertDialog(
      onDismissRequest = { },
      containerColor = TkdSurface,
      shape = RoundedCornerShape(16.dp),
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isWin) Icons.Default.EmojiEvents else Icons.Default.Info,
            contentDescription = null,
            tint = if (isWin) TkdGold else TkdBlue,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isWin) "Match Victory!" else "Match Completed",
            color = TkdTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Final Score: Blue ${sparringState.playerScore} - Red ${sparringState.opponentScore}",
            color = if (isWin) TkdSuccess else TkdTextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Clean Strikes Landed: ${sparringState.cleanAttacks} | Successful Blocks: ${sparringState.blocksCount}",
            color = TkdTextSecondary,
            fontSize = 12.sp
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(TkdSurfaceVariant)
              .padding(10.dp)
          ) {
            Text(
              text = "Coach Analysis: ${sparringState.postMatchFeedback}",
              color = TkdGold,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { viewModel.startSparring(sparringState.difficulty) },
          colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson)
        ) {
          Text("Rematch")
        }
      },
      dismissButton = {
        Button(
          onClick = { viewModel.navigateTo(TkdScreen.DASHBOARD) },
          colors = ButtonDefaults.buttonColors(containerColor = TkdSurfaceVariant)
        ) {
          Text("Return to Dojang", color = TkdTextPrimary)
        }
      }
    )
  }
}
