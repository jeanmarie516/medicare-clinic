import React, { useEffect, useState, useRef } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { rendezVousApi, patientApi, medecinApi } from '../services/api';
import { RendezVous, Medecin } from '../types';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import BackButton from '../components/ui/BackButton';
import Spinner from '../components/ui/Spinner';
import toast from 'react-hot-toast';
import { Save } from 'lucide-react';
import { useTranslation } from '../contexts/LanguageContext';

const RendezVousForm: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const isEdit = !!id;

  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);

  // Patient search
  const [patientSearch, setPatientSearch] = useState('');
  const [patients, setPatients] = useState<any[]>([]);
  const [selectedPatientId, setSelectedPatientId] = useState<number | null>(null);

  // Medecins
  const [medecins, setMedecins] = useState<Medecin[]>([]);

  // Form fields
  const [medecinId, setMedecinId] = useState<number | ''>('');
  const [dateHeure, setDateHeure] = useState('');
  const [dureeMinutes, setDureeMinutes] = useState(30);
  const [motif, setMotif] = useState('');
  const [salle, setSalle] = useState('');
  const [notes, setNotes] = useState('');

  useEffect(() => {
    medecinApi.getAll().then(res => setMedecins(res.data)).catch(console.error);
  }, []);

  useEffect(() => {
    if (!id) return;
    rendezVousApi.getById(Number(id)).then(res => {
      const r = res.data;
      setPatientSearch(`${r.patientPrenom} ${r.patientNom}`);
      setSelectedPatientId(r.patientId);
      setMedecinId(r.medecinId);
      setDateHeure(r.dateHeure.slice(0, 16));
      setDureeMinutes(r.dureeMinutes || 30);
      setMotif(r.motif || '');
      setSalle(r.salle || '');
      setNotes(r.notes || '');
    }).catch(err => {
      toast.error(err.response?.data?.message || t.empty.erreurChargement);
      navigate('/rendez-vous');
    }).finally(() => setLoading(false));
  }, [id, navigate, t]);

  const searchPatients = async (q: string) => {
    setPatientSearch(q);
    if (q.length < 2) { setPatients([]); return; }
    try {
      const res = await patientApi.search(q);
      setPatients(res.data);
    } catch { /* */ }
  };

  const pickPatient = (p: any) => {
    setSelectedPatientId(p.id);
    setPatientSearch(`${p.prenom} ${p.nom}`);
    setPatients([]);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPatientId) { toast.error(t.empty.veuillezSelectionnerPatient || 'Veuillez sélectionner un patient'); return; }
    if (!medecinId) { toast.error(t.rendezVous.selectMedecin); return; }
    if (!dateHeure) { toast.error(t.rendezVous.dateHeure); return; }

    setSaving(true);
    const payload: Partial<RendezVous> = {
      patientId: selectedPatientId,
      medecinId: Number(medecinId),
      dateHeure: dateHeure.length === 16 ? `${dateHeure}:00` : dateHeure,
      dureeMinutes,
      motif: motif || undefined,
      salle: salle || undefined,
      notes: notes || undefined,
    };

    try {
      if (isEdit) {
        await rendezVousApi.update(Number(id), payload);
        toast.success(t.rendezVous.rdvModifieSucces);
      } else {
        await rendezVousApi.create(payload);
        toast.success(t.rendezVous.rdvCreeSucces);
      }
      navigate('/rendez-vous/list');
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.empty.erreurCreation);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Spinner className="py-20" />;

  return (
    <div className="max-w-3xl mx-auto animate-fade-in">
      <BackButton to="/rendez-vous/list" />
      <h1 className="text-2xl font-display font-bold text-surface-900 mb-6">
        {isEdit ? t.rendezVous.editRdv : t.rendezVous.newRdv}
      </h1>

      <form onSubmit={handleSubmit}>
        {/* Patient */}
        <Card className="mb-4">
          <h3 className="font-semibold text-surface-900 mb-4">{t.rendezVous.patient}</h3>
          <div>
            <label className="block text-sm font-medium text-surface-700 mb-1">
              {t.buttons.rechercherPatient}
            </label>
            <input
              type="text"
              value={patientSearch}
              onChange={e => searchPatients(e.target.value)}
              placeholder={t.facture.placeholderRecherchePatient}
              className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
              disabled={isEdit}
            />
            {patients.length > 0 && (
              <div className="mt-2 border rounded-lg max-h-40 overflow-y-auto">
                {patients.map(p => (
                  <div
                    key={p.id}
                    className={`p-3 cursor-pointer text-sm hover:bg-surface-50 ${selectedPatientId === p.id ? 'bg-primary-50' : ''}`}
                    onClick={() => pickPatient(p)}
                  >
                    {p.prenom} {p.nom}
                    {p.telephone && <span className="text-surface-400 ml-2">· {p.telephone}</span>}
                  </div>
                ))}
              </div>
            )}
          </div>
        </Card>

        {/* Médecin & Date */}
        <Card className="mb-4">
          <h3 className="font-semibold text-surface-900 mb-4">{t.rendezVous.medecin} & Date</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">
                {t.rendezVous.medecin}
              </label>
              <select
                value={medecinId}
                onChange={e => setMedecinId(e.target.value ? Number(e.target.value) : '')}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
                required
              >
                <option value="">{t.rendezVous.selectMedecin}</option>
                {medecins.filter(m => m.disponible).map(m => (
                  <option key={m.id} value={m.id}>
                    Dr. {m.prenom} {m.nom} — {m.specialite}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">
                {t.rendezVous.dateHeure}
              </label>
              <input
                type="datetime-local"
                value={dateHeure}
                onChange={e => setDateHeure(e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">
                {t.rendezVous.duree}
              </label>
              <select
                value={dureeMinutes}
                onChange={e => setDureeMinutes(Number(e.target.value))}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
              >
                <option value={15}>15 minutes</option>
                <option value={30}>30 minutes</option>
                <option value={45}>45 minutes</option>
                <option value={60}>1 heure</option>
                <option value={90}>1h30</option>
                <option value={120}>2 heures</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">
                {t.rendezVous.salle || 'Salle'}
              </label>
              <input
                type="text"
                value={salle}
                onChange={e => setSalle(e.target.value)}
                placeholder="Ex: Salle 101"
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
              />
            </div>
          </div>
        </Card>

        {/* Détails */}
        <Card className="mb-6">
          <h3 className="font-semibold text-surface-900 mb-4">{t.rendezVous.motif} & Notes</h3>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">
                {t.rendezVous.motif}
              </label>
              <input
                type="text"
                value={motif}
                onChange={e => setMotif(e.target.value)}
                placeholder="Consultation de routine, Suivi médical..."
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">
                {t.facture.notes}
              </label>
              <textarea
                value={notes}
                onChange={e => setNotes(e.target.value)}
                rows={3}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
              />
            </div>
          </div>
        </Card>

        <div className="flex justify-end space-x-3">
          <Button type="button" variant="ghost" onClick={() => navigate('/rendez-vous/list')}>
            {t.common.cancel}
          </Button>
          <Button type="submit" isLoading={saving} leftIcon={<Save className="w-4 h-4" />}>
            {isEdit ? t.common.save : t.buttons.nouveauRendezVous}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default RendezVousForm;
