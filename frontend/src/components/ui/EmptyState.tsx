import React from 'react';
import { Inbox } from 'lucide-react';
import { useTranslation } from '../../contexts/LanguageContext';

interface EmptyStateProps {
  icon?: React.ReactNode;
  title?: string;
  description?: string;
  action?: React.ReactNode;
}

const EmptyState: React.FC<EmptyStateProps> = ({
  icon, title, description, action
}) => {
  const { t } = useTranslation();
  const displayTitle = title || t.empty.aucuneDonnee;
  const displayDescription = description || t.empty.rienAfficher;

  return (
    <div className="flex flex-col items-center justify-center py-12 px-4">
      <div className="text-surface-300 mb-4">
        {icon || <Inbox className="w-16 h-16" />}
      </div>
      <h3 className="text-lg font-medium text-surface-900">{displayTitle}</h3>
      <p className="mt-1 text-sm text-surface-500 text-center max-w-sm">{displayDescription}</p>
      {action && <div className="mt-4">{action}</div>}
    </div>
  );
};

export default EmptyState;
