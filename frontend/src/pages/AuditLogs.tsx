import React, { useEffect, useState } from 'react';
import { adminApi } from '../services/api';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Card from '../components/ui/Card';
import Spinner from '../components/ui/Spinner';
import Pagination from '../components/ui/Pagination';
import { Activity } from 'lucide-react';
import { useTranslation } from '../contexts/LanguageContext';

const AuditLogs: React.FC = () => {
  const { t } = useTranslation();
  const [logs, setLogs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    adminApi.getAuditLogs(page, 50)
      .then((res: any) => {
        setLogs(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [page]);

  if (loading) return <Spinner className="py-20" />;

  return (
    <div>
      <BackButton to="/dashboard" label={t.buttons.retourDashboard} />
      <PageHeader title={t.admin.journalAudit} subtitle={t.admin.audit.title} />
      <Card padding="none">
        {logs.length === 0 ? (
          <p className="text-center text-surface-400 py-12">{t.admin.audit.aucune}</p>
        ) : (
          <div className="divide-y divide-surface-100">
            {logs.map((log: any, idx: number) => (
              <div key={log.id || idx} className="flex items-start space-x-3 p-4 hover:bg-surface-50">
                <Activity className="w-4 h-4 text-surface-400 mt-0.5 flex-shrink-0" />
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-surface-900">
                    <span className="font-medium">{log.userNom || t.admin.utilisateur}</span>
                    {' '}{t.admin.audit.action}{' '}
                    <span className="font-medium text-primary-600">{log.action}</span>
                    {' '}{t.admin.audit.sur}{' '}
                    <span className="font-medium">{log.ressource}</span>
                  </p>
                  {log.detail && <p className="text-xs text-surface-500 mt-0.5">{log.detail}</p>}
                  <p className="text-xs text-surface-400 mt-1">{log.createdAt || log.created_at || ''}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
};

export default AuditLogs;
