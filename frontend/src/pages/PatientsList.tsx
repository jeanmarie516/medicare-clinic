import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { patientApi } from '../services/api';
import { Patient } from '../types';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Table from '../components/ui/Table';
import SearchBar from '../components/ui/SearchBar';
import Pagination from '../components/ui/Pagination';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import { Plus, Users, Mail, Phone, Droplets, Calendar } from 'lucide-react';
import { format } from 'date-fns';
import { useTranslation } from '../contexts/LanguageContext';

const PatientsList: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const fetchPatients = useCallback(async () => {
    setLoading(true);
    try {
      const res = await patientApi.getAll(page, 20, search || undefined);
      setPatients(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [page, search]);

  useEffect(() => { fetchPatients(); }, [fetchPatients]);

  const columns = [
    {
      key: 'nom', header: t.patient.title,
      render: (p: Patient) => (
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center relative">
            <span className="text-sm font-bold text-primary-700 dark:text-primary-300">
              {p.prenom[0]}{p.nom[0]}
            </span>
            {p.groupeSanguin && (
              <span className="absolute -bottom-1 -right-1 w-4 h-4 rounded-full bg-white dark:bg-surface-200 border border-surface-200 flex items-center justify-center">
                <Droplets className="w-2.5 h-2.5 text-danger-500" />
              </span>
            )}
          </div>
          <div>
            <p className="font-semibold text-surface-900">{p.prenom} {p.nom}</p>
            <p className="text-xs text-surface-400">
              {t.patient.neLe} {format(new Date(p.dateNaissance), 'dd/MM/yyyy')}
              {p.groupeSanguin && <span className="ml-2">· {p.groupeSanguin}</span>}
            </p>
          </div>
        </div>
      )
    },
    {
      key: 'sexe', header: t.patient.sexe,
      render: (p: Patient) => (
        <Badge variant={p.sexe === 'M' ? 'info' : 'purple'} size="sm">
          {p.sexe === 'M' ? t.patient.homme : t.patient.femme}
        </Badge>
      )
    },
    {
      key: 'telephone', header: t.patient.telephone,
      render: (p: Patient) => p.telephone ? (
        <div className="flex items-center space-x-1.5 text-sm text-surface-600 dark:text-surface-400">
          <Phone className="w-3.5 h-3.5" />
          <span>{p.telephone}</span>
        </div>
      ) : <span className="text-surface-300">-</span>
    },
    {
      key: 'allergies', header: t.patient.allergies,
      render: (p: Patient) => p.allergies ? (
        <Badge variant="danger" size="sm">{p.allergies.slice(0, 20)}{p.allergies.length > 20 ? '...' : ''}</Badge>
      ) : <span className="text-surface-300 text-sm">{t.common.none}</span>
    },
    {
      key: 'createdAt', header: t.nav.patients, // Date d'inscription
      render: (p: Patient) => (
        <span className="text-sm text-surface-500 dark:text-surface-400">{format(new Date(p.createdAt), 'dd/MM/yyyy')}</span>
      )
    },
  ];

  return (
    <div className="animate-fade-in">
      <BackButton to="/dashboard" label={t.buttons.retourDashboard} />
      <PageHeader
        title={t.patient.title}
        subtitle={t.nav.patients}
        icon={<Users className="w-6 h-6" />}
        gradient
        actions={
          <Button onClick={() => navigate('/patients/new')} leftIcon={<Plus className="w-4 h-4" />}>
            {t.buttons.nouveauPatient}
          </Button>
        }
      />

      <div className="mb-5">
        <SearchBar
          value={search}
          onChange={setSearch}
          placeholder={t.patient.searchPlaceholder}
        />
      </div>

      <Table
        columns={columns}
        data={patients}
        isLoading={loading}
        onRowClick={(p) => navigate(`/patients/${p.id}`)}
        emptyMessage={t.empty.aucunPatientTrouve}
      />

      <div className="mt-4">
        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>
    </div>
  );
};

export default PatientsList;
