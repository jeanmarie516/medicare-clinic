import React from 'react';
import { clsx } from 'clsx';
import { TrendingUp, TrendingDown } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  icon?: React.ReactNode;
  description?: string;
  trend?: { value: number; isUp: boolean };
  color?: 'primary' | 'secondary' | 'warning' | 'danger' | 'info' | 'health';
  onClick?: () => void;
  /** Classe CSS additionnelle */
  className?: string;
}

const StatCard: React.FC<StatCardProps> = ({ title, value, icon, description, trend, color = 'primary', onClick, className }) => {
  const colors: Record<string, { bg: string; iconBg: string; iconColor: string; trendUp: string; trendDown: string }> = {
    primary: {
      bg: 'bg-gradient-to-br from-primary-500 to-primary-600',
      iconBg: 'bg-white/20',
      iconColor: 'text-white',
      trendUp: 'text-white',
      trendDown: 'text-white/70',
    },
    secondary: {
      bg: 'bg-gradient-to-br from-secondary-500 to-secondary-600',
      iconBg: 'bg-white/20',
      iconColor: 'text-white',
      trendUp: 'text-white',
      trendDown: 'text-white/70',
    },
    warning: {
      bg: 'bg-gradient-to-br from-accent-500 to-accent-600',
      iconBg: 'bg-white/20',
      iconColor: 'text-white',
      trendUp: 'text-white',
      trendDown: 'text-white/70',
    },
    danger: {
      bg: 'bg-gradient-to-br from-danger-500 to-danger-600',
      iconBg: 'bg-white/20',
      iconColor: 'text-white',
      trendUp: 'text-white',
      trendDown: 'text-white/70',
    },
    info: {
      bg: 'bg-gradient-to-br from-secondary-400 to-secondary-500',
      iconBg: 'bg-white/20',
      iconColor: 'text-white',
      trendUp: 'text-white',
      trendDown: 'text-white/70',
    },
    health: {
      bg: 'bg-gradient-to-br from-health-500 to-health-600',
      iconBg: 'bg-white/20',
      iconColor: 'text-white',
      trendUp: 'text-white',
      trendDown: 'text-white/70',
    },
  };

  const c = colors[color];

  return (
    <div
      className={clsx(
        'medical-card relative overflow-hidden group pl-4',
        onClick && 'cursor-pointer',
        className
      )}
      onClick={onClick}
    >
      {/* Decorative medical cross background */}
      <div className="absolute -right-3 -top-3 w-20 h-20 opacity-[0.03] dark:opacity-[0.05] pointer-events-none">
        <svg viewBox="0 0 24 24" fill="currentColor" className="w-full h-full text-primary-600">
          <rect x="9" y="2" width="6" height="20" rx="2" />
          <rect x="2" y="9" width="20" height="6" rx="2" />
        </svg>
      </div>

      <div className="flex items-start justify-between relative z-10">
        <div className="flex-1 min-w-0">
          <p className="text-sm font-medium text-surface-500 dark:text-surface-400 truncate">{title}</p>
          <p className="mt-2 text-3xl font-bold text-surface-900 font-display tracking-tight">{value}</p>
          {trend && (
            <div className="flex items-center mt-2">
              {trend.isUp ? (
                <TrendingUp className="w-4 h-4 text-health-500 mr-1" />
              ) : (
                <TrendingDown className="w-4 h-4 text-danger-500 mr-1" />
              )}
              <span className={clsx('text-sm font-medium', trend.isUp ? 'text-health-600 dark:text-health-400' : 'text-danger-600 dark:text-danger-400')}>
                {trend.value}%
              </span>
            </div>
          )}
          {description && <p className="mt-1.5 text-xs text-surface-400 dark:text-surface-500">{description}</p>}
        </div>
        {icon && (
          <div className={clsx('p-3.5 rounded-xl shadow-sm transition-transform duration-300 group-hover:scale-110', c.bg)}>
            <div className={clsx('w-6 h-6', c.iconColor)}>
              {icon}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default StatCard;
