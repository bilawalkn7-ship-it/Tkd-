package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TkdGold
import com.example.ui.theme.TkdSurfaceVariant
import com.example.ui.theme.TkdTextMuted
import com.example.ui.theme.TkdTextPrimary

@Composable
fun SafetyDisclaimerCard(
  modifier: Modifier = Modifier,
  compact: Boolean = false
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(TkdSurfaceVariant.copy(alpha = 0.85f))
      .border(1.dp, TkdGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
      .padding(if (compact) 10.dp else 14.dp)
  ) {
    Row(
      verticalAlignment = Alignment.Top,
      modifier = Modifier.fillMaxWidth()
    ) {
      Icon(
        imageVector = Icons.Default.Security,
        contentDescription = "Safety Disclaimer",
        tint = TkdGold,
        modifier = Modifier.size(if (compact) 20.dp else 24.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "Martial Arts Safety & Educational Disclaimer",
          color = TkdGold,
          fontSize = if (compact) 12.sp else 13.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "This application is an educational game and training aid. It does not replace instruction from a qualified Taekwondo instructor. Practice physical techniques safely in a clear space and follow appropriate supervision. Focus on sport, fitness, discipline, and self-control. In-game belts are virtual achievements only.",
          color = TkdTextPrimary.copy(alpha = 0.85f),
          fontSize = if (compact) 11.sp else 12.sp,
          lineHeight = if (compact) 15.sp else 17.sp
        )
      }
    }
  }
}
