import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { patientApi } from '../services/api';
import { Patient } from '../types';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import BackButton from '../components/ui/BackButton';
import toast from 'react-hot-toast';
import { useTranslation } from '../contexts/LanguageContext';
import { Save } from 'lucide-react';

const PatientForm: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const isEdit = !!id;

  const [formData, setFormData] = useState<Partial<Patient>>({
    nom: '',
    prenom: '',
    dateNaissance: '',
    sexe: 'M',
    telephone: '',
    email: '',
    adresse: '',
    groupeSanguin: '',
    allergies: '',
    antecedents: '',
    personneContactNom: '',
    personneContactTelephone: '',
  });
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (id) {
      patientApi.getById(Number(id)).then(res => {
        const p = res.data;
        setFormData({
          ...p,
          dateNaissance: p.dateNaissance ? p.dateNaissance.split('T')[0] : '',
        });
      }).catch(console.error);
    }
  }, [id]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      if (isEdit) {
        await patientApi.update(Number(id), formData);
        toast.success(t.common.update);
      } else {
        await patientApi.create(formData);
        toast.success(t.common.create);
      }
      navigate('/patients');
    } catch (err) {
      toast.error(t.empty.erreurEnregistrement);
    } finally {
      setSaving(false);
    }
  };

  const updateField = (field: string, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  return (
    <div className="max-w-3xl mx-auto animate-fade-in">
      <BackButton to="/patients" />

      <h1 className="text-2xl font-display font-bold text-surface-900 mb-6">
        {isEdit ? t.buttons.modifierPatient : t.buttons.nouveauPatient}
      </h1>

      <form onSubmit={handleSubmit}>
        <Card>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.nom}</label>
              <input type="text" value={formData.nom || ''} onChange={e => updateField('nom', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.prenom}</label>
              <input type="text" value={formData.prenom || ''} onChange={e => updateField('prenom', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.dateNaissance}</label>
              <input type="date" value={formData.dateNaissance || ''} onChange={e => updateField('dateNaissance', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.sexe}</label>
              <select value={formData.sexe || 'M'} onChange={e => updateField('sexe', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500">
                <option value="M">{t.patient.homme}</option>
                <option value="F">{t.patient.femme}</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.telephone}</label>
              <input type="tel" value={formData.telephone || ''} onChange={e => updateField('telephone', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">Email</label>
              <input type="email" value={formData.email || ''} onChange={e => updateField('email', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.adresse}</label>
              <input type="text" value={formData.adresse || ''} onChange={e => updateField('adresse', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.groupeSanguin}</label>
              <select value={formData.groupeSanguin || ''} onChange={e => updateField('groupeSanguin', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500">
                <option value="">-</option>
                <option value="A+">A+</option>
                <option value="A-">A-</option>
                <option value="B+">B+</option>
                <option value="B-">B-</option>
                <option value="AB+">AB+</option>
                <option value="AB-">AB-</option>
                <option value="O+">O+</option>
                <option value="O-">O-</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.allergies}</label>
              <input type="text" value={formData.allergies || ''} onChange={e => updateField('allergies', e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-surface-700 mb-1">{t.patient.antecedents}</label>
              <textarea value={formData.antecedents || ''} onChange={e => updateField('antecedents', e.target.value)} rows={3}
                className="w-full px-3 py-2 rounded-lg border border-surface-300 focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
            </div>
          </div>
        </Card>

        <div className="flex justify-end mt-6 space-x-3">
          <Button variant="ghost" onClick={() => navigate('/patients')}>{t.common.cancel}</Button>
          <Button type="submit" leftIcon={<Save className="w-4 h-4" />} isLoading={saving}>{t.common.save}</Button>
        </div>
      </form>
    </div>
  );
};

export default PatientForm;
