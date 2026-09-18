import React, { useEffect, useState } from 'react';
import { configurationApi } from '../services/api';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Card from '../components/ui/Card';
import Input from '../components/ui/Input';
import Button from '../components/ui/Button';
import Spinner from '../components/ui/Spinner';
import toast from 'react-hot-toast';
import { Settings, Building2, CalendarCheck, Receipt, Bell, RotateCcw, Save, Phone, Mail, Clock, MapPin, Hash, DollarSign, Percent, FileText, Volume2, MessageCircle, Users } from 'lucide-react';
import { useTranslation } from '../contexts/LanguageContext';
import { useAuthStore } from '../store/authStore';

const ConfigurationPage: React.FC = () => {
  const { t } = useTranslation();
  const { user } = useAuthStore();
  const [configs, setConfigs] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [resetting, setResetting] = useState(false);
  const [activeTab, setActiveTab] = useState<'clinique' | 'rdv' | 'facturation' | 'notification'>('clinique');

  useEffect(() => {
    loadConfigs();
  }, []);

  const loadConfigs = async () => {
    try {
      const res = await configurationApi.getAll();
      setConfigs(res.data);
    } catch (err) {
      toast.error(t.common.error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (key: string, value: string) => {
    setConfigs(prev => ({ ...prev, [key]: value }));
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      await configurationApi.save(configs);
      toast.success(t.config.saveSuccess);
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.common.error);
    } finally {
      setSaving(false);
    }
  };

  const handleReset = async () => {
    if (!window.confirm(t.config.confirmReset)) return;
    setResetting(true);
    try {
      const res = await configurationApi.resetDefaults();
      setConfigs(res.data.configurations);
      toast.success(t.config.resetSuccess);
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.common.error);
    } finally {
      setResetting(false);
    }
  };

  if (loading) return <Spinner className="py-20" />;

  const tabs = [
    { id: 'clinique' as const, label: t.config.tabs.clinique, icon: Building2 },
    { id: 'rdv' as const, label: t.config.tabs.rdv, icon: CalendarCheck },
    { id: 'facturation' as const, label: t.config.tabs.facturation, icon: Receipt },
    { id: 'notification' as const, label: t.config.tabs.notification, icon: Bell },
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      <BackButton to="/dashboard" label={t.buttons.retourDashboard} />
      <PageHeader
        title={t.config.title}
        subtitle={t.config.subtitle}
        icon={<Settings className="w-6 h-6" />}
        gradient
        actions={
          <div className="flex items-center space-x-2">
            <Button
              variant="ghost"
              leftIcon={<RotateCcw className="w-4 h-4" />}
              onClick={handleReset}
              isLoading={resetting}
              size="sm"
            >
              {t.config.resetDefaults}
            </Button>
            <Button
              leftIcon={<Save className="w-4 h-4" />}
              onClick={handleSave}
              isLoading={saving}
              size="sm"
            >
              {t.common.save}
            </Button>
          </div>
        }
      />

      {/* Tabs */}
      <div className="flex flex-wrap gap-2">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center px-4 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 ${
              activeTab === tab.id
                ? 'bg-primary-600 text-white shadow-md shadow-primary-500/20'
                : 'bg-white dark:bg-surface-100 text-surface-600 hover:bg-surface-50 border border-surface-200 dark:border-surface-300/30'
            }`}
          >
            <tab.icon className="w-4 h-4 mr-2" />
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      {activeTab === 'clinique' && (
        <Card title={t.config.clinique.title} icon={<Building2 className="w-5 h-5" />}>
          <div className="space-y-4">
            <Input
              label={t.config.clinique.nom}
              value={configs['clinique.nom'] || ''}
              onChange={(e) => handleChange('clinique.nom', e.target.value)}
              leftIcon={<Building2 className="w-4 h-4" />}
            />
            <Input
              label={t.config.clinique.adresse}
              value={configs['clinique.adresse'] || ''}
              onChange={(e) => handleChange('clinique.adresse', e.target.value)}
              leftIcon={<MapPin className="w-4 h-4" />}
            />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label={t.config.clinique.telephone}
                value={configs['clinique.telephone'] || ''}
                onChange={(e) => handleChange('clinique.telephone', e.target.value)}
                leftIcon={<Phone className="w-4 h-4" />}
              />
              <Input
                label={t.config.clinique.email}
                type="email"
                value={configs['clinique.email'] || ''}
                onChange={(e) => handleChange('clinique.email', e.target.value)}
                leftIcon={<Mail className="w-4 h-4" />}
              />
            </div>
            <Input
              label={t.config.clinique.horaires}
              value={configs['clinique.horaires'] || ''}
              onChange={(e) => handleChange('clinique.horaires', e.target.value)}
              leftIcon={<Clock className="w-4 h-4" />}
              helperText={t.config.clinique.horairesHelp}
            />
            <Input
              label={t.config.clinique.logo}
              value={configs['clinique.logo'] || ''}
              onChange={(e) => handleChange('clinique.logo', e.target.value)}
              helperText={t.config.clinique.logoHelp}
            />
          </div>
        </Card>
      )}

      {activeTab === 'rdv' && (
        <Card title={t.config.rdv.title} icon={<CalendarCheck className="w-5 h-5" />}>
          <div className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label={t.config.rdv.dureeDefaut}
                type="number"
                value={configs['rdv.duree_defaut'] || '30'}
                onChange={(e) => handleChange('rdv.duree_defaut', e.target.value)}
                leftIcon={<Clock className="w-4 h-4" />}
                helperText={t.config.rdv.dureeDefautHelp}
              />
              <Input
                label={t.config.rdv.dureeMax}
                type="number"
                value={configs['rdv.duree_max'] || '120'}
                onChange={(e) => handleChange('rdv.duree_max', e.target.value)}
                leftIcon={<Clock className="w-4 h-4" />}
              />
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label={t.config.rdv.rappelHeures}
                type="number"
                value={configs['rdv.rappel_heures'] || '24'}
                onChange={(e) => handleChange('rdv.rappel_heures', e.target.value)}
                leftIcon={<Bell className="w-4 h-4" />}
                helperText={t.config.rdv.rappelHeuresHelp}
              />
              <Input
                label={t.config.rdv.annulationHeures}
                type="number"
                value={configs['rdv.annulation_heures'] || '24'}
                onChange={(e) => handleChange('rdv.annulation_heures', e.target.value)}
                leftIcon={<CalendarCheck className="w-4 h-4" />}
                helperText={t.config.rdv.annulationHeuresHelp}
              />
            </div>
            <Input
              label={t.config.rdv.salleDefaut}
              value={configs['rdv.salle_defaut'] || ''}
              onChange={(e) => handleChange('rdv.salle_defaut', e.target.value)}
              helperText={t.config.rdv.salleDefautHelp}
            />
            <div className="flex items-center space-x-3 p-4 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
              <input
                type="checkbox"
                id="numero_rdv_auto"
                checked={configs['rdv.numero_rdv_auto'] === 'true'}
                onChange={(e) => handleChange('rdv.numero_rdv_auto', e.target.checked ? 'true' : 'false')}
                className="w-4 h-4 rounded border-surface-300 text-primary-600 focus:ring-primary-500"
              />
              <label htmlFor="numero_rdv_auto" className="text-sm text-surface-700">
                {t.config.rdv.numeroAuto}
              </label>
            </div>
          </div>
        </Card>
      )}

      {activeTab === 'facturation' && (
        <Card title={t.config.facturation.title} icon={<Receipt className="w-5 h-5" />}>
          <div className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label={t.config.facturation.devise}
                value={configs['facturation.devise'] || 'FCFA'}
                onChange={(e) => handleChange('facturation.devise', e.target.value)}
                leftIcon={<DollarSign className="w-4 h-4" />}
              />
              <Input
                label={t.config.facturation.tvaDefaut}
                type="number"
                value={configs['facturation.tva_defaut'] || '0'}
                onChange={(e) => handleChange('facturation.tva_defaut', e.target.value)}
                leftIcon={<Percent className="w-4 h-4" />}
                helperText={t.config.facturation.tvaDefautHelp}
              />
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label={t.config.facturation.prefix}
                value={configs['facturation.prefix'] || 'FACT-'}
                onChange={(e) => handleChange('facturation.prefix', e.target.value)}
                leftIcon={<Hash className="w-4 h-4" />}
              />
              <Input
                label={t.config.facturation.echeanceJours}
                type="number"
                value={configs['facturation.echeance_jours'] || '30'}
                onChange={(e) => handleChange('facturation.echeance_jours', e.target.value)}
                leftIcon={<CalendarCheck className="w-4 h-4" />}
                helperText={t.config.facturation.echeanceJoursHelp}
              />
            </div>
            <div className="space-y-3">
              <div className="flex items-center space-x-3 p-4 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
                <input
                  type="checkbox"
                  id="numero_auto"
                  checked={configs['facturation.numero_auto'] === 'true'}
                  onChange={(e) => handleChange('facturation.numero_auto', e.target.checked ? 'true' : 'false')}
                  className="w-4 h-4 rounded border-surface-300 text-primary-600 focus:ring-primary-500"
                />
                <label htmlFor="numero_auto" className="text-sm text-surface-700">
                  {t.config.facturation.numeroAuto}
                </label>
              </div>
              <div className="flex items-center space-x-3 p-4 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
                <input
                  type="checkbox"
                  id="remise_auto"
                  checked={configs['facturation.remise_auto'] === 'true'}
                  onChange={(e) => handleChange('facturation.remise_auto', e.target.checked ? 'true' : 'false')}
                  className="w-4 h-4 rounded border-surface-300 text-primary-600 focus:ring-primary-500"
                />
                <label htmlFor="remise_auto" className="text-sm text-surface-700">
                  {t.config.facturation.remiseAuto}
                </label>
              </div>
            </div>
          </div>
        </Card>
      )}

      {activeTab === 'notification' && (
        <Card title={t.config.notification.title} icon={<Bell className="w-5 h-5" />}>
          <div className="space-y-3">
            <div className="flex items-center space-x-3 p-4 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
              <input
                type="checkbox"
                id="email_active"
                checked={configs['notification.email_active'] === 'true'}
                onChange={(e) => handleChange('notification.email_active', e.target.checked ? 'true' : 'false')}
                className="w-4 h-4 rounded border-surface-300 text-primary-600 focus:ring-primary-500"
              />
              <div>
                <label htmlFor="email_active" className="text-sm font-medium text-surface-700 flex items-center">
                  <Mail className="w-4 h-4 mr-2" />
                  {t.config.notification.emailActive}
                </label>
                <p className="text-xs text-surface-400 mt-0.5">{t.config.notification.emailActiveHelp}</p>
              </div>
            </div>

            <div className="flex items-center space-x-3 p-4 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
              <input
                type="checkbox"
                id="rappel_rdv"
                checked={configs['notification.rappel_rdv'] === 'true'}
                onChange={(e) => handleChange('notification.rappel_rdv', e.target.checked ? 'true' : 'false')}
                className="w-4 h-4 rounded border-surface-300 text-primary-600 focus:ring-primary-500"
              />
              <div>
                <label htmlFor="rappel_rdv" className="text-sm font-medium text-surface-700 flex items-center">
                  <CalendarCheck className="w-4 h-4 mr-2" />
                  {t.config.notification.rappelRdv}
                </label>
                <p className="text-xs text-surface-400 mt-0.5">{t.config.notification.rappelRdvHelp}</p>
              </div>
            </div>

            <div className="flex items-center space-x-3 p-4 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
              <input
                type="checkbox"
                id="facture_impayee"
                checked={configs['notification.facture_impayee'] === 'true'}
                onChange={(e) => handleChange('notification.facture_impayee', e.target.checked ? 'true' : 'false')}
                className="w-4 h-4 rounded border-surface-300 text-primary-600 focus:ring-primary-500"
              />
              <div>
                <label htmlFor="facture_impayee" className="text-sm font-medium text-surface-700 flex items-center">
                  <Receipt className="w-4 h-4 mr-2" />
                  {t.config.notification.factureImpayee}
                </label>
                <p className="text-xs text-surface-400 mt-0.5">{t.config.notification.factureImpayeeHelp}</p>
              </div>
            </div>

            <div className="flex items-center space-x-3 p-4 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
              <input
                type="checkbox"
                id="nouveau_rdv"
                checked={configs['notification.nouveau_rdv'] === 'true'}
                onChange={(e) => handleChange('notification.nouveau_rdv', e.target.checked ? 'true' : 'false')}
                className="w-4 h-4 rounded border-surface-300 text-primary-600 focus:ring-primary-500"
              />
              <div>
                <label htmlFor="nouveau_rdv" className="text-sm font-medium text-surface-700 flex items-center">
                  <MessageCircle className="w-4 h-4 mr-2" />
                  {t.config.notification.nouveauRdv}
                </label>
                <p className="text-xs text-surface-400 mt-0.5">{t.config.notification.nouveauRdvHelp}</p>
              </div>
            </div>

            <div className="flex items-center space-x-3 p-4 bg-surface-50 dark:bg-surface-100/50 rounded-xl">
              <input
                type="checkbox"
                id="sound"
                checked={configs['notification.sound'] === 'true'}
                onChange={(e) => handleChange('notification.sound', e.target.checked ? 'true' : 'false')}
                className="w-4 h-4 rounded border-surface-300 text-primary-600 focus:ring-primary-500"
              />
              <div>
                <label htmlFor="sound" className="text-sm font-medium text-surface-700 flex items-center">
                  <Volume2 className="w-4 h-4 mr-2" />
                  {t.config.notification.sound}
                </label>
                <p className="text-xs text-surface-400 mt-0.5">{t.config.notification.soundHelp}</p>
              </div>
            </div>
          </div>
        </Card>
      )}
    </div>
  );
};

export default ConfigurationPage;
