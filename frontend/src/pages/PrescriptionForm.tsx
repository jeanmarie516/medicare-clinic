import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { prescriptionApi, patientApi, rendezVousApi } from '../services/api';
import { MedicamentItem } from '../types';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import BackButton from '../components/ui/BackButton';
import toast from 'react-hot-toast';
import { Plus, Trash2, Save, Pill } from 'lucide-react';
import { useTranslation } from '../contexts/LanguageContext';

const PrescriptionForm: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [saving, setSaving] = useState(false);
  const [patientSearch, setPatientSearch] = useState('');
  const [patients, setPatients] = useState<any[]>([]);
  const [selectedPatientId, setSelectedPatientId] = useState<number | null>(null);
  const [diagnostic, setDiagnostic] = useState('');
  const [instructions, setInstructions] = useState('');
  const [medicaments, setMedicaments] = useState<MedicamentItem[]>([
    { nom: '', dosage: '', frequence: '', duree: '' }
  ]);

  const searchPatients = async (q: string) => {
    setPatientSearch(q);
    if (q.length < 2) return;
    try {
      const res = await patientApi.search(q);
      setPatients(res.data);
    } catch (err) { /* ignore */ }
  };

  const addMedicament = () => {
    setMedicaments(prev => [...prev, { nom: '', dosage: '', frequence: '', duree: '' }]);
  };

  const removeMedicament = (index: number) => {
    setMedicaments(prev => prev.filter((_, i) => i !== index));
  };

  const updateMedicament = (index: number, field: string, value: string) => {
    setMedicaments(prev => prev.map((m, i) => i === index ? { ...m, [field]: value } : m));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPatientId) { toast.error(t.empty.veuillezSelectionnerPatient); return; }
    if (medicaments.length === 0 || !medicaments[0].nom) { toast.error(t.empty.ajoutezAuMoinsMedicament); return; }

    setSaving(true);
    try {
      await prescriptionApi.create({
        patientId: selectedPatientId,
        medicamentsJson: JSON.stringify(medicaments.filter(m => m.nom)),
        diagnostic,
        instructions,
      });
      toast.success(`${t.prescription.title} ${t.empty.creationSucces}`);
      navigate('/prescriptions');
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.empty.erreurCreation);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto">
      <BackButton to="/prescriptions" />
      <h1 className="text-2xl font-display font-bold text-surface-900 mb-6">{t.prescription.newPrescription}</h1>

      <form onSubmit={handleSubmit}>
        <Card className="mb-4">
          <h3 className="font-semibold text-surface-900 mb-4">{t.rendezVous.patient}</h3>
          <Input label={t.buttons.rechercherPatient} value={patientSearch} onChange={e => searchPatients(e.target.value)} placeholder={t.patient.searchPlaceholder} />
          {patients.length > 0 && (
            <div className="mt-2 border border-surface-200 rounded-lg max-h-40 overflow-y-auto">
              {patients.map(p => (
                <div key={p.id} className={`p-3 cursor-pointer text-sm hover:bg-surface-50 ${selectedPatientId === p.id ? 'bg-primary-50 text-primary-700' : ''}`}
                  onClick={() => { setSelectedPatientId(p.id); setPatientSearch(`${p.prenom} ${p.nom}`); setPatients([]); }}>
                  {p.prenom} {p.nom} - {p.telephone || t.common.none}
                </div>
              ))}
            </div>
          )}
        </Card>

        <Card className="mb-4">
          <h3 className="font-semibold text-surface-900 mb-4">{t.prescription.diagnostic}</h3>
          <textarea value={diagnostic} onChange={e => setDiagnostic(e.target.value)} rows={2}
            className="w-full rounded-lg border border-surface-300 px-3 py-2.5 text-sm focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
            placeholder={t.prescription.placeholderDiagnostic} />
        </Card>

        <Card className="mb-4">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-surface-900">{t.prescription.medicaments}</h3>
            <Button type="button" variant="ghost" size="sm" onClick={addMedicament} leftIcon={<Plus className="w-4 h-4" />}>
              {t.buttons.ajouterMedicament}
            </Button>
          </div>
          <div className="space-y-3">
            {medicaments.map((med, idx) => (
              <div key={idx} className="p-3 bg-surface-50 rounded-lg relative">
                {medicaments.length > 1 && (
                  <button type="button" onClick={() => removeMedicament(idx)} className="absolute top-2 right-2 text-surface-400 hover:text-red-500">
                    <Trash2 className="w-4 h-4" />
                  </button>
                )}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                  <Input label={t.prescription.medicament} value={med.nom} onChange={e => updateMedicament(idx, 'nom', e.target.value)} placeholder={t.prescription.placeholderMedicament} />
                  <Input label={t.prescription.dosage} value={med.dosage} onChange={e => updateMedicament(idx, 'dosage', e.target.value)} placeholder={t.prescription.placeholderDosage} />
                  <Input label={t.prescription.frequence} value={med.frequence} onChange={e => updateMedicament(idx, 'frequence', e.target.value)} placeholder={t.prescription.placeholderFrequence} />
                  <Input label={t.prescription.dureeTraitement} value={med.duree} onChange={e => updateMedicament(idx, 'duree', e.target.value)} placeholder={t.prescription.placeholderDuree} />
                </div>
              </div>
            ))}
          </div>
        </Card>

        <Card className="mb-6">
          <h3 className="font-semibold text-surface-900 mb-4">{t.prescription.instructions}</h3>
          <textarea value={instructions} onChange={e => setInstructions(e.target.value)} rows={3}
            className="w-full rounded-lg border border-surface-300 px-3 py-2.5 text-sm focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
            placeholder={t.prescription.placeholderInstructions} />
        </Card>

        <div className="flex justify-end space-x-3">
          <Button type="button" variant="ghost" onClick={() => navigate('/prescriptions')}>{t.common.cancel}</Button>
          <Button type="submit" isLoading={saving} leftIcon={<Save className="w-4 h-4" />}>{t.buttons.creerPrescription}</Button>
        </div>
      </form>
    </div>
  );
};

export default PrescriptionForm;
