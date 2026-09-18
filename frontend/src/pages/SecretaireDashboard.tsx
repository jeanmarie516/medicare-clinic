import React, { useEffect, useState } from 'react';
import { rendezVousApi, factureApi } from '../services/api';
import { RendezVous, Facture } from '../types';
import Card from '../components/ui/Card';
import StatCard from '../components/ui/StatCard';
import PageHeader from '../components/ui/PageHeader';
import { StatutBadge } from '../components/ui/Badge';
import Spinner from '../components/ui/Spinner';
import { CalendarCheck, DollarSign, Users, Clock, ClipboardList, AlertTriangle } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { useTranslation } from '../contexts/LanguageContext';

const SecretaireDashboard: React.FC = () => {
  const { t, language } = useTranslation();
  const [rdvs, setRdvs] = useState<RendezVous[]>([]);
  const [impayees, setImpayees] = useState<Facture[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      rendezVousApi.getAll(0, 50),
      factureApi.getImpayees(),
    ]).then(([rdvRes, factRes]) => {
      const today = new Date().toISOString().split('T')[0];
      setRdvs(rdvRes.data.content.filter(r => r.dateHeure.startsWith(today)));
      setImpayees(factRes.data);
    }).catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title={t.dashboard.title}
        subtitle={
          <span className="flex items-center text-primary-700/70">
            <span className="inline-block w-2 h-2 rounded-full bg-health-400 animate-pulse mr-2" />
            {format(new Date(), 'EEEE d MMMM yyyy', { locale: language === 'en' ? undefined : fr })}
          </span>
        }
        icon={<ClipboardList className="w-6 h-6" />}
        gradient
      />

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title={t.dashboard.rdvAujourdhui}
          value={rdvs.length}
          icon={<CalendarCheck className="w-6 h-6" />}
          color="primary"
          description={t.dashboard.consultationsJour}
        />
        <StatCard
          title={t.dashboard.facturesImpayees}
          value={impayees.length}
          icon={<DollarSign className="w-6 h-6" />}
          color="danger"
          description={t.dashboard.enAttentePaiement}
        />
        <StatCard
          title={t.dashboard.rdvEnAttente}
          value={rdvs.filter(r => r.statut === 'EN_ATTENTE').length}
          icon={<Users className="w-6 h-6" />}
          color="warning"
          description={t.dashboard.patientsSalleAttente}
        />
        <StatCard
          title={t.dashboard.confirmes}
          value={rdvs.filter(r => r.statut === 'CONFIRME').length}
          icon={<Clock className="w-6 h-6" />}
          color="health"
          description={t.dashboard.rdvPlanifies}
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title={t.dashboard.rdvDuJour} icon={<CalendarCheck className="w-5 h-5" />}>
          {rdvs.length === 0 ? (
            <p className="text-center text-surface-400 py-8">{t.empty.aucunRdv}</p>
          ) : (
            <div className="space-y-2">
              {rdvs
                .sort((a, b) => a.dateHeure.localeCompare(b.dateHeure))
                .map((rdv, idx) => (
                  <div
                    key={rdv.id}
                    className="flex items-center justify-between p-3 rounded-xl bg-surface-50 dark:bg-surface-100/40 hover:bg-primary-50/50 dark:hover:bg-primary-900/10 transition-all group"
                  >
                    <div className="flex items-center space-x-3">
                      {/* Time indicator */}
                      <div className="flex flex-col items-center min-w-[44px]">
                        <p className="text-sm font-bold text-primary-600 dark:text-primary-400">
                          {format(new Date(rdv.dateHeure), 'HH:mm')}
                        </p>
                        <div className={`w-1.5 h-1.5 rounded-full mt-0.5 ${
                          rdv.statut === 'CONFIRME' ? 'bg-secondary-500' :
                          rdv.statut === 'EN_ATTENTE' ? 'bg-accent-500' :
                          rdv.statut === 'EN_COURS' ? 'bg-purple-500' :
                          'bg-surface-300'
                        }`} />
                      </div>
                      <div>
                        <p className="text-sm font-semibold text-surface-900">
                          {rdv.patientPrenom} {rdv.patientNom}
                        </p>
                        <p className="text-xs text-surface-500">
                          Dr. {rdv.medecinPrenom} {rdv.medecinNom}
                          {rdv.salle && <span> · Salle {rdv.salle}</span>}
                        </p>
                      </div>
                    </div>
                    <StatutBadge statut={rdv.statut} />
                  </div>
                ))}
            </div>
          )}
        </Card>

        <Card title={t.dashboard.facturesImpayees} icon={<AlertTriangle className="w-5 h-5" />}>
          {impayees.length === 0 ? (
            <div className="text-center py-8">
              <div className="w-14 h-14 rounded-full bg-health-50 dark:bg-health-900/20 flex items-center justify-center mx-auto mb-3">
                <DollarSign className="w-7 h-7 text-health-500" />
              </div>
              <p className="text-surface-500 font-medium">{t.empty.toutesFacturesSoldees}</p>
              <p className="text-xs text-surface-400 mt-1">{t.empty.aucuneImpayee}</p>
            </div>
          ) : (
            <div className="space-y-2">
              {impayees.map(f => (
                <div
                  key={f.id}
                  className="flex items-center justify-between p-3 rounded-xl bg-gradient-to-r from-danger-50 to-white dark:from-danger-900/10 dark:to-surface-100 border border-danger-200 dark:border-danger-700/20"
                >
                  <div className="flex items-center space-x-3">
                    <div className="w-9 h-9 rounded-lg bg-danger-100 dark:bg-danger-900/30 flex items-center justify-center">
                      <DollarSign className="w-5 h-5 text-danger-600 dark:text-danger-400" />
                    </div>
                    <div>
                      <p className="text-sm font-semibold text-surface-900">
                        {f.patientPrenom} {f.patientNom}
                      </p>
                      <p className="text-xs text-surface-500">{f.numeroFacture}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-bold text-danger-600 dark:text-danger-400">{f.montantRestant.toLocaleString()} FCFA</p>
                    <p className="text-xs text-surface-400">{t.dashboard.restantDu}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};

export default SecretaireDashboard;
