package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.BeltBlack
import com.example.ui.theme.TkdBlue
import com.example.ui.theme.TkdCrimson
import com.example.ui.theme.TkdGold
import kotlin.math.sin

enum class FighterAction {
  IDLE_STANCE,
  CHAMBER,
  FRONT_KICK,
  ROUNDHOUSE_KICK,
  SIDE_KICK,
  AXE_KICK,
  PUNCH,
  HIGH_BLOCK,
  LOW_BLOCK,
  HIT_REACTION
}

@Composable
fun AnimatedFighterCanvas(
  modifier: Modifier = Modifier,
  action: FighterAction = FighterAction.IDLE_STANCE,
  facingRight: Boolean = true,
  hoguColor: Color = TkdBlue, // Blue (Cheong) or Red (Hong)
  beltColor: Color = BeltBlack,
  showEnergyTrail: Boolean = true,
  actionProgress: Float = 1f // 0f to 1f for manual scrubber/animations
) {
  val infiniteTransition = rememberInfiniteTransition(label = "fighter_bounce")
  val bounceOffset by infiniteTransition.animateFloat(
    initialValue = -4f,
    targetValue = 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bounce"
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val canvasW = size.width
    val canvasH = size.height

    val centerX = canvasW / 2f
    val groundY = canvasH * 0.88f
    val baseFighterH = canvasH * 0.65f

    // Current bounce only applies during idle or soft stances
    val curBounce = if (action == FighterAction.IDLE_STANCE) bounceOffset else 0f
    val direction = if (facingRight) 1f else -1f

    drawFighter(
      centerX = centerX,
      groundY = groundY + curBounce,
      height = baseFighterH,
      direction = direction,
      action = action,
      progress = actionProgress,
      hoguColor = hoguColor,
      beltColor = beltColor,
      showEnergy = showEnergyTrail
    )
  }
}

