package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
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
import com.example.ui.viewmodel.TkdViewModel

@Composable
fun ProfileScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  val coachMsg by viewModel.coachMessage.collectAsStateWithLifecycle()
  val isCoachLoading by viewModel.isCoachLoading.collectAsStateWithLifecycle()

  val currentProf = profile ?: return

  var isEditingName by remember { mutableStateOf(false) }
  var editedName by remember(currentProf.name) { mutableStateOf(currentProf.name) }

  var customQuestion by remember { mutableStateOf("") }
  var isAudioMuted by remember { mutableStateOf(viewModel.soundManager.isAudioMuted()) }
  var showResetConfirm by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("profile_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Profile Header Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TkdSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .border(2.dp, TkdCrimson, CircleShape)
          ) {
            Image(
              painter = painterResource(R.drawable.tkd_fighter_avatar_1790968416564),
              contentDescription = "Player Profile Avatar",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (isEditingName) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier.width(180.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = TkdCrimson,
                  unfocusedBorderColor = TkdSurfaceBorder
                )
              )
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = {
                  viewModel.updatePlayerName(editedName)
                  isEditingName = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson)
              ) {
                Text("Save")
              }
            }
          } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = currentProf.name,
                color = TkdTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              IconButton(onClick = { isEditingName = true }, modifier = Modifier.size(28.dp)) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Name", tint = TkdTextMuted, modifier = Modifier.size(16.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          BeltBadge(belt = currentProf.currentBelt)
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "Total XP: ${currentProf.currentXp} · Streak: ${currentProf.streakDays} Days", color = TkdGold, fontSize = 12.sp)
        }
      }
    }

    // 2. Ask AI Coach Consultation
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TkdSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TkdBlue.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = TkdBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(text = "Master Kwan (AI Taekwondo Assistant)", color = TkdTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text(text = "Educational martial arts consultation", color = TkdTextMuted, fontSize = 10.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Coach Answer Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(TkdSurfaceVariant)
              .padding(12.dp)
          ) {
            if (isCoachLoading) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TkdBlue, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Consulting Master Kwan...", color = TkdTextSecondary, fontSize = 12.sp)
              }
            } else {
              Text(
                text = coachMsg,
                color = TkdTextPrimary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Preset Questions
          Text(text = "Quick Inquiries:", color = TkdTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))

          val presets = listOf(
            "How do I add power to my roundhouse kick?",
            "How do I prevent dropping guard after kicks?",
            "What are the 5 tenets of Taekwondo?"
          )
          presets.forEach { preset ->
            Button(
              onClick = { viewModel.askAiCoach(preset) },
              colors = ButtonDefaults.buttonColors(containerColor = TkdSurfaceVariant.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
            ) {
              Text(text = "❓ $preset", color = TkdTextSecondary, fontSize = 11.sp, maxLines = 1)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Custom question input
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = customQuestion,
              onValueChange = { customQuestion = it },
              placeholder = { Text("Ask any Taekwondo question...", fontSize = 12.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TkdBlue,
                unfocusedBorderColor = TkdSurfaceBorder
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
              onClick = {
                if (customQuestion.isNotBlank()) {
                  viewModel.askAiCoach(customQuestion)
                  customQuestion = ""
                }
              },
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(TkdBlue)
            ) {
              Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }

    // 3. Audio & App Settings
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TkdSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "APP SETTINGS & AUDIO", color = TkdTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = TkdGold, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "Sound Effects & Kihap Audio", color = TkdTextPrimary, fontSize = 13.sp)
            }
            Switch(
              checked = !isAudioMuted,
              onCheckedChange = {
                isAudioMuted = viewModel.soundManager.toggleMute()
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = TkdGold,
                checkedTrackColor = TkdGold.copy(alpha = 0.3f)
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { showResetConfirm = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350).copy(alpha = 0.15f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Reset Training Progress", color = Color(0xFFEF5350), fontSize = 12.sp)
          }
        }
      }
    }

    // 4. Responsible Learning & Safety Disclaimer
    item {
      SafetyDisclaimerCard()
    }
  }

  if (showResetConfirm) {
    AlertDialog(
      onDismissRequest = { showResetConfirm = false },
      containerColor = TkdSurface,
      shape = RoundedCornerShape(14.dp),
      title = { Text(text = "Reset All Training Data?", color = TkdTextPrimary, fontWeight = FontWeight.Bold) },
      text = { Text(text = "This will reset your belt, XP, technique records, and sparring stats back to White Belt beginner status.", color = TkdTextSecondary) },
      confirmButton = {
        Button(
          onClick = {
            viewModel.resetAllProgress()
            showResetConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson)
        ) {
          Text("Reset")
        }
      },
      dismissButton = {
        Button(
          onClick = { showResetConfirm = false },
          colors = ButtonDefaults.buttonColors(containerColor = TkdSurfaceVariant)
        ) {
          Text("Cancel", color = TkdTextPrimary)
        }
      }
    )
  }
}
