package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BeltBlack
import com.example.ui.theme.BeltBlue
import com.example.ui.theme.BeltGreen
import com.example.ui.theme.BeltRed
import com.example.ui.theme.BeltWhite
import com.example.ui.theme.BeltYellow

enum class BeltLevel(
  val rankTitle: String,
  val koreanTitle: String,
  val hangul: String,
  val requiredXp: Int,
  val beltColor: Color,
  val description: String,
  val minimumTechniques: Int
) {
  WHITE(
    rankTitle = "White Belt (10th Gup)",
    koreanTitle = "Baek-tti",
    hangul = "백띠",
    requiredXp = 0,
    beltColor = BeltWhite,
    description = "Signifies innocence and purity; the beginner with no prior martial arts knowledge. Focus on basic stance, etiquette, and front kick.",
    minimumTechniques = 0
  ),
  YELLOW(
    rankTitle = "Yellow Belt (8th Gup)",
    koreanTitle = "Norang-tti",
    hangul = "노랑띠",
    requiredXp = 300,
    beltColor = BeltYellow,
    description = "Signifies the earth in which the seed of Taekwondo is planted and sprouts. Focus on roundhouse kick and low/high blocks.",
    minimumTechniques = 4
  ),
  GREEN(
    rankTitle = "Green Belt (6th Gup)",
    koreanTitle = "Chorok-tti",
    hangul = "초록띠",
    requiredXp = 800,
    beltColor = BeltGreen,
    description = "Signifies the plant growing and taking root as the student's skill begins to develop. Focus on side kick, combinations, and guard control.",
    minimumTechniques = 7
  ),
  BLUE(
    rankTitle = "Blue Belt (4th Gup)",
    koreanTitle = "Cheong-tti",
    hangul = "파랑띠",
    requiredXp = 1500,
    beltColor = BeltBlue,
    description = "Signifies the sky toward which the plant matures into a towering tree. Focus on axe kick, back kick, and counter-attacks.",
    minimumTechniques = 10
  ),
  RED(
    rankTitle = "Red Belt (2nd Gup)",
    koreanTitle = "Hong-tti",
    hangul = "빨강띠",
    requiredXp = 2500,
    beltColor = BeltRed,
    description = "Signifies danger, cautioning the student to exercise control and warning the opponent to stay away. Focus on spinning kicks and tactical sparring.",
    minimumTechniques = 13
  ),
  BLACK(
    rankTitle = "Black Belt (1st Dan)",
    koreanTitle = "Geomeun-tti",
    hangul = "검은띠",
    requiredXp = 4000,
    beltColor = BeltBlack,
    description = "Opposite of white, signifying maturity, mastery of fundamentals, and imperviousness to darkness and fear.",
    minimumTechniques = 16
  );

  val nextBelt: BeltLevel?
    get() = when (this) {
      WHITE -> YELLOW
      YELLOW -> GREEN
      GREEN -> BLUE
      BLUE -> RED
      RED -> BLACK
      BLACK -> null
    }
}
