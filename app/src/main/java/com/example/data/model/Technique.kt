package com.example.data.model

enum class TechniqueCategory(val displayName: String, val icon: String) {
  STANCE("Stance & Guard", "🥋"),
  FOOTWORK("Footwork & Movement", "👣"),
  KICK("Kicks (Chagi)", "🦵"),
  PUNCH("Strikes (Jireugi)", "👊"),
  BLOCK("Blocks (Makgi)", "🛡️"),
  COMBO("Combinations", "⚡"),
  TACTICS("Sparring Tactics", "⚔️")
}

enum class DifficultyLevel(val label: String, val stars: Int) {
  BEGINNER("Beginner", 1),
  BASIC("Basic", 2),
  INTERMEDIATE("Intermediate", 3),
  ADVANCED("Advanced", 4),
  MASTER("Master", 5)
}

data class TechniqueStep(
  val stepNumber: Int,
  val title: String,
  val instruction: String,
  val keyPoint: String
)

data class Technique(
  val id: String,
  val name: String,
  val koreanName: String,
  val hangul: String,
  val category: TechniqueCategory,
  val difficulty: DifficultyLevel,
  val beltRequired: BeltLevel,
  val purpose: String,
  val targetArea: String,
  val startingPosition: String,
  val steps: List<TechniqueStep>,
  val commonMistakes: List<String>,
  val safetyTips: List<String>,
  val practiceDrill: String,
  val xpReward: Int,
  val prerequisites: List<String> = emptyList()
)

