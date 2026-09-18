import React, { useEffect, useState } from 'react';
import { rendezVousApi } from '../services/api';
import { RendezVous } from '../types';
import Card from '../components/ui/Card';
import StatCard from '../components/ui/StatCard';
import PageHeader from '../components/ui/PageHeader';
import { StatutBadge } from '../components/ui/Badge';
import Spinner from '../components/ui/Spinner';
import { CalendarCheck, Users, Clock, Stethoscope, Heart, ChevronRight } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from '../contexts/LanguageContext';

const MedecinDashboard: React.FC = () => {
  const [rdvs, setRdvs] = useState<RendezVous[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { t, language } = useTranslation();

  useEffect(() => {
    const today = new Date().toISOString().split('T')[0];
    rendezVousApi.getAll(0, 50)
      .then(res => {
        const todayRdvs = res.data.content.filter(r =>
          r.dateHeure.startsWith(today) && r.statut !== 'ANNULE'
        );
        setRdvs(todayRdvs);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;

  const enAttente = rdvs.filter(r => r.statut === 'EN_ATTENTE').length;
  const confirmes = rdvs.filter(r => r.statut === 'CONFIRME').length;
  const enCours = rdvs.filter(r => r.statut === 'EN_COURS').length;

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
        icon={<Stethoscope className="w-6 h-6" />}
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
          title={t.dashboard.rdvEnAttente}
          value={enAttente}
          icon={<Clock className="w-6 h-6" />}
          color="warning"
          description={t.dashboard.enAttentePaiement}
        />
        <StatCard
          title={t.rendezVous.statut.EN_COURS}
          value={enCours}
          icon={<Stethoscope className="w-6 h-6" />}
          color="info"
          description={t.dashboard.enCours}
        />
        <StatCard
          title={t.dashboard.confirmes}
          value={confirmes}
          icon={<CalendarCheck className="w-6 h-6" />}
          color="health"
          description={t.dashboard.rdvPlanifies}
        />
      </div>

      <Card
        title={t.dashboard.rdvDuJour}
        icon={<Heart className="w-5 h-5" fill="currentColor" />}
        action={
          <button
            onClick={() => navigate('/rendez-vous')}
            className="text-xs font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400 flex items-center"
          >
            {t.buttons.voirTout} <ChevronRight className="w-3.5 h-3.5 ml-0.5" />
          </button>
        }
      >
        {rdvs.length === 0 ? (
          <div className="text-center py-10">
            <div className="w-16 h-16 rounded-full bg-primary-50 dark:bg-primary-900/20 flex items-center justify-center mx-auto mb-4">
              <CalendarCheck className="w-8 h-8 text-primary-400" />
            </div>
            <p className="text-surface-500 font-medium">{t.empty.aucunRdv}</p>
            <p className="text-xs text-surface-400 mt-1">{t.empty.profitezAccalmie}</p>
          </div>
        ) : (
          <div className="space-y-3">
            {rdvs
              .sort((a, b) => a.dateHeure.localeCompare(b.dateHeure))
              .map((rdv, idx) => {
                const isNext = idx === 0;
                return (
                  <div
                    key={rdv.id}
                    className={`relative flex items-center justify-between p-4 rounded-xl cursor-pointer transition-all duration-200 group
                      ${isNext
                        ? 'bg-gradient-to-r from-primary-50 to-white dark:from-primary-900/15 dark:to-surface-100 border border-primary-200 dark:border-primary-700/30 shadow-sm'
                        : 'bg-surface-50 dark:bg-surface-100/40 hover:bg-primary-50/50 dark:hover:bg-primary-900/10'
                      }`}
                    onClick={() => navigate('/rendez-vous')}
                  >
                    {/* Timeline dot */}
                    <div className="absolute left-4 top-1/2 -translate-y-1/2">
                      <div className={`w-2 h-2 rounded-full ${
                        isNext ? 'bg-primary-500 healing-pulse' : 'bg-surface-300'
                      }`} />
                    </div>

                    <div className="flex items-center space-x-6 pl-6">
                      <div className="text-center min-w-[60px]">
                        <p className={`text-xl font-bold font-display ${
                          isNext ? 'text-primary-600' : 'text-surface-800'
                        }`}>
                          {format(new Date(rdv.dateHeure), 'HH:mm')}
                        </p>
                        <p className="text-xs text-surface-400">{rdv.dureeMinutes}min</p>
                      </div>
                      <div className="border-l border-surface-200 dark:border-surface-300/20 pl-4">
                        <p className="font-semibold text-surface-900">
                          {rdv.patientPrenom} {rdv.patientNom}
                        </p>
                        <p className="text-sm text-surface-500">{rdv.motif || t.dashboard.consultationGenerale}</p>
                        {rdv.salle && (
                          <p className="text-xs text-surface-400 mt-0.5">
                            <span className="inline-block w-1.5 h-1.5 rounded-full bg-primary-400 mr-1" />
                            Salle {rdv.salle}
                          </p>
                        )}
                      </div>
                    </div>
                    <StatutBadge statut={rdv.statut} />
                  </div>
                );
              })}
          </div>
        )}
      </Card>
    </div>
  );
};

export default MedecinDashboard;
