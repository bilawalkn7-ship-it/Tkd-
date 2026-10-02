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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.ChallengeQuest
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
fun ChallengesScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val quests by viewModel.quests.collectAsStateWithLifecycle()
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Daily, 1 = Weekly

  val displayedQuests = remember(quests, selectedTab) {
    quests.filter { it.isWeekly == (selectedTab == 1) }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("challenges_screen")
  ) {
    // Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(TkdSurface)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "도전과제 · Quests & Tenets",
        color = TkdGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Daily Discipline & Weekly Challenges",
        color = TkdTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(12.dp))

      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = TkdSurfaceVariant,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = TkdCrimson
          )
        },
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Daily Quests", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
          selectedContentColor = Color.White,
          unselectedContentColor = TkdTextMuted
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Weekly Challenges", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
          selectedContentColor = Color.White,
          unselectedContentColor = TkdTextMuted
        )
      }
    }

    // List of quests
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Taekwondo Tenets Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdGold.copy(alpha = 0.3f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = TkdGold, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "5 TENETS OF TAEKWONDO (태권도 5대 정신)", color = TkdGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "1. Courtesy (예의) · 2. Integrity (염치) · 3. Perseverance (인내)\n4. Self-Control (극기) · 5. Indomitable Spirit (백절불굴)",
              color = TkdTextSecondary,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          }
        }
      }

      items(displayedQuests) { quest ->
        QuestItemCard(
          quest = quest,
          onClaim = { viewModel.claimQuest(quest) }
        )
      }
    }
  }
}

@Composable
private fun QuestItemCard(
  quest: ChallengeQuest,
  onClaim: () -> Unit
) {
  val progress = (quest.currentProgress.toFloat() / quest.targetCount.toFloat()).coerceIn(0f, 1f)
  val isReadyToClaim = quest.currentProgress >= quest.targetCount && !quest.isCompleted

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = if (quest.isCompleted) TkdSurfaceVariant.copy(alpha = 0.5f) else TkdSurface
    ),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isReadyToClaim) TkdGold else TkdSurfaceBorder
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = quest.title, color = TkdTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(2.dp))
          Text(text = quest.description, color = TkdTextSecondary, fontSize = 12.sp)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TkdGold.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(text = "+${quest.rewardXp} XP", color = TkdGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Progress", color = TkdTextMuted, fontSize = 10.sp)
            Text(text = "${quest.currentProgress} / ${quest.targetCount}", color = TkdTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(4.dp))
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = if (isReadyToClaim || quest.isCompleted) TkdSuccess else TkdCrimson,
            trackColor = TkdSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (quest.isCompleted) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Completed", tint = TkdSuccess, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Claimed", color = TkdSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        } else if (isReadyToClaim) {
          Button(
            onClick = onClaim,
            colors = ButtonDefaults.buttonColors(containerColor = TkdGold),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Text(text = "Claim", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