object TechniqueCurriculum {
  val allTechniques: List<Technique> = listOf(
    // 1. Fighting Stance
    Technique(
      id = "fighting-stance",
      name = "Fighting Stance",
      koreanName = "Gyeorugi Junbi",
      hangul = "겨루기 준비",
      category = TechniqueCategory.STANCE,
      difficulty = DifficultyLevel.BEGINNER,
      beltRequired = BeltLevel.WHITE,
      purpose = "Establishes a balanced, agile base allowing rapid offensive attacks and defensive evasions in all directions.",
      targetArea = "Full Body Stability",
      startingPosition = "Feet shoulder-width apart, 45-degree angle to the opponent.",
      steps = listOf(
        TechniqueStep(1, "Feet Placement", "Step back with your rear leg approximately one and a half shoulder widths. Both feet angled at roughly 45 degrees.", "Keep 60% of weight on rear leg, 40% on front."),
        TechniqueStep(2, "Knee Flexion", "Slightly bend both knees to maintain spring-like elasticity for explosive movement.", "Do not lock knees; stay springy on the balls of your feet."),
        TechniqueStep(3, "Guard Up", "Raise front fist to eye/temple level and rear fist guarding the jaw and solar plexus.", "Keep elbows tucked in close to your ribs."),
        TechniqueStep(4, "Core & Chin", "Tuck chin down toward your chest and keep shoulders relaxed, not shrugged.", "Never expose your throat or center line.")
      ),
      commonMistakes = listOf(
        "Standing too tall with locked knees, eliminating explosive speed.",
        "Dropping guard hands below the chest, leaving the head vulnerable.",
        "Feet aligned in a straight tightrope line, losing lateral balance.",
        "Putting too much weight on the front foot, making kicks slow."
      ),
      safetyTips = listOf(
        "Always stay relaxed; excessive muscle tension causes premature fatigue.",
        "Bounce gently on the balls of your feet without leaving the mat completely."
      ),
      practiceDrill = "Maintain fighting stance for 45 seconds while bouncing lightly, maintaining high guard discipline.",
      xpReward = 100
    ),

    // 2. Basic Footwork & Sliding
    Technique(
      id = "basic-footwork",
      name = "Slide & Step Footwork",
      koreanName = "Bal Didim",
      hangul = "발 디딤",
      category = TechniqueCategory.FOOTWORK,
      difficulty = DifficultyLevel.BEGINNER,
      beltRequired = BeltLevel.WHITE,
      purpose = "Controls fighting distance, enters striking range safely, and creates rapid backward evasion.",
      targetArea = "Distance & Ring Control",
      startingPosition = "Gyeorugi Junbi (Fighting Stance).",
      steps = listOf(
        TechniqueStep(1, "Slide Step Forward", "Push off the rear ball of the foot and slide the lead foot forward, immediately followed by the rear foot.", "Maintain equal stance width throughout the slide."),
        TechniqueStep(2, "Slide Step Backward", "Push off the lead foot and slide the rear foot backward, immediately followed by the lead foot.", "Do not cross your legs at any time."),
        TechniqueStep(3, "Switch Stance", "Jump lightly, rotating your hips 180 degrees to switch lead and rear legs in mid-air.", "Land quietly with guard intact.")
      ),
      commonMistakes = listOf(
        "Crossing feet while moving backwards, making you easy to knock down.",
        "Dragging feet heavily instead of gliding on the balls of your feet.",
        "Flailing hands during movement, exposing your face to counters."
      ),
      safetyTips = listOf(
        "Ensure the floor has good traction to prevent ankle rollover.",
        "Keep movements sharp and controlled rather than lunging."
      ),
      practiceDrill = "Perform 10 forward slides, 10 backward slides, and 5 stance switches while maintaining chin protection.",
      xpReward = 120,
      prerequisites = listOf("fighting-stance")
    ),

    // 3. Front Kick
    Technique(
      id = "front-kick",
      name = "Front Snap Kick",
      koreanName = "Ap Chagi",
      hangul = "앞차기",
      category = TechniqueCategory.KICK,
      difficulty = DifficultyLevel.BEGINNER,
      beltRequired = BeltLevel.WHITE,
      purpose = "A direct, linear thrusting and snapping kick designed to intercept rushing opponents or strike the midsection/chin.",
      targetArea = "Momtong (Solar Plexus) & Olgul (Chin)",
      startingPosition = "Fighting Stance, weight balanced.",
      steps = listOf(
        TechniqueStep(1, "Chambering", "Lift the rear knee vertically toward your chest as high and tight as possible, keeping hips forward.", "Knee height directly determines the maximum height of your kick."),
        TechniqueStep(2, "Foot Preparation", "Pull your toes backward, curling them up tightly to expose the ball of the foot (Ap Chuk).", "Never kick with flat toes; ball of the foot delivers concentrated impact."),
        TechniqueStep(3, "Snapping Extension", "Extend your lower leg swiftly like a whip into the target, fully engaging the quadriceps.", "Lock the hips forward at impact for maximum penetration."),
        TechniqueStep(4, "Chamber Retraction", "Immediately snap the lower leg back to the high chambered position before planting.", "Retraction prevents the opponent from catching your leg."),
        TechniqueStep(5, "Controlled Recovery", "Return the kicking foot back to the fighting stance under complete balance.", "Keep both hands up guarding throughout the entire kick.")
      ),
      commonMistakes = listOf(
        "Kicking with flat or curled-down toes, risking severe toe injury.",
        "Dropping the knee before extending the kick, resulting in a swinging motion.",
        "Dropping guard hands to balance, leaving head wide open.",
        "Failing to retract the leg, allowing the opponent to sweep or counter."
      ),
      safetyTips = listOf(
        "Thoroughly warm up hamstrings and hip flexors before practicing.",
        "Always practice kicking into empty air with soft knee extension to protect joints."
      ),
      practiceDrill = "Perform 15 chambered front snap kicks on each leg, focusing on high knee lift and instant retraction.",
      xpReward = 150,
      prerequisites = listOf("fighting-stance")
    ),

    // 4. Roundhouse Kick
    Technique(
      id = "roundhouse-kick",
      name = "Roundhouse Kick",
      koreanName = "Dollyo Chagi",
      hangul = "돌려차기",
      category = TechniqueCategory.KICK,
      difficulty = DifficultyLevel.BASIC,
      beltRequired = BeltLevel.YELLOW,
      purpose = "The cornerstone scoring technique of Olympic Taekwondo. Fast, rotational, and powerful strike to the flank or head.",
      targetArea = "Hogu (Chest Protector) Flank or Headgear",
      startingPosition = "Fighting Stance with active bouncing.",
      steps = listOf(
        TechniqueStep(1, "Knee Chamber & Angle", "Raise your rear knee up angled 45 degrees, driving your hips into the rotation.", "Lift knee pointed slightly to the side of the target."),
        TechniqueStep(2, "Pivot Supporting Foot", "Pivot your base foot on the ball between 90 and 180 degrees, pointing your heel toward the target.", "The pivot opens your hips and protects the supporting knee."),
        TechniqueStep(3, "Hip Turnover & Snap", "Turn your hip completely over so your knee and laces point horizontally, then snap the lower leg.", "Strike with the instep (Baldeung) or ball of foot."),
        TechniqueStep(4, "Snap Back & Guard", "Whip the foot back along the same arc to the chamber, maintaining guard with rear hand on chin.", "Do not let the leg drop lazily straight down.")
      ),
      commonMistakes = listOf(
        "Not pivoting the base foot, placing extreme damaging torque on the supporting knee.",
        "Swinging the leg straight without chambering the knee first.",
        "Leaning too far backward, losing all power and recovery speed.",
        "Dropping the guarding arm across the chest."
      ),
      safetyTips = listOf(
        "Never perform high roundhouse kicks without pivoting the supporting heel.",
        "Keep the core engaged to avoid hyper-extending the lower back."
      ),
      practiceDrill = "Perform 20 roundhouse kicks against mid-level pad with crisp hip turnover.",
      xpReward = 180,
      prerequisites = listOf("front-kick")
    ),

    // 5. Side Kick
    Technique(
      id = "side-kick",
      name = "Side Thrust Kick",
      koreanName = "Yeop Chagi",
      hangul = "옆차기",
      category = TechniqueCategory.KICK,
      difficulty = DifficultyLevel.BASIC,
      beltRequired = BeltLevel.YELLOW,
      purpose = "The ultimate stopping and defensive thrust kick in Taekwondo. Pushes opponents backward and delivers penetrating power.",
      targetArea = "Ribcage, Solar Plexus, Sternum",
      startingPosition = "Fighting Stance, side profile.",
      steps = listOf(
        TechniqueStep(1, "Deep Chamber", "Lift the knee tightly across your chest, keeping your kicking foot close to your supporting knee.", "Turn your body completely sideways to the target."),
        TechniqueStep(2, "Heel Blade Alignment", "Cock your foot with toes pulled back and heel pushed out, forming the foot blade (Balnal).", "Strike strictly with the solid heel or edge of the foot."),
        TechniqueStep(3, "Linear Thrust", "Thrust your heel straight forward in a linear piston motion while driving your hip forward.", "Align your shoulder, hip, and heel in a direct line of force."),
        TechniqueStep(4, "Retract Along Path", "Retract the knee back tightly to the chest along the exact same path before setting down.", "Keep torso strong and upright.")
      ),
      commonMistakes = listOf(
        "Swinging the leg in a circular arc like a bad roundhouse instead of pushing in a straight line.",
        "Striking with flat sole instead of the hard heel or foot blade.",
        "Dropping the head backward below the waist level."
      ),
      safetyTips = listOf(
        "Warm up the gluteus medius and hip abductors thoroughly.",
        "Do not overextend the knee joint on missed air kicks."
      ),
      practiceDrill = "Hold a wall or bar for balance and practice 12 slow linear side thrusts, locking out for 1 second each.",
      xpReward = 200,
      prerequisites = listOf("roundhouse-kick")
    ),

    // 6. Basic Punch
    Technique(
      id = "basic-punch",
      name = "Straight Punch",
      koreanName = "Baro / Bandae Jireugi",
      hangul = "바로 / 반대 지르기",
      category = TechniqueCategory.PUNCH,
      difficulty = DifficultyLevel.BEGINNER,
      beltRequired = BeltLevel.WHITE,
      purpose = "Scores points to the chest protector (Hogu) during close-quarters exchanges and interrupts kicking rhythms.",
      targetArea = "Trunk Hogu (Middle Section)",
      startingPosition = "Fighting Stance, hands chambered at chin.",
      steps = listOf(
        TechniqueStep(1, "Hip Drive", "Push off the rear foot and rotate your rear hip forward toward the center line.", "Power originates from the floor through the hips."),
        TechniqueStep(2, "Straight Piston", "Launch the fist directly forward from the guard in a straight laser path.", "Do not flare the elbow outward (no chicken wings)."),
        TechniqueStep(3, "Pronation at Impact", "Rotate the fist 180 degrees so the knuckles face upward at the instant of impact.", "Connect squarely with the first two knuckles."),
        TechniqueStep(4, "Instant Recoil", "Snap the fist back to guard position just as rapidly as it fired.", "Never leave the punching arm extended.")
      ),
      commonMistakes = listOf(
        "Aiming for the face in Olympic sparring (face punches are illegal Gam-jeom penalties).",
        "Winding up the arm behind the shoulder, telegraphing the attack.",
        "Dropping the opposite hand while punching."
      ),
      safetyTips = listOf(
        "Keep the wrist locked perfectly straight to prevent sprains upon impact."
      ),
      practiceDrill = "Perform 30 alternating straight punches while maintaining active footwork.",
      xpReward = 110,
      prerequisites = listOf("fighting-stance")
    ),

    // 7. Low & High Blocks
    Technique(
      id = "basic-blocks",
      name = "Core Defense Blocks",
      koreanName = "Arae & Olgul Makgi",
      hangul = "아래 & 얼굴 막기",
      category = TechniqueCategory.BLOCK,
      difficulty = DifficultyLevel.BASIC,
      beltRequired = BeltLevel.YELLOW,
      purpose = "Deflects incoming kicks and strikes to protect the vital organs and head.",
      targetArea = "Defending Low (Groin/Thigh) & High (Face/Head)",
      startingPosition = "Fighting Stance, guarded.",
      steps = listOf(
        TechniqueStep(1, "Low Block (Arae Makgi)", "Chamber blocking arm at opposite shoulder, sweep downward across the body finishing 2 fists above the knee.", "Forearm bone deflects low kicks."),
        TechniqueStep(2, "High Block (Olgul Makgi)", "Chamber blocking arm at opposite hip, drive forearm upward at a 45-degree angle above the forehead.", "Keep one fist distance between forearm and forehead."),
        TechniqueStep(3, "Body Block (Momtong Makgi)", "Drive outer forearm inward across chest from ear level to center line.", "Protects solar plexus from straight kicks.")
      ),
      commonMistakes = listOf(
        "Slapping wildly with open fingers instead of a solid reinforced forearm.",
        "Holding high block right against the forehead, absorbing the blow directly.",
        "Dropping the other hand during the block."
      ),
      safetyTips = listOf(
        "Engage core muscles during block contact to brace against kinetic energy."
      ),
      practiceDrill = "Practice 10 Low Blocks, 10 Body Blocks, and 10 High Blocks in continuous sequence.",
      xpReward = 140,
      prerequisites = listOf("fighting-stance")
    ),

    // 8. Axe Kick
    Technique(
      id = "axe-kick",
      name = "Axe Kick",
      koreanName = "Naeryeo Chagi",
      hangul = "내려차기",
      category = TechniqueCategory.KICK,
      difficulty = DifficultyLevel.INTERMEDIATE,
      beltRequired = BeltLevel.GREEN,
      purpose = "A vertical descending strike that crushes down onto the opponent's collarbone, nose, or chest protector.",
      targetArea = "Crown of Head, Face, Collarbone",
      startingPosition = "Fighting Stance with light forward bounce.",
      steps = listOf(
        TechniqueStep(1, "Vertical Ascend", "Swing the straight leg up high past your shoulder in an explosive upward arc.", "Keep the knee locked or slightly bent on the way up."),
        TechniqueStep(2, "Apex Reach", "Reach maximum height well above the opponent's head level.", "Engage lower abdomen to elevate hip."),
        TechniqueStep(3, "Downward Chop", "Violently accelerate the heel straight downward onto the target using full body weight.", "Strike with the base of the solid heel."),
        TechniqueStep(4, "Control & Recovery", "Retract slightly before ground contact to absorb shock, planting back into stance.", "Do not collapse forward off balance.")
      ),
      commonMistakes = listOf(
        "Kicking with the sole instead of the hard heel.",
        "Leaning backwards while pulling the leg down, neutralizing downward force.",
        "Lack of flexibility causing the hip to pull to the side."
      ),
      safetyTips = listOf(
        "Requires warm dynamic hamstring stretches to prevent pulled muscles.",
        "Do not slam foot uncontrolled into hard floors."
      ),
      practiceDrill = "Perform 10 axe kicks inside-to-out and 10 outside-to-in on both legs.",
      xpReward = 220,
      prerequisites = listOf("roundhouse-kick", "front-kick")
    ),

    // 9. Back Kick
    Technique(
      id = "back-kick",
      name = "Turning Back Kick",
      koreanName = "Dwi Chagi",
      hangul = "뒤차기",
      category = TechniqueCategory.KICK,
      difficulty = DifficultyLevel.INTERMEDIATE,
      beltRequired = BeltLevel.BLUE,
      purpose = "The most lethal counter-attack in Taekwondo sparring. Knocks rushing opponents backwards with devastating power.",
      targetArea = "Solar Plexus, Ribs, Liver",
      startingPosition = "Fighting Stance, facing opponent.",
      steps = listOf(
        TechniqueStep(1, "Turn Hips 180°", "Turn your shoulders and hips 180 degrees away from the opponent on the balls of your feet.", "Look over your kicking shoulder to identify the target."),
        TechniqueStep(2, "Tight Knee Chamber", "Tuck your kicking knee tightly to your chest, keeping both knees grazing each other.", "Do not flare the knee wide like a roundhouse; keep it compact."),
        TechniqueStep(3, "Straight Piston Thrust", "Thrust the heel straight backward like a horse kick, striking with the solid heel.", "Keep your toes pointed downward, not to the side."),
        TechniqueStep(4, "Linear Recoil", "Pull the heel back along the center line and rotate back facing the opponent.", "Keep hands up to catch any counter-blows.")
      ),
      commonMistakes = listOf(
        "Kicking blindly without spotting the target over the shoulder first.",
        "Turning it into a spinning roundhouse with a wide looping arc.",
        "Pointing toes up or sideways, weakening the heel thrust."
      ),
      safetyTips = listOf(
        "Always spot your target visually before extending the leg.",
        "Start practicing slowly against a heavy bag before sparring."
      ),
      practiceDrill = "Practice 15 Dwi Chagi kicks on each leg, focusing on tight knee grazing and linear trajectory.",
      xpReward = 280,
      prerequisites = listOf("side-kick")
    ),

    // 10. Kick Combination
    Technique(
      id = "double-roundhouse-combo",
      name = "Double Roundhouse Combo",
      koreanName = "Naraebong Chagi",
      hangul = "나래봉 차기",
      category = TechniqueCategory.COMBO,
      difficulty = DifficultyLevel.INTERMEDIATE,
      beltRequired = BeltLevel.BLUE,
      purpose = "Continuous alternating kicks without setting the foot down, confusing opponent's defense and scoring multiple points.",
      targetArea = "Body Hogu followed by Headgear",
      startingPosition = "Active fighting stance.",
      steps = listOf(
        TechniqueStep(1, "First Low/Mid Kick", "Fire a rapid lead roundhouse kick to the body to draw the opponent's guard down.", "Do not commit 100% power; prioritize speed."),
        TechniqueStep(2, "Scissor Jump", "Hop off the supporting foot in mid-air while retracting the first kick.", "Switch legs rapidly in the air."),
        TechniqueStep(3, "Second High Kick", "Unleash the opposite roundhouse kick to the head before the opponent recovers.", "Maximize hip snap and Kihap shout.")
      ),
      commonMistakes = listOf(
        "Pausing between the first and second kick, allowing opponent to counter.",
        "Dropping hands during the jump."
      ),
      safetyTips = listOf(
        "Land softly on the balls of both feet to protect knee ligaments."
      ),
      practiceDrill = "Perform 8 double roundhouse sets continuously on target pads.",
      xpReward = 320,
      prerequisites = listOf("roundhouse-kick")
    ),

    // 11. Spinning Hook Kick
    Technique(
      id = "spinning-hook-kick",
      name = "Spinning Hook Kick",
      koreanName = "Dwi Huryeo Chagi",
      hangul = "뒤 후려차기",
      category = TechniqueCategory.KICK,
      difficulty = DifficultyLevel.ADVANCED,
      beltRequired = BeltLevel.RED,
      purpose = "A high-damage spinning head kick with maximum rotational momentum. Can produce clean knockouts in competition.",
      targetArea = "Headgear, Jaw, Temple",
      startingPosition = "Fighting Stance.",
      steps = listOf(
        TechniqueStep(1, "Fast Pivot", "Pivot 180 degrees rapidly on supporting ball of foot, snapping head around to locate target.", "Head must lead the rotation."),
        TechniqueStep(2, "Leg Lift & Sweep", "Extend leg slightly past the target line.", "Sweep horizontally across the face plane."),
        TechniqueStep(3, "Heel Hook Snap", "Violently whip the knee backwards, hooking the heel into the target like a scythe.", "Connect with the back of the heel or sole."),
        TechniqueStep(4, "Rotational Follow-Through", "Complete the 360-degree rotation and land back in a rock-solid fighting stance.", "Keep hands guarding chin.")
      ),
      commonMistakes = listOf(
        "Over-rotating and spinning out of control onto the mat.",
        "Bending the knee too early, missing the target range.",
        "Dropping guard completely during the spin."
      ),
      safetyTips = listOf(
        "Only practice this kick when completely warmed up.",
        "Ensure wide spatial clearance so you do not hit objects."
      ),
      practiceDrill = "Execute 10 controlled slow spins on each side, then 10 high-speed kicks targeting head level.",
      xpReward = 400,
      prerequisites = listOf("back-kick")
    ),

    // 12. Tactical Sparring & Distance
    Technique(
      id = "tactical-sparring-concepts",
      name = "Olympic Sparring Tactics",
      koreanName = "Gyeorugi Jeonryak",
      hangul = "겨루기 전략",
      category = TechniqueCategory.TACTICS,
      difficulty = DifficultyLevel.ADVANCED,
      beltRequired = BeltLevel.RED,
      purpose = "Master distance manipulation, baiting opponent attacks, feints, and converting defensive evasions into counter-points.",
      targetArea = "Tactical Mastery",
      startingPosition = "Light hopping and rhythm switching.",
      steps = listOf(
        TechniqueStep(1, "Distance Management", "Stay right at the edge of striking range (half step outside). Bait attacks by offering a false opening.", "Force opponent to over-commit."),
        TechniqueStep(2, "Step-Back Evasion", "When the opponent attacks, slide back just 20cm, letting their kick miss by inches.", "Do not retreat too far; stay in counter distance."),
        TechniqueStep(3, "Immediate Counter", "As their foot drops to the mat, explode forward with a counter roundhouse or back kick.", "Strike while they are off balance."),
        TechniqueStep(4, "Clinch & Reset", "If trapped close, initiate a clean chest-to-chest clinch and wait for referee 'Kal-yeo' (break).", "Never turn your back.")
      ),
      commonMistakes = listOf(
        "Backing straight up in a line until running out of the ring (Gam-jeom penalty).",
        "Reacting with panic when the opponent charges.",
        "Dropping guard after scoring."
      ),
      safetyTips = listOf(
        "Always wear complete WTF/WT approved protective gear during live sparring (Hogu, headgear, shin guards, arm guards, groin guard, mouthguard)."
      ),
      practiceDrill = "Complete 3 rounds of controlled sparring drills, focusing on bait-and-counter timing.",
      xpReward = 450,
      prerequisites = listOf("double-roundhouse-combo", "back-kick")
    )
  )

  fun getTechnique(id: String): Technique? = allTechniques.find { it.id == id }
}
