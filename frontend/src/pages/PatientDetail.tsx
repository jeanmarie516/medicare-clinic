import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { patientApi, rendezVousApi, prescriptionApi, factureApi } from '../services/api';
import { Patient, RendezVous, Prescription, Facture } from '../types';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import Tabs from '../components/ui/Tabs';
import Spinner from '../components/ui/Spinner';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import { Edit, Phone, Mail, MapPin, Droplets, AlertTriangle, CalendarCheck, FileText, Receipt, User, Heart, Stethoscope } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { StatutBadge } from '../components/ui/Badge';
import { useTranslation } from '../contexts/LanguageContext';

const PatientDetail: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t, language } = useTranslation();
  const [patient, setPatient] = useState<Patient | null>(null);
  const [rdvs, setRdvs] = useState<RendezVous[]>([]);
  const [prescriptions, setPrescriptions] = useState<Prescription[]>([]);
  const [factures, setFactures] = useState<Facture[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('rdv');

  useEffect(() => {
    if (!id) return;
    const pid = Number(id);
    Promise.all([
      patientApi.getById(pid),
      rendezVousApi.getAll(0, 50).then(r => r.data.content.filter(rdv => rdv.patientId === pid)),
      prescriptionApi.getByPatient(pid),
      factureApi.getByPatient(pid),
    ]).then(([p, r, pr, f]) => {
      setPatient(p.data);
      setRdvs(r);
      setPrescriptions(pr.data);
      setFactures(f.data);
    }).catch(console.error)
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;
  if (!patient) return <p className="text-center text-surface-500 py-20">{t.empty.patientNonTrouve}</p>;

  const age = Math.floor((Date.now() - new Date(patient.dateNaissance).getTime()) / (365.25 * 24 * 60 * 60 * 1000));

  const tabs = [
    { id: 'rdv', label: t.rendezVous.title, count: rdvs.length, icon: <CalendarCheck className="w-4 h-4" /> },
    { id: 'prescriptions', label: t.prescription.title, count: prescriptions.length, icon: <FileText className="w-4 h-4" /> },
    { id: 'factures', label: t.facture.title, count: factures.length, icon: <Receipt className="w-4 h-4" /> },
  ];

  return (
    <div className="animate-fade-in">
      <BackButton to="/patients" label={t.buttons.retourPatients} />

      {/* Profile Card */}
      <div className="medical-card overflow-hidden mb-6">
        {/* Gradient header */}
        <div className="medical-gradient px-6 py-5">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <div className="w-16 h-16 rounded-2xl bg-white/20 backdrop-blur-sm flex items-center justify-center shadow-lg">
                <span className="text-2xl font-bold text-white">{patient.prenom[0]}{patient.nom[0]}</span>
              </div>
              <div className="text-white">
                <h1 className="text-2xl font-display font-bold">{patient.prenom} {patient.nom}</h1>
                <div className="flex items-center space-x-3 mt-1">
                  <Badge variant={patient.sexe === 'M' ? 'info' : 'purple'} size="sm">
                    {patient.sexe === 'M' ? t.patient.homme : t.patient.femme}
                  </Badge>
                  <span className="text-white/80 text-sm">{age} {t.patient.ans}</span>
                  <span className="text-white/80 text-sm">{t.patient.neLe} {format(new Date(patient.dateNaissance), 'dd MMMM yyyy', { locale: language === 'en' ? undefined : fr })}</span>
                </div>
              </div>
            </div>
            <Button onClick={() => navigate(`/patients/${id}/edit`)} variant="outline" leftIcon={<Edit className="w-4 h-4" />}>
              {t.common.edit}
            </Button>
          </div>
        </div>

        {/* Profile info grid */}
        <div className="p-6">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
            {patient.telephone && <InfoItem icon={<Phone className="w-4 h-4" />} label={t.patient.infoTelephone} value={patient.telephone} />}
            {patient.email && <InfoItem icon={<Mail className="w-4 h-4" />} label={t.patient.infoEmail} value={patient.email} />}
            {patient.adresse && <InfoItem icon={<MapPin className="w-4 h-4" />} label={t.patient.infoAdresse} value={patient.adresse} />}
            {patient.groupeSanguin && <InfoItem icon={<Droplets className="w-4 h-4" />} label={t.patient.infoGroupeSanguin} value={patient.groupeSanguin} />}
          </div>

          <div className="flex flex-wrap gap-2 mt-4">
            <div className="medical-badge">
              <User className="w-3 h-3 mr-1" />
              {t.patient.id}: {patient.id}
            </div>
            {patient.createdById && (
              <div className="medical-badge">
                <Heart className="w-3 h-3 mr-1" fill="currentColor" />
                {t.patient.infoCreateur}: {patient.createdById}
              </div>
            )}
          </div>

          {/* Allergies */}
          {patient.allergies && (
            <div className="mt-4 p-4 rounded-xl bg-gradient-to-r from-danger-50 to-white dark:from-danger-900/15 dark:to-surface-100 border border-danger-200 dark:border-danger-700/30 flex items-start space-x-3">
              <div className="w-9 h-9 rounded-lg bg-danger-100 dark:bg-danger-900/30 flex items-center justify-center shrink-0">
                <AlertTriangle className="w-5 h-5 text-danger-600 dark:text-danger-400" />
              </div>
              <div>
                <p className="text-sm font-bold text-danger-700 dark:text-danger-300">{t.patient.allergiesEnregistrees}</p>
                <p className="text-sm text-danger-600 dark:text-danger-400 mt-0.5">{patient.allergies}</p>
              </div>
            </div>
          )}

          {/* Antécédents */}
          {patient.antecedents && (
            <div className="mt-3 p-4 rounded-xl bg-gradient-to-r from-accent-50 to-white dark:from-accent-900/15 dark:to-surface-100 border border-accent-200 dark:border-accent-700/30 flex items-start space-x-3">
              <div className="w-9 h-9 rounded-lg bg-accent-100 dark:bg-accent-900/30 flex items-center justify-center shrink-0">
                <FileText className="w-5 h-5 text-accent-600 dark:text-accent-400" />
              </div>
              <div>
                <p className="text-sm font-bold text-accent-700 dark:text-accent-300">{t.patient.antecedentsMedicaux}</p>
                <p className="text-sm text-accent-600 dark:text-accent-400 mt-0.5">{patient.antecedents}</p>
              </div>
            </div>
          )}

          {/* Contact urgence */}
          {patient.personneContactNom && (
            <div className="mt-3 p-4 rounded-xl bg-surface-50 dark:bg-surface-100/50 flex items-start space-x-3">
              <div className="w-9 h-9 rounded-lg bg-surface-200 dark:bg-surface-200 flex items-center justify-center shrink-0">
                <Phone className="w-5 h-5 text-surface-500" />
              </div>
              <div>
                <p className="text-sm font-bold text-surface-700 dark:text-surface-300">{t.patient.contactUrgence}</p>
                <p className="text-sm text-surface-600 dark:text-surface-400">{patient.personneContactNom}</p>
                {patient.personneContactTelephone && (
                  <p className="text-sm text-surface-500">{patient.personneContactTelephone}</p>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="mb-4">
        <Tabs tabs={tabs} activeTab={activeTab} onTabChange={setActiveTab} />
      </div>

      {/* Tab Content */}
      <div className="min-h-[200px]">
        {activeTab === 'rdv' && (
          <Card padding="none">
            {rdvs.length === 0 ? (
              <div className="text-center py-12">
                <CalendarCheck className="w-12 h-12 text-surface-300 mx-auto mb-3" />
                <p className="text-surface-500 font-medium">{t.empty.aucunRendezVousTrouve}</p>
              </div>
            ) : (
              <div className="divide-y divide-surface-100 dark:divide-surface-300/10">
                {rdvs.sort((a, b) => b.dateHeure.localeCompare(a.dateHeure)).map(rdv => (
                  <div key={rdv.id} className="flex items-center justify-between p-4 hover:bg-primary-50/40 dark:hover:bg-primary-900/10 transition-colors">
                    <div className="flex items-center space-x-4">
                      <div className="text-center min-w-[60px] p-2 rounded-lg bg-primary-50 dark:bg-primary-900/20">
                        <p className="text-sm font-bold text-primary-600 dark:text-primary-400">
                          {format(new Date(rdv.dateHeure), 'HH:mm')}
                        </p>
                        <p className="text-xs text-primary-400">{format(new Date(rdv.dateHeure), 'dd/MM')}</p>
                      </div>
                      <div>
                        <p className="font-semibold text-surface-900">
                          {format(new Date(rdv.dateHeure), 'dd MMMM yyyy', { locale: language === 'en' ? undefined : fr })}
                        </p>
                        <p className="text-sm text-surface-500 flex items-center mt-0.5">
                          <Stethoscope className="w-3.5 h-3.5 mr-1" />
                          Dr. {rdv.medecinPrenom} {rdv.medecinNom}
                          {rdv.medecinSpecialite && <span className="ml-1">· {rdv.medecinSpecialite}</span>}
                        </p>
                        {rdv.motif && <p className="text-xs text-surface-400 mt-0.5">{rdv.motif}</p>}
                      </div>
                    </div>
                    <StatutBadge statut={rdv.statut} />
                  </div>
                ))}
              </div>
            )}
          </Card>
        )}

        {activeTab === 'prescriptions' && (
          <Card padding="none">
            {prescriptions.length === 0 ? (
              <div className="text-center py-12">
                <FileText className="w-12 h-12 text-surface-300 mx-auto mb-3" />
                <p className="text-surface-500 font-medium">{t.empty.aucunePrescription}</p>
              </div>
            ) : (
              <div className="divide-y divide-surface-100 dark:divide-surface-300/10">
                {prescriptions.map(p => (
                  <div key={p.id} className="p-4 hover:bg-primary-50/40 dark:hover:bg-primary-900/10 transition-colors">
                    <div className="flex items-center justify-between mb-2">
                      <div className="flex items-center space-x-2">
                        <FileText className="w-4 h-4 text-primary-500" />
                        <p className="font-semibold text-surface-900">
                          {t.patient.prescriptionDu} {format(new Date(p.datePrescription), 'dd/MM/yyyy')}
                        </p>
                      </div>
                      <Badge variant="primary" size="sm">{t.prescription.valideJusqua} {p.valideJusqua ? format(new Date(p.valideJusqua), 'dd/MM/yyyy') : 'N/A'}</Badge>
                    </div>
                    <p className="text-sm text-surface-500">Dr. {p.medecinPrenom} {p.medecinNom}</p>
                    {p.diagnostic && (
                      <p className="text-sm text-surface-700 dark:text-surface-300 mt-2 bg-surface-50 dark:bg-surface-100/50 rounded-lg p-2">
                        <span className="font-medium">{t.patient.diagnostic}</span> {p.diagnostic}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            )}
          </Card>
        )}

        {activeTab === 'factures' && (
          <Card padding="none">
            {factures.length === 0 ? (
              <div className="text-center py-12">
                <Receipt className="w-12 h-12 text-surface-300 mx-auto mb-3" />
                <p className="text-surface-500 font-medium">{t.empty.aucuneFacture}</p>
              </div>
            ) : (
              <div className="divide-y divide-surface-100 dark:divide-surface-300/10">
                {factures.map(f => (
                  <div key={f.id} className="flex items-center justify-between p-4 hover:bg-primary-50/40 dark:hover:bg-primary-900/10 transition-colors">
                    <div className="flex items-center space-x-3">
                      <div className={`w-9 h-9 rounded-lg flex items-center justify-center ${
                        f.statutPaiement === 'SOLDE'
                          ? 'bg-health-50 dark:bg-health-900/30 text-health-600'
                          : 'bg-danger-50 dark:bg-danger-900/30 text-danger-600'
                      }`}>
                        <Receipt className="w-5 h-5" />
                      </div>
                      <div>
                        <p className="font-semibold text-surface-900">{f.numeroFacture}</p>
                        <p className="text-sm text-surface-500">{format(new Date(f.dateFacture), 'dd/MM/yyyy')} · {f.montantTotal.toLocaleString()} FCFA</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <StatutBadge statut={
                        f.statutPaiement === 'SOLDE' ? 'TERMINE' :
                        f.statutPaiement === 'EN_ATTENTE' ? 'EN_ATTENTE_PAIEMENT' :
                        f.statutPaiement
                      } />
                      {f.montantRestant > 0 && (
                        <p className="text-xs text-danger-500 mt-1 font-semibold">
                          {t.patient.reste} {f.montantRestant.toLocaleString()} FCFA
                        </p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>
        )}
      </div>
    </div>
  );
};

const InfoItem: React.FC<{ icon: React.ReactNode; label: string; value: string }> = ({ icon, label, value }) => (
  <div className="flex items-center space-x-3 p-3 rounded-xl bg-surface-50 dark:bg-surface-100/50 hover:bg-primary-50/50 dark:hover:bg-primary-900/10 transition-colors group">
    <div className="w-8 h-8 rounded-lg bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-primary-600 dark:text-primary-400 group-hover:scale-110 transition-transform">
      {icon}
    </div>
    <div className="min-w-0">
      <p className="text-xs text-surface-500 dark:text-surface-400">{label}</p>
      <p className="text-sm font-semibold text-surface-900 truncate">{value}</p>
    </div>
  </div>
);

export default PatientDetail;
