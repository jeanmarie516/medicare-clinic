import React, { useEffect, useState } from 'react';
import { factureApi } from '../services/api';
import { Facture } from '../types';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Table from '../components/ui/Table';
import Button from '../components/ui/Button';
import { StatutBadge } from '../components/ui/Badge';
import Pagination from '../components/ui/Pagination';
import Spinner from '../components/ui/Spinner';
import Modal from '../components/ui/Modal';
import toast from 'react-hot-toast';
import { Plus, Download, CheckCircle, Receipt, AlertTriangle, Calendar } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from '../contexts/LanguageContext';

const FacturesList: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [factures, setFactures] = useState<Facture[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [selected, setSelected] = useState<Facture | null>(null);

  useEffect(() => {
    factureApi.getAll(page, 20)
      .then(res => { setFactures(res.data.content); setTotalPages(res.data.totalPages); })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [page]);

  const marquerPayee = async (id: number) => {
    try {
      await factureApi.marquerPayee(id, 'ESPECES');
      toast.success(t.facture.marquerPayee);
      setFactures(prev => prev.map(f => f.id === id ? { ...f, statutPaiement: 'SOLDE' as any, montantPaye: f.montantTotal, montantRestant: 0 } : f));
      setSelected(null);
    } catch (err) { toast.error(t.empty.erreurEnregistrement); }
  };

  const columns = [
    {
      key: 'numeroFacture', header: t.facture.numeroFacture,
      render: (f: Facture) => (
        <div className="flex items-center space-x-2">
          <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${
            f.statutPaiement === 'SOLDE'
              ? 'bg-health-50 dark:bg-health-900/30 text-health-600'
              : 'bg-danger-50 dark:bg-danger-900/30 text-danger-600'
          }`}>
            <Receipt className="w-4 h-4" />
          </div>
          <span className="font-semibold text-surface-900">{f.numeroFacture}</span>
        </div>
      )
    },
    {
      key: 'patient', header: t.rendezVous.patient,
      render: (f: Facture) => (
        <span className="font-medium text-surface-700 dark:text-surface-300">{f.patientPrenom} {f.patientNom}</span>
      )
    },
    {
      key: 'montantTotal', header: t.facture.montantTotal,
      render: (f: Facture) => (
        <span className="font-bold text-surface-900">{f.montantTotal.toLocaleString()} <span className="text-xs text-surface-400">FCFA</span></span>
      )
    },
    {
      key: 'montantPaye', header: t.facture.montantPaye,
      render: (f: Facture) => (
        <span className={`font-semibold ${f.montantPaye >= f.montantTotal ? 'text-health-600 dark:text-health-400' : 'text-surface-500'}`}>
          {f.montantPaye.toLocaleString()} FCFA
        </span>
      )
    },
    {
      key: 'montantRestant', header: t.facture.montantRestant,
      render: (f: Facture) => (
        <span className={`font-bold ${f.montantRestant > 0 ? 'text-danger-600 dark:text-danger-400' : 'text-health-600 dark:text-health-400'}`}>
          {f.montantRestant > 0 ? `${f.montantRestant.toLocaleString()} FCFA` : `✅ ${t.facture.statut.SOLDE}`}
        </span>
      )
    },
    {
      key: 'statutPaiement', header: t.common.status,
      render: (f: Facture) => (
        <StatutBadge statut={
          f.statutPaiement === 'SOLDE' ? 'TERMINE' :
          f.statutPaiement === 'EN_ATTENTE' ? 'EN_ATTENTE_PAIEMENT' :
          f.statutPaiement
        } />
      )
    },
    {
      key: 'dateFacture', header: t.common.date,
      render: (f: Facture) => (
        <span className="text-sm text-surface-500 dark:text-surface-400">{format(new Date(f.dateFacture), 'dd/MM/yyyy')}</span>
      )
    },
    {
      key: 'actions', header: '',
      render: (f: Facture) => (
        <div className="flex space-x-1">
          {f.statutPaiement !== 'SOLDE' && (
            <button
              onClick={(e) => { e.stopPropagation(); setSelected(f); }}
              className="p-1.5 rounded-lg text-health-600 hover:bg-health-50 dark:hover:bg-health-900/30 transition-colors"
              title={t.facture.marquerPayee}
            >
              <CheckCircle className="w-4 h-4" />
            </button>
          )}
        </div>
      )
    },
  ];

  if (loading) return <Spinner className="py-20" />;

  return (
    <div className="animate-fade-in">
      <BackButton to="/dashboard" label={t.buttons.retourDashboard} />
      <PageHeader
        title={t.facture.title}
        subtitle={t.facture.title}
        icon={<Receipt className="w-6 h-6" />}
        gradient
        actions={
          <Button onClick={() => navigate('/factures/new')} leftIcon={<Plus className="w-4 h-4" />}>
            {t.buttons.nouvelleFacture}
          </Button>
        }
      />

      <Table
        columns={columns}
        data={factures}
        emptyMessage={t.empty.aucuneFactureTrouvee}
        onRowClick={f => setSelected(f)}
      />

      <div className="mt-4">
        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>

      <Modal
        isOpen={!!selected}
        onClose={() => setSelected(null)}                title={t.facture.details}
        footer={
          selected && selected.statutPaiement !== 'SOLDE' ? (
            <div className="flex space-x-2">
              <Button variant="primary" onClick={() => marquerPayee(selected.id)} leftIcon={<CheckCircle className="w-4 h-4" />}>
                {t.facture.marquerPayee}
              </Button>
            </div>
          ) : undefined
        }
      >
        {selected && (
          <div className="space-y-5">
            {/* Status banner */}
            <div className={`flex items-center space-x-3 p-4 rounded-xl ${
              selected.statutPaiement === 'SOLDE'
                ? 'bg-health-50 dark:bg-health-900/20 border border-health-200 dark:border-health-700/30'
                : 'bg-danger-50 dark:bg-danger-900/20 border border-danger-200 dark:border-danger-700/30'
            }`}>
              {selected.statutPaiement === 'SOLDE' ? (
                <CheckCircle className="w-8 h-8 text-health-500" />
              ) : (
                <AlertTriangle className="w-8 h-8 text-danger-500" />
              )}
              <div>
                <p className="font-semibold text-surface-900">
                  {selected.statutPaiement === 'SOLDE' ? t.facture.statut.SOLDE : t.facture.statut.EN_ATTENTE}
                </p>
                <p className="text-sm text-surface-500">
                  {selected.numeroFacture} · {format(new Date(selected.dateFacture), 'dd MMMM yyyy', { locale: fr })}
                </p>
              </div>
            </div>

            {/* Montants */}
            <div className="grid grid-cols-2 gap-4">
              <div className="p-4 rounded-xl bg-surface-50 dark:bg-surface-100/50">
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.facture.montantTotal}</p>
                <p className="text-2xl font-bold text-surface-900 font-display">
                  {selected.montantTotal.toLocaleString()} <span className="text-sm text-surface-400">FCFA</span>
                </p>
              </div>
              <div className="p-4 rounded-xl bg-surface-50 dark:bg-surface-100/50">
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.facture.montantRestant}</p>
                <p className={`text-2xl font-bold font-display ${
                  selected.montantRestant > 0 ? 'text-danger-600' : 'text-health-600'
                }`}>
                  {selected.montantRestant.toLocaleString()} <span className="text-sm text-surface-400">FCFA</span>
                </p>
              </div>
            </div>

            {/* Infos */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.rendezVous.patient}</p>
                <p className="font-medium text-surface-900">{selected.patientPrenom} {selected.patientNom}</p>
              </div>
              <div>
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.facture.dateEcheance}</p>
                <p className="font-medium text-surface-900 flex items-center">
                  <Calendar className="w-3.5 h-3.5 mr-1.5 text-surface-400" />
                  {format(new Date(selected.dateEcheance), 'dd/MM/yyyy')}
                </p>
              </div>
              <div>
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.facture.montantPaye}</p>
                <p className="font-medium text-surface-900">{selected.montantPaye.toLocaleString()} FCFA</p>
              </div>
              <div>
                <p className="text-xs text-surface-500 uppercase font-semibold mb-1">{t.common.status}</p>
                <StatutBadge statut={
                  selected.statutPaiement === 'SOLDE' ? 'TERMINE' :
                  selected.statutPaiement === 'EN_ATTENTE' ? 'EN_ATTENTE_PAIEMENT' :
                  selected.statutPaiement
                } />
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default FacturesList;
