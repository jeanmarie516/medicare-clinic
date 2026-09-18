import React, { useEffect, useState, useMemo, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { rendezVousApi, medecinApi } from '../services/api';
import { RendezVous } from '../types';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import { StatutBadge } from '../components/ui/Badge';
import Tabs from '../components/ui/Tabs';
import Spinner from '../components/ui/Spinner';
import Modal from '../components/ui/Modal';
import SearchBar from '../components/ui/SearchBar';
import Select from '../components/ui/Select';
import RendezVousStatusActions from '../components/rdv/RendezVousStatusActions';
import toast from 'react-hot-toast';
import { CalendarCheck, CalendarDays, User, Stethoscope, Plus, Edit, Clock, CheckCircle, PlayCircle, CheckCheck, UserX, ChevronRight, MapPin } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { useTranslation } from '../contexts/LanguageContext';

const RendezVousList: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t, language } = useTranslation();
  const [rdvs, setRdvs] = useState<RendezVous[]>([]);
  const [medecins, setMedecins] = useState<Array<{ value: number; label: string }>>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('TOUS');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedMedecin, setSelectedMedecin] = useState<number | ''>(
    () => (location.state as { selectedMedecin?: number })?.selectedMedecin ?? ''
  );
  const [selectedRdv, setSelectedRdv] = useState<RendezVous | null>(null);

  const fetchRdvs = useCallback(async () => {
    setLoading(true);
    try {
      const res = await rendezVousApi.getAll(0, 200, searchTerm || undefined, selectedMedecin || undefined);
      setRdvs(res.data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [searchTerm, selectedMedecin]);

  useEffect(() => {
    fetchRdvs();
  }, [fetchRdvs]);

  useEffect(() => {
    medecinApi.getAll().then(res =>
      setMedecins(res.data.map(m => ({ value: m.id, label: `Dr. ${m.prenom} ${m.nom} - ${m.specialite}` })))
    ).catch(console.error);
  }, []);

  const filtered = statusFilter === 'TOUS' ? rdvs : rdvs.filter(r => r.statut === statusFilter);

  // Grouper par section de date
  const groupedRdvs = useMemo(() => {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const tomorrow = new Date(today); tomorrow.setDate(tomorrow.getDate() + 1);
    const weekEnd = new Date(today); weekEnd.setDate(weekEnd.getDate() + 7);

    const sorted = [...filtered].sort((a, b) => b.dateHeure.localeCompare(a.dateHeure));

    const groups: { label: string; items: RendezVous[]; color: string; icon: React.ReactNode }[] = [];
    const passés: RendezVous[] = [];
    const auj: RendezVous[] = [];
    const demain: RendezVous[] = [];
    const semaine: RendezVous[] = [];
    const plusTard: RendezVous[] = [];

    sorted.forEach(r => {
      const d = new Date(r.dateHeure);
      if (d < today) return passés.push(r);
      if (d.toDateString() === today.toDateString()) return auj.push(r);
      if (d.toDateString() === tomorrow.toDateString()) return demain.push(r);
      if (d < weekEnd) return semaine.push(r);
      plusTard.push(r);
    });

    if (auj.length) groups.push({ label: 'Aujourd\'hui', items: auj, color: 'text-primary-600', icon: <Clock className="w-4 h-4" /> });
    if (demain.length) groups.push({ label: 'Demain', items: demain, color: 'text-blue-600', icon: <CalendarDays className="w-4 h-4" /> });
    if (semaine.length) groups.push({ label: 'Cette semaine', items: semaine, color: 'text-green-600', icon: <CalendarCheck className="w-4 h-4" /> });
    if (plusTard.length) groups.push({ label: 'Plus tard', items: plusTard, color: 'text-purple-600', icon: <CalendarDays className="w-4 h-4" /> });
    if (passés.length) groups.push({ label: 'Passés', items: passés, color: 'text-surface-400', icon: <CalendarDays className="w-4 h-4" /> });

    return groups;
  }, [filtered]);

  const changeStatutInline = async (id: number, statut: string) => {
    try {
      const res = await rendezVousApi.changerStatut(id, statut);
      toast.success(t.rendezVous.statutMisAJour);
      setRdvs(prev => prev.map(r => r.id === id ? res.data : r));
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.common.error);
    }
  };

  const annulerRdv = async (id: number) => {
    try {
      await rendezVousApi.annuler(id);
      toast.success(t.rendezVous.annulationReussie);
      setRdvs(prev => prev.map(r => r.id === id ? { ...r, statut: 'ANNULE' as any } : r));
      setSelectedRdv(null);
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.rendezVous.erreurAnnulation);
    }
  };

  const statuts = useMemo(() => [
    { id: 'TOUS', label: t.rendezVous.tous, count: rdvs.length },
    { id: 'EN_ATTENTE', label: t.rendezVous.statut.EN_ATTENTE, count: rdvs.filter(r => r.statut === 'EN_ATTENTE').length },
    { id: 'CONFIRME', label: t.rendezVous.statut.CONFIRME, count: rdvs.filter(r => r.statut === 'CONFIRME').length },
    { id: 'EN_COURS', label: t.rendezVous.statut.EN_COURS, count: rdvs.filter(r => r.statut === 'EN_COURS').length },
    { id: 'TERMINE', label: t.rendezVous.statut.TERMINE, count: rdvs.filter(r => r.statut === 'TERMINE').length },
    { id: 'ANNULE', label: t.rendezVous.statut.ANNULE, count: rdvs.filter(r => r.statut === 'ANNULE').length },
    { id: 'ABSENT', label: t.rendezVous.statut.ABSENT, count: rdvs.filter(r => r.statut === 'ABSENT').length },
  ], [rdvs, t]);

  const subtitle = `${filtered.length} / ${rdvs.length} ${t.rendezVous.title.toLowerCase()}`;

  if (loading) return <Spinner className="py-20" />;

  return (
    <div className="animate-fade-in">
      <BackButton to="/rendez-vous" />
      <PageHeader
        title={t.rendezVous.listeTitre}
        subtitle={subtitle}
        icon={<CalendarDays className="w-6 h-6" />}
        gradient
        actions={
          <div className="flex space-x-2">
            <Button onClick={() => navigate('/rendez-vous/new')} leftIcon={<Plus className="w-4 h-4" />}>
              {t.buttons.nouveauRendezVous}
            </Button>
            <Button onClick={() => navigate('/rendez-vous', { state: { selectedMedecin } })} variant="outline" leftIcon={<CalendarCheck className="w-4 h-4" />}>
              {t.rendezVous.vueCalendrier}
            </Button>
          </div>
        }
      />

      {/* Barre de recherche + Filtre médecin */}
      <div className="flex flex-col sm:flex-row gap-3 mb-4">
        <div className="flex-1">
          <SearchBar
            value={searchTerm}
            onChange={setSearchTerm}
            placeholder={t.rendezVous.rechercherPatient || 'Rechercher un patient...'}
          />
        </div>
        <div className="sm:w-64">
          <Select
            options={[
              { value: '', label: t.rendezVous.tousMedecins },
              ...medecins
            ]}
            value={selectedMedecin}
            onChange={(e) => setSelectedMedecin(e.target.value ? Number(e.target.value) : '')}
          />
        </div>
      </div>

      <Card padding="none">
        <div className="p-4 border-b border-surface-200 dark:border-surface-300/20 bg-gradient-to-r from-primary-50/50 to-transparent dark:from-primary-900/5">
          <Tabs tabs={statuts} activeTab={statusFilter} onTabChange={setStatusFilter} />
        </div>

        <div className="divide-y divide-surface-100 dark:divide-surface-300/10">
          {filtered.length === 0 ? (
            <div className="text-center py-12">
              <div className="w-16 h-16 rounded-full bg-surface-100 dark:bg-surface-200 flex items-center justify-center mx-auto mb-3">
                <CalendarDays className="w-8 h-8 text-surface-400" />
              </div>
              <p className="text-surface-500 font-medium">{t.empty.aucunRendezVousTrouve}</p>
              <p className="text-xs text-surface-400 mt-1">{t.rendezVous.essayezModifierFiltres}</p>
            </div>
          ) : (
            groupedRdvs.map((group) => (
              <div key={group.label}>
                {/* En-tête de groupe */}
                <div className="px-4 py-2 bg-surface-50/80 dark:bg-surface-100/30 flex items-center gap-2">
                  <span className={`${group.color}`}>{group.icon}</span>
                  <span className={`text-xs font-bold uppercase tracking-wider ${group.color}`}>{group.label}</span>
                  <span className="text-xs text-surface-400 bg-surface-200 dark:bg-surface-300/30 px-1.5 py-0.5 rounded-full ml-1">{group.items.length}</span>
                </div>
                {group.items.map((rdv) => {
                  const isToday = new Date(rdv.dateHeure).toDateString() === new Date().toDateString();
                  const isActive = rdv.statut === 'EN_COURS';
                  return (
                    <div
                      key={rdv.id}
                      className={`flex items-center justify-between p-4 hover:bg-primary-50/40 dark:hover:bg-primary-900/10 cursor-pointer transition-all group ${isActive ? 'bg-purple-50/50 dark:bg-purple-900/10 border-l-3 border-purple-500' : ''}`}
                      onClick={() => setSelectedRdv(rdv)}
                    >
                      <div className="flex items-center space-x-4">
                        {/* Date/Time block */}
                        <div className={`text-center min-w-[70px] p-2 rounded-xl ${
                          isToday
                            ? 'bg-primary-50 dark:bg-primary-900/20 border border-primary-200 dark:border-primary-700/30'
                            : 'bg-surface-50 dark:bg-surface-100/50'
                        }`}>
                          <p className={`text-lg font-bold font-display ${
                            isToday ? 'text-primary-600 dark:text-primary-400' : 'text-surface-800 dark:text-surface-200'
                          }`}>
                            {format(new Date(rdv.dateHeure), 'HH:mm')}
                          </p>
                          <p className={`text-xs ${
                            isToday ? 'text-primary-400' : 'text-surface-400'
                          }`}>
                            {format(new Date(rdv.dateHeure), 'dd/MM')}
                          </p>
                        </div>

                        {/* Info */}
                        <div className="flex-1 min-w-0">
                          <p className="font-semibold text-surface-900">
                            {rdv.patientPrenom} {rdv.patientNom}
                          </p>
                          <p className="text-sm text-surface-500 flex items-center mt-0.5">
                            <Stethoscope className="w-3.5 h-3.5 mr-1" />
                            Dr. {rdv.medecinPrenom} {rdv.medecinNom}
                          </p>
                          <div className="flex items-center gap-3 mt-1">
                            {rdv.motif && (
                              <p className="text-xs text-surface-400 truncate max-w-[200px]">{rdv.motif}</p>
                            )}
                            {rdv.salle && (
                              <span className="hidden sm:flex items-center text-xs text-surface-400">
                                <MapPin className="w-3 h-3 mr-0.5" />{rdv.salle}
                              </span>
                            )}
                          </div>
                        </div>
                      </div>

                      <div className="flex items-center space-x-2">
                        {/* Actions rapides inline */}
                        <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                          {rdv.statut === 'EN_ATTENTE' && (
                            <button onClick={(e) => { e.stopPropagation(); changeStatutInline(rdv.id, 'CONFIRME'); }} title="Confirmer"
                              className="p-1.5 rounded-lg bg-blue-50 text-blue-600 hover:bg-blue-100 transition-colors">
                              <CheckCircle className="w-3.5 h-3.5" />
                            </button>
                          )}
                          {rdv.statut === 'CONFIRME' && (
                            <button onClick={(e) => { e.stopPropagation(); changeStatutInline(rdv.id, 'EN_COURS'); }} title="Commencer"
                              className="p-1.5 rounded-lg bg-purple-50 text-purple-600 hover:bg-purple-100 transition-colors">
                              <PlayCircle className="w-3.5 h-3.5" />
                            </button>
                          )}
                          {rdv.statut === 'EN_COURS' && (
                            <button onClick={(e) => { e.stopPropagation(); changeStatutInline(rdv.id, 'TERMINE'); }} title="Terminer"
                              className="p-1.5 rounded-lg bg-green-50 text-green-600 hover:bg-green-100 transition-colors">
                              <CheckCheck className="w-3.5 h-3.5" />
                            </button>
                          )}
                        </div>
                        <ChevronRight className="w-4 h-4 text-surface-300 group-hover:text-primary-500 transition-colors" />
                        <StatutBadge statut={rdv.statut} />
                      </div>
                    </div>
                  );
                })}
              </div>
            ))
          )}
        </div>
      </Card>

      <Modal
        isOpen={!!selectedRdv}
        onClose={() => setSelectedRdv(null)}
        title={t.rendezVous.detailsTitre}
        footer={
          selectedRdv && (
            <div className="flex flex-wrap items-center justify-between w-full gap-2">
              <RendezVousStatusActions
                rdv={selectedRdv}
                onChanged={(updated) => {
                  setSelectedRdv(updated);
                  setRdvs(prev => prev.map(r => r.id === updated.id ? updated : r));
                }}
              />
              <div className="flex items-center space-x-2">
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => navigate(`/rendez-vous/${selectedRdv.id}/edit`)}
                  leftIcon={<Edit className="w-4 h-4" />}
                >
                  {t.common.edit}
                </Button>
                {selectedRdv.statut !== 'ANNULE' && selectedRdv.statut !== 'TERMINE' && (
                  <Button variant="danger" size="sm" onClick={() => annulerRdv(selectedRdv.id)}>
                    {t.buttons.annulerRdv}
                  </Button>
                )}
              </div>
            </div>
          )
        }
      >
        {selectedRdv && (
          <div className="space-y-4">
            {/* Date highlight */}
            <div className="flex items-center space-x-3 p-3 rounded-xl bg-primary-50 dark:bg-primary-900/15">
              <CalendarDays className="w-5 h-5 text-primary-600 dark:text-primary-400" />
              <div>
                <p className="font-semibold text-surface-900">
                  {format(new Date(selectedRdv.dateHeure), 'EEEE dd MMMM yyyy', { locale: language === 'en' ? undefined : fr })}
                </p>
                <p className="text-sm text-primary-600 dark:text-primary-400">
                  {format(new Date(selectedRdv.dateHeure), 'HH:mm')} · {selectedRdv.dureeMinutes || 30} min
                </p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="p-3 rounded-xl bg-surface-50 dark:bg-surface-100/50">
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.rendezVous.patient}</p>
                <p className="font-medium text-surface-900 flex items-center">
                  <User className="w-3.5 h-3.5 mr-1.5 text-primary-500" />
                  {selectedRdv.patientPrenom} {selectedRdv.patientNom}
                </p>
              </div>
              <div className="p-3 rounded-xl bg-surface-50 dark:bg-surface-100/50">
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.rendezVous.medecin}</p>
                <p className="font-medium text-surface-900 flex items-center">
                  <Stethoscope className="w-3.5 h-3.5 mr-1.5 text-primary-500" />
                  Dr. {selectedRdv.medecinPrenom} {selectedRdv.medecinNom}
                </p>
              </div>
              <div>
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.common.status}</p>
                <StatutBadge statut={selectedRdv.statut} />
              </div>
              {selectedRdv.salle && (
                <div>
                  <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.rendezVous.salle}</p>
                  <p className="font-medium text-surface-900">{selectedRdv.salle}</p>
                </div>
              )}
            </div>
            {selectedRdv.motif && (
              <div>
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1.5">{t.rendezVous.motif}</p>
                <p className="text-sm text-surface-700 dark:text-surface-300 bg-surface-50 dark:bg-surface-100/50 rounded-lg p-3">{selectedRdv.motif}</p>
              </div>
            )}
            {selectedRdv.notes && (
              <div>
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1.5">{t.facture.notes}</p>
                <p className="text-sm text-surface-700 dark:text-surface-300 bg-surface-50 dark:bg-surface-100/50 rounded-lg p-3">{selectedRdv.notes}</p>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default RendezVousList;
