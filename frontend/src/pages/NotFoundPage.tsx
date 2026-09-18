import React from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../components/ui/Button';
import { Home } from 'lucide-react';

const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <div className="min-h-screen flex items-center justify-center bg-surface-50">
      <div className="text-center">
        <h1 className="text-9xl font-display font-bold text-primary-600">404</h1>
        <h2 className="text-2xl font-semibold text-surface-900 mt-4">Page Non Trouvée</h2>
        <p className="text-surface-500 mt-2">La page que vous cherchez n'existe pas.</p>
        <Button onClick={() => navigate('/dashboard')} className="mt-6" leftIcon={<Home className="w-4 h-4" />}>
          Retour au Dashboard
        </Button>
      </div>
    </div>
  );
};

export default NotFoundPage;
