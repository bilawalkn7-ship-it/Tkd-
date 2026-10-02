import React, { useEffect, useRef } from 'react';

export type FighterPose =
  | 'IDLE_STANCE'
  | 'CHAMBER'
  | 'FRONT_KICK'
  | 'ROUNDHOUSE_KICK'
  | 'SIDE_KICK'
  | 'AXE_KICK'
  | 'PUNCH'
  | 'HIGH_BLOCK'
  | 'HIT_REACTION';

interface Props {
  pose: FighterPose;
  facingRight?: boolean;
  hoguColor?: string;
  beltColor?: string;
  width?: number;
  height?: number;
}

export const AnimatedFighter: React.FC<Props> = ({
  pose,
  facingRight = true,
  hoguColor = '#1e88e5',
  beltColor = '#212121',
  width = 240,
  height = 280,
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animId: number;
    let frame = 0;

    const render = () => {
      ctx.clearRect(0, 0, width, height);

      const bounce = pose === 'IDLE_STANCE' ? Math.sin(frame * 0.1) * 3 : 0;
      const dir = facingRight ? 1 : -1;
      const centerX = width / 2;
      const groundY = height * 0.88 + bounce;
      const h = height * 0.65;

      const headRadius = h * 0.08;
      const hipY = groundY - h * 0.48;
      const chestY = groundY - h * 0.70;
      const headCenterY = groundY - h * 0.85;

      const dobokColor = '#ffffff';
      const skinColor = '#ffd1a4';
      const limbW = h * 0.045;

      // Ground shadow
      ctx.beginPath();
      ctx.ellipse(centerX, groundY, h * 0.28, h * 0.04, 0, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(0, 0, 0, 0.35)';
      ctx.fill();

      // Foot & Leg calculation
      let backFootX = centerX - dir * (h * 0.22);
      let backFootY = groundY;
      let leadFootX = centerX + dir * (h * 0.16);
      let leadFootY = groundY;
      let leadKneeX = centerX + dir * (h * 0.08);
      let leadKneeY = hipY + h * 0.22;

      let rearHandX = centerX + dir * (h * 0.06);
      let rearHandY = chestY - h * 0.02;
      let leadHandX = centerX + dir * (h * 0.16);
      let leadHandY = chestY - h * 0.06;

      if (pose === 'CHAMBER') {
        backFootX = centerX - dir * (h * 0.05);
        leadKneeX = centerX + dir * (h * 0.18);
        leadKneeY = hipY - h * 0.08;
        leadFootX = leadKneeX - dir * (h * 0.08);
        leadFootY = leadKneeY + h * 0.14;
      } else if (pose === 'FRONT_KICK') {
        backFootX = centerX - dir * (h * 0.10);
        leadKneeX = centerX + dir * (h * 0.24);
        leadKneeY = chestY + h * 0.05;
        leadFootX = centerX + dir * (h * 0.48);
        leadFootY = chestY + h * 0.02;
      } else if (pose === 'ROUNDHOUSE_KICK') {
        backFootX = centerX - dir * (h * 0.08);
        leadKneeX = centerX + dir * (h * 0.26);
        leadKneeY = chestY;
        leadFootX = centerX + dir * (h * 0.52);
        leadFootY = chestY - h * 0.05;
      } else if (pose === 'SIDE_KICK') {
        backFootX = centerX - dir * (h * 0.12);
        leadKneeX = centerX + dir * (h * 0.22);
        leadKneeY = hipY;
        leadFootX = centerX + dir * (h * 0.54);
        leadFootY = hipY - h * 0.02;
      } else if (pose === 'AXE_KICK') {
        backFootX = centerX - dir * (h * 0.06);
        leadKneeX = centerX + dir * (h * 0.20);
        leadKneeY = chestY - h * 0.20;
        leadFootX = centerX + dir * (h * 0.26);
        leadFootY = chestY - h * 0.05;
      } else if (pose === 'PUNCH') {
        rearHandX = centerX + dir * (h * 0.38);
        rearHandY = chestY + h * 0.02;
      } else if (pose === 'HIGH_BLOCK') {
        leadHandX = centerX + dir * (h * 0.12);
        leadHandY = headCenterY - h * 0.08;
      } else if (pose === 'HIT_REACTION') {
        backFootX = centerX - dir * (h * 0.26);
        leadFootX = centerX - dir * (h * 0.02);
      }

      ctx.lineCap = 'round';

      // 1. Back leg
      ctx.lineWidth = limbW;
      ctx.strokeStyle = dobokColor;
      ctx.beginPath();
      ctx.moveTo(centerX, hipY);
      ctx.lineTo(backFootX + dir * (h * 0.05), (hipY + groundY) / 2);
      ctx.lineTo(backFootX, backFootY);
      ctx.stroke();

      // Back foot
      ctx.strokeStyle = skinColor;
      ctx.beginPath();
      ctx.moveTo(backFootX, backFootY);
      ctx.lineTo(backFootX + dir * (h * 0.07), backFootY);
      ctx.stroke();

      // 2. Kicking/Lead leg
      ctx.strokeStyle = dobokColor;
      ctx.beginPath();
      ctx.moveTo(centerX, hipY);
      ctx.lineTo(leadKneeX, leadKneeY);
      ctx.lineTo(leadFootX, leadFootY);
      ctx.stroke();

      // Lead foot
      ctx.strokeStyle = skinColor;
      ctx.beginPath();
      ctx.moveTo(leadFootX, leadFootY);
      ctx.lineTo(leadFootX + dir * (h * 0.06), leadFootY - h * 0.01);
      ctx.stroke();

      // 3. Torso (Dobok)
      ctx.fillStyle = dobokColor;
      ctx.beginPath();
      ctx.moveTo(centerX - h * 0.09, chestY);
      ctx.lineTo(centerX + h * 0.09, chestY);
      ctx.lineTo(centerX + h * 0.07, hipY);
      ctx.lineTo(centerX - h * 0.07, hipY);
      ctx.closePath();
      ctx.fill();

      // 4. Hogu (Chest protector)
      ctx.fillStyle = hoguColor;
      ctx.beginPath();
      ctx.moveTo(centerX - h * 0.08, chestY + h * 0.03);
      ctx.lineTo(centerX + h * 0.08, chestY + h * 0.03);
      ctx.lineTo(centerX + h * 0.065, hipY - h * 0.03);
      ctx.lineTo(centerX - h * 0.065, hipY - h * 0.03);
      ctx.closePath();
      ctx.fill();

      // Taegeuk symbol on chest
      ctx.fillStyle = '#ffffff';
      ctx.beginPath();
      ctx.arc(centerX + dir * (h * 0.015), (chestY + hipY) / 2, h * 0.025, 0, Math.PI * 2);
      ctx.fill();

      // Belt
      ctx.fillStyle = beltColor;
      ctx.fillRect(centerX - h * 0.08, hipY - h * 0.015, h * 0.16, h * 0.03);

      // Belt knot tails
      ctx.strokeStyle = beltColor;
      ctx.lineWidth = limbW * 0.5;
      ctx.beginPath();
      ctx.moveTo(centerX + dir * (h * 0.02), hipY + h * 0.01);
      ctx.lineTo(centerX + dir * (h * 0.025), hipY + h * 0.12);
      ctx.stroke();

      // 5. Rear Arm
      ctx.lineWidth = limbW * 0.85;
      ctx.strokeStyle = dobokColor;
      ctx.beginPath();
      ctx.moveTo(centerX - dir * (h * 0.06), chestY + h * 0.03);
      ctx.lineTo(rearHandX - dir * (h * 0.04), (chestY + rearHandY) / 2);
      ctx.lineTo(rearHandX, rearHandY);
      ctx.stroke();

      ctx.fillStyle = skinColor;
      ctx.beginPath();
      ctx.arc(rearHandX, rearHandY, limbW * 0.7, 0, Math.PI * 2);
      ctx.fill();

      // 6. Lead Arm
      ctx.strokeStyle = dobokColor;
      ctx.beginPath();
      ctx.moveTo(centerX + dir * (h * 0.06), chestY + h * 0.03);
      ctx.lineTo((centerX + leadHandX) / 2, (chestY + leadHandY) / 2 + h * 0.03);
      ctx.lineTo(leadHandX, leadHandY);
      ctx.stroke();

      ctx.fillStyle = skinColor;
      ctx.beginPath();
      ctx.arc(leadHandX, leadHandY, limbW * 0.7, 0, Math.PI * 2);
      ctx.fill();

      // 7. Head & Neck
      ctx.strokeStyle = skinColor;
      ctx.lineWidth = limbW * 0.8;
      ctx.beginPath();
      ctx.moveTo(centerX, chestY);
      ctx.lineTo(centerX, headCenterY + headRadius * 0.8);
      ctx.stroke();

      ctx.fillStyle = skinColor;
      ctx.beginPath();
      ctx.arc(centerX, headCenterY, headRadius, 0, Math.PI * 2);
      ctx.fill();

      // Headgear
      ctx.fillStyle = hoguColor;
      ctx.beginPath();
      ctx.moveTo(centerX - headRadius * 1.05, headCenterY);
      ctx.lineTo(centerX + headRadius * 1.05, headCenterY);
      ctx.lineTo(centerX + headRadius * 0.9, headCenterY - headRadius * 1.05);
      ctx.lineTo(centerX - headRadius * 0.9, headCenterY - headRadius * 1.05);
      ctx.closePath();
      ctx.fill();

      // Spark on kick
      if (['FRONT_KICK', 'ROUNDHOUSE_KICK', 'SIDE_KICK', 'AXE_KICK', 'PUNCH'].includes(pose)) {
        const impactX = pose === 'PUNCH' ? rearHandX : leadFootX;
        const impactY = pose === 'PUNCH' ? rearHandY : leadFootY;
        ctx.fillStyle = 'rgba(255, 193, 7, 0.7)';
        ctx.beginPath();
        ctx.arc(impactX + dir * 10, impactY, 14, 0, Math.PI * 2);
        ctx.fill();
        ctx.fillStyle = '#ffffff';
        ctx.beginPath();
        ctx.arc(impactX + dir * 10, impactY, 7, 0, Math.PI * 2);
        ctx.fill();
      }

      frame++;
      animId = requestAnimationFrame(render);
    };

    render();

    return () => {
      cancelAnimationFrame(animId);
    };
  }, [pose, facingRight, hoguColor, beltColor, width, height]);

  return (
    <canvas
      ref={canvasRef}
      width={width}
      height={height}
      style={{ display: 'block', maxWidth: '100%', margin: '0 auto' }}
    />
  );
};
