import React, { ReactNode } from 'react';
import { clsx } from 'clsx';

interface PageHeaderProps {
  title: string;
  subtitle?: string | React.ReactNode;
  actions?: React.ReactNode;
  className?: string;
  /** Icône médicale à afficher à côté du titre */
  icon?: React.ReactNode;
  /** Utilise un fond avec dégradé */
  gradient?: boolean;
}

const PageHeader: React.FC<PageHeaderProps> = ({ title, subtitle, actions, className, icon, gradient = false }) => {
  return (
    <div
      className={clsx(
        'flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6',
        gradient && 'medical-gradient-light dark:medical-gradient rounded-2xl p-6',
        className
      )}
    >
      <div className="flex items-start space-x-4">
        {icon && (
          <div className="hidden sm:flex w-12 h-12 rounded-xl bg-primary-100 dark:bg-primary-900/30 items-center justify-center text-primary-600 dark:text-primary-300 shadow-sm shrink-0">
            {icon}
          </div>
        )}
        <div>
          <h1 className={clsx(
            'text-2xl font-display font-bold',
            gradient ? 'text-primary-900' : 'text-surface-900'
          )}>
            {title}
          </h1>
          {subtitle && (
            <p className={clsx(
              'mt-1.5 text-sm',
              gradient ? 'text-primary-700/70' : 'text-surface-500 dark:text-surface-400'
            )}>
              {subtitle}
            </p>
          )}
        </div>
      </div>
      {actions && <div className="flex items-center space-x-3 shrink-0">{actions}</div>}
    </div>
  );
};

export default PageHeader;
