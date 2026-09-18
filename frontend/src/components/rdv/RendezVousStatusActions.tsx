import React, { useState } from 'react';
import { rendezVousApi } from '../../services/api';
import { RendezVous } from '../../types';
import Button from '../ui/Button';
import toast from 'react-hot-toast';
import { CheckCircle, PlayCircle, CheckCheck, UserX } from 'lucide-react';
import { useTranslation } from '../../contexts/LanguageContext';

interface RendezVousStatusActionsProps {
  rdv: RendezVous;
  onChanged: (updated: RendezVous) => void;
}

/**
 * Boutons contextuels de gestion du statut d'un rendez-vous :
 * EN_ATTENTE → Confirmer / Marquer absent
 * CONFIRME   → Commencer / Marquer absent
 * EN_COURS   → Terminer
 */
const RendezVousStatusActions: React.FC<RendezVousStatusActionsProps> = ({ rdv, onChanged }) => {
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);

  const changeStatut = async (statut: string) => {
    setLoading(true);
    try {
      const res = await rendezVousApi.changerStatut(rdv.id, statut);
      toast.success(t.rendezVous.statutMisAJour);
      onChanged(res.data);
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.common.error);
    } finally {
      setLoading(false);
    }
  };

  const isTerminal = rdv.statut === 'TERMINE' || rdv.statut === 'ANNULE' || rdv.statut === 'ABSENT';

  if (isTerminal) return null;

  return (
    <div className="flex flex-wrap items-center gap-2">
      {rdv.statut === 'EN_ATTENTE' && (
        <Button
          size="sm"
          variant="outline"
          onClick={() => changeStatut('CONFIRME')}
          isLoading={loading}
          leftIcon={<CheckCircle className="w-4 h-4" />}
        >
          {t.rendezVous.statut.CONFIRME}
        </Button>
      )}
      {rdv.statut === 'CONFIRME' && (
        <Button
          size="sm"
          variant="secondary"
          onClick={() => changeStatut('EN_COURS')}
          isLoading={loading}
          leftIcon={<PlayCircle className="w-4 h-4" />}
        >
          {t.rendezVous.statut.EN_COURS}
        </Button>
      )}
      {rdv.statut === 'EN_COURS' && (
        <Button
          size="sm"
          onClick={() => changeStatut('TERMINE')}
          isLoading={loading}
          leftIcon={<CheckCheck className="w-4 h-4" />}
        >
          {t.rendezVous.statut.TERMINE}
        </Button>
      )}
      {(rdv.statut === 'EN_ATTENTE' || rdv.statut === 'CONFIRME') && (
        <Button
          size="sm"
          variant="ghost"
          onClick={() => changeStatut('ABSENT')}
          isLoading={loading}
          leftIcon={<UserX className="w-4 h-4" />}
        >
          {t.rendezVous.statut.ABSENT}
        </Button>
      )}
    </div>
  );
};

export default RendezVousStatusActions;
