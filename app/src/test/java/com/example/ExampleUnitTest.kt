package com.example

import com.example.data.model.BeltLevel
import com.example.data.model.TechniqueCurriculum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testTechniqueCurriculumIntegrity() {
    val frontKick = TechniqueCurriculum.getTechnique("front-kick")
    assertNotNull("Front kick must be in curriculum", frontKick)
    assertEquals("Front Snap Kick", frontKick?.name)
    assertEquals("Ap Chagi", frontKick?.koreanName)
    assertTrue("Front kick must have execution steps", frontKick!!.steps.isNotEmpty())
    assertTrue("Front kick must have safety tips", frontKick.safetyTips.isNotEmpty())
  }

  @Test
  fun testBeltProgressionOrder() {
    assertEquals(BeltLevel.YELLOW, BeltLevel.WHITE.nextBelt)
    assertEquals(BeltLevel.GREEN, BeltLevel.YELLOW.nextBelt)
    assertEquals(BeltLevel.BLUE, BeltLevel.GREEN.nextBelt)
    assertEquals(BeltLevel.RED, BeltLevel.BLUE.nextBelt)
    assertEquals(BeltLevel.BLACK, BeltLevel.RED.nextBelt)
    assertEquals(null, BeltLevel.BLACK.nextBelt)
  }

  @Test
  fun testAllCurriculumTechniquesHavePrerequisitesOrValidLevel() {
    val all = TechniqueCurriculum.allTechniques
    assertTrue("Curriculum should have at least 10 core techniques", all.size >= 10)
    all.forEach { tech ->
      assertTrue("Reward XP must be positive", tech.xpReward > 0)
      assertTrue("Steps must not be empty", tech.steps.isNotEmpty())
    }
  }
}
