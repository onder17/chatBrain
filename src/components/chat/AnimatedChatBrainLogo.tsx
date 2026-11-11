import { useState, useRef, type FC, type CSSProperties } from 'react';
import { Sparkles } from 'lucide-react';

export type MascotSize = 'sm' | 'md' | 'lg' | number;

interface AnimatedChatBrainLogoProps {
  size?: MascotSize;
  className?: string;
  style?: CSSProperties;
  showRing?: boolean;
  scale?: number;
}

export const AnimatedChatBrainLogo: FC<AnimatedChatBrainLogoProps> = ({
  size = 'md',
  className = '',
  style = {},
  showRing = true,
  scale = 2.1,
}) => {
  const [hasError, setHasError] = useState(false);
  const videoRef = useRef<HTMLVideoElement>(null);

  // Compute pixel dimensions
  const pixelSize =
    typeof size === 'number'
      ? size
      : size === 'sm'
      ? 32
      : size === 'md'
      ? 38
      : 76; // 'lg'

  const borderWidth = pixelSize >= 70 ? 3 : pixelSize >= 38 ? 2 : 1.5;

  return (
    <div
      className={`chatbrain-mascot ${className}`}
      style={{
        width: `${pixelSize}px`,
        height: `${pixelSize}px`,
        borderRadius: '50%',
        overflow: 'hidden',
        position: 'relative',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        flexShrink: 0,
        backgroundColor: '#ffffff',
        border: showRing ? `${borderWidth}px solid #f97316` : 'none',
        boxShadow: showRing
          ? `0 0 ${Math.max(10, pixelSize * 0.35)}px rgba(249, 115, 22, ${
              pixelSize >= 70 ? '0.45' : '0.3'
            })`
          : 'none',
        ...style,
      }}
    >
      {!hasError ? (
        <video
          ref={videoRef}
          src="/assets/chatbrain-mascot.mp4"
          autoPlay
          loop
          muted
          playsInline
          onError={() => setHasError(true)}
          style={{
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            objectPosition: 'center center',
            transform: `translate(6%, -14%) scale(${scale})`,
            transformOrigin: 'center center',
            display: 'block',
            pointerEvents: 'none',
          }}
        />
      ) : (
        /* Graceful fallback if video playback is not supported */
        <div
          style={{
            width: '100%',
            height: '100%',
            background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#f97316',
          }}
        >
          <Sparkles size={Math.round(pixelSize * 0.45)} />
        </div>
      )}
    </div>
  );
};
