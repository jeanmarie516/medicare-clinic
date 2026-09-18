import React, { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';

// Layouts
import DashboardLayout from './layouts/DashboardLayout';
import AuthLayout from './layouts/AuthLayout';
import ProtectedRoute from './layouts/ProtectedRoute';

// Pages
import LoginPage from './pages/LoginPage';
import AdminDashboard from './pages/AdminDashboard';
import MedecinDashboard from './pages/MedecinDashboard';
import SecretaireDashboard from './pages/SecretaireDashboard';
import PatientsList from './pages/PatientsList';
import PatientForm from './pages/PatientForm';
import PatientDetail from './pages/PatientDetail';
import MedecinsList from './pages/MedecinsList';
import RendezVousCalendar from './pages/RendezVousCalendar';
import RendezVousList from './pages/RendezVousList';
import RendezVousForm from './pages/RendezVousForm';
import PrescriptionsList from './pages/PrescriptionsList';
import PrescriptionForm from './pages/PrescriptionForm';
import FacturesList from './pages/FacturesList';
import FactureForm from './pages/FactureForm';
import ChatPage from './pages/ChatPage';
import AdminUsers from './pages/AdminUsers';
import AuditLogs from './pages/AuditLogs';
import ConfigurationPage from './pages/ConfigurationPage';
import NotFoundPage from './pages/NotFoundPage';
import ForbiddenPage from './pages/ForbiddenPage';
import { Helmet } from 'react-helmet-async';

const App: React.FC = () => {
  const { checkAuth, isAuthenticated } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  return (
    <>
      <Helmet>
        <title>MediCare - Gestion de Clinique</title>
      </Helmet>
      <Routes>
        {/* Route racine */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />

        {/* Route publique */}
        <Route element={<AuthLayout />}>
          <Route
            path="/login"
            element={
              isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />
            }
          />
        </Route>

        {/* Routes protégées */}
        <Route
          element={
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route path="/dashboard" element={<DashboardRouter />} />
          
          {/* Patients */}
          <Route path="/patients" element={<PatientsList />} />
          <Route path="/patients/new" element={<PatientForm />} />
          <Route path="/patients/:id" element={<PatientDetail />} />
          <Route path="/patients/:id/edit" element={<PatientForm />} />

          {/* Médecins */}
          <Route path="/medecins" element={<MedecinsList />} />

          {/* Rendez-vous */}
          <Route path="/rendez-vous" element={<RendezVousCalendar />} />
          <Route path="/rendez-vous/list" element={<RendezVousList />} />
          <Route path="/rendez-vous/new" element={<RendezVousForm />} />
          <Route path="/rendez-vous/:id/edit" element={<RendezVousForm />} />

          {/* Prescriptions */}
          <Route path="/prescriptions" element={<PrescriptionsList />} />
          <Route path="/prescriptions/new" element={<PrescriptionForm />} />

          {/* Factures */}
          <Route path="/factures" element={<FacturesList />} />
          <Route path="/factures/new" element={<FactureForm />} />

          {/* Chat */}
          <Route path="/chat" element={<ChatPage />} />

          {/* Administration */}
          <Route path="/admin/users" element={<AdminUsers />} />
          <Route path="/admin/audit-logs" element={<AuditLogs />} />
          <Route path="/admin/configuration" element={<ConfigurationPage />} />
        </Route>

        {/* Pages d'erreur */}
        <Route path="/403" element={<ForbiddenPage />} />
        <Route path="/404" element={<NotFoundPage />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Routes>
    </>
  );
};

// Composant pour rediriger vers le bon dashboard selon le rôle
const DashboardRouter: React.FC = () => {
  const { user } = useAuthStore();

  if (!user) return <Navigate to="/login" replace />;

  switch (user.role) {
    case 'ADMIN':
      return <AdminDashboard />;
    case 'MEDECIN':
      return <MedecinDashboard />;
    case 'SECRETAIRE':
      return <SecretaireDashboard />;
    default:
      return <Navigate to="/login" replace />;
  }
};

export default App;
