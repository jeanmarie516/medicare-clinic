import React, { useEffect, useState } from 'react';
import { medecinApi } from '../services/api';
import { Medecin } from '../types';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Spinner from '../components/ui/Spinner';
import { useTranslation } from '../contexts/LanguageContext';
import { Stethoscope, Phone, Mail, Award, Clock } from 'lucide-react';

const MedecinsList: React.FC = () => {
  const { t } = useTranslation();
  const [medecins, setMedecins] = useState<Medecin[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    medecinApi.getAll()
      .then(res => setMedecins(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;

  return (
    <div>
      <BackButton to="/dashboard" label={t.buttons.retourDashboard} />
      <PageHeader title={t.medecin.title} subtitle={t.medecin.title} />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {medecins.map(m => (
          <Card key={m.id} hover>
            <div className="flex items-start space-x-4">
              <div className="w-14 h-14 bg-primary-100 rounded-full flex items-center justify-center flex-shrink-0">
                <span className="text-lg font-bold text-primary-700">{m.prenom[0]}{m.nom[0]}</span>
              </div>
              <div className="flex-1 min-w-0">
                <h3 className="font-semibold text-surface-900">Dr. {m.prenom} {m.nom}</h3>
                <Badge variant="info" size="sm">{m.specialite}</Badge>
                <div className="mt-2 space-y-1 text-sm text-surface-500">
                  {m.email && <span className="flex items-center"><Mail className="w-3.5 h-3.5 mr-1.5 flex-shrink-0" />{m.email}</span>}
                  {m.telephone && <span className="flex items-center"><Phone className="w-3.5 h-3.5 mr-1.5 flex-shrink-0" />{m.telephone}</span>}
                  {m.anneesExperience && <span className="flex items-center"><Award className="w-3.5 h-3.5 mr-1.5 flex-shrink-0" />{m.anneesExperience} ans d'expérience</span>}
                  {m.tarifConsultation && <span className="flex items-center"><Clock className="w-3.5 h-3.5 mr-1.5 flex-shrink-0" />{m.tarifConsultation.toLocaleString()} FCFA / consultation</span>}
                </div>
                <div className="mt-3">
                  {m.disponible ? (
                    <Badge variant="success" size="sm">{t.medecin.disponible}</Badge>
                  ) : (
                    <Badge variant="danger" size="sm">{t.medecin.indisponible}</Badge>
                  )}
                  {m.numeroOrdre && <span className="ml-2 text-xs text-surface-400">N° {m.numeroOrdre}</span>}
                </div>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default MedecinsList;
