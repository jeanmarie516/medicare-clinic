import React from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../components/ui/Button';
import { ArrowLeft, ShieldAlert } from 'lucide-react';

const ForbiddenPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <div className="min-h-screen flex items-center justify-center bg-surface-50">
      <div className="text-center">
        <ShieldAlert className="w-20 h-20 mx-auto text-danger-500 mb-4" />
        <h1 className="text-6xl font-display font-bold text-surface-900">403</h1>
        <h2 className="text-2xl font-semibold text-surface-900 mt-4">Accès Refusé</h2>
        <p className="text-surface-500 mt-2">Vous n'avez pas les droits nécessaires pour accéder à cette page.</p>
        <Button onClick={() => navigate('/dashboard')} className="mt-6" leftIcon={<ArrowLeft className="w-4 h-4" />}>
          Retour au Dashboard
        </Button>
      </div>
    </div>
  );
};

export default ForbiddenPage;
