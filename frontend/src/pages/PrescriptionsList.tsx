import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { prescriptionApi } from '../services/api';
import { Prescription, MedicamentItem } from '../types';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import Spinner from '../components/ui/Spinner';
import Pagination from '../components/ui/Pagination';
import Modal from '../components/ui/Modal';
import { Plus, FileText, Download, Pill, Calendar, User, Stethoscope, AlertCircle, ClipboardList, Info } from 'lucide-react';
import { useTranslation } from '../contexts/LanguageContext';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import toast from 'react-hot-toast';

const PrescriptionsList: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [prescriptions, setPrescriptions] = useState<Prescription[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    prescriptionApi.getAll(page, 20)
      .then(res => {
        setPrescriptions(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [page]);

  const [selectedPrescription, setSelectedPrescription] = useState<Prescription | null>(null);
  const [downloading, setDownloading] = useState(false);

  const downloadPdf = async (prescription: Prescription) => {
    setDownloading(true);
    try {
      const response = await prescriptionApi.getPdf(prescription.id);
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      window.open(url, '_blank');
      setTimeout(() => window.URL.revokeObjectURL(url), 10000);
    } catch (err: any) {
      if (err.response?.status === 403) {
        toast.error(t.common.accesRefuse);
      } else {
        toast.error(t.empty.pdfNonDisponible);
      }
    } finally {
      setDownloading(false);
    }
  };

  const parseMedicaments = (json: string | null | undefined): MedicamentItem[] => {
    if (!json) return [];
    try {
      const parsed = JSON.parse(json);
      return Array.isArray(parsed) ? parsed : [parsed];
    } catch {
      return [];
    }
  };

  const getDrugCount = (medicamentsJson: string | null | undefined): number => {
    if (!medicamentsJson) return 0;
    try {
      const meds = JSON.parse(medicamentsJson);
      return Array.isArray(meds) ? meds.length : 1;
    } catch {
      return 0;
    }
  };

  const columns = [
    {
      key: 'patient', header: t.patient.title,
      render: (p: Prescription) => (
        <div className="flex items-center space-x-2">
          <div className="w-9 h-9 rounded-lg bg-primary-50 dark:bg-primary-900/30 flex items-center justify-center">
            <span className="text-xs font-bold text-primary-700 dark:text-primary-300">
              {p.patientPrenom?.[0]}{p.patientNom?.[0]}
            </span>
          </div>
          <span className="font-semibold text-surface-900">{p.patientPrenom} {p.patientNom}</span>
        </div>
      )
    },
    {
      key: 'medecin', header: t.prescription.prescritPar,
      render: (p: Prescription) => (
        <span className="text-sm text-surface-600 dark:text-surface-400">
          Dr. {p.medecinPrenom} {p.medecinNom}
        </span>
      )
    },
    {
      key: 'datePrescription', header: t.common.date,
      render: (p: Prescription) => (
        <div className="flex items-center space-x-1.5 text-sm text-surface-600 dark:text-surface-400">
          <Calendar className="w-3.5 h-3.5" />
          <span>{format(new Date(p.datePrescription), 'dd/MM/yyyy')}</span>
        </div>
      )
    },
    {
      key: 'medicaments', header: t.prescription.medicaments,
      render: (p: Prescription) => {
        const count = getDrugCount(p.medicamentsJson);
        return (
          <Badge variant="primary" size="sm">
            <Pill className="w-3 h-3 mr-1" />
            {count}
          </Badge>
        );
      }
    },
    {
      key: 'diagnostic', header: t.prescription.diagnostic,
      render: (p: Prescription) => (
        <span className="text-sm text-surface-500 dark:text-surface-400">{p.diagnostic || '-'}</span>
      )
    },
    {
      key: 'validite', header: t.prescription.valideJusqua,
      render: (p: Prescription) => p.valideJusqua ? (
        <span className="text-sm text-surface-500 dark:text-surface-400">
          {format(new Date(p.valideJusqua), 'dd/MM/yyyy')}
        </span>
      ) : <span className="text-surface-300">-</span>
    },
    {
      key: 'actions', header: t.prescription.telechargerPdf,
      render: (p: Prescription) => (
        <button
          onClick={(e) => { e.stopPropagation(); downloadPdf(p); }}
          className="p-2 rounded-lg bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400 hover:bg-primary-100 dark:hover:bg-primary-900/50 transition-colors"
          title={t.prescription.telechargerPdf}
        >
          <Download className="w-4 h-4" />
        </button>
      )
    },
  ];

  if (loading) return <Spinner className="py-20" />;

  const selectedMeds = selectedPrescription ? parseMedicaments(selectedPrescription.medicamentsJson) : [];

  return (
    <div className="animate-fade-in">
      <BackButton to="/dashboard" label={t.buttons.retourDashboard} />
      <PageHeader
        title={t.prescription.title}
        subtitle={t.prescription.title}
        icon={<FileText className="w-6 h-6" />}
        gradient
        actions={
          <Button onClick={() => navigate('/prescriptions/new')} leftIcon={<Plus className="w-4 h-4" />}>
            {t.buttons.nouvellePrescription}
          </Button>
        }
      />

      <Table
        columns={columns}
        data={prescriptions}
        emptyMessage={t.empty.aucunePrescriptionTrouvee}
        onRowClick={(row) => setSelectedPrescription(row)}
      />

      <div className="mt-4">
        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>

      {/* Modal détail prescription */}
      <Modal
        isOpen={!!selectedPrescription}
        onClose={() => setSelectedPrescription(null)}
        title={undefined}
        size="xl"
        footer={
          <>
            <Button variant="ghost" onClick={() => setSelectedPrescription(null)}>
              {t.common.cancel}
            </Button>
            <Button
              onClick={() => selectedPrescription && downloadPdf(selectedPrescription)}
              isLoading={downloading}
              leftIcon={<Download className="w-4 h-4" />}
            >
              {t.prescription.telechargerPdf}
            </Button>
          </>
        }
      >
        {selectedPrescription && (
          <div className="space-y-6">
            {/* En-tête prescription */}
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-xl font-display font-bold text-surface-900">Prescription #{selectedPrescription.id}</h3>
                <p className="text-sm text-surface-400 mt-0.5">
                  {format(new Date(selectedPrescription.datePrescription), 'EEEE dd MMMM yyyy', { locale: fr })}
                </p>
              </div>
              <Badge variant="primary" size="md">
                <Pill className="w-3.5 h-3.5 mr-1" />
                {selectedMeds.length} {selectedMeds.length > 1 ? 'médicaments' : 'médicament'}
              </Badge>
            </div>

            {/* Grille info Patient + Médecin */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Carte Patient */}
              <div className="p-4 bg-gradient-to-br from-primary-50 to-primary-100/50 dark:from-primary-900/20 dark:to-primary-800/10 border border-primary-200/50 dark:border-primary-700/30 rounded-2xl">
                <div className="flex items-center gap-3 mb-3">
                  <div className="w-12 h-12 rounded-full bg-white dark:bg-surface-800 shadow-sm flex items-center justify-center">
                    <User className="w-6 h-6 text-primary-600 dark:text-primary-400" />
                  </div>
                  <div>
                    <p className="text-xs text-primary-500 uppercase tracking-wide font-semibold">Patient</p>
                    <p className="font-bold text-surface-900 text-lg">{selectedPrescription.patientPrenom} {selectedPrescription.patientNom}</p>
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-2 text-sm">
                  {selectedPrescription.patientGroupeSanguin && (
                    <div className="flex items-center gap-1.5">
                      <span className="text-red-400">🩸</span>
                      <span className="text-surface-500">Groupe :</span>
                      <span className="font-semibold text-red-600 dark:text-red-400">{selectedPrescription.patientGroupeSanguin}</span>
                    </div>
                  )}
                  {selectedPrescription.patientSexe && (
                    <div className="flex items-center gap-1.5">
                      <span>{selectedPrescription.patientSexe === 'M' ? '👨' : '👩'}</span>
                      <span className="text-surface-500">Sexe :</span>
                      <span className="font-semibold text-surface-700">{selectedPrescription.patientSexe === 'M' ? 'Masculin' : 'Féminin'}</span>
                    </div>
                  )}
                  {selectedPrescription.patientTelephone && (
                    <div className="flex items-center gap-1.5">
                      <span>📞</span>
                      <span className="text-surface-500">Tél :</span>
                      <span className="font-semibold text-surface-700">{selectedPrescription.patientTelephone}</span>
                    </div>
                  )}
                  {selectedPrescription.patientEmail && (
                    <div className="flex items-center gap-1.5">
                      <span>✉️</span>
                      <span className="text-surface-500">Email :</span>
                      <span className="font-semibold text-surface-700 truncate">{selectedPrescription.patientEmail}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Carte Médecin */}
              <div className="p-4 bg-gradient-to-br from-green-50 to-emerald-100/50 dark:from-green-900/20 dark:to-emerald-800/10 border border-green-200/50 dark:border-green-700/30 rounded-2xl">
                <div className="flex items-center gap-3 mb-3">
                  <div className="w-12 h-12 rounded-full bg-white dark:bg-surface-800 shadow-sm flex items-center justify-center">
                    <Stethoscope className="w-6 h-6 text-green-600 dark:text-green-400" />
                  </div>
                  <div>
                    <p className="text-xs text-green-600 uppercase tracking-wide font-semibold">Médecin prescripteur</p>
                    <p className="font-bold text-surface-900 text-lg">Dr. {selectedPrescription.medecinPrenom} {selectedPrescription.medecinNom}</p>
                  </div>
                </div>
                <div className="text-sm">
                  {selectedPrescription.medecinSpecialite && (
                    <div className="flex items-center gap-1.5">
                      <span>🏥</span>
                      <span className="text-surface-500">Spécialité :</span>
                      <Badge variant="success" size="sm">{selectedPrescription.medecinSpecialite}</Badge>
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Allergies & Antécédents */}
            {(selectedPrescription.patientAllergies || selectedPrescription.patientAntecedents) && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {selectedPrescription.patientAllergies && (
                  <div className="p-3 bg-red-50 dark:bg-red-900/15 border border-red-200/50 dark:border-red-700/30 rounded-xl">
                    <div className="flex items-center gap-2 mb-1.5">
                      <AlertCircle className="w-4 h-4 text-red-500" />
                      <p className="text-xs text-red-600 dark:text-red-400 uppercase tracking-wide font-semibold">Allergies</p>
                    </div>
                    <p className="text-sm text-red-700 dark:text-red-300 font-medium">{selectedPrescription.patientAllergies}</p>
                  </div>
                )}
                {selectedPrescription.patientAntecedents && (
                  <div className="p-3 bg-orange-50 dark:bg-orange-900/15 border border-orange-200/50 dark:border-orange-700/30 rounded-xl">
                    <div className="flex items-center gap-2 mb-1.5">
                      <ClipboardList className="w-4 h-4 text-orange-500" />
                      <p className="text-xs text-orange-600 dark:text-orange-400 uppercase tracking-wide font-semibold">Antécédents</p>
                    </div>
                    <p className="text-sm text-orange-700 dark:text-orange-300 font-medium">{selectedPrescription.patientAntecedents}</p>
                  </div>
                )}
              </div>
            )}

            {/* Séparateur */}
            <div className="border-t border-surface-200 dark:border-surface-700" />

            {/* Diagnostic */}
            {selectedPrescription.diagnostic && (
              <div className="p-4 bg-amber-50 dark:bg-amber-900/15 border border-amber-200/50 dark:border-amber-700/30 rounded-2xl">
                <div className="flex items-center gap-2 mb-2">
                  <div className="w-8 h-8 rounded-lg bg-amber-100 dark:bg-amber-800/50 flex items-center justify-center">
                    <AlertCircle className="w-4 h-4 text-amber-600 dark:text-amber-400" />
                  </div>
                  <p className="text-xs text-amber-600 dark:text-amber-400 uppercase tracking-wide font-semibold">{t.prescription.diagnostic}</p>
                </div>
                <p className="text-surface-700 dark:text-surface-300 pl-10">{selectedPrescription.diagnostic}</p>
              </div>
            )}

            {/* Médicaments */}
            <div>
              <div className="flex items-center gap-2 mb-3">
                <div className="w-8 h-8 rounded-lg bg-primary-100 dark:bg-primary-800 flex items-center justify-center">
                  <Pill className="w-4 h-4 text-primary-600 dark:text-primary-400" />
                </div>
                <h4 className="font-display font-bold text-surface-900">{t.prescription.medicaments}</h4>
              </div>
              {selectedMeds.length > 0 ? (
                <div className="space-y-2.5">
                  {selectedMeds.map((med, idx) => (
                    <div key={idx} className="flex items-start gap-3 p-4 bg-surface-50 dark:bg-surface-800/80 border border-surface-200/50 dark:border-surface-700/30 rounded-xl hover:shadow-sm transition-shadow">
                      <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-sm font-bold text-white shadow-sm shrink-0">
                        {idx + 1}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="font-bold text-surface-900 dark:text-surface-100">{med.nom}</p>
                        <div className="flex flex-wrap gap-1.5 mt-2">
                          {med.dosage && (
                            <Badge variant="primary" size="sm">
                              <span className="mr-1">💊</span>{med.dosage}
                            </Badge>
                          )}
                          {med.frequence && (
                            <Badge variant="info" size="sm">
                              <span className="mr-1">⏰</span>{med.frequence}
                            </Badge>
                          )}
                          {med.duree && (
                            <Badge variant="warning" size="sm">
                              <span className="mr-1">📅</span>{med.duree}
                            </Badge>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-surface-400 italic pl-11">{t.prescription.aucunMedicament}</p>
              )}
            </div>

            {/* Instructions */}
            {selectedPrescription.instructions && (
              <div className="p-4 bg-blue-50 dark:bg-blue-900/15 border border-blue-200/50 dark:border-blue-700/30 rounded-2xl">
                <div className="flex items-center gap-2 mb-2">
                  <div className="w-8 h-8 rounded-lg bg-blue-100 dark:bg-blue-800/50 flex items-center justify-center">
                    <ClipboardList className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                  </div>
                  <p className="text-xs text-blue-600 dark:text-blue-400 uppercase tracking-wide font-semibold">{t.prescription.instructions}</p>
                </div>
                <p className="text-surface-700 dark:text-surface-300 pl-10 whitespace-pre-line">{selectedPrescription.instructions}</p>
              </div>
            )}

            {/* Validité */}
            {selectedPrescription.valideJusqua && (
              <div className="flex items-center gap-3 p-3 bg-surface-50 dark:bg-surface-800 border border-surface-200/50 dark:border-surface-700/30 rounded-xl">
                <Info className="w-5 h-5 text-surface-400" />
                <span className="text-sm text-surface-600 dark:text-surface-400">
                  {t.prescription.valideJusqua} :
                  <strong className="ml-1 text-surface-900 dark:text-surface-100">
                    {format(new Date(selectedPrescription.valideJusqua), 'dd MMMM yyyy', { locale: fr })}
                  </strong>
                </span>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default PrescriptionsList;
