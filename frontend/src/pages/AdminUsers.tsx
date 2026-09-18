import React, { useEffect, useState } from 'react';
import { adminApi } from '../services/api';
import PageHeader from '../components/ui/PageHeader';
import BackButton from '../components/ui/BackButton';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import Spinner from '../components/ui/Spinner';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import toast from 'react-hot-toast';
import { UserPlus, ToggleLeft, ToggleRight, Trash2 } from 'lucide-react';
import { useTranslation } from '../contexts/LanguageContext';

const AdminUsers: React.FC = () => {
  const { t } = useTranslation();
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [form, setForm] = useState({ nom: '', prenom: '', email: '', password: '', role: 'MEDECIN', telephone: '', specialite: '' });
  const [saving, setSaving] = useState(false);

  const fetchUsers = () => {
    adminApi.getUsers()
      .then(res => setUsers(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchUsers(); }, []);

  const toggleActif = async (id: number) => {
    try {
      const res = await adminApi.toggleActif(id);
      toast.success(res.data.message);
      setUsers(prev => prev.map(u => u.id === id ? { ...u, actif: res.data.actif } : u));
    } catch (err) { toast.error(t.common.error); }
  };

  const deleteUser = async (id: number, email: string) => {
    if (!window.confirm(t.common.confirmDelete)) return;
    try {
      await adminApi.deleteUser(id);
      toast.success(t.common.delete);
      fetchUsers();
    } catch (err: any) {
      toast.error(err.response?.data?.message || t.common.error);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await adminApi.createUser(form);
      toast.success(t.admin.compteCree);
      setShowCreate(false);
      setForm({ nom: '', prenom: '', email: '', password: '', role: 'MEDECIN', telephone: '', specialite: '' });
      fetchUsers();
    } catch (err: any) { toast.error(err.response?.data?.message || t.common.error); }
    finally { setSaving(false); }
  };

  const columns = [
    { key: 'user', header: t.admin.utilisateur, render: (u: any) => (
      <div className="flex items-center space-x-3">
        <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-xs font-bold text-primary-700">{u.prenom[0]}{u.nom[0]}</div>
        <div><p className="font-medium">{u.prenom} {u.nom}</p><p className="text-xs text-surface-400">{u.email}</p></div>
      </div>
    )},
    { key: 'role', header: t.admin.role, render: (u: any) => (
      <Badge variant={u.role === 'ADMIN' ? 'danger' : u.role === 'MEDECIN' ? 'info' : 'warning'}>{u.role}</Badge>
    )},
    { key: 'actif', header: t.common.status, render: (u: any) => u.actif ? <Badge variant="success">{t.admin.actif}</Badge> : <Badge variant="danger">{t.admin.inactif}</Badge> },
    { key: 'actions', header: '', render: (u: any) => (
      u.role !== 'ADMIN' && (
        <div className="flex items-center space-x-2">
          <button onClick={(e) => { e.stopPropagation(); toggleActif(u.id); }}
            className={u.actif ? 'text-yellow-600 hover:text-yellow-700' : 'text-green-600 hover:text-green-700'}>
            {u.actif ? <ToggleRight className="w-5 h-5" /> : <ToggleLeft className="w-5 h-5" />}
          </button>
          {!u.actif && (
            <button onClick={(e) => { e.stopPropagation(); deleteUser(u.id, u.email); }}
              className="text-danger-500 hover:text-danger-700 transition-colors" title="Supprimer">
              <Trash2 className="w-4 h-4" />
            </button>
          )}
        </div>
      )
    )},
  ];

  if (loading) return <Spinner className="py-20" />;

  return (
    <div>
      <BackButton to="/dashboard" label={t.buttons.retourDashboard} />
      <PageHeader title={t.admin.gestionUtilisateurs} subtitle={t.admin.title} actions={
        <Button onClick={() => setShowCreate(true)} leftIcon={<UserPlus className="w-4 h-4" />}>{t.admin.nouvelUtilisateur}</Button>
      } />
      <Table columns={columns} data={users} emptyMessage={t.empty.aucunUtilisateur} />

      <Modal isOpen={showCreate} onClose={() => setShowCreate(false)} title={t.admin.creerCompte}
        footer={<><Button variant="ghost" onClick={() => setShowCreate(false)}>{t.common.cancel}</Button><Button onClick={handleCreate} isLoading={saving}>{t.common.create}</Button></>}>
        <div className="space-y-3">
          <div className="grid grid-cols-2 gap-3">
            <Input label={t.patient.nom} value={form.nom} onChange={e => setForm(f => ({ ...f, nom: e.target.value }))} required />
            <Input label={t.patient.prenom} value={form.prenom} onChange={e => setForm(f => ({ ...f, prenom: e.target.value }))} required />
          </div>
          <Input label={t.auth.email} type="email" value={form.email} onChange={e => setForm(f => ({ ...f, email: e.target.value }))} required />
          <Input label={t.admin.motDePasse} type="password" value={form.password} onChange={e => setForm(f => ({ ...f, password: e.target.value }))} required />
          <div>
            <label className="block text-sm text-surface-700 mb-1">{t.admin.role}</label>
            <select value={form.role} onChange={e => setForm(f => ({ ...f, role: e.target.value }))}
              className="w-full rounded-lg border border-surface-300 px-3 py-2.5 text-sm">
              <option value="MEDECIN">{t.roles.MEDECIN}</option>
              <option value="SECRETAIRE">{t.roles.SECRETAIRE}</option>
            </select>
          </div>
          {form.role === 'MEDECIN' && <Input label={t.admin.specialite} value={form.specialite} onChange={e => setForm(f => ({ ...f, specialite: e.target.value }))} />}
          <Input label={t.admin.telephone} value={form.telephone} onChange={e => setForm(f => ({ ...f, telephone: e.target.value }))} />
        </div>
      </Modal>
    </div>
  );
};

export default AdminUsers;
