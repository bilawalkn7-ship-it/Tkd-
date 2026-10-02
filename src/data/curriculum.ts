import { BeltInfo, BeltLevel, Technique } from '../types';

export const BELT_INFO: Record<BeltLevel, BeltInfo> = {
  WHITE: {
    id: 'WHITE',
    rankTitle: 'White Belt (10th Gup)',
    koreanTitle: 'Baek-tti',
    hangul: '백띠',
    requiredXp: 0,
    color: '#ECEFF1',
    textColor: '#1A1A1A',
    description: 'Signifies innocence and purity; the beginner with no prior martial arts knowledge. Focus on fighting stance and front kick.'
  },
  YELLOW: {
    id: 'YELLOW',
    rankTitle: 'Yellow Belt (8th Gup)',
    koreanTitle: 'Norang-tti',
    hangul: '노랑띠',
    requiredXp: 300,
    color: '#FFD54F',
    textColor: '#1A1A1A',
    description: 'Signifies the earth in which the seed of Taekwondo is planted and sprouts. Focus on roundhouse kick and core blocks.'
  },
  GREEN: {
    id: 'GREEN',
    rankTitle: 'Green Belt (6th Gup)',
    koreanTitle: 'Chorok-tti',
    hangul: '초록띠',
    requiredXp: 800,
    color: '#43A047',
    textColor: '#FFFFFF',
    description: 'Signifies the plant growing and taking root as skill develops. Focus on side kick, combinations, and guard discipline.'
  },
  BLUE: {
    id: 'BLUE',
    rankTitle: 'Blue Belt (4th Gup)',
    koreanTitle: 'Cheong-tti',
    hangul: '파랑띠',
    requiredXp: 1500,
    color: '#1E88E5',
    textColor: '#FFFFFF',
    description: 'Signifies the sky toward which the plant matures. Focus on axe kick, back kick, and counter-attacks.'
  },
  RED: {
    id: 'RED',
    rankTitle: 'Red Belt (2nd Gup)',
    koreanTitle: 'Hong-tti',
    hangul: '빨강띠',
    requiredXp: 2500,
    color: '#E53935',
    textColor: '#FFFFFF',
    description: 'Signifies danger, cautioning the student to exercise control. Focus on spinning kicks and tactical sparring.'
  },
  BLACK: {
    id: 'BLACK',
    rankTitle: 'Black Belt (1st Dan)',
    koreanTitle: 'Geomeun-tti',
    hangul: '검은띠',
    requiredXp: 4000,
    color: '#212121',
    textColor: '#FFFFFF',
    description: 'Opposite of white, signifying maturity, mastery of fundamentals, and indomitable spirit.'
  }
};

export const NEXT_BELT: Record<BeltLevel, BeltLevel | null> = {
  WHITE: 'YELLOW',
  YELLOW: 'GREEN',
  GREEN: 'BLUE',
  BLUE: 'RED',
  RED: 'BLACK',
  BLACK: null
};