private fun DrawScope.drawFighter(
  centerX: Float,
  groundY: Float,
  height: Float,
  direction: Float,
  action: FighterAction,
  progress: Float,
  hoguColor: Color,
  beltColor: Color,
  showEnergy: Boolean
) {
  val headRadius = height * 0.08f
  val hipY = groundY - height * 0.48f
  val chestY = groundY - height * 0.70f
  val headCenterY = groundY - height * 0.85f

  val dobokColor = Color.White
  val skinColor = Color(0xFFFFD1A4)
  val limbWidth = height * 0.045f

  // 1. Shadow on ground
  drawOval(
    color = Color.Black.copy(alpha = 0.35f),
    topLeft = Offset(centerX - height * 0.28f, groundY - height * 0.03f),
    size = Size(height * 0.56f, height * 0.07f)
  )

  // Calculate leg endpoints based on action
  val backFootX: Float
  val backFootY: Float
  val leadFootX: Float
  val leadFootY: Float
  val leadKneeX: Float
  val leadKneeY: Float

  when (action) {
    FighterAction.IDLE_STANCE -> {
      backFootX = centerX - direction * (height * 0.22f)
      backFootY = groundY
      leadFootX = centerX + direction * (height * 0.16f)
      leadFootY = groundY
      leadKneeX = centerX + direction * (height * 0.08f)
      leadKneeY = hipY + height * 0.22f
    }
    FighterAction.CHAMBER -> {
      backFootX = centerX - direction * (height * 0.05f)
      backFootY = groundY
      leadKneeX = centerX + direction * (height * 0.18f)
      leadKneeY = hipY - height * 0.08f // Chambered high to chest
      leadFootX = leadKneeX - direction * (height * 0.08f)
      leadFootY = leadKneeY + height * 0.14f
    }
    FighterAction.FRONT_KICK -> {
      backFootX = centerX - direction * (height * 0.10f)
      backFootY = groundY
      leadKneeX = centerX + direction * (height * 0.24f)
      leadKneeY = chestY + height * 0.05f
      leadFootX = centerX + direction * (height * 0.48f)
      leadFootY = chestY + height * 0.02f
    }
    FighterAction.ROUNDHOUSE_KICK -> {
      backFootX = centerX - direction * (height * 0.08f)
      backFootY = groundY
      leadKneeX = centerX + direction * (height * 0.26f)
      leadKneeY = chestY
      leadFootX = centerX + direction * (height * 0.52f)
      leadFootY = chestY - height * 0.05f
    }
    FighterAction.SIDE_KICK -> {
      backFootX = centerX - direction * (height * 0.12f)
      backFootY = groundY
      leadKneeX = centerX + direction * (height * 0.22f)
      leadKneeY = hipY
      leadFootX = centerX + direction * (height * 0.54f)
      leadFootY = hipY - height * 0.02f
    }
    FighterAction.AXE_KICK -> {
      backFootX = centerX - direction * (height * 0.06f)
      backFootY = groundY
      leadKneeX = centerX + direction * (height * 0.20f)
      leadKneeY = chestY - height * 0.22f
      leadFootX = centerX + direction * (height * 0.28f)
      leadFootY = chestY - height * 0.25f + (progress * height * 0.28f)
    }
    FighterAction.PUNCH -> {
      backFootX = centerX - direction * (height * 0.24f)
      backFootY = groundY
      leadFootX = centerX + direction * (height * 0.20f)
      leadFootY = groundY
      leadKneeX = centerX + direction * (height * 0.12f)
      leadKneeY = hipY + height * 0.20f
    }
    FighterAction.HIGH_BLOCK, FighterAction.LOW_BLOCK -> {
      backFootX = centerX - direction * (height * 0.18f)
      backFootY = groundY
      leadFootX = centerX + direction * (height * 0.15f)
      leadFootY = groundY
      leadKneeX = centerX + direction * (height * 0.08f)
      leadKneeY = hipY + height * 0.22f
    }
    FighterAction.HIT_REACTION -> {
      backFootX = centerX - direction * (height * 0.26f)
      backFootY = groundY
      leadFootX = centerX - direction * (height * 0.02f)
      leadFootY = groundY
      leadKneeX = centerX - direction * (height * 0.10f)
      leadKneeY = hipY + height * 0.22f
    }
  }

  // Draw Support Leg (Back leg)
  drawLine(
    color = dobokColor,
    start = Offset(centerX, hipY),
    end = Offset(backFootX + direction * (height * 0.05f), (hipY + groundY) / 2),
    strokeWidth = limbWidth,
    cap = StrokeCap.Round
  )
  drawLine(
    color = dobokColor,
    start = Offset(backFootX + direction * (height * 0.05f), (hipY + groundY) / 2),
    end = Offset(backFootX, backFootY),
    strokeWidth = limbWidth,
    cap = StrokeCap.Round
  )
  // Foot (barefoot / foot protector)
  drawLine(
    color = skinColor,
    start = Offset(backFootX, backFootY),
    end = Offset(backFootX + direction * (height * 0.07f), backFootY),
    strokeWidth = limbWidth * 0.9f,
    cap = StrokeCap.Round
  )

  // Draw Kicking / Lead Leg
  drawLine(
    color = dobokColor,
    start = Offset(centerX, hipY),
    end = Offset(leadKneeX, leadKneeY),
    strokeWidth = limbWidth,
    cap = StrokeCap.Round
  )
  drawLine(
    color = dobokColor,
    start = Offset(leadKneeX, leadKneeY),
    end = Offset(leadFootX, leadFootY),
    strokeWidth = limbWidth,
    cap = StrokeCap.Round
  )
  // Foot
  drawLine(
    color = skinColor,
    start = Offset(leadFootX, leadFootY),
    end = Offset(leadFootX + direction * (height * 0.06f), leadFootY - height * 0.01f),
    strokeWidth = limbWidth * 0.9f,
    cap = StrokeCap.Round
  )

  // Draw Torso (White Dobok + Colored Hogu)
  val torsoPath = Path().apply {
    moveTo(centerX - height * 0.09f, chestY)
    lineTo(centerX + height * 0.09f, chestY)
    lineTo(centerX + height * 0.07f, hipY)
    lineTo(centerX - height * 0.07f, hipY)
    close()
  }
  drawPath(torsoPath, color = dobokColor)

  // Hogu (Chest protector)
  val hoguPath = Path().apply {
    moveTo(centerX - height * 0.08f, chestY + height * 0.03f)
    lineTo(centerX + height * 0.08f, chestY + height * 0.03f)
    lineTo(centerX + height * 0.065f, hipY - height * 0.03f)
    lineTo(centerX - height * 0.065f, hipY - height * 0.03f)
    close()
  }
  drawPath(hoguPath, color = hoguColor)

  // Hogu Center Taegeuk emblem circle
  drawCircle(
    color = Color.White.copy(alpha = 0.85f),
    radius = height * 0.028f,
    center = Offset(centerX + direction * (height * 0.015f), (chestY + hipY) / 2)
  )

  // Belt tied at waist
  val beltY = hipY - height * 0.015f
  drawRect(
    color = beltColor,
    topLeft = Offset(centerX - height * 0.08f, beltY),
    size = Size(height * 0.16f, height * 0.03f)
  )
  // Belt knot tails hanging down
  drawLine(
    color = beltColor,
    start = Offset(centerX + direction * (height * 0.02f), beltY + height * 0.02f),
    end = Offset(centerX + direction * (height * 0.025f), beltY + height * 0.12f),
    strokeWidth = limbWidth * 0.5f,
    cap = StrokeCap.Round
  )

  // Arms and Hands
  val rearHandX: Float
  val rearHandY: Float
  val leadHandX: Float
  val leadHandY: Float

  when (action) {
    FighterAction.PUNCH -> {
      // Rear hand punches straight forward
      rearHandX = centerX + direction * (height * 0.38f)
      rearHandY = chestY + height * 0.02f
      leadHandX = centerX + direction * (height * 0.12f)
      leadHandY = chestY - height * 0.04f
    }
    FighterAction.HIGH_BLOCK -> {
      leadHandX = centerX + direction * (height * 0.12f)
      leadHandY = headCenterY - height * 0.08f // High above forehead
      rearHandX = centerX - direction * (height * 0.04f)
      rearHandY = chestY
    }
    FighterAction.LOW_BLOCK -> {
      leadHandX = centerX + direction * (height * 0.22f)
      leadHandY = hipY + height * 0.15f // Low over knee
      rearHandX = centerX - direction * (height * 0.04f)
      rearHandY = chestY
    }
    else -> {
      // Guard stance fists at chin and temple
      leadHandX = centerX + direction * (height * 0.16f)
      leadHandY = chestY - height * 0.06f
      rearHandX = centerX + direction * (height * 0.06f)
      rearHandY = chestY - height * 0.02f
    }
  }

  // Draw Rear Arm
  drawLine(
    color = dobokColor,
    start = Offset(centerX - direction * (height * 0.06f), chestY + height * 0.03f),
    end = Offset(rearHandX - direction * (height * 0.04f), (chestY + rearHandY) / 2),
    strokeWidth = limbWidth * 0.85f,
    cap = StrokeCap.Round
  )
  drawLine(
    color = dobokColor,
    start = Offset(rearHandX - direction * (height * 0.04f), (chestY + rearHandY) / 2),
    end = Offset(rearHandX, rearHandY),
    strokeWidth = limbWidth * 0.85f,
    cap = StrokeCap.Round
  )
  // Rear Fist
  drawCircle(
    color = skinColor,
    radius = limbWidth * 0.7f,
    center = Offset(rearHandX, rearHandY)
  )

  // Draw Lead Arm
  drawLine(
    color = dobokColor,
    start = Offset(centerX + direction * (height * 0.06f), chestY + height * 0.03f),
    end = Offset((centerX + leadHandX) / 2, (chestY + leadHandY) / 2 + height * 0.03f),
    strokeWidth = limbWidth * 0.85f,
    cap = StrokeCap.Round
  )
  drawLine(
    color = dobokColor,
    start = Offset((centerX + leadHandX) / 2, (chestY + leadHandY) / 2 + height * 0.03f),
    end = Offset(leadHandX, leadHandY),
    strokeWidth = limbWidth * 0.85f,
    cap = StrokeCap.Round
  )
  // Lead Fist
  drawCircle(
    color = skinColor,
    radius = limbWidth * 0.7f,
    center = Offset(leadHandX, leadHandY)
  )

  // Neck
  drawLine(
    color = skinColor,
    start = Offset(centerX, chestY),
    end = Offset(centerX, headCenterY + headRadius * 0.8f),
    strokeWidth = limbWidth * 0.8f,
    cap = StrokeCap.Round
  )

  // Head
  drawCircle(
    color = skinColor,
    radius = headRadius,
    center = Offset(centerX, headCenterY)
  )

  // Headgear (Matching Hogu color)
  val headgearPath = Path().apply {
    moveTo(centerX - headRadius * 1.05f, headCenterY)
    lineTo(centerX + headRadius * 1.05f, headCenterY)
    lineTo(centerX + headRadius * 0.9f, headCenterY - headRadius * 1.05f)
    lineTo(centerX - headRadius * 0.9f, headCenterY - headRadius * 1.05f)
    close()
  }
  drawPath(headgearPath, color = hoguColor)

  // Headgear side chin strap
  drawCircle(
    color = hoguColor,
    radius = headRadius * 0.95f,
    center = Offset(centerX, headCenterY),
    style = Stroke(width = height * 0.018f)
  )

  // Energy Swoosh / Impact Spark for kicks & punches
  if (showEnergy && (action == FighterAction.FRONT_KICK || action == FighterAction.ROUNDHOUSE_KICK || action == FighterAction.SIDE_KICK || action == FighterAction.AXE_KICK || action == FighterAction.PUNCH)) {
    val impactX = if (action == FighterAction.PUNCH) rearHandX else leadFootX
    val impactY = if (action == FighterAction.PUNCH) rearHandY else leadFootY

    drawCircle(
      color = TkdGold.copy(alpha = 0.8f),
      radius = height * 0.06f,
      center = Offset(impactX + direction * (height * 0.04f), impactY)
    )
    drawCircle(
      color = Color.White,
      radius = height * 0.03f,
      center = Offset(impactX + direction * (height * 0.04f), impactY)
    )
  }
}
