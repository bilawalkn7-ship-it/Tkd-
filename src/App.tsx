import React, { useState, useEffect } from 'react';
import {
  Shield, Flame, Timer, BookOpen, Trophy, Award, User, Volume2, VolumeX,
  Play, Pause, ChevronRight, ArrowLeft, RotateCcw, Swords, CheckCircle2,
  AlertTriangle, Sparkles, Send, Target, Eye
} from 'lucide-react';
import { BELT_INFO, NEXT_BELT, ALL_TECHNIQUES } from './data/curriculum';
import { BeltLevel, Technique, PlayerProfile, ChallengeQuest } from './types';
import { AnimatedFighter, FighterPose } from './components/AnimatedFighter';
import { SafetyDisclaimer } from './components/SafetyDisclaimer';
import { sounds } from './components/AudioEffects';

type TabType = 'DASHBOARD' | 'LEARN' | 'PRACTICE' | 'SPARRING' | 'CHALLENGES' | 'PROGRESS' | 'PROFILE';

export default function App() {
  const [activeTab, setActiveTab] = useState<TabType>('DASHBOARD');
  const [selectedTechnique, setSelectedTechnique] = useState<Technique>(ALL_TECHNIQUES[2]); // Front Kick default
  const [isDetailView, setIsDetailView] = useState<boolean>(false);
  const [isAudioMuted, setIsAudioMuted] = useState<boolean>(false);

  // Player State
  const [profile, setProfile] = useState<PlayerProfile>(() => {
    const saved = localStorage.getItem('tkd_profile');
    if (saved) {
      try { return JSON.parse(saved); } catch (e) {}
    }
    return {
      name: 'Dojang Student',
      currentBelt: 'WHITE',
      currentXp: 120,
      streakDays: 1,
      totalTrainingMinutes: 18,
      techniquesLearnedCount: 3,
      sparringWins: 1,
      sparringLosses: 0,
    };
  });

  useEffect(() => {
    localStorage.setItem('tkd_profile', JSON.stringify(profile));
  }, [profile]);

  // Quests State
  const [quests, setQuests] = useState<ChallengeQuest[]>([
    { id: 'q1', title: 'Daily Stance Drill', description: 'Hold disciplined fighting stance for 60s.', targetCount: 1, currentProgress: 1, isCompleted: true, rewardXp: 100, isWeekly: false },
    { id: 'q2', title: 'Execute 15 Technique Drills', description: 'Complete 15 kicks or strikes with high accuracy.', targetCount: 15, currentProgress: 7, isCompleted: false, rewardXp: 150, isWeekly: false },
    { id: 'q3', title: 'Solid Iron Guard', description: 'Successfully block 5 attacks in Practice or Sparring.', targetCount: 5, currentProgress: 2, isCompleted: false, rewardXp: 120, isWeekly: false },
    { id: 'q4', title: 'Finish 1 Sparring Match', description: 'Complete a full 2-round controlled sparring match.', targetCount: 1, currentProgress: 1, isCompleted: false, rewardXp: 200, isWeekly: false },
    { id: 'q5', title: 'Curriculum Advancement', description: 'Learn and drill 3 distinct techniques this week.', targetCount: 3, currentProgress: 2, isCompleted: false, rewardXp: 400, isWeekly: true },
    { id: 'q6', title: 'Dojang Champion', description: 'Win 3 controlled sparring matches.', targetCount: 3, currentProgress: 1, isCompleted: false, rewardXp: 500, isWeekly: true },
  ]);

  // AI Coach state
  const [coachMsg, setCoachMsg] = useState<string>("Pil-Seung! Welcome to the Dojang. Focus on knee chambering, proper hip pivot, and instant guard recovery.");
  const [coachLoading, setCoachLoading] = useState<boolean>(false);
  const [customQuestion, setCustomQuestion] = useState<string>('');

  // Technique Demo State
  const [demoProgress, setDemoProgress] = useState<number>(0);
  const [demoPlaying, setDemoPlaying] = useState<boolean>(true);

  // Practice State
  const [practiceActive, setPracticeActive] = useState<boolean>(false);
  const [practiceTarget, setPracticeTarget] = useState<string>('Momtong (Body Level)');
  const [timingWindow, setTimingWindow] = useState<boolean>(false);
  const [practicePose, setPracticePose] = useState<FighterPose>('IDLE_STANCE');
  const [practiceScore, setPracticeScore] = useState<number>(0);
  const [practiceReps, setPracticeReps] = useState<number>(0);
  const [practiceStreak, setPracticeStreak] = useState<number>(0);
  const [practiceFeedback, setPracticeFeedback] = useState<string>('Assume Fighting Stance (Gyeorugi Junbi)');
  const [practicePositive, setPracticePositive] = useState<boolean>(true);
  const [practiceComplete, setPracticeComplete] = useState<boolean>(false);
  const [practiceMistakes, setPracticeMistakes] = useState<string[]>([]);

  // Pose Alignment State
  const [poseKnee, setPoseKnee] = useState<number>(85);
  const [poseFoot, setPoseFoot] = useState<number>(90);
  const [poseGuard, setPoseGuard] = useState<boolean>(true);
  const [showGrid, setShowGrid] = useState<boolean>(true);

  // Sparring State
  const [sparringActive, setSparringActive] = useState<boolean>(false);
  const [sparringDifficulty, setSparringDifficulty] = useState<string>('Beginner Match');
  const [playerScore, setPlayerScore] = useState<number>(0);
  const [opponentScore, setOpponentScore] = useState<number>(0);
  const [roundSeconds, setRoundSeconds] = useState<number>(45);
  const [roundNum, setRoundNum] = useState<number>(1);
  const [playerStamina, setPlayerStamina] = useState<number>(100);
  const [playerPose, setPlayerPose] = useState<FighterPose>('IDLE_STANCE');
  const [opponentPose, setOpponentPose] = useState<FighterPose>('IDLE_STANCE');
  const [combatLog, setCombatLog] = useState<string>('Touch gloves! Shi-jak (Begin)!');
  const [sparringOver, setSparringOver] = useState<boolean>(false);
  const [cleanAttacks, setCleanAttacks] = useState<number>(0);
  const [blocksCount, setBlocksCount] = useState<number>(0);

  // Demo loop
  useEffect(() => {
    if (!demoPlaying || !isDetailView) return;
    const interval = setInterval(() => {
      setDemoProgress((p) => (p + 0.02) % 1);
    }, 40);
    return () => clearInterval(interval);
  }, [demoPlaying, isDetailView]);

  const demoPose: FighterPose = (() => {
    if (demoProgress < 0.2) return 'IDLE_STANCE';
    if (demoProgress < 0.45) return 'CHAMBER';
    if (demoProgress < 0.75) {
      if (selectedTechnique.id === 'roundhouse-kick') return 'ROUNDHOUSE_KICK';
      if (selectedTechnique.id === 'side-kick') return 'SIDE_KICK';
      if (selectedTechnique.id === 'axe-kick') return 'AXE_KICK';
      if (selectedTechnique.id === 'basic-punch') return 'PUNCH';
      if (selectedTechnique.id === 'basic-blocks') return 'HIGH_BLOCK';
      return 'FRONT_KICK';
    }
    if (demoProgress < 0.9) return 'CHAMBER';
    return 'IDLE_STANCE';
  })();

  // Practice interval loop
  useEffect(() => {
    if (!practiceActive || practiceComplete) return;
    const interval = setInterval(() => {
      if (practiceReps >= 10) {
        setPracticeComplete(true);
        setPracticeActive(false);
        sounds.playFanfare();
        addXp(120);
        return;
      }
      const isHead = Math.random() > 0.5;
      setPracticeTarget(isHead ? 'Olgul (Head Level)' : 'Momtong (Body Level)');
      setTimingWindow(true);
      setPracticeFeedback('STRIKE TARGET NOW! Chamber high and snap!');
      setPracticePositive(true);

      setTimeout(() => {
        setTimingWindow(false);
      }, 1600);
    }, 2800);
    return () => clearInterval(interval);
  }, [practiceActive, practiceReps, practiceComplete]);

  // Sparring timer loop
  useEffect(() => {
    if (!sparringActive || sparringOver) return;
    const timer = setInterval(() => {
      setRoundSeconds((sec) => {
        if (sec <= 1) {
          if (roundNum === 1) {
            setRoundNum(2);
            sounds.playGong();
            setCombatLog('Round 2! Reset guard and control center of the ring!');
            return 45;
          } else {
            setSparringOver(true);
            setSparringActive(false);
            sounds.playFanfare();
            const won = playerScore > opponentScore;
            addXp(won ? 250 : 100);
            if (won) {
              setProfile((p) => ({ ...p, sparringWins: p.sparringWins + 1 }));
            } else {
              setProfile((p) => ({ ...p, sparringLosses: p.sparringLosses + 1 }));
            }
            return 0;
          }
        }
        return sec - 1;
      });

      // Passive stamina recovery
      setPlayerStamina((s) => Math.min(100, s + 4));

      // AI Action
      const aiAttackProb = sparringDifficulty === 'Training Sparring' ? 0.2 : sparringDifficulty === 'Beginner Match' ? 0.35 : 0.55;
      if (Math.random() < aiAttackProb) {
        const moves: FighterPose[] = ['ROUNDHOUSE_KICK', 'FRONT_KICK', 'AXE_KICK', 'PUNCH'];
        const chosen = moves[Math.floor(Math.random() * moves.length)];
        setOpponentPose(chosen);

        if (playerPose === 'HIGH_BLOCK') {
          sounds.playBlock();
          setBlocksCount((b) => b + 1);
          setCombatLog('Blocked! Reinforced guard defused the opponent strike!');
        } else {
          sounds.playImpact();
          const pts = chosen === 'AXE_KICK' ? 3 : 2;
          setOpponentScore((s) => s + pts);
          setPlayerPose('HIT_REACTION');
          setCombatLog(`Opponent scored +${pts}! Keep hands guarding chin.`);
        }

        setTimeout(() => {
          setOpponentPose('IDLE_STANCE');
          setPlayerPose('IDLE_STANCE');
        }, 450);
      }
    }, 1000);
    return () => clearInterval(timer);
  }, [sparringActive, roundNum, sparringOver, playerScore, opponentScore, playerPose, sparringDifficulty]);

  const addXp = (amount: number) => {
    setProfile((prev) => ({
      ...prev,
      currentXp: prev.currentXp + amount,
      totalTrainingMinutes: prev.totalTrainingMinutes + 3,
    }));
  };

  const handleSoundToggle = () => {
    const muted = sounds.toggleMute();
    setIsAudioMuted(muted);
  };

  const handlePracticeAction = (pose: FighterPose) => {
    if (!practiceActive || practiceComplete) return;
    setPracticePose(pose);

    if (['FRONT_KICK', 'ROUNDHOUSE_KICK', 'SIDE_KICK', 'AXE_KICK'].includes(pose)) {
      sounds.playKick();
    } else if (pose === 'PUNCH') {
      sounds.playImpact();
    } else {
      sounds.playBlock();
    }

    if (timingWindow) {
      setTimingWindow(false);
      setPracticeReps((r) => r + 1);
      setPracticeStreak((s) => s + 1);
      setPracticeScore((sc) => sc + 100 + practiceStreak * 15);
      setPracticeFeedback('✅ Excellent timing, crisp extension, and instant recovery!');
      setPracticePositive(true);
    } else {
      setPracticeReps((r) => r + 1);
      setPracticeStreak(0);
      setPracticeFeedback('⚠️ Off-tempo! Wait for the pad cue before releasing kick.');
      setPracticePositive(false);
      setPracticeMistakes((m) => [...m, 'Missed pad timing sweet-spot']);
    }

    setTimeout(() => {
      setPracticePose('IDLE_STANCE');
    }, 400);
  };

  const handleSparringAction = (pose: FighterPose) => {
    if (!sparringActive || sparringOver) return;
    if (playerStamina < 15) {
      setCombatLog('Low stamina! Breathe and hold defensive stance.');
      return;
    }

    setPlayerPose(pose);
    setPlayerStamina((s) => Math.max(0, s - 18));

    if (pose === 'HIGH_BLOCK') {
      sounds.playBlock();
      setCombatLog('Guard high! Ready to counter attack.');
    } else {
      sounds.playKick();
      const aiBlocks = Math.random() < (sparringDifficulty === 'Beginner Match' ? 0.3 : 0.55);
      if (aiBlocks) {
        sounds.playBlock();
        setOpponentPose('HIGH_BLOCK');
        setCombatLog('Opponent deflected the kick with forearm block!');
      } else {
        const pts = pose === 'AXE_KICK' ? 3 : pose === 'PUNCH' ? 1 : 2;
        setPlayerScore((s) => s + pts);
        setCleanAttacks((c) => c + 1);
        setOpponentPose('HIT_REACTION');
        setCombatLog(`CLEAN STRIKE! +${pts} points to Blue!`);
      }
    }

    setTimeout(() => {
      setPlayerPose('IDLE_STANCE');
      setOpponentPose('IDLE_STANCE');
    }, 450);
  };

  const handleAskCoach = (q?: string) => {
    setCoachLoading(true);
    const query = q || customQuestion;
    setTimeout(() => {
      setCoachLoading(false);
      if (query.toLowerCase().includes('roundhouse') || query.toLowerCase().includes('dollyo')) {
        setCoachMsg("For Dollyo Chagi, turn your supporting heel 180° towards the target to open the hip. Whip the instep horizontally and keep your rear fist glued to your jaw!");
      } else if (query.toLowerCase().includes('guard') || query.toLowerCase().includes('drop')) {
        setCoachMsg("Guard discipline separates novices from champions! After every strike, snap your hands back to your chin before your foot touches the tatami.");
      } else if (query.toLowerCase().includes('tenet') || query.toLowerCase().includes('spirit')) {
        setCoachMsg("The 5 Tenets of Taekwondo: 1. Courtesy (예의), 2. Integrity (염치), 3. Perseverance (인내), 4. Self-Control (극기), 5. Indomitable Spirit (백절불굴).");
      } else {
        setCoachMsg(`Master Kwan: Excellent question. Remember that power in Taekwondo flows from the ground, through hip rotation, and into snappy retraction. Practice 15 reps on each side.`);
      }
      setCustomQuestion('');
    }, 700);
  };

  const currentBeltInfo = BELT_INFO[profile.currentBelt];
  const nextBeltKey = NEXT_BELT[profile.currentBelt];
  const nextBeltInfo = nextBeltKey ? BELT_INFO[nextBeltKey] : null;

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#0f1017', color: '#f5f5f7', display: 'flex', flexDirection: 'column' }}>
      {/* Top Navbar */}
      <header style={{ backgroundColor: '#171924', borderBottom: '1px solid #2e3249', padding: '10px 20px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', position: 'sticky', top: 0, zIndex: 50 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div style={{ width: '34px', height: '34px', borderRadius: '50%', backgroundColor: '#e53935', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px solid #ffc107', fontSize: '18px' }}>
            🥋
          </div>
          <div>
            <div style={{ fontSize: '16px', fontWeight: 900, letterSpacing: '0.05em' }}>TKD MASTER</div>
            <div style={{ fontSize: '11px', color: '#ffc107', fontWeight: 600 }}>태권도 아카데미 · Academy & Simulator</div>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {/* Belt Tag */}
          <div style={{ backgroundColor: currentBeltInfo.color, color: currentBeltInfo.textColor, padding: '4px 10px', borderRadius: '6px', fontSize: '12px', fontWeight: 800, border: '1px solid rgba(0,0,0,0.2)' }}>
            {currentBeltInfo.hangul} · {currentBeltInfo.rankTitle}
          </div>

          <button onClick={handleSoundToggle} title="Toggle Audio" style={{ background: 'none', color: isAudioMuted ? '#6c7289' : '#ffc107', padding: '6px' }}>
            {isAudioMuted ? <VolumeX size={20} /> : <Volume2 size={20} />}
          </button>
        </div>
      </header>

      {/* Main Container */}
      <main style={{ flex: 1, maxWidth: '1000px', width: '100%', margin: '0 auto', padding: '16px 16px 80px' }}>
        {/* Navigation Tabs */}
        <nav style={{ display: 'flex', gap: '6px', overflowX: 'auto', paddingBottom: '12px', marginBottom: '16px', borderBottom: '1px solid #222536' }}>
          {[
            { id: 'DASHBOARD', label: 'Home', icon: <Flame size={16} /> },
            { id: 'LEARN', label: 'Learn', icon: <BookOpen size={16} /> },
            { id: 'PRACTICE', label: 'Practice', icon: <Target size={16} /> },
            { id: 'SPARRING', label: 'Sparring', icon: <Swords size={16} /> },
            { id: 'CHALLENGES', label: 'Quests', icon: <Trophy size={16} /> },
            { id: 'PROGRESS', label: 'Progress', icon: <Award size={16} /> },
            { id: 'PROFILE', label: 'Profile', icon: <User size={16} /> },
          ].map((tab) => {
            const isSel = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => {
                  setActiveTab(tab.id as TabType);
                  setIsDetailView(false);
                }}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  padding: '8px 14px',
                  borderRadius: '8px',
                  backgroundColor: isSel ? '#e53935' : '#171924',
                  color: isSel ? '#ffffff' : '#a0a5b8',
                  fontSize: '13px',
                  fontWeight: isSel ? 700 : 500,
                  transition: 'all 0.15s ease',
                  whiteSpace: 'nowrap',
                }}
              >
                {tab.icon}
                {tab.label}
              </button>
            );
          })}
        </nav>

        {/* 1. DASHBOARD VIEW */}
        {activeTab === 'DASHBOARD' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {/* Hero Card */}
            <div style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '16px', padding: '20px', position: 'relative', overflow: 'hidden' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                <div style={{ width: '68px', height: '68px', borderRadius: '50%', backgroundColor: '#222536', border: '2px solid #e53935', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '32px' }}>
                  🥋
                </div>
                <div>
                  <div style={{ fontSize: '20px', fontWeight: 800 }}>{profile.name}</div>
                  <div style={{ fontSize: '13px', color: '#ffc107', marginTop: '2px' }}>{currentBeltInfo.rankTitle} · {profile.currentXp} XP</div>
                  <div style={{ fontSize: '11px', color: '#a0a5b8', marginTop: '4px' }}>{currentBeltInfo.description}</div>
                </div>
              </div>
            </div>

            {/* Stats Row */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '10px' }}>
              {[
                { title: 'Streak', val: `${profile.streakDays} Days`, icon: <Flame color="#ff7043" size={18} /> },
                { title: 'Training', val: `${profile.totalTrainingMinutes}m`, icon: <Timer color="#1e88e5" size={18} /> },
                { title: 'Curriculum', val: `${profile.techniquesLearnedCount} Techs`, icon: <BookOpen color="#ffc107" size={18} /> },
                { title: 'Sparring', val: `${profile.sparringWins} Wins`, icon: <Trophy color="#e53935" size={18} /> },
              ].map((s, idx) => (
                <div key={idx} style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '12px', padding: '12px 10px', textAlign: 'center' }}>
                  <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '4px' }}>{s.icon}</div>
                  <div style={{ fontSize: '14px', fontWeight: 800 }}>{s.val}</div>
                  <div style={{ fontSize: '11px', color: '#6c7289' }}>{s.title}</div>
                </div>
              ))}
            </div>

            {/* AI Coach Card */}
            <div style={{ backgroundColor: '#171924', border: '1px solid rgba(30, 136, 229, 0.4)', borderRadius: '14px', padding: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#1e88e5', fontWeight: 700, fontSize: '14px' }}>
                  <Sparkles size={18} /> Master Kwan (AI Assistant)
                </div>
                <span style={{ fontSize: '11px', color: '#6c7289' }}>Educational Guide</span>
              </div>
              <div style={{ color: '#f5f5f7', fontSize: '13px', lineHeight: '20px', fontStyle: 'italic', marginBottom: '12px' }}>
                "{coachLoading ? 'Consulting Master Kwan...' : coachMsg}"
              </div>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button
                  onClick={() => handleAskCoach()}
                  style={{ backgroundColor: '#222536', color: '#f5f5f7', padding: '6px 14px', borderRadius: '6px', fontSize: '12px', fontWeight: 600 }}
                >
                  🔄 Refresh Advice
                </button>
                <button
                  onClick={() => setActiveTab('PROFILE')}
                  style={{ backgroundColor: 'rgba(30, 136, 229, 0.2)', color: '#1e88e5', padding: '6px 14px', borderRadius: '6px', fontSize: '12px', fontWeight: 700 }}
                >
                  💬 Ask Question
                </button>
              </div>
            </div>

            {/* Quick Pathways */}
            <div style={{ fontSize: '12px', fontWeight: 800, color: '#a0a5b8', letterSpacing: '0.05em' }}>TRAINING PATHWAYS</div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '12px' }}>
              <button
                onClick={() => setActiveTab('LEARN')}
                style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '12px', padding: '16px', textAlign: 'left' }}
              >
                <div style={{ width: '38px', height: '38px', borderRadius: '8px', backgroundColor: 'rgba(229, 57, 53, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#e53935', marginBottom: '10px' }}>
                  <BookOpen size={20} />
                </div>
                <div style={{ fontSize: '15px', fontWeight: 800, color: '#f5f5f7' }}>Learn Curriculum</div>
                <div style={{ fontSize: '12px', color: '#a0a5b8' }}>White → Black Belt Progression</div>
              </button>

              <button
                onClick={() => {
                  setActiveTab('PRACTICE');
                  setPracticeActive(true);
                  setPracticeReps(0);
                  setPracticeScore(0);
                  setPracticeComplete(false);
                }}
                style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '12px', padding: '16px', textAlign: 'left' }}
              >
                <div style={{ width: '38px', height: '38px', borderRadius: '8px', backgroundColor: 'rgba(30, 136, 229, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#1e88e5', marginBottom: '10px' }}>
                  <Target size={20} />
                </div>
                <div style={{ fontSize: '15px', fontWeight: 800, color: '#f5f5f7' }}>Practice Simulator</div>
                <div style={{ fontSize: '12px', color: '#a0a5b8' }}>Focus Mitts & Posture Feedback</div>
              </button>

              <button
                onClick={() => {
                  setActiveTab('SPARRING');
                  setSparringActive(true);
                  setPlayerScore(0);
                  setOpponentScore(0);
                  setRoundSeconds(45);
                  setRoundNum(1);
                  setSparringOver(false);
                }}
                style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '12px', padding: '16px', textAlign: 'left' }}
              >
                <div style={{ width: '38px', height: '38px', borderRadius: '8px', backgroundColor: 'rgba(255, 193, 7, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#ffc107', marginBottom: '10px' }}>
                  <Swords size={20} />
                </div>
                <div style={{ fontSize: '15px', fontWeight: 800, color: '#f5f5f7' }}>Controlled Sparring</div>
                <div style={{ fontSize: '12px', color: '#a0a5b8' }}>Olympic-Style Electronic Scoring</div>
              </button>

              <button
                onClick={() => setActiveTab('PROGRESS')}
                style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '12px', padding: '16px', textAlign: 'left' }}
              >
                <div style={{ width: '38px', height: '38px', borderRadius: '8px', backgroundColor: 'rgba(0, 176, 255, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#00b0ff', marginBottom: '10px' }}>
                  <Award size={20} />
                </div>
                <div style={{ fontSize: '15px', fontWeight: 800, color: '#f5f5f7' }}>Belt Grading & Badges</div>
                <div style={{ fontSize: '12px', color: '#a0a5b8' }}>Exam Promotions & Stats</div>
              </button>
            </div>

            <SafetyDisclaimer compact />
          </div>
        )}

        {/* 2. LEARN & DETAIL VIEW */}
        {activeTab === 'LEARN' && (
          <div>
            {!isDetailView ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div>
                  <div style={{ fontSize: '12px', color: '#ffc107', fontWeight: 700 }}>태권도 교육과정 · Curriculum</div>
                  <div style={{ fontSize: '20px', fontWeight: 800 }}>Taekwondo Fundamentals Curriculum</div>
                </div>

                {ALL_TECHNIQUES.map((tech) => (
                  <div
                    key={tech.id}
                    style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '14px', padding: '16px', display: 'flex', flexDirection: 'column', gap: '10px' }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <span style={{ fontSize: '22px' }}>{tech.categoryIcon}</span>
                        <div>
                          <div style={{ fontSize: '16px', fontWeight: 800 }}>
                            {tech.name} <span style={{ color: '#ffc107', fontSize: '13px' }}>({tech.hangul})</span>
                          </div>
                          <div style={{ fontSize: '12px', color: '#a0a5b8' }}>{tech.koreanName} · {tech.categoryLabel}</div>
                        </div>
                      </div>
                      <div style={{ color: '#ffc107', fontSize: '14px' }}>{'★'.repeat(tech.difficultyStars)}</div>
                    </div>

                    <div style={{ fontSize: '13px', color: '#6c7289' }}>{tech.purpose}</div>

                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '6px' }}>
                      <span style={{ fontSize: '12px', color: '#ffc107', fontWeight: 700 }}>+{tech.xpReward} XP</span>
                      <div style={{ display: 'flex', gap: '8px' }}>
                        <button
                          onClick={() => {
                            setSelectedTechnique(tech);
                            setActiveTab('PRACTICE');
                            setPracticeActive(true);
                            setPracticeReps(0);
                            setPracticeScore(0);
                            setPracticeComplete(false);
                          }}
                          style={{ backgroundColor: 'rgba(30, 136, 229, 0.2)', color: '#1e88e5', padding: '6px 12px', borderRadius: '6px', fontSize: '12px', fontWeight: 700 }}
                        >
                          Practice
                        </button>
                        <button
                          onClick={() => {
                            setSelectedTechnique(tech);
                            setIsDetailView(true);
                          }}
                          style={{ backgroundColor: '#e53935', color: '#ffffff', padding: '6px 14px', borderRadius: '6px', fontSize: '12px', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '4px' }}
                        >
                          Learn Steps <ChevronRight size={14} />
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              /* Technique Detail View */
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                <button
                  onClick={() => setIsDetailView(false)}
                  style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#a0a5b8', fontSize: '13px', fontWeight: 600, width: 'fit-content' }}
                >
                  <ArrowLeft size={16} /> Back to Curriculum
                </button>

                <div style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '16px', padding: '16px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                    <div>
                      <div style={{ fontSize: '20px', fontWeight: 900 }}>{selectedTechnique.name} ({selectedTechnique.hangul})</div>
                      <div style={{ fontSize: '13px', color: '#ffc107' }}>{selectedTechnique.koreanName} · {selectedTechnique.categoryLabel}</div>
                    </div>
                    <button
                      onClick={() => setDemoPlaying((p) => !p)}
                      style={{ backgroundColor: '#222536', color: '#ffc107', padding: '8px 12px', borderRadius: '8px', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '12px', fontWeight: 700 }}
                    >
                      {demoPlaying ? <Pause size={14} /> : <Play size={14} />} {demoPlaying ? 'Pause' : 'Play Demo'}
                    </button>
                  </div>

                  {/* Animated Canvas */}
                  <div style={{ backgroundColor: '#13141f', borderRadius: '12px', padding: '10px 0', border: '1px solid #222536' }}>
                    <AnimatedFighter pose={demoPose} width={260} height={240} />
                  </div>

                  {/* Scrubber */}
                  <input
                    type="range"
                    min="0"
                    max="1"
                    step="0.01"
                    value={demoProgress}
                    onChange={(e) => {
                      setDemoPlaying(false);
                      setDemoProgress(parseFloat(e.target.value));
                    }}
                    style={{ width: '100%', marginTop: '12px', accentColor: '#e53935' }}
                  />
                </div>

                {/* Steps */}
                <div style={{ fontSize: '14px', fontWeight: 800, color: '#a0a5b8' }}>EXECUTION CHECKPOINTS</div>
                {selectedTechnique.steps.map((s) => (
                  <div key={s.stepNumber} style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '12px', padding: '14px', display: 'flex', gap: '12px' }}>
                    <div style={{ width: '28px', height: '28px', borderRadius: '50%', backgroundColor: '#e53935', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 800, flexShrink: 0 }}>
                      {s.stepNumber}
                    </div>
                    <div>
                      <div style={{ fontWeight: 800, fontSize: '14px' }}>{s.title}</div>
                      <div style={{ fontSize: '13px', color: '#a0a5b8', marginTop: '3px' }}>{s.instruction}</div>
                      <div style={{ fontSize: '12px', color: '#ffc107', backgroundColor: '#222536', padding: '4px 8px', borderRadius: '6px', marginTop: '6px' }}>
                        Key: {s.keyPoint}
                      </div>
                    </div>
                  </div>
                ))}

                {/* Common Mistakes */}
                <div style={{ backgroundColor: '#171924', border: '1px solid rgba(239, 83, 80, 0.4)', borderRadius: '14px', padding: '16px' }}>
                  <div style={{ color: '#ef5350', fontWeight: 800, fontSize: '13px', display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '8px' }}>
                    <AlertTriangle size={16} /> COMMON MISTAKES TO AVOID
                  </div>
                  {selectedTechnique.commonMistakes.map((m, idx) => (
                    <div key={idx} style={{ fontSize: '12px', color: '#a0a5b8', marginTop: '4px' }}>• {m}</div>
                  ))}
                </div>

                <button
                  onClick={() => {
                    setActiveTab('PRACTICE');
                    setPracticeActive(true);
                    setPracticeReps(0);
                    setPracticeScore(0);
                    setPracticeComplete(false);
                  }}
                  style={{ backgroundColor: '#e53935', color: '#ffffff', padding: '14px', borderRadius: '10px', fontSize: '14px', fontWeight: 800, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}
                >
                  <Target size={18} /> Start Interactive Practice Drill
                </button>
              </div>
            )}
          </div>
        )}

        {/* 3. PRACTICE SIMULATOR VIEW */}
        {activeTab === 'PRACTICE' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ fontSize: '18px', fontWeight: 800 }}>Practice Simulator: {selectedTechnique.name}</div>
                <div style={{ fontSize: '12px', color: '#ffc107' }}>Reps: {practiceReps} / 10 · Streak: {practiceStreak}x</div>
              </div>
              <div style={{ backgroundColor: 'rgba(229, 57, 53, 0.2)', color: '#e53935', padding: '6px 12px', borderRadius: '8px', fontWeight: 800, fontSize: '13px' }}>
                Score: {practiceScore}
              </div>
            </div>

            {/* Arena Canvas with Target Pad */}
            <div style={{ backgroundColor: '#13141f', border: '1px solid #222536', borderRadius: '16px', padding: '16px', textAlign: 'center', position: 'relative' }}>
              {/* Target Cue */}
              <div style={{ display: 'inline-block', backgroundColor: timingWindow ? 'rgba(229, 57, 53, 0.3)' : '#222536', border: `1px solid ${timingWindow ? '#e53935' : '#2e3249'}`, padding: '6px 16px', borderRadius: '20px', fontSize: '12px', fontWeight: 800, color: timingWindow ? '#ffffff' : '#a0a5b8', marginBottom: '8px' }}>
                {timingWindow ? `🎯 STRIKE TARGET NOW: ${practiceTarget}` : 'PREPARE FIGHTING STANCE...'}
              </div>

              <AnimatedFighter pose={practicePose} width={280} height={260} />

              {/* Feedback Banner */}
              <div style={{ backgroundColor: practicePositive ? '#171924' : '#2a1517', border: `1px solid ${practicePositive ? '#4caf50' : '#ffa726'}`, borderRadius: '10px', padding: '10px 14px', fontSize: '13px', fontWeight: 700, color: '#f5f5f7', marginTop: '8px' }}>
                {practiceFeedback}
              </div>
            </div>

            {/* Control Pad */}
            <div style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '14px', padding: '14px' }}>
              <div style={{ fontSize: '11px', color: '#6c7289', fontWeight: 800, marginBottom: '10px' }}>TECHNIQUE STRIKE CONTROLS</div>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '8px' }}>
                <button
                  onClick={() => handlePracticeAction('FRONT_KICK')}
                  style={{ backgroundColor: 'rgba(229, 57, 53, 0.18)', border: '1px solid #e53935', borderRadius: '8px', padding: '10px 6px', color: '#ffffff', fontWeight: 800, fontSize: '13px' }}
                >
                  Front Kick <div style={{ fontSize: '10px', color: '#e53935' }}>앞차기</div>
                </button>
                <button
                  onClick={() => handlePracticeAction('ROUNDHOUSE_KICK')}
                  style={{ backgroundColor: 'rgba(30, 136, 229, 0.18)', border: '1px solid #1e88e5', borderRadius: '8px', padding: '10px 6px', color: '#ffffff', fontWeight: 800, fontSize: '13px' }}
                >
                  Roundhouse <div style={{ fontSize: '10px', color: '#1e88e5' }}>돌려차기</div>
                </button>
                <button
                  onClick={() => handlePracticeAction('SIDE_KICK')}
                  style={{ backgroundColor: 'rgba(255, 193, 7, 0.18)', border: '1px solid #ffc107', borderRadius: '8px', padding: '10px 6px', color: '#ffffff', fontWeight: 800, fontSize: '13px' }}
                >
                  Side Kick <div style={{ fontSize: '10px', color: '#ffc107' }}>옆차기</div>
                </button>
                <button
                  onClick={() => handlePracticeAction('AXE_KICK')}
                  style={{ backgroundColor: 'rgba(171, 71, 188, 0.18)', border: '1px solid #ab47bc', borderRadius: '8px', padding: '10px 6px', color: '#ffffff', fontWeight: 800, fontSize: '13px' }}
                >
                  Axe Kick <div style={{ fontSize: '10px', color: '#ab47bc' }}>내려차기</div>
                </button>
                <button
                  onClick={() => handlePracticeAction('PUNCH')}
                  style={{ backgroundColor: 'rgba(255, 112, 67, 0.18)', border: '1px solid #ff7043', borderRadius: '8px', padding: '10px 6px', color: '#ffffff', fontWeight: 800, fontSize: '13px' }}
                >
                  Punch <div style={{ fontSize: '10px', color: '#ff7043' }}>지르기</div>
                </button>
                <button
                  onClick={() => handlePracticeAction('HIGH_BLOCK')}
                  style={{ backgroundColor: 'rgba(38, 166, 154, 0.18)', border: '1px solid #26a69a', borderRadius: '8px', padding: '10px 6px', color: '#ffffff', fontWeight: 800, fontSize: '13px' }}
                >
                  Guard Block <div style={{ fontSize: '10px', color: '#26a69a' }}>막기</div>
                </button>
              </div>
            </div>

            {/* Drill Summary Modal */}
            {practiceComplete && (
              <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.8)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '16px', zIndex: 100 }}>
                <div style={{ backgroundColor: '#171924', border: '1px solid #ffc107', borderRadius: '16px', padding: '24px', maxWidth: '420px', width: '100%', textAlign: 'center' }}>
                  <CheckCircle2 size={48} color="#4caf50" style={{ margin: '0 auto 12px' }} />
                  <div style={{ fontSize: '20px', fontWeight: 900 }}>Drill Completed!</div>
                  <div style={{ fontSize: '13px', color: '#a0a5b8', marginTop: '4px' }}>Technical Evaluation for {selectedTechnique.name}:</div>

                  <div style={{ backgroundColor: '#222536', borderRadius: '10px', padding: '12px', margin: '14px 0', textAlign: 'left', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px' }}>
                      <span>Form Accuracy:</span>
                      <strong style={{ color: '#4caf50' }}>88%</strong>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px' }}>
                      <span>Reaction Timing:</span>
                      <strong style={{ color: '#1e88e5' }}>92%</strong>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px' }}>
                      <span>Technique Grade:</span>
                      <strong style={{ color: '#ffc107' }}>A (Mastery Level)</strong>
                    </div>
                    <div style={{ borderTop: '1px solid #2e3249', paddingTop: '6px', fontSize: '12px', color: '#ffc107', fontWeight: 700 }}>
                      +120 XP Earned · Streak Maintained!
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '10px' }}>
                    <button
                      onClick={() => {
                        setPracticeReps(0);
                        setPracticeScore(0);
                        setPracticeComplete(false);
                        setPracticeActive(true);
                      }}
                      style={{ flex: 1, backgroundColor: '#e53935', color: '#ffffff', padding: '10px', borderRadius: '8px', fontWeight: 700, fontSize: '13px' }}
                    >
                      Practice Again
                    </button>
                    <button
                      onClick={() => {
                        setActiveTab('SPARRING');
                        setPracticeComplete(false);
                      }}
                      style={{ flex: 1, backgroundColor: '#1e88e5', color: '#ffffff', padding: '10px', borderRadius: '8px', fontWeight: 700, fontSize: '13px' }}
                    >
                      Test in Sparring
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* 4. SPARRING VIEW */}
        {activeTab === 'SPARRING' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {/* Scoreboard */}
            <div style={{ backgroundColor: '#10121a', border: '1px solid #ffc107', borderRadius: '16px', padding: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ fontSize: '12px', fontWeight: 800, color: '#ffc107' }}>ROUND {roundNum} / 2</span>
                <span style={{ fontSize: '24px', fontWeight: 900, color: roundSeconds <= 10 ? '#e53935' : '#ffffff' }}>{roundSeconds}s</span>
                <span style={{ fontSize: '11px', color: '#a0a5b8' }}>{sparringDifficulty}</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: '12px', color: '#1e88e5', fontWeight: 800 }}>BLUE (청) · Player</div>
                  <div style={{ fontSize: '36px', fontWeight: 900, color: '#1e88e5' }}>{playerScore}</div>
                  <div style={{ fontSize: '10px', color: '#6c7289' }}>Stamina: {playerStamina}%</div>
                </div>
                <div style={{ fontSize: '16px', fontWeight: 800, color: '#6c7289' }}>VS</div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '12px', color: '#e53935', fontWeight: 800 }}>RED (홍) · Opponent</div>
                  <div style={{ fontSize: '36px', fontWeight: 900, color: '#e53935' }}>{opponentScore}</div>
                  <div style={{ fontSize: '10px', color: '#6c7289' }}>Stamina: 100%</div>
                </div>
              </div>
            </div>

            {/* Arena Canvas with Both Fighters */}
            <div style={{ backgroundColor: '#13141f', border: '1px solid #222536', borderRadius: '16px', padding: '16px', position: 'relative' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <AnimatedFighter pose={playerPose} facingRight={true} hoguColor="#1e88e5" width={180} height={220} />
                <AnimatedFighter pose={opponentPose} facingRight={false} hoguColor="#e53935" width={180} height={220} />
              </div>

              <div style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '8px', padding: '8px 12px', fontSize: '12px', fontWeight: 700, color: '#ffc107', textAlign: 'center', marginTop: '10px' }}>
                {combatLog}
              </div>
            </div>

            {/* Combat Controls */}
            <div style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '14px', padding: '14px' }}>
              <div style={{ fontSize: '11px', color: '#6c7289', fontWeight: 800, marginBottom: '8px' }}>OLYMPIC SPARRING ATTACK CONTROLS</div>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '8px' }}>
                <button
                  onClick={() => handleSparringAction('ROUNDHOUSE_KICK')}
                  style={{ backgroundColor: '#1e88e5', color: '#ffffff', borderRadius: '8px', padding: '10px 4px', fontWeight: 800, fontSize: '12px' }}
                >
                  Roundhouse (2pt)
                </button>
                <button
                  onClick={() => handleSparringAction('FRONT_KICK')}
                  style={{ backgroundColor: '#e53935', color: '#ffffff', borderRadius: '8px', padding: '10px 4px', fontWeight: 800, fontSize: '12px' }}
                >
                  Front Kick (2pt)
                </button>
                <button
                  onClick={() => handleSparringAction('AXE_KICK')}
                  style={{ backgroundColor: '#ab47bc', color: '#ffffff', borderRadius: '8px', padding: '10px 4px', fontWeight: 800, fontSize: '12px' }}
                >
                  Axe Kick (3pt)
                </button>
                <button
                  onClick={() => handleSparringAction('HIGH_BLOCK')}
                  style={{ backgroundColor: '#26a69a', color: '#ffffff', borderRadius: '8px', padding: '10px 4px', fontWeight: 800, fontSize: '12px' }}
                >
                  Iron Guard
                </button>
              </div>
            </div>

            {/* Sparring End Modal */}
            {sparringOver && (
              <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.85)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '16px', zIndex: 100 }}>
                <div style={{ backgroundColor: '#171924', border: '1px solid #ffc107', borderRadius: '16px', padding: '24px', maxWidth: '420px', width: '100%', textAlign: 'center' }}>
                  <Trophy size={48} color="#ffc107" style={{ margin: '0 auto 12px' }} />
                  <div style={{ fontSize: '22px', fontWeight: 900 }}>
                    {playerScore > opponentScore ? 'Match Victory!' : 'Match Completed!'}
                  </div>
                  <div style={{ fontSize: '16px', color: '#ffc107', fontWeight: 800, margin: '6px 0' }}>
                    Final Score: Blue {playerScore} - Red {opponentScore}
                  </div>
                  <div style={{ fontSize: '12px', color: '#a0a5b8' }}>
                    Clean Strikes: {cleanAttacks} | Blocks Defended: {blocksCount}
                  </div>

                  <div style={{ backgroundColor: '#222536', borderRadius: '10px', padding: '12px', margin: '14px 0', fontSize: '12px', color: '#f5f5f7', lineHeight: '18px' }}>
                    Coach Analysis: You capitalized well on roundhouse attacks when the opponent stepped in. Continue practicing your high guard recovery to prevent counter points!
                  </div>

                  <button
                    onClick={() => {
                      setSparringOver(false);
                      setSparringActive(true);
                      setPlayerScore(0);
                      setOpponentScore(0);
                      setRoundSeconds(45);
                      setRoundNum(1);
                    }}
                    style={{ backgroundColor: '#e53935', color: '#ffffff', width: '100%', padding: '12px', borderRadius: '8px', fontWeight: 800, fontSize: '14px' }}
                  >
                    Rematch
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        {/* 5. CHALLENGES VIEW */}
        {activeTab === 'CHALLENGES' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            <div>
              <div style={{ fontSize: '12px', color: '#ffc107', fontWeight: 700 }}>도전과제 · Quests & Tenets</div>
              <div style={{ fontSize: '20px', fontWeight: 800 }}>Daily Discipline & Weekly Goals</div>
            </div>

            {/* 5 Tenets Card */}
            <div style={{ backgroundColor: '#171924', border: '1px solid rgba(255, 193, 7, 0.3)', borderRadius: '14px', padding: '16px' }}>
              <div style={{ color: '#ffc107', fontWeight: 800, fontSize: '13px', marginBottom: '6px' }}>5 TENETS OF TAEKWONDO (태권도 5대 정신)</div>
              <div style={{ fontSize: '12px', color: '#a0a5b8', lineHeight: '18px' }}>
                1. Courtesy (예의) · 2. Integrity (염치) · 3. Perseverance (인내) · 4. Self-Control (극기) · 5. Indomitable Spirit (백절불굴)
              </div>
            </div>

            {quests.map((q) => {
              const isReady = q.currentProgress >= q.targetCount && !q.isCompleted;
              return (
                <div key={q.id} style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '14px', padding: '16px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <div style={{ fontWeight: 800, fontSize: '14px' }}>{q.title}</div>
                      <div style={{ fontSize: '12px', color: '#a0a5b8', marginTop: '2px' }}>{q.description}</div>
                    </div>
                    <span style={{ fontSize: '12px', color: '#ffc107', fontWeight: 800 }}>+{q.rewardXp} XP</span>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '12px' }}>
                    <div style={{ fontSize: '11px', color: '#6c7289' }}>
                      Progress: {q.currentProgress} / {q.targetCount}
                    </div>
                    {q.isCompleted ? (
                      <span style={{ fontSize: '12px', color: '#4caf50', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <CheckCircle2 size={14} /> Completed
                      </span>
                    ) : isReady ? (
                      <button
                        onClick={() => {
                          setQuests((prev) => prev.map((item) => (item.id === q.id ? { ...item, isCompleted: true } : item)));
                          addXp(q.rewardXp);
                          sounds.playFanfare();
                        }}
                        style={{ backgroundColor: '#ffc107', color: '#000000', padding: '4px 12px', borderRadius: '6px', fontSize: '12px', fontWeight: 800 }}
                      >
                        Claim XP
                      </button>
                    ) : (
                      <span style={{ fontSize: '11px', color: '#a0a5b8' }}>In Progress</span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* 6. PROGRESS VIEW */}
        {activeTab === 'PROGRESS' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div>
              <div style={{ fontSize: '12px', color: '#ffc107', fontWeight: 700 }}>수련 성과 분석 · Performance Analytics</div>
              <div style={{ fontSize: '20px', fontWeight: 800 }}>Taekwondo Mastery & Belt Promotion</div>
            </div>

            {/* Belt Exam Promotion Card */}
            <div style={{ backgroundColor: '#171924', border: '1px solid #ffc107', borderRadius: '16px', padding: '18px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: '12px', color: '#a0a5b8' }}>CURRENT RANK</div>
                  <div style={{ fontSize: '18px', fontWeight: 900, color: currentBeltInfo.color }}>{currentBeltInfo.rankTitle}</div>
                </div>
                {nextBeltInfo && (
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '12px', color: '#a0a5b8' }}>NEXT RANK EXAM</div>
                    <div style={{ fontSize: '18px', fontWeight: 900, color: '#ffc107' }}>{nextBeltInfo.rankTitle}</div>
                  </div>
                )}
              </div>

              {nextBeltInfo ? (
                <div style={{ marginTop: '14px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: '#a0a5b8', marginBottom: '6px' }}>
                    <span>Promotion Eligibility:</span>
                    <span>{profile.currentXp} / {nextBeltInfo.requiredXp} XP</span>
                  </div>
                  {profile.currentXp >= nextBeltInfo.requiredXp ? (
                    <button
                      onClick={() => {
                        setProfile((p) => ({ ...p, currentBelt: nextBeltInfo.id }));
                        sounds.playFanfare();
                      }}
                      style={{ backgroundColor: '#ffc107', color: '#000000', width: '100%', padding: '12px', borderRadius: '8px', fontWeight: 800, fontSize: '14px', marginTop: '6px' }}
                    >
                      Promote to {nextBeltInfo.rankTitle}!
                    </button>
                  ) : (
                    <div style={{ fontSize: '12px', color: '#ffc107', backgroundColor: '#222536', padding: '8px 12px', borderRadius: '6px', textAlign: 'center' }}>
                      Earn {nextBeltInfo.requiredXp - profile.currentXp} more XP through drills & sparring to test for promotion.
                    </div>
                  )}
                </div>
              ) : (
                <div style={{ marginTop: '12px', color: '#ffc107', fontSize: '13px', fontWeight: 700 }}>
                  Black Belt (1st Dan) Achieved. Mastery is an indomitable lifelong journey.
                </div>
              )}
            </div>

            {/* Technique Accuracy Benchmark */}
            <div style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '14px', padding: '16px' }}>
              <div style={{ fontSize: '13px', fontWeight: 800, color: '#e53935', marginBottom: '12px' }}>TECHNIQUE ACCURACY BENCHMARK</div>
              {[
                { name: 'Front Kick (Ap Chagi)', acc: 87, color: '#e53935' },
                { name: 'Roundhouse Kick (Dollyo Chagi)', acc: 74, color: '#1e88e5' },
                { name: 'Side Thrust Kick (Yeop Chagi)', acc: 69, color: '#ffc107' },
                { name: 'Core Defense Blocks (Makgi)', acc: 82, color: '#26a69a' },
              ].map((t, idx) => (
                <div key={idx} style={{ marginBottom: '10px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', marginBottom: '4px' }}>
                    <span>{t.name}</span>
                    <strong style={{ color: t.color }}>{t.acc}%</strong>
                  </div>
                  <div style={{ width: '100%', height: '7px', backgroundColor: '#222536', borderRadius: '4px', overflow: 'hidden' }}>
                    <div style={{ width: `${t.acc}%`, height: '100%', backgroundColor: t.color }} />
                  </div>
                </div>
              ))}
            </div>

            <SafetyDisclaimer compact />
          </div>
        )}

        {/* 7. PROFILE & SETTINGS VIEW */}
        {activeTab === 'PROFILE' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ backgroundColor: '#171924', border: '1px solid #2e3249', borderRadius: '16px', padding: '20px', textAlign: 'center' }}>
              <div style={{ width: '72px', height: '72px', borderRadius: '50%', backgroundColor: '#222536', border: '2px solid #e53935', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '34px', margin: '0 auto 10px' }}>
                🥋
              </div>
              <div style={{ fontSize: '18px', fontWeight: 800 }}>{profile.name}</div>
              <div style={{ fontSize: '12px', color: '#ffc107', marginTop: '2px' }}>{currentBeltInfo.rankTitle}</div>
            </div>

            {/* Ask AI Coach Consultation */}
            <div style={{ backgroundColor: '#171924', border: '1px solid rgba(30, 136, 229, 0.4)', borderRadius: '14px', padding: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#1e88e5', fontWeight: 800, fontSize: '14px', marginBottom: '6px' }}>
                <Sparkles size={18} /> Master Kwan (AI Assistant)
              </div>
              <div style={{ fontSize: '11px', color: '#a0a5b8', marginBottom: '10px' }}>Ask any martial arts question regarding technique, power, or etiquette:</div>

              <div style={{ display: 'flex', gap: '8px', marginBottom: '10px' }}>
                <input
                  type="text"
                  placeholder="e.g. How do I add speed to my roundhouse kick?"
                  value={customQuestion}
                  onChange={(e) => setCustomQuestion(e.target.value)}
                  style={{ flex: 1, backgroundColor: '#222536', border: '1px solid #2e3249', borderRadius: '8px', padding: '8px 12px', color: '#fff', fontSize: '12px' }}
                />
                <button
                  onClick={() => handleAskCoach()}
                  style={{ backgroundColor: '#1e88e5', color: '#fff', padding: '8px 14px', borderRadius: '8px', display: 'flex', alignItems: 'center', gap: '4px', fontSize: '12px', fontWeight: 700 }}
                >
                  <Send size={14} /> Ask
                </button>
              </div>

              <div style={{ backgroundColor: '#222536', borderRadius: '8px', padding: '12px', fontSize: '12px', color: '#f5f5f7', lineHeight: '18px' }}>
                "{coachMsg}"
              </div>
            </div>

            <SafetyDisclaimer />
          </div>
        )}
      </main>
    </div>
  );
}
