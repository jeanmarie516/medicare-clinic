import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { factureApi, patientApi } from '../services/api';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import BackButton from '../components/ui/BackButton';
import toast from 'react-hot-toast';
import { Plus, Trash2, Save, Calculator } from 'lucide-react';
import { LigneFactureItem } from '../types';
import { useTranslation } from '../contexts/LanguageContext';

const FactureForm: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [saving, setSaving] = useState(false);
  const [patientSearch, setPatientSearch] = useState('');
  const [patients, setPatients] = useState<any[]>([]);
  const [selectedPatientId, setSelectedPatientId] = useState<number | null>(null);
  const [notes, setNotes] = useState('');
  const [lignes, setLignes] = useState<LigneFactureItem[]>([
    { description: 'Consultation médicale', quantite: 1, prixUnitaire: 15000, montant: 15000 }
  ]);

  const searchPatients = async (q: string) => {
    setPatientSearch(q);
    if (q.length < 2) return;
    try {
      const res = await patientApi.search(q);
      setPatients(res.data);
    } catch (err) { /* ignore */ }
  };

  const addLigne = () => setLignes(prev => [...prev, { description: '', quantite: 1, prixUnitaire: 0, montant: 0 }]);
  const removeLigne = (idx: number) => setLignes(prev => prev.filter((_, i) => i !== idx));

  const updateLigne = (idx: number, field: string, value: string | number) => {
    setLignes(prev => prev.map((l, i) => {
      if (i !== idx) return l;
      const updated = { ...l, [field]: typeof value === 'string' ? value : value };
      if (field === 'quantite' || field === 'prixUnitaire') {
        updated.montant = updated.quantite * updated.prixUnitaire;
      }
      return updated;
    }));
  };

  const total = lignes.reduce((sum, l) => sum + l.montant, 0);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPatientId) { toast.error(t.facture.selectionnezPatient); return; }
    if (lignes.length === 0) { toast.error(t.facture.ajoutezAuMoinsLigne); return; }

    setSaving(true);
    try {
      await factureApi.create({
        patientId: selectedPatientId,
        montantTotal: total,
        tva: total * 0.18,
        ligneItemsJson: JSON.stringify(lignes),
        notes,
      });
      toast.success(t.facture.creeeSucces);
      navigate('/factures');
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.common.error);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto">
      <BackButton to="/factures" />
      <h1 className="text-2xl font-display font-bold text-surface-900 mb-6">{t.facture.newFacture}</h1>

      <form onSubmit={handleSubmit}>
        <Card className="mb-4">
          <h3 className="font-semibold text-surface-900 mb-4">{t.rendezVous.patient}</h3>
          <Input label={t.buttons.rechercherPatient} value={patientSearch} onChange={e => searchPatients(e.target.value)} placeholder={t.facture.placeholderRecherchePatient} />
          {patients.length > 0 && (
            <div className="mt-2 border rounded-lg max-h-40 overflow-y-auto">
              {patients.map(p => (
                <div key={p.id} className={`p-3 cursor-pointer text-sm hover:bg-surface-50 ${selectedPatientId === p.id ? 'bg-primary-50' : ''}`}
                  onClick={() => { setSelectedPatientId(p.id); setPatientSearch(`${p.prenom} ${p.nom}`); setPatients([]); }}>
                  {p.prenom} {p.nom}
                </div>
              ))}
            </div>
          )}
        </Card>

        <Card className="mb-4">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-surface-900">{t.facture.lignesFacturation}</h3>
            <Button type="button" variant="ghost" size="sm" onClick={addLigne} leftIcon={<Plus className="w-4 h-4" />}>{t.buttons.ajouterLigne}</Button>
          </div>
          <div className="space-y-3">
            {lignes.map((l, idx) => (
              <div key={idx} className="p-3 bg-surface-50 rounded-lg relative">
                {lignes.length > 1 && (
                  <button type="button" onClick={() => removeLigne(idx)} className="absolute top-2 right-2 text-surface-400 hover:text-red-500">
                    <Trash2 className="w-4 h-4" />
                  </button>
                )}
                <div className="grid grid-cols-4 gap-2">
                  <div className="col-span-2">
                    <label className="text-xs text-surface-500">{t.facture.ligne.description}</label>
                    <input value={l.description} onChange={e => updateLigne(idx, 'description', e.target.value)}
                      className="w-full px-2 py-1.5 text-sm border rounded" />
                  </div>
                  <div>
                    <label className="text-xs text-surface-500">{t.facture.ligne.quantite}</label>
                    <input type="number" min="1" value={l.quantite} onChange={e => updateLigne(idx, 'quantite', Number(e.target.value))}
                      className="w-full px-2 py-1.5 text-sm border rounded" />
                  </div>
                  <div>
                    <label className="text-xs text-surface-500">{t.facture.ligne.prixUnitaire}</label>
                    <input type="number" min="0" value={l.prixUnitaire} onChange={e => updateLigne(idx, 'prixUnitaire', Number(e.target.value))}
                      className="w-full px-2 py-1.5 text-sm border rounded" />
                  </div>
                </div>
                <p className="text-right text-sm font-medium text-surface-700 mt-1">{t.facture.ligne.montant}: {l.montant.toLocaleString()} FCFA</p>
              </div>
            ))}
          </div>
          <div className="mt-4 p-3 bg-primary-50 rounded-lg flex justify-between items-center">
            <span className="font-semibold text-primary-700">{t.facture.total}</span>
            <span className="text-xl font-bold text-primary-700">{total.toLocaleString()} FCFA</span>
          </div>
        </Card>

        <Card className="mb-6">
          <h3 className="font-semibold text-surface-900 mb-4">{t.facture.notes}</h3>
          <textarea value={notes} onChange={e => setNotes(e.target.value)} rows={2}
            className="w-full rounded-lg border border-surface-300 px-3 py-2.5 text-sm focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </Card>

        <div className="flex justify-end space-x-3">
          <Button type="button" variant="ghost" onClick={() => navigate('/factures')}>{t.common.cancel}</Button>
          <Button type="submit" isLoading={saving} leftIcon={<Save className="w-4 h-4" />}>{t.buttons.creerFacture}</Button>
        </div>
      </form>
    </div>
  );
};

export default FactureForm;
