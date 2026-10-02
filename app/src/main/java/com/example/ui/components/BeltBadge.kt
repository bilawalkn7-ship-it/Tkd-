package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.BeltLevel
import com.example.ui.theme.BeltBlack
import com.example.ui.theme.BeltWhite

@Composable
fun BeltBadge(
  belt: BeltLevel,
  modifier: Modifier = Modifier,
  showHangul: Boolean = true
) {
  val textColor = if (belt == BeltLevel.WHITE || belt == BeltLevel.YELLOW) BeltBlack else BeltWhite

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(belt.beltColor)
      .border(
        width = 1.dp,
        color = if (belt == BeltLevel.WHITE) Color.LightGray else Color.Black.copy(alpha = 0.3f),
        shape = RoundedCornerShape(6.dp)
      )
      .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Traditional belt stripe/knot accent
      Box(
        modifier = Modifier
          .size(width = 4.dp, height = 12.dp)
          .background(if (belt == BeltLevel.BLACK) Color(0xFFFFD700) else Color.Black.copy(alpha = 0.4f))
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = if (showHangul) "${belt.hangul} · ${belt.rankTitle}" else belt.rankTitle,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
