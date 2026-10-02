package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SafetyDisclaimerCard
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
fun PoseSimulatorScreen(
  viewModel: TkdViewModel,
  modifier: Modifier = Modifier
) {
  val poseState by viewModel.poseState.collectAsStateWithLifecycle()
  var showCameraGrid by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("pose_simulator_screen")
  ) {
    // Header
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
          contentDescription = "Back",
          tint = TkdTextPrimary
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = "Pose Alignment Analyzer (AI)",
          color = TkdTextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Biomechanical Stance & Angle Evaluation",
          color = TkdGold,
          fontSize = 11.sp
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Mandatory Medical / Educational Disclaimer
      item {
        SafetyDisclaimerCard(compact = true)
      }

      // 2. Pose Skeleton Viewport
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdBlue.copy(alpha = 0.4f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = TkdBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Pose Skeleton: ${poseState.selectedTechnique.name}",
                  color = TkdTextPrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              // Mode toggle (Camera Grid overlay)
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = TkdTextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Guide Grid", color = TkdTextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                  checked = showCameraGrid,
                  onCheckedChange = { showCameraGrid = it },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = TkdBlue,
                    checkedTrackColor = TkdBlue.copy(alpha = 0.3f)
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Canvas Drawing Skeleton Joints
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (showCameraGrid) Color(0xFF101924) else TkdSurfaceVariant.copy(alpha = 0.6f)),
              contentAlignment = Alignment.Center
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Camera grid lines if enabled
                if (showCameraGrid) {
                  drawLine(Color.White.copy(alpha = 0.1f), Offset(w * 0.33f, 0f), Offset(w * 0.33f, h), 1f)
                  drawLine(Color.White.copy(alpha = 0.1f), Offset(w * 0.66f, 0f), Offset(w * 0.66f, h), 1f)
                  drawLine(Color.White.copy(alpha = 0.1f), Offset(0f, h * 0.33f), Offset(w, h * 0.33f), 1f)
                  drawLine(Color.White.copy(alpha = 0.1f), Offset(0f, h * 0.66f), Offset(w, h * 0.66f), 1f)
                }

                val centerX = w * 0.45f
                val headPos = Offset(centerX, h * 0.22f)
                val neckPos = Offset(centerX, h * 0.32f)
                val spinePos = Offset(centerX, h * 0.52f)

                // Hands / Guard position based on guardUp
                val leftHand = if (poseState.guardUp) Offset(centerX + 35f, h * 0.28f) else Offset(centerX + 35f, h * 0.48f)
                val rightHand = if (poseState.guardUp) Offset(centerX + 15f, h * 0.32f) else Offset(centerX + 15f, h * 0.52f)

                // Back supporting leg
                val backHip = Offset(centerX - 20f, h * 0.52f)
                val backKnee = Offset(centerX - 35f, h * 0.70f)
                val backFoot = Offset(centerX - 45f, h * 0.88f)

                // Kicking leg angle from kneeChamberAngle
                val chamberNorm = (poseState.kneeChamberAngle / 110f).coerceIn(0.2f, 1f)
                val kickKnee = Offset(centerX + (chamberNorm * 65f), h * 0.52f - (chamberNorm * 35f))
                val kickFoot = Offset(kickKnee.x + 60f, kickKnee.y - 10f)

                // Joint colors: green if aligned, amber if suboptimal
                val guardColor = if (poseState.guardUp) TkdSuccess else TkdWarning
                val kneeColor = if (poseState.kneeChamberAngle >= 75f) TkdSuccess else TkdWarning
                val footColor = if (poseState.supportingFootPivot >= 45f) TkdSuccess else TkdWarning

                // Bones
                drawLine(Color.White, neckPos, headPos, 4f, StrokeCap.Round)
                drawLine(Color.White, neckPos, spinePos, 5f, StrokeCap.Round)

                // Arms
                drawLine(guardColor, neckPos, leftHand, 4f, StrokeCap.Round)
                drawLine(guardColor, neckPos, rightHand, 4f, StrokeCap.Round)

                // Supporting Leg
                drawLine(Color.White, spinePos, backHip, 4f, StrokeCap.Round)
                drawLine(footColor, backHip, backKnee, 4f, StrokeCap.Round)
                drawLine(footColor, backKnee, backFoot, 4f, StrokeCap.Round)

                // Kicking Leg
                drawLine(kneeColor, spinePos, kickKnee, 5f, StrokeCap.Round)
                drawLine(kneeColor, kickKnee, kickFoot, 5f, StrokeCap.Round)

                // Joints
                drawCircle(Color.White, 16f, headPos)
                drawCircle(guardColor, 7f, leftHand)
                drawCircle(guardColor, 7f, rightHand)
                drawCircle(footColor, 7f, backFoot)
                drawCircle(kneeColor, 8f, kickKnee)
                drawCircle(kneeColor, 8f, kickFoot)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-time Feedback Status
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TkdSurfaceVariant)
                .padding(10.dp)
            ) {
              Text(
                text = poseState.statusNote,
                color = TkdTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      // 3. Biomechanical Adjusters (Sliders)
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TkdSurface),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TkdSurfaceBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "BIOMECHANICAL ALIGNMENT CONTROLS",
              color = TkdTextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Knee Chamber Angle Slider
            Text(
              text = "Knee Chamber Lift: ${poseState.kneeChamberAngle.toInt()}° (Target: 80° - 100°)",
              color = TkdTextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Slider(
              value = poseState.kneeChamberAngle,
              onValueChange = { viewModel.updatePoseJoint(it, "knee") },
              valueRange = 30f..110f,
              colors = SliderDefaults.colors(thumbColor = TkdCrimson, activeTrackColor = TkdCrimson)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Supporting Foot Pivot Angle
            Text(
              text = "Supporting Foot Pivot: ${poseState.supportingFootPivot.toInt()}° (Target: 90° for hip rotation)",
              color = TkdTextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Slider(
              value = poseState.supportingFootPivot,
              onValueChange = { viewModel.updatePoseJoint(it, "foot") },
              valueRange = 0f..180f,
              colors = SliderDefaults.colors(thumbColor = TkdBlue, activeTrackColor = TkdBlue)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Guard Position Toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Guard Hands at Chin Level:",
                color = TkdTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
              Switch(
                checked = poseState.guardUp,
                onCheckedChange = { viewModel.updatePoseJoint(if (it) 80f else 20f, "guard") },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = TkdSuccess,
                  checkedTrackColor = TkdSuccess.copy(alpha = 0.3f)
                )
              )
            }
          }
        }
      }

      // 4. Test in Interactive Practice Button
      item {
        Button(
          onClick = { viewModel.startPracticeDrill(poseState.selectedTechnique) },
          colors = ButtonDefaults.buttonColors(containerColor = TkdCrimson),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
        ) {
          Text(text = "Test Alignment in Practice Drill", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
