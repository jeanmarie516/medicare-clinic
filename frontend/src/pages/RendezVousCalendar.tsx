import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { Calendar, dateFnsLocalizer, Views, DateRange } from 'react-big-calendar';
import { format, parse, startOfWeek, getDay, startOfDay, endOfDay, addDays, isToday } from 'date-fns';
import { fr, enUS } from 'date-fns/locale';
import { rendezVousApi, medecinApi } from '../services/api';
import { RendezVous } from '../types';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Spinner from '../components/ui/Spinner';
import Modal from '../components/ui/Modal';
import Tabs from '../components/ui/Tabs';
import Select from '../components/ui/Select';
import { StatutBadge } from '../components/ui/Badge';
import RendezVousStatusActions from '../components/rdv/RendezVousStatusActions';
import { useNavigate, useLocation } from 'react-router-dom';
import toast from 'react-hot-toast';
import {
  CalendarCheck, Plus, Edit, CalendarDays, User, Stethoscope,
  Clock, MapPin, MessageSquare, ChevronLeft, ChevronRight,
  CheckCircle, PlayCircle, CheckCheck, List,
} from 'lucide-react';
import { useTranslation } from '../contexts/LanguageContext';

const locales = { fr, en: enUS };
const localizer = dateFnsLocalizer({ format, parse, startOfWeek, getDay, locales });

const MEDECIN_COLORS = [
  '#14b8a6', '#8b5cf6', '#f59e0b', '#3b82f6', '#ef4444', '#10b981',
  '#ec4899', '#6366f1', '#f97316', '#06b6d4',
];

const STATUT_CONFIG: Record<string, { bg: string; text: string; border: string; dot: string; label: string }> = {
  EN_ATTENTE: { bg: '#fef3c7', text: '#92400e', border: '#fbbf24', dot: '#f59e0b', label: 'En attente' },
  CONFIRME: { bg: '#dbeafe', text: '#1e40af', border: '#60a5fa', dot: '#3b82f6', label: 'Confirmé' },
  EN_COURS: { bg: '#ede9fe', text: '#5b21b6', border: '#a78bfa', dot: '#8b5cf6', label: 'En cours' },
  TERMINE: { bg: '#d1fae5', text: '#065f46', border: '#34d399', dot: '#22c55e', label: 'Terminé' },
  ANNULE: { bg: '#f3f4f6', text: '#6b7280', border: '#d1d5db', dot: '#9ca3af', label: 'Annulé' },
  ABSENT: { bg: '#fee2e2', text: '#991b1b', border: '#f87171', dot: '#ef4444', label: 'Absent' },
};

