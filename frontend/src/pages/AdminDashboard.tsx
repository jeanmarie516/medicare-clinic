import React, { useEffect, useState } from 'react';
import { dashboardApi } from '../services/api';
import { DashboardStats } from '../types';
import StatCard from '../components/ui/StatCard';
import Card from '../components/ui/Card';
import Spinner from '../components/ui/Spinner';
import PageHeader from '../components/ui/PageHeader';
import { Users, Stethoscope, CalendarCheck, DollarSign, Activity, TrendingUp, Heart } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { useTranslation } from '../contexts/LanguageContext';

const AdminDashboard: React.FC = () => {
  const { t } = useTranslation();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dashboardApi.getAdminStats()
      .then(res => setStats(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;
  if (!stats) return <p className="text-center text-surface-500 py-20">{t.empty.erreurChargement}</p>;

  const COLORS = ['#14b8a6', '#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6'];
  const chartTooltipStyle = { contentStyle: { borderRadius: 12, border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' } };

  const revenusData = stats.revenusMensuels?.map(r => ({
    name: `${r.mois.toString().padStart(2, '0')}/${r.annee}`,
    Revenus: r.montant,
  })) || [];

  const specialitesData = stats.rdvParSpecialite?.map(s => ({
    name: s.specialite,
    value: s.nombre,
  })) || [];

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title={t.dashboard.title}
        subtitle={t.dashboard.adminTitle}
        icon={<Heart className="w-6 h-6" fill="currentColor" />}
        gradient
      />

      {/* KPIs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title={t.dashboard.totalPatients}
          value={stats.totalPatients}
          icon={<Users className="w-6 h-6" />}
          color="primary"
          description={t.dashboard.patientsEnregistres}
        />
        <StatCard
          title={t.dashboard.medecinsActifs}
          value={stats.totalMedecins}
          icon={<Stethoscope className="w-6 h-6" />}
          color="secondary"
          description={t.dashboard.toutesSpecialites}
        />
        <StatCard
          title={t.dashboard.rdvAujourdhui}
          value={stats.rdvAujourdhui}
          icon={<CalendarCheck className="w-6 h-6" />}
          color="health"
          description={t.dashboard.consultationsJour}
        />
        <StatCard
          title={t.dashboard.revenusMois}
          value={`${(stats.revenusDuMois || 0).toLocaleString()} FCFA`}
          icon={<DollarSign className="w-6 h-6" />}
          color="warning"
          description={t.dashboard.chiffreAffaires}
        />
      </div>

      {/* Sub KPIs */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard title={t.dashboard.rdvConfirmes} value={stats.rdvConfirmes} description={t.time.today} color="secondary" />
        <StatCard title={t.dashboard.rdvTermines} value={stats.rdvTermines} description={t.time.today} color="health" />
        <StatCard title={t.dashboard.facturesImpayees} value={stats.facturesImpayees} icon={<Activity className="w-6 h-6" />} color="danger" />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title={t.dashboard.revenusMensuels} icon={<TrendingUp className="w-5 h-5" />}>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={revenusData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
                <XAxis dataKey="name" tick={{ fontSize: 12, fill: '#64748b' }} />
                <YAxis tick={{ fontSize: 12, fill: '#64748b' }} />
                <Tooltip {...chartTooltipStyle} />
                <Bar dataKey="Revenus" fill="#14b8a6" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Card>

        <Card title={t.dashboard.rdvParSpecialite} icon={<Activity className="w-5 h-5" />}>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={specialitesData}
                  cx="50%" cy="50%"
                  outerRadius={90}
                  innerRadius={40}
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                >
                  {specialitesData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </Card>
      </div>

      {/* Bottom row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title={t.dashboard.topMedecins} icon={<Stethoscope className="w-5 h-5" />}>
          <div className="space-y-3">
            {stats.topMedecins?.slice(0, 5).map((m, i) => {
              const medals = ['🥇', '🥈', '🥉'];
              return (
                <div key={m.medecinId} className="flex items-center justify-between p-3 rounded-xl bg-surface-50 dark:bg-surface-100/50 hover:bg-primary-50/60 dark:hover:bg-primary-900/10 transition-all group">
                  <div className="flex items-center space-x-3">
                    <span className={`w-8 h-8 rounded-xl flex items-center justify-center text-sm font-bold ${
                      i === 0 ? 'bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300' :
                      i === 1 ? 'bg-secondary-100 text-secondary-700' :
                      'bg-surface-200 text-surface-600'
                    }`}>
                      {i < 3 ? medals[i] : i + 1}
                    </span>
                    <div>
                      <p className="text-sm font-semibold text-surface-900">Dr. {m.prenom} {m.nom}</p>
                      <p className="text-xs text-surface-500">{m.specialite}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-bold text-primary-600 dark:text-primary-400">{m.totalRdv}</p>
                    <p className="text-xs text-surface-400">RDV</p>
                  </div>
                </div>
              );
            })}
            {(!stats.topMedecins || stats.topMedecins.length === 0) && (
              <p className="text-center text-surface-400 py-6">{t.empty.aucuneDonnee}</p>
            )}
          </div>
        </Card>

        <Card title={t.dashboard.activiteRecente} icon={<Activity className="w-5 h-5" />}>
          <div className="space-y-1">
            {stats.activitesRecentes?.slice(0, 8).map((a, i) => (
              <div key={i} className="flex items-start space-x-3 p-2.5 rounded-lg hover:bg-surface-50 dark:hover:bg-surface-100/30 transition-colors">
                <div className="w-2 h-2 rounded-full bg-primary-400 mt-2 shrink-0 ring-2 ring-primary-50 dark:ring-primary-900/30" />
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-surface-700 dark:text-surface-300">
                    <span className="font-semibold text-surface-900">{a.userNom}</span>
                    {' — '}{a.detail}
                  </p>
                  <p className="text-xs text-surface-400 mt-0.5">{a.createdAt}</p>
                </div>
              </div>
            ))}
            {(!stats.activitesRecentes || stats.activitesRecentes.length === 0) && (
              <p className="text-center text-surface-400 py-6">{t.empty.aucuneActivite}</p>
            )}
          </div>
        </Card>
      </div>
    </div>
  );
};

export default AdminDashboard;
