package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BeltLevel
import com.example.data.model.DifficultyLevel
import com.example.data.model.Technique
import com.example.data.model.TechniqueCategory
import com.example.data.model.TechniqueCurriculum
import com.example.ui.components.BeltBadge
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
fun LearnScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  val playerBelt = profile?.currentBelt ?: BeltLevel.WHITE

  var selectedCategory by remember { mutableStateOf<TechniqueCategory?>(null) }
  var selectedDifficultyFilter by remember { mutableStateOf("All") }

  val filteredTechniques = remember(selectedCategory, selectedDifficultyFilter) {
    TechniqueCurriculum.allTechniques.filter { tech ->
      val matchesCategory = selectedCategory == null || tech.category == selectedCategory
      val matchesDiff = when (selectedDifficultyFilter) {
        "Beginner" -> tech.difficulty == DifficultyLevel.BEGINNER
        "Basic" -> tech.difficulty == DifficultyLevel.BASIC
        "Intermediate" -> tech.difficulty == DifficultyLevel.INTERMEDIATE
        "Advanced" -> tech.difficulty == DifficultyLevel.ADVANCED || tech.difficulty == DifficultyLevel.MASTER
        else -> true
      }
      matchesCategory && matchesDiff
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("learn_screen")
  ) {
    // Top Curriculum Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(TkdSurface)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "태권도 교육과정 · Curriculum",
        color = TkdGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Progressive Martial Arts Academy",
        color = TkdTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))

      // Difficulty Tabs (Beginner -> Basic -> Intermediate -> Advanced)
      val tabs = listOf("All", "Beginner", "Basic", "Intermediate", "Advanced")
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tabs) { tab ->
          val isSelected = selectedDifficultyFilter == tab
          FilterChip(
            selected = isSelected,
            onClick = { selectedDifficultyFilter = tab },
            label = { Text(text = tab, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TkdCrimson,
              selectedLabelColor = TkdTextPrimary,
              containerColor = TkdSurfaceVariant,
              labelColor = TkdTextSecondary
            ),
            shape = RoundedCornerShape(8.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Category filter chips
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
          FilterChip(
            selected = selectedCategory == null,
            onClick = { selectedCategory = null },
            label = { Text("All Categories", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TkdBlue,
              selectedLabelColor = TkdTextPrimary,
              containerColor = TkdSurfaceVariant.copy(alpha = 0.6f),
              labelColor = TkdTextMuted
            ),
            shape = RoundedCornerShape(6.dp)
          )
        }
        items(TechniqueCategory.values()) { cat ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = if (isSelected) null else cat },
            label = { Text("${cat.icon} ${cat.displayName}", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TkdBlue,
              selectedLabelColor = TkdTextPrimary,
              containerColor = TkdSurfaceVariant.copy(alpha = 0.6f),
              labelColor = TkdTextMuted
            ),
            shape = RoundedCornerShape(6.dp)
          )
        }
      }
    }

    // Technique List
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredTechniques) { technique ->
        val isBeltLocked = technique.beltRequired.ordinal > playerBelt.ordinal

        TechniqueItemCard(
          technique = technique,
          isLocked = isBeltLocked,
          onLearnClick = { viewModel.openTechniqueDetail(technique) },
          onPracticeClick = { viewModel.startPracticeDrill(technique) }
        )
      }
    }
  }
}

@Composable
private fun TechniqueItemCard(
  technique: Technique,
  isLocked: Boolean,
  onLearnClick: () -> Unit,
  onPracticeClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onLearnClick),
    colors = CardDefaults.cardColors(containerColor = TkdSurface),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isLocked) TkdSurfaceBorder else TkdSurfaceBorder.copy(alpha = 0.8f)
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isLocked) TkdSurfaceVariant else TkdCrimson.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            if (isLocked) {
              Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = TkdTextMuted, modifier = Modifier.size(18.dp))
            } else {
              Text(text = technique.category.icon, fontSize = 18.sp)
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = technique.name,
                color = TkdTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = technique.hangul,
                color = TkdGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
            Text(
              text = "${technique.koreanName} · ${technique.category.displayName}",
              color = TkdTextSecondary,
              fontSize = 12.sp
            )
          }
        }

        // Difficulty stars
        Row {
          repeat(technique.difficulty.stars) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = TkdGold,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = technique.purpose,
        color = TkdTextMuted,
        fontSize = 12.sp,
        maxLines = 2,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        BeltBadge(belt = technique.beltRequired, showHangul = false)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = onPracticeClick,
            colors = ButtonDefaults.buttonColors(containerColor = TkdBlue.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = TkdBlue, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Practice", color = TkdBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onLearnClick,
            colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text(text = "Learn Steps", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          }
        }
      }
    }
  }
}
