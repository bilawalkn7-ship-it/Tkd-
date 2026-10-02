import React from 'react';
import { Shield } from 'lucide-react';

export const SafetyDisclaimer: React.FC<{ compact?: boolean }> = ({ compact = false }) => {
  return (
    <div
      style={{
        backgroundColor: 'rgba(34, 37, 54, 0.85)',
        border: '1px solid rgba(255, 193, 7, 0.35)',
        borderRadius: '12px',
        padding: compact ? '10px 14px' : '14px 18px',
        display: 'flex',
        alignItems: 'flex-start',
        gap: '12px',
      }}
    >
      <Shield size={compact ? 20 : 24} color="#ffc107" style={{ flexShrink: 0, marginTop: '2px' }} />
      <div>
        <div style={{ color: '#ffc107', fontWeight: 'bold', fontSize: compact ? '12px' : '13px' }}>
          Martial Arts Safety & Educational Disclaimer
        </div>
        <div
          style={{
            color: 'rgba(245, 245, 247, 0.85)',
            fontSize: compact ? '11px' : '12px',
            lineHeight: compact ? '16px' : '18px',
            marginTop: '3px',
          }}
        >
          This application is an educational game and training aid. It does not replace instruction from a qualified Taekwondo instructor. Practice physical techniques safely in a clear space and follow appropriate supervision. Focus on sport, fitness, discipline, and self-control. In-game belts are virtual achievements only.
        </div>
      </div>
    </div>
  );
};