const RendezVousCalendar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t, language } = useTranslation();
  const [rdvs, setRdvs] = useState<RendezVous[]>([]);
  const [medecins, setMedecins] = useState<Array<{ id: number; nom: string; prenom: string; specialite: string }>>([]);
  const [selectedMedecin, setSelectedMedecin] = useState<number | ''>(
    () => (location.state as { selectedMedecin?: number })?.selectedMedecin ?? ''
  );
  const [statusFilter, setStatusFilter] = useState('TOUS');
  const [loading, setLoading] = useState(true);
  const [selectedRdv, setSelectedRdv] = useState<RendezVous | null>(null);
  const [currentRange, setCurrentRange] = useState<{ start: Date; end: Date } | null>(null);
  const [viewMode, setViewMode] = useState<'week' | 'day' | 'month'>('week');
  const [currentDate, setCurrentDate] = useState(new Date());

  useEffect(() => {
    medecinApi.getAll().then(res =>
      setMedecins(res.data.map((m: any) => ({ id: m.id, nom: m.nom, prenom: m.prenom, specialite: m.specialite })))
    ).catch(console.error);
  }, []);

  const medecinOptions = useMemo(() => [
    { value: '', label: t.rendezVous.tousMedecins },
    ...medecins.map(m => ({ value: m.id, label: `Dr. ${m.prenom} ${m.nom} - ${m.specialite}` })),
  ], [medecins, t]);

  const fetchRdvs = useCallback(async (start?: Date, end?: Date) => {
    setLoading(true);
    try {
      if (start && end) {
        const res = await rendezVousApi.getForCalendar(
          format(start, "yyyy-MM-dd'T'HH:mm:ss"),
          format(end, "yyyy-MM-dd'T'HH:mm:ss"),
          selectedMedecin || undefined
        );
        setRdvs(res.data);
      } else {
        const res = await rendezVousApi.getAll(0, 300, undefined, selectedMedecin || undefined);
        setRdvs(res.data.content);
      }
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  }, [selectedMedecin]);

  useEffect(() => {
    const today = new Date();
    if (viewMode === 'week') {
      const ws = startOfWeek(today, { weekStartsOn: 1 });
      setCurrentRange({ start: ws, end: endOfDay(addDays(ws, 6)) });
    } else if (viewMode === 'day') {
      setCurrentRange({ start: startOfDay(currentDate), end: endOfDay(currentDate) });
    } else {
      const ms = startOfWeek(addDays(today, -today.getDate() + 1), { weekStartsOn: 1 });
      setCurrentRange({ start: ms, end: endOfDay(addDays(ms, 41)) });
    }
  }, [viewMode]); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    if (currentRange) fetchRdvs(currentRange.start, currentRange.end);
  }, [currentRange, fetchRdvs]);

  const handleRangeChange = (range: Date[] | DateRange) => {
    let start: Date, end: Date;
    if (Array.isArray(range)) {
      start = startOfDay(range[0]); end = endOfDay(range[range.length - 1]);
    } else {
      start = startOfDay(range.start); end = endOfDay(range.end);
    }
    setCurrentRange(prev => {
      if (prev && prev.start.getTime() === start.getTime() && prev.end.getTime() === end.getTime()) return prev;
      return { start, end };
    });
  };

  const navigateToday = () => setCurrentDate(new Date());
  const navigatePrev = () => setCurrentDate(d => viewMode === 'week' ? addDays(d, -7) : addDays(d, -1));
  const navigateNext = () => setCurrentDate(d => viewMode === 'week' ? addDays(d, 7) : addDays(d, 1));

  // Stats du jour
  const todayRdvs = useMemo(() => {
    const today = new Date();
    return rdvs.filter(r => new Date(r.dateHeure).toDateString() === today.toDateString())
      .sort((a, b) => a.dateHeure.localeCompare(b.dateHeure));
  }, [rdvs]);

  const todayStats = useMemo(() => ({
    total: todayRdvs.length,
    enCours: todayRdvs.filter(r => r.statut === 'EN_COURS').length,
    confirmes: todayRdvs.filter(r => r.statut === 'CONFIRME').length,
    enAttente: todayRdvs.filter(r => r.statut === 'EN_ATTENTE').length,
  }), [todayRdvs]);

  // Filtre par statut
  const filteredRdvs = useMemo(() => {
    if (statusFilter === 'TOUS') return rdvs;
    return rdvs.filter(r => r.statut === statusFilter);
  }, [rdvs, statusFilter]);

  const statuts = useMemo(() => [
    { id: 'TOUS', label: t.rendezVous.tous, count: rdvs.length },
    { id: 'EN_ATTENTE', label: t.rendezVous.statut.EN_ATTENTE, count: rdvs.filter(r => r.statut === 'EN_ATTENTE').length },
    { id: 'CONFIRME', label: t.rendezVous.statut.CONFIRME, count: rdvs.filter(r => r.statut === 'CONFIRME').length },
    { id: 'EN_COURS', label: t.rendezVous.statut.EN_COURS, count: rdvs.filter(r => r.statut === 'EN_COURS').length },
    { id: 'TERMINE', label: t.rendezVous.statut.TERMINE, count: rdvs.filter(r => r.statut === 'TERMINE').length },
  ], [rdvs, t]);

  // Events pour le calendrier
  const events = filteredRdvs.map(rdv => ({
    title: `Dr. ${rdv.medecinPrenom} — ${rdv.patientPrenom} ${rdv.patientNom}`,
    start: new Date(rdv.dateHeure),
    end: new Date(new Date(rdv.dateHeure).getTime() + (rdv.dureeMinutes || 30) * 60000),
    resource: rdv,
  }));

  const eventPropGetter = (event: any) => {
    const rdv = event.resource as RendezVous;
    const cancelled = rdv.statut === 'ANNULE' || rdv.statut === 'ABSENT';
    const statutConfig = STATUT_CONFIG[rdv.statut] || STATUT_CONFIG.EN_ATTENTE;
    const medecinIdx = medecins.findIndex(m => m.id === rdv.medecinId);
    const medecinColor = MEDECIN_COLORS[medecinIdx % MEDECIN_COLORS.length] || '#14b8a6';
    return {
      style: {
        backgroundColor: statutConfig.bg,
        borderRadius: '6px',
        border: `2px solid ${medecinColor}`,
        color: statutConfig.text,
        padding: '2px 6px',
        fontSize: '0.78rem',
        fontWeight: 600,
        opacity: cancelled ? 0.4 : 1,
        textDecoration: cancelled ? 'line-through' : 'none',
        boxShadow: `0 1px 4px ${medecinColor}20`,
      },
    };
  };

  const handleSelectSlot = ({ start }: { start: Date }) => {
    navigate('/rendez-vous/new', { state: { prefillDate: format(start, "yyyy-MM-dd'T'HH:mm") } });
  };

  const changeStatutInline = async (id: number, statut: string) => {
    try {
      const res = await rendezVousApi.changerStatut(id, statut);
      toast.success(t.rendezVous.statutMisAJour);
      setRdvs(prev => prev.map(r => r.id === id ? res.data : r));
      if (selectedRdv?.id === id) setSelectedRdv(res.data);
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.common.error);
    }
  };

  const headerLabel = useMemo(() => {
    if (!currentRange) return '';
    if (viewMode === 'week') {
      return `${format(currentRange.start, 'dd MMM', { locale: fr })} – ${format(currentRange.end, 'dd MMM yyyy', { locale: fr })}`;
    }
    return format(currentDate, 'EEEE dd MMMM yyyy', { locale: language === 'en' ? undefined : fr });
  }, [currentRange, viewMode, currentDate, language]);

  return (
    <div className="animate-fade-in">
      <BackButton to="/rendez-vous" />
      <PageHeader
        title={t.rendezVous.calendarTitle || 'Calendrier des Rendez-vous'}
        subtitle={`${rdvs.length} rendez-vous`}
        icon={<CalendarDays className="w-6 h-6" />}
        gradient
        actions={
          <div className="flex space-x-2">
            <Button onClick={() => navigate('/rendez-vous/new')} leftIcon={<Plus className="w-4 h-4" />}>
              {t.buttons.nouveauRendezVous}
            </Button>
            <Button onClick={() => navigate('/rendez-vous/list', { state: { selectedMedecin } })} variant="outline" leftIcon={<List className="w-4 h-4" />}>
              {t.rendezVous.listeTitre}
            </Button>
          </div>
        }
      />

      {/* Filtre médecin */}
      <div className="flex flex-col sm:flex-row gap-3 mb-4">
        <div className="sm:w-64">
          <Select
            options={medecinOptions}
            value={selectedMedecin}
            onChange={(e) => setSelectedMedecin(e.target.value ? Number(e.target.value) : '')}
          />
        </div>
      </div>

      <div className="flex flex-col xl:flex-row gap-4">
        {/* ─── Sidebar gauche ─────────────────────── */}
        <div className="w-full xl:w-64 shrink-0 space-y-4">
          {/* Stats du jour */}
          <div className="bg-white dark:bg-surface-100 rounded-xl border border-surface-200 dark:border-surface-300/30 overflow-hidden">
            <div className="px-4 py-3 bg-gradient-to-br from-primary-500 to-primary-700 text-white">
              <p className="text-xs font-bold uppercase tracking-wider opacity-80">{t.time.today}</p>
              <p className="text-sm font-bold mt-0.5">
                {format(new Date(), 'EEEE dd MMMM', { locale: fr })}
              </p>
            </div>
            <div className="p-3 grid grid-cols-2 gap-2">
              <div className="text-center p-2 rounded-lg bg-primary-50 dark:bg-primary-900/15">
                <p className="text-xl font-bold text-primary-700 dark:text-primary-300">{todayStats.total}</p>
                <p className="text-[10px] text-primary-500 uppercase font-semibold">Total</p>
              </div>
              <div className="text-center p-2 rounded-lg bg-purple-50 dark:bg-purple-900/15">
                <p className="text-xl font-bold text-purple-700 dark:text-purple-300">{todayStats.enCours}</p>
                <p className="text-[10px] text-purple-500 uppercase font-semibold">En cours</p>
              </div>
              <div className="text-center p-2 rounded-lg bg-blue-50 dark:bg-blue-900/15">
                <p className="text-xl font-bold text-blue-700 dark:text-blue-300">{todayStats.confirmes}</p>
                <p className="text-[10px] text-blue-500 uppercase font-semibold">Confirmés</p>
              </div>
              <div className="text-center p-2 rounded-lg bg-amber-50 dark:bg-amber-900/15">
                <p className="text-xl font-bold text-amber-700 dark:text-amber-300">{todayStats.enAttente}</p>
                <p className="text-[10px] text-amber-500 uppercase font-semibold">En attente</p>
              </div>
            </div>
          </div>

          {/* Légende statuts */}
          <div className="bg-white dark:bg-surface-100 rounded-xl border border-surface-200 dark:border-surface-300/30 p-4">
            <p className="text-xs font-bold text-surface-500 uppercase tracking-wider mb-2">Statuts</p>
            <div className="space-y-1.5">
              {Object.entries(STATUT_CONFIG).map(([key, cfg]) => (
                <div key={key} className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: cfg.dot }} />
                  <span className="text-xs text-surface-600">{cfg.label}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Légende médecins */}
          {medecins.length > 0 && (
            <div className="bg-white dark:bg-surface-100 rounded-xl border border-surface-200 dark:border-surface-300/30 p-4">
              <p className="text-xs font-bold text-surface-500 uppercase tracking-wider mb-2">Médecins</p>
              <div className="space-y-1.5">
                {medecins.map((m, idx) => (
                  <div key={m.id} className="flex items-center gap-2">
                    <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: MEDECIN_COLORS[idx % MEDECIN_COLORS.length] }} />
                    <span className="text-xs text-surface-600 truncate">Dr. {m.prenom}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* ─── Colonne principale ──────────────────── */}
        <div className="flex-1 min-w-0 space-y-4">
          {/* Tabs statuts */}
          <Card padding="none">
            <div className="p-3 border-b border-surface-200 dark:border-surface-300/20 bg-gradient-to-r from-primary-50/50 to-transparent dark:from-primary-900/5">
              <Tabs tabs={statuts} activeTab={statusFilter} onTabChange={setStatusFilter} />
            </div>

            {/* Navigation calendrier */}
            <div className="px-4 py-3 border-b border-surface-200 dark:border-surface-300/20 flex items-center justify-between flex-wrap gap-3">
              <div className="flex items-center gap-2">
                <Button variant="outline" size="sm" onClick={navigateToday}>{t.time.today}</Button>
                <button onClick={navigatePrev} className="p-1.5 rounded-lg hover:bg-surface-100 transition-colors">
                  <ChevronLeft className="w-4 h-4 text-surface-600" />
                </button>
                <button onClick={navigateNext} className="p-1.5 rounded-lg hover:bg-surface-100 transition-colors">
                  <ChevronRight className="w-4 h-4 text-surface-600" />
                </button>
                <span className="text-sm font-semibold text-surface-800 ml-1">{headerLabel}</span>
              </div>
              <div className="flex items-center gap-1">
                {(['week', 'day', 'month'] as const).map(v => (
                  <button
                    key={v}
                    onClick={() => setViewMode(v)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
                      viewMode === v
                        ? 'bg-primary-600 text-white shadow-sm'
                        : 'bg-surface-100 text-surface-600 hover:bg-surface-200'
                    }`}
                  >
                    {v === 'week' ? 'Semaine' : v === 'day' ? 'Jour' : 'Mois'}
                  </button>
                ))}
              </div>
            </div>

            {/* Calendar */}
            {loading ? (
              <div className="flex items-center justify-center py-20"><Spinner /></div>
            ) : (
              <div className="p-3" style={{ height: 560 }}>
                <Calendar
                  localizer={localizer}
                  events={events}
                  startAccessor="start"
                  endAccessor="end"
                  view={viewMode}
                  date={currentDate}
                  views={[Views.WEEK, Views.DAY, Views.MONTH]}
                  eventPropGetter={eventPropGetter}
                  onSelectEvent={(event) => setSelectedRdv(event.resource)}
                  onSelectSlot={handleSelectSlot}
                  onRangeChange={handleRangeChange}
                  onNavigate={(date) => setCurrentDate(date)}
                  selectable
                  popup
                  messages={{
                    today: t.time.today,
                    previous: t.common.previous,
                    next: t.common.next,
                    month: 'Mois',
                    week: 'Semaine',
                    day: 'Jour',
                    agenda: 'Agenda',
                    noEventsInRange: t.empty.aucunRendezVousTrouve,
                    showMore: (count: number) => `+${count} de plus`,
                  }}
                  formats={{
                    dateFormat: 'dd',
                    dayFormat: 'dd EEE',
                    weekdayFormat: 'EEE',
                    monthHeaderFormat: 'MMMM yyyy',
                    dayHeaderFormat: 'EEEE dd MMMM',
                    timeGutterFormat: 'HH:mm',
                  }}
                  culture={language === 'en' ? 'en' : 'fr'}
                  style={{ height: '100%' }}
                />
              </div>
            )}
          </Card>

          {/* Aujourd'hui — liste rapide */}
          {todayRdvs.length > 0 && (
            <Card padding="none">
              <div className="px-4 py-3 border-b border-surface-200 dark:border-surface-300/20 flex items-center gap-2">
                <Clock className="w-4 h-4 text-primary-500" />
                <span className="text-sm font-bold text-surface-800">{t.time.today} — {todayRdvs.length} {t.rendezVous.title}</span>
              </div>
              <div className="divide-y divide-surface-100 dark:divide-surface-300/10">
                {todayRdvs.map(rdv => {
                  const isActive = rdv.statut === 'EN_COURS';
                  return (
                    <div
                      key={rdv.id}
                      className={`flex items-center justify-between px-4 py-3 hover:bg-primary-50/40 dark:hover:bg-primary-900/10 cursor-pointer transition-all group ${isActive ? 'bg-purple-50/50 dark:bg-purple-900/10 border-l-3 border-purple-500' : ''}`}
                      onClick={() => setSelectedRdv(rdv)}
                    >
                      <div className="flex items-center space-x-3">
                        <div className="text-center min-w-[60px] p-1.5 rounded-lg bg-primary-50 dark:bg-primary-900/20 border border-primary-200/50">
                          <p className="text-sm font-bold text-primary-600 dark:text-primary-400">{format(new Date(rdv.dateHeure), 'HH:mm')}</p>
                          <p className="text-[10px] text-primary-400">{rdv.dureeMinutes || 30} min</p>
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-semibold text-surface-900">{rdv.patientPrenom} {rdv.patientNom}</p>
                          <p className="text-xs text-surface-500 flex items-center">
                            <Stethoscope className="w-3 h-3 mr-1" />
                            Dr. {rdv.medecinPrenom} {rdv.medecinNom}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center space-x-2">
                        <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                          {rdv.statut === 'EN_ATTENTE' && (
                            <button onClick={(e) => { e.stopPropagation(); changeStatutInline(rdv.id, 'CONFIRME'); }} title="Confirmer"
                              className="p-1 rounded-lg bg-blue-50 text-blue-600 hover:bg-blue-100 transition-colors">
                              <CheckCircle className="w-3.5 h-3.5" />
                            </button>
                          )}
                          {rdv.statut === 'CONFIRME' && (
                            <button onClick={(e) => { e.stopPropagation(); changeStatutInline(rdv.id, 'EN_COURS'); }} title="Commencer"
                              className="p-1 rounded-lg bg-purple-50 text-purple-600 hover:bg-purple-100 transition-colors">
                              <PlayCircle className="w-3.5 h-3.5" />
                            </button>
                          )}
                          {rdv.statut === 'EN_COURS' && (
                            <button onClick={(e) => { e.stopPropagation(); changeStatutInline(rdv.id, 'TERMINE'); }} title="Terminer"
                              className="p-1 rounded-lg bg-green-50 text-green-600 hover:bg-green-100 transition-colors">
                              <CheckCheck className="w-3.5 h-3.5" />
                            </button>
                          )}
                        </div>
                        <StatutBadge statut={rdv.statut} />
                      </div>
                    </div>
                  );
                })}
              </div>
            </Card>
          )}
        </div>
      </div>

      {/* ─── Modal Détails RDV ──────────────────── */}
      <Modal
        isOpen={!!selectedRdv}
        onClose={() => setSelectedRdv(null)}
        title={t.rendezVous.detailsTitre}
        size="md"
        footer={selectedRdv && (
          <div className="flex flex-wrap items-center justify-between w-full gap-2">
            <RendezVousStatusActions
              rdv={selectedRdv}
              onChanged={(updated) => {
                setSelectedRdv(updated);
                setRdvs(prev => prev.map(r => r.id === updated.id ? updated : r));
              }}
            />
            <div className="flex items-center space-x-2">
              <Button variant="ghost" size="sm" onClick={() => navigate(`/rendez-vous/${selectedRdv.id}/edit`)} leftIcon={<Edit className="w-4 h-4" />}>
                {t.common.edit}
              </Button>
              {selectedRdv.statut !== 'ANNULE' && selectedRdv.statut !== 'TERMINE' && (
                <Button variant="danger" size="sm" onClick={() => {
                  rendezVousApi.annuler(selectedRdv.id)
                    .then(res => { setSelectedRdv(res.data); setRdvs(prev => prev.map(r => r.id === res.data.id ? res.data : r)); })
                    .catch((err: any) => toast.error(err.response?.data?.message || t.rendezVous.erreurAnnulation));
                }}>
                  {t.buttons.annulerRdv}
                </Button>
              )}
            </div>
          </div>
        )}
      >
        {selectedRdv && (
          <div className="space-y-4">
            <div className="flex items-center space-x-3 p-3 rounded-xl bg-primary-50 dark:bg-primary-900/15">
              <CalendarCheck className="w-5 h-5 text-primary-600 dark:text-primary-400" />
              <div>
                <p className="font-semibold text-surface-900">
                  {format(new Date(selectedRdv.dateHeure), 'EEEE dd MMMM yyyy', { locale: language === 'en' ? undefined : fr })}
                </p>
                <p className="text-sm text-primary-600 dark:text-primary-400">
                  {format(new Date(selectedRdv.dateHeure), 'HH:mm')} · {selectedRdv.dureeMinutes || 30} min
                </p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
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
                <p className="text-xs text-surface-400 mt-0.5">{selectedRdv.medecinSpecialite}</p>
              </div>
              <div>
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.common.status}</p>
                <StatutBadge statut={selectedRdv.statut} />
              </div>
              {selectedRdv.salle && (
                <div>
                  <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.rendezVous.salle}</p>
                  <p className="font-medium text-surface-900 flex items-center">
                    <MapPin className="w-3.5 h-3.5 mr-1.5 text-surface-400" />
                    {selectedRdv.salle}
                  </p>
                </div>
              )}
            </div>

            {selectedRdv.motif && (
              <div className="p-3 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1 flex items-center gap-1">
                  <MessageSquare className="w-3 h-3" /> {t.rendezVous.motif}
                </p>
                <p className="text-sm text-surface-700 dark:text-surface-300">{selectedRdv.motif}</p>
              </div>
            )}
            {selectedRdv.notes && (
              <div className="p-3 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.facture.notes}</p>
                <p className="text-sm text-surface-700 dark:text-surface-300">{selectedRdv.notes}</p>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default RendezVousCalendar;
