import React from 'react';
import { clsx } from 'clsx';
import { Activity, CheckCircle, Clock, AlertTriangle, XCircle, HelpCircle } from 'lucide-react';
import { useTranslation } from '../../contexts/LanguageContext';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'purple' | 'primary';
  size?: 'sm' | 'md';
  className?: string;
}

const Badge: React.FC<BadgeProps> = ({ children, variant = 'default', size = 'md', className }) => {
  const variants: Record<string, string> = {
    default: 'bg-surface-100 text-surface-600 border-surface-200 dark:bg-surface-200 dark:text-surface-700 dark:border-surface-300',
    success: 'bg-health-50 text-health-700 border-health-200 dark:bg-health-900/20 dark:text-health-300 dark:border-health-700/30',
    warning: 'bg-accent-50 text-accent-700 border-accent-200 dark:bg-accent-900/20 dark:text-accent-300 dark:border-accent-700/30',
    danger: 'bg-danger-50 text-danger-700 border-danger-200 dark:bg-danger-900/20 dark:text-danger-300 dark:border-danger-700/30',
    info: 'bg-secondary-50 text-secondary-700 border-secondary-200 dark:bg-secondary-900/20 dark:text-secondary-300 dark:border-secondary-700/30',
    purple: 'bg-purple-50 text-purple-700 border-purple-200 dark:bg-purple-900/20 dark:text-purple-300 dark:border-purple-700/30',
    primary: 'bg-primary-50 text-primary-700 border-primary-200 dark:bg-primary-900/20 dark:text-primary-300 dark:border-primary-700/30',
  };

  const sizes = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-2.5 py-1 text-xs',
  };

  return (
    <span
      className={clsx(
        'inline-flex items-center font-medium rounded-full border',
        variants[variant],
        sizes[size],
        className
      )}
    >
      {children}
    </span>
  );
};

export const StatutBadge: React.FC<{ statut: string }> = ({ statut }) => {
  const { t } = useTranslation();

  const config: Record<string, { label: string; variant: 'success' | 'warning' | 'danger' | 'info' | 'purple' | 'default' | 'primary'; icon?: React.ReactNode }> = {
    EN_ATTENTE: { label: t.rendezVous.statut.EN_ATTENTE, variant: 'warning', icon: <Clock className="w-3 h-3 mr-1" /> },
    CONFIRME: { label: t.rendezVous.statut.CONFIRME, variant: 'info', icon: <CheckCircle className="w-3 h-3 mr-1" /> },
    EN_COURS: { label: t.rendezVous.statut.EN_COURS, variant: 'purple', icon: <Activity className="w-3 h-3 mr-1" /> },
    TERMINE: { label: t.rendezVous.statut.TERMINE, variant: 'success', icon: <CheckCircle className="w-3 h-3 mr-1" /> },
    ANNULE: { label: t.rendezVous.statut.ANNULE, variant: 'danger', icon: <XCircle className="w-3 h-3 mr-1" /> },
    ABSENT: { label: t.rendezVous.statut.ABSENT, variant: 'default', icon: <HelpCircle className="w-3 h-3 mr-1" /> },
    SOLDE: { label: t.facture.statut.SOLDE, variant: 'success', icon: <CheckCircle className="w-3 h-3 mr-1" /> },
    PARTIELLEMENT_PAYE: { label: t.facture.statut.PARTIELLEMENT_PAYE, variant: 'warning', icon: <AlertTriangle className="w-3 h-3 mr-1" /> },
    EN_ATTENTE_PAIEMENT: { label: t.facture.statut.EN_ATTENTE, variant: 'danger', icon: <AlertTriangle className="w-3 h-3 mr-1" /> },
  };

  const c = config[statut] || { label: statut, variant: 'default' as const };
  return (
    <Badge variant={c.variant} size="sm">
      {c.icon}
      {c.label}
    </Badge>
  );
};

export default Badge;