export const ALL_TECHNIQUES: Technique[] = [
  {
    id: 'fighting-stance',
    name: 'Fighting Stance',
    koreanName: 'Gyeorugi Junbi',
    hangul: '겨루기 준비',
    category: 'STANCE',
    categoryLabel: 'Stance & Guard',
    categoryIcon: '🥋',
    difficulty: 'Beginner',
    difficultyStars: 1,
    beltRequired: 'WHITE',
    purpose: 'Establishes a balanced, agile base allowing rapid offensive attacks and defensive evasions in all directions.',
    targetArea: 'Full Body Stability',
    steps: [
      { stepNumber: 1, title: 'Feet Placement', instruction: 'Step back with your rear leg approximately one and a half shoulder widths. Both feet angled at roughly 45 degrees.', keyPoint: 'Keep 60% of weight on rear leg, 40% on front.' },
      { stepNumber: 2, title: 'Knee Flexion', instruction: 'Slightly bend both knees to maintain spring-like elasticity for explosive movement.', keyPoint: 'Do not lock knees; stay springy on the balls of your feet.' },
      { stepNumber: 3, title: 'Guard Up', instruction: 'Raise front fist to eye/temple level and rear fist guarding the jaw and solar plexus.', keyPoint: 'Keep elbows tucked in close to your ribs.' },
      { stepNumber: 4, title: 'Core & Chin', instruction: 'Tuck chin down toward your chest and keep shoulders relaxed, not shrugged.', keyPoint: 'Never expose your throat or center line.' }
    ],
    commonMistakes: [
      'Standing too tall with locked knees, eliminating explosive speed.',
      'Dropping guard hands below the chest, leaving the head vulnerable.',
      'Feet aligned in a straight tightrope line, losing lateral balance.'
    ],
    safetyTips: [
      'Always stay relaxed; excessive muscle tension causes premature fatigue.',
      'Bounce gently on the balls of your feet without leaving the mat completely.'
    ],
    xpReward: 100
  },
  {
    id: 'basic-footwork',
    name: 'Slide & Step Footwork',
    koreanName: 'Bal Didim',
    hangul: '발 디딤',
    category: 'FOOTWORK',
    categoryLabel: 'Footwork & Movement',
    categoryIcon: '👣',
    difficulty: 'Beginner',
    difficultyStars: 1,
    beltRequired: 'WHITE',
    purpose: 'Controls fighting distance, enters striking range safely, and creates rapid backward evasion.',
    targetArea: 'Distance & Ring Control',
    steps: [
      { stepNumber: 1, title: 'Slide Step Forward', instruction: 'Push off the rear ball of the foot and slide the lead foot forward, followed by rear foot.', keyPoint: 'Maintain equal stance width throughout the slide.' },
      { stepNumber: 2, title: 'Slide Step Backward', instruction: 'Push off lead foot and slide rear foot backward, immediately followed by lead foot.', keyPoint: 'Do not cross your legs at any time.' },
      { stepNumber: 3, title: 'Switch Stance', instruction: 'Jump lightly, rotating your hips 180 degrees to switch lead and rear legs in mid-air.', keyPoint: 'Land quietly with guard intact.' }
    ],
    commonMistakes: [
      'Crossing feet while moving backwards, making you easy to knock down.',
      'Dragging feet heavily instead of gliding on the balls of your feet.'
    ],
    safetyTips: [
      'Ensure the floor has good traction to prevent ankle rollover.'
    ],
    xpReward: 120
  },
  {
    id: 'front-kick',
    name: 'Front Snap Kick',
    koreanName: 'Ap Chagi',
    hangul: '앞차기',
    category: 'KICK',
    categoryLabel: 'Kicks (Chagi)',
    categoryIcon: '🦵',
    difficulty: 'Beginner',
    difficultyStars: 1,
    beltRequired: 'WHITE',
    purpose: 'A direct, linear thrusting and snapping kick designed to intercept rushing opponents or strike the midsection/chin.',
    targetArea: 'Momtong (Solar Plexus) & Olgul (Chin)',
    steps: [
      { stepNumber: 1, title: 'Chambering', instruction: 'Lift the rear knee vertically toward your chest as high and tight as possible, keeping hips forward.', keyPoint: 'Knee height directly determines the maximum height of your kick.' },
      { stepNumber: 2, title: 'Foot Preparation', instruction: 'Pull your toes backward, curling them up tightly to expose the ball of the foot (Ap Chuk).', keyPoint: 'Never kick with flat toes; strike with the ball of the foot.' },
      { stepNumber: 3, title: 'Snapping Extension', instruction: 'Extend your lower leg swiftly like a whip into the target, fully engaging the quadriceps.', keyPoint: 'Lock the hips forward at impact for maximum penetration.' },
      { stepNumber: 4, title: 'Chamber Retraction', instruction: 'Immediately snap the lower leg back to the high chambered position before planting.', keyPoint: 'Retraction prevents the opponent from catching your leg.' },
      { stepNumber: 5, title: 'Controlled Recovery', instruction: 'Return the kicking foot back to the fighting stance under complete balance.', keyPoint: 'Keep both hands up guarding throughout the entire kick.' }
    ],
    commonMistakes: [
      'Kicking with flat or curled-down toes, risking severe toe injury.',
      'Dropping the knee before extending the kick, resulting in a swinging motion.',
      'Dropping guard hands to balance, leaving head wide open.',
      'Failing to retract the leg, allowing the opponent to sweep or counter.'
    ],
    safetyTips: [
      'Thoroughly warm up hamstrings and hip flexors before practicing.',
      'Always practice kicking into empty air with soft knee extension to protect joints.'
    ],
    xpReward: 150
  },
  {
    id: 'roundhouse-kick',
    name: 'Roundhouse Kick',
    koreanName: 'Dollyo Chagi',
    hangul: '돌려차기',
    category: 'KICK',
    categoryLabel: 'Kicks (Chagi)',
    categoryIcon: '🦵',
    difficulty: 'Basic',
    difficultyStars: 2,
    beltRequired: 'YELLOW',
    purpose: 'The cornerstone scoring technique of Olympic Taekwondo. Fast, rotational, and powerful strike to the flank or head.',
    targetArea: 'Hogu (Chest Protector) Flank or Headgear',
    steps: [
      { stepNumber: 1, title: 'Knee Chamber & Angle', instruction: 'Raise rear knee angled 45 degrees, driving your hips into the rotation.', keyPoint: 'Lift knee pointed slightly to the side of the target.' },
      { stepNumber: 2, title: 'Pivot Supporting Foot', instruction: 'Pivot base foot on the ball 90 to 180 degrees, pointing your heel toward the target.', keyPoint: 'The pivot opens your hips and protects the supporting knee.' },
      { stepNumber: 3, title: 'Hip Turnover & Snap', instruction: 'Turn hip over completely so knee and laces point horizontally, then snap lower leg.', keyPoint: 'Strike with the instep (Baldeung).' },
      { stepNumber: 4, title: 'Snap Back & Guard', instruction: 'Whip foot back along the same arc to chamber, maintaining rear hand at chin.', keyPoint: 'Do not let leg drop lazily straight down.' }
    ],
    commonMistakes: [
      'Not pivoting base foot, placing damaging torque on supporting knee.',
      'Swinging leg straight without chambering first.',
      'Leaning too far backward, losing power and recovery speed.'
    ],
    safetyTips: [
      'Never perform high roundhouse kicks without pivoting the supporting heel.'
    ],
    xpReward: 180
  },
  {
    id: 'side-kick',
    name: 'Side Thrust Kick',
    koreanName: 'Yeop Chagi',
    hangul: '옆차기',
    category: 'KICK',
    categoryLabel: 'Kicks (Chagi)',
    categoryIcon: '🦵',
    difficulty: 'Basic',
    difficultyStars: 2,
    beltRequired: 'YELLOW',
    purpose: 'The ultimate stopping and defensive thrust kick in Taekwondo. Pushes opponents backward and delivers penetrating power.',
    targetArea: 'Ribcage, Solar Plexus, Sternum',
    steps: [
      { stepNumber: 1, title: 'Deep Chamber', instruction: 'Lift knee tightly across chest, keeping kicking foot close to supporting knee.', keyPoint: 'Turn body completely sideways to target.' },
      { stepNumber: 2, title: 'Heel Blade Alignment', instruction: 'Cock foot with toes pulled back and heel pushed out, forming foot blade (Balnal).', keyPoint: 'Strike strictly with the solid heel blade.' },
      { stepNumber: 3, title: 'Linear Thrust', instruction: 'Thrust heel straight forward in a linear piston motion while driving hip forward.', keyPoint: 'Align shoulder, hip, and heel in a direct line of force.' },
      { stepNumber: 4, title: 'Retract Along Path', instruction: 'Retract knee back tightly to chest along same path before setting down.', keyPoint: 'Keep torso strong and upright.' }
    ],
    commonMistakes: [
      'Swinging leg in a circular arc like a bad roundhouse instead of pushing in a straight line.',
      'Striking with flat sole instead of the hard heel blade.'
    ],
    safetyTips: [
      'Warm up hip abductors and glutes thoroughly.'
    ],
    xpReward: 200
  },
  {
    id: 'basic-punch',
    name: 'Straight Punch',
    koreanName: 'Baro / Bandae Jireugi',
    hangul: '바로 / 반대 지르기',
    category: 'PUNCH',
    categoryLabel: 'Strikes (Jireugi)',
    categoryIcon: '👊',
    difficulty: 'Beginner',
    difficultyStars: 1,
    beltRequired: 'WHITE',
    purpose: 'Scores points to the chest protector (Hogu) during close-quarters exchanges and interrupts kicking rhythms.',
    targetArea: 'Trunk Hogu (Middle Section)',
    steps: [
      { stepNumber: 1, title: 'Hip Drive', instruction: 'Push off rear foot and rotate rear hip forward toward the center line.', keyPoint: 'Power originates from the floor through the hips.' },
      { stepNumber: 2, title: 'Straight Piston', instruction: 'Launch fist directly forward from the guard in a straight laser path.', keyPoint: 'Do not flare elbow outward.' },
      { stepNumber: 3, title: 'Pronation at Impact', instruction: 'Rotate fist 180 degrees so knuckles face upward at impact.', keyPoint: 'Connect squarely with first two knuckles.' },
      { stepNumber: 4, title: 'Instant Recoil', instruction: 'Snap fist back to guard position just as rapidly as it fired.', keyPoint: 'Never leave punching arm extended.' }
    ],
    commonMistakes: [
      'Aiming for the face in Olympic sparring (face punches are illegal penalties).',
      'Winding up the arm behind the shoulder, telegraphing the attack.'
    ],
    safetyTips: [
      'Keep wrist locked perfectly straight to prevent sprains upon impact.'
    ],
    xpReward: 110
  },
  {
    id: 'basic-blocks',
    name: 'Core Defense Blocks',
    koreanName: 'Arae & Olgul Makgi',
    hangul: '아래 & 얼굴 막기',
    category: 'BLOCK',
    categoryLabel: 'Blocks (Makgi)',
    categoryIcon: '🛡️',
    difficulty: 'Basic',
    difficultyStars: 2,
    beltRequired: 'YELLOW',
    purpose: 'Deflects incoming kicks and strikes to protect vital organs and head.',
    targetArea: 'Defending Low (Groin/Thigh) & High (Face/Head)',
    steps: [
      { stepNumber: 1, title: 'Low Block (Arae Makgi)', instruction: 'Chamber blocking arm at opposite shoulder, sweep downward across body finishing 2 fists above knee.', keyPoint: 'Forearm bone deflects low kicks.' },
      { stepNumber: 2, title: 'High Block (Olgul Makgi)', instruction: 'Chamber blocking arm at opposite hip, drive forearm upward at 45-degree angle above forehead.', keyPoint: 'Keep one fist distance between forearm and forehead.' }
    ],
    commonMistakes: [
      'Slapping wildly with open fingers instead of a solid reinforced forearm.',
      'Holding high block right against the forehead, absorbing the blow directly.'
    ],
    safetyTips: [
      'Engage core muscles during block contact to brace against kinetic energy.'
    ],
    xpReward: 140
  },
  {
    id: 'axe-kick',
    name: 'Axe Kick',
    koreanName: 'Naeryeo Chagi',
    hangul: '내려차기',
    category: 'KICK',
    categoryLabel: 'Kicks (Chagi)',
    categoryIcon: '🦵',
    difficulty: 'Intermediate',
    difficultyStars: 3,
    beltRequired: 'GREEN',
    purpose: 'A vertical descending strike that crushes down onto the opponent\'s collarbone, nose, or chest protector.',
    targetArea: 'Crown of Head, Face, Collarbone',
    steps: [
      { stepNumber: 1, title: 'Vertical Ascend', instruction: 'Swing straight leg up high past your shoulder in an explosive upward arc.', keyPoint: 'Keep knee locked or slightly bent on the way up.' },
      { stepNumber: 2, title: 'Apex Reach', instruction: 'Reach maximum height well above opponent\'s head level.', keyPoint: 'Engage lower abdomen to elevate hip.' },
      { stepNumber: 3, title: 'Downward Chop', instruction: 'Violently accelerate heel straight downward onto target using body weight.', keyPoint: 'Strike with base of the solid heel.' },
      { stepNumber: 4, title: 'Control & Recovery', instruction: 'Retract slightly before ground contact to absorb shock, planting back into stance.', keyPoint: 'Do not collapse forward off balance.' }
    ],
    commonMistakes: [
      'Kicking with the sole instead of the hard heel.',
      'Leaning backwards while pulling the leg down, neutralizing downward force.'
    ],
    safetyTips: [
      'Requires dynamic hamstring stretches to prevent pulled muscles.'
    ],
    xpReward: 220
  }
];
