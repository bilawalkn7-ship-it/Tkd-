export type BeltLevel = 'WHITE' | 'YELLOW' | 'GREEN' | 'BLUE' | 'RED' | 'BLACK';

export interface BeltInfo {
  id: BeltLevel;
  rankTitle: string;
  koreanTitle: string;
  hangul: string;
  requiredXp: number;
  color: string;
  textColor: string;
  description: string;
}

export type TechniqueCategory = 'STANCE' | 'FOOTWORK' | 'KICK' | 'PUNCH' | 'BLOCK' | 'COMBO' | 'TACTICS';
export type DifficultyLevel = 'Beginner' | 'Basic' | 'Intermediate' | 'Advanced' | 'Master';

export interface TechniqueStep {
  stepNumber: number;
  title: string;
  instruction: string;
  keyPoint: string;
}

export interface Technique {
  id: string;
  name: string;
  koreanName: string;
  hangul: string;
  category: TechniqueCategory;
  categoryLabel: string;
  categoryIcon: string;
  difficulty: DifficultyLevel;
  difficultyStars: number;
  beltRequired: BeltLevel;
  purpose: string;
  targetArea: string;
  steps: TechniqueStep[];
  commonMistakes: string[];
  safetyTips: string[];
  xpReward: number;
}

export interface PlayerProfile {
  name: string;
  currentBelt: BeltLevel;
  currentXp: number;
  streakDays: number;
  totalTrainingMinutes: number;
  techniquesLearnedCount: number;
  sparringWins: number;
  sparringLosses: number;
}

export interface ChallengeQuest {
  id: string;
  title: string;
  description: string;
  targetCount: number;
  currentProgress: number;
  isCompleted: boolean;
  rewardXp: number;
  isWeekly: boolean;
}
