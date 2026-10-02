package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsKabaddi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BeltBadge
import com.example.ui.screens.ChallengesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.PoseSimulatorScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SparringScreen
import com.example.ui.screens.TechniqueDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TkdBlue
import com.example.ui.theme.TkdCrimson
import com.example.ui.theme.TkdGold
import com.example.ui.theme.TkdSurface
import com.example.ui.theme.TkdSurfaceBorder
import com.example.ui.theme.TkdTextMuted
import com.example.ui.theme.TkdTextPrimary
import com.example.ui.viewmodel.TkdScreen
import com.example.ui.viewmodel.TkdViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        TaekwondoApp()
      }
    }
  }
}

data class NavItem(
  val screen: TkdScreen,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaekwondoApp(
  viewModel: TkdViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val profile by viewModel.profile.collectAsStateWithLifecycle()

  var isMuted by remember { mutableStateOf(viewModel.soundManager.isAudioMuted()) }

  // Handle hardware / gesture back button
  BackHandler(enabled = currentScreen != TkdScreen.DASHBOARD) {
    when (currentScreen) {
      TkdScreen.TECHNIQUE_DETAIL -> viewModel.navigateTo(TkdScreen.LEARN)
      TkdScreen.PRACTICE -> viewModel.navigateTo(TkdScreen.LEARN)
      TkdScreen.POSE_SIMULATOR -> viewModel.navigateTo(TkdScreen.LEARN)
      else -> viewModel.navigateTo(TkdScreen.DASHBOARD)
    }
  }

  val navItems = listOf(
    NavItem(TkdScreen.DASHBOARD, "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    NavItem(TkdScreen.LEARN, "Learn", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook, "nav_learn"),
    NavItem(TkdScreen.PRACTICE, "Practice", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "nav_practice"),
    NavItem(TkdScreen.SPARRING, "Sparring", Icons.Filled.SportsKabaddi, Icons.Outlined.SportsKabaddi, "nav_sparring"),
    NavItem(TkdScreen.CHALLENGES, "Quests", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_challenges"),
    NavItem(TkdScreen.PROGRESS, "Progress", Icons.Filled.Assessment, Icons.Outlined.Assessment, "nav_progress"),
    NavItem(TkdScreen.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
  )

  Scaffold(
    contentWindowInsets = WindowInsets.statusBars,
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Taegeuk circle logo
            Box(
              modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(TkdCrimson)
                .border(1.dp, TkdGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🥋", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "TKD MASTER",
                color = TkdTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
              Text(
                text = "태권도 아카데미 · Academy & Simulator",
                color = TkdGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        },
        actions = {
          profile?.let { prof ->
            BeltBadge(belt = prof.currentBelt, showHangul = false)
            Spacer(modifier = Modifier.width(6.dp))
          }
          IconButton(onClick = {
            isMuted = viewModel.soundManager.toggleMute()
          }) {
            Icon(
              imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Toggle Audio",
              tint = if (isMuted) TkdTextMuted else TkdGold,
              modifier = Modifier.size(20.dp)
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = TkdSurface,
          titleContentColor = TkdTextPrimary
        )
      )
    },
    bottomBar = {
      // Show bottom navigation bar on primary screens
      if (currentScreen != TkdScreen.TECHNIQUE_DETAIL) {
        NavigationBar(
          containerColor = TkdSurface,
          windowInsets = WindowInsets.navigationBars,
          modifier = Modifier.height(64.dp)
        ) {
          navItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
              selected = isSelected,
              onClick = { viewModel.navigateTo(item.screen) },
              icon = {
                Icon(
                  imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                  contentDescription = item.label,
                  modifier = Modifier.size(20.dp)
                )
              },
              label = {
                Text(
                  text = item.label,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TkdCrimson,
                selectedTextColor = TkdCrimson,
                unselectedIconColor = TkdTextMuted,
                unselectedTextColor = TkdTextMuted,
                indicatorColor = TkdCrimson.copy(alpha = 0.15f)
              ),
              modifier = Modifier.testTag(item.testTag)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        TkdScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
        TkdScreen.LEARN -> LearnScreen(viewModel = viewModel)
        TkdScreen.TECHNIQUE_DETAIL -> TechniqueDetailScreen(viewModel = viewModel)
        TkdScreen.PRACTICE -> PracticeScreen(viewModel = viewModel)
        TkdScreen.POSE_SIMULATOR -> PoseSimulatorScreen(viewModel = viewModel)
        TkdScreen.SPARRING -> SparringScreen(viewModel = viewModel)
        TkdScreen.CHALLENGES -> ChallengesScreen(viewModel = viewModel)
        TkdScreen.PROGRESS -> ProgressScreen(viewModel = viewModel)
        TkdScreen.PROFILE, TkdScreen.SETTINGS -> ProfileScreen(viewModel = viewModel)
      }
    }
  }
}
