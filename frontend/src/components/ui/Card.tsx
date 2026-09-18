import React from 'react';
import { clsx } from 'clsx';

interface CardProps {
  children: React.ReactNode;
  className?: string;
  padding?: 'none' | 'sm' | 'md' | 'lg';
  hover?: boolean;
  onClick?: () => void;
  /** Affiche une barre de titre en haut avec dégradé médical */
  title?: string;
  /** Icône à gauche du titre */
  icon?: React.ReactNode;
  /** Action dans le header (bouton, etc.) */
  action?: React.ReactNode;
}

const Card: React.FC<CardProps> = ({ children, className, padding = 'md', hover = false, onClick, title, icon, action }) => {
  const paddings = { none: '', sm: 'p-4', md: 'p-6', lg: 'p-8' };

  return (
    <div
      className={clsx(
        'medical-card overflow-hidden',
        hover && 'hover:shadow-lg hover:-translate-y-0.5 cursor-pointer',
        onClick && 'cursor-pointer',
        className
      )}
      onClick={onClick}
    >
      {title && (
        <div className="flex items-center justify-between px-6 py-4 border-b border-surface-200 dark:border-surface-300/20 bg-gradient-to-r from-primary-50 to-white dark:from-primary-900/10 dark:to-surface-100">
          <div className="flex items-center space-x-3">
            {icon && (
              <div className="w-8 h-8 rounded-lg bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-primary-600 dark:text-primary-300">
                {icon}
              </div>
            )}
            <h3 className="text-base font-semibold text-surface-900">{title}</h3>
          </div>
          {action && <div>{action}</div>}
        </div>
      )}
      <div className={paddings[padding]}>{children}</div>
    </div>
  );
};

export default Card;
