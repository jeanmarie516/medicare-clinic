import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { clsx } from 'clsx';
import { useTranslation } from '../../contexts/LanguageContext';

interface BackButtonProps {
  /** Route vers laquelle revenir. Par défaut : historique du navigateur */
  to?: string;
  /** Libellé personnalisé (par défaut : t.common.back) */
  label?: string;
  className?: string;
}

const BackButton: React.FC<BackButtonProps> = ({ to, label, className }) => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <button
      type="button"
      onClick={() => (to ? navigate(to) : navigate(-1))}
      className={clsx(
        'group inline-flex items-center text-sm font-medium text-surface-500 hover:text-primary-600 dark:text-surface-400 dark:hover:text-primary-400 mb-4 transition-colors',
        className
      )}
    >
      <ArrowLeft className="w-4 h-4 mr-1.5 transition-transform group-hover:-translate-x-0.5" />
      {label ?? t.common.back}
    </button>
  );
};

export default BackButton;
