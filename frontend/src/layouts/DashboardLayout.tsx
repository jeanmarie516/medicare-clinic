import React, { useState, useEffect } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { useNotificationStore } from '../store/notificationStore';
import { useChatStore } from '../store/chatStore';
import { useTheme } from '../contexts/ThemeContext';
import { useTranslation } from '../contexts/LanguageContext';
import { Helmet } from 'react-helmet-async';
import { formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';
import { websocketService } from '../services/websocketService';
import {
  LayoutDashboard, Users, Stethoscope, CalendarCheck, FileText,
  Receipt, MessageCircle, Bell, Settings, Shield, LogOut,
  Menu, X, ChevronDown, Search, User, Sun, Moon, Languages,
  Calendar, DollarSign, Info, SlidersHorizontal,
} from 'lucide-react';

const getNavigation = (t: ReturnType<typeof useTranslation>['t'], role: string) => {
  const nav = {
    ADMIN: [
      { name: t.nav.dashboard, href: '/dashboard', icon: LayoutDashboard },
      { name: t.nav.patients, href: '/patients', icon: Users },
      { name: t.nav.medecins, href: '/medecins', icon: Stethoscope },
      { name: t.nav.rendezVous, href: '/rendez-vous', icon: CalendarCheck },
      { name: t.nav.prescriptions, href: '/prescriptions', icon: FileText },
      { name: t.nav.factures, href: '/factures', icon: Receipt },
      { name: t.nav.chat, href: '/chat', icon: MessageCircle },
      { name: t.nav.utilisateurs, href: '/admin/users', icon: Shield },
      { name: t.nav.journalAudit, href: '/admin/audit-logs', icon: Settings },
      { name: t.nav.configuration, href: '/admin/configuration', icon: SlidersHorizontal },
    ],
    MEDECIN: [
      { name: t.nav.dashboard, href: '/dashboard', icon: LayoutDashboard },
      { name: t.nav.patients, href: '/patients', icon: Users },
      { name: t.nav.rendezVous, href: '/rendez-vous', icon: CalendarCheck },
      { name: t.nav.prescriptions, href: '/prescriptions', icon: FileText },
      { name: t.nav.factures, href: '/factures', icon: Receipt },
      { name: t.nav.chat, href: '/chat', icon: MessageCircle },
    ],
    SECRETAIRE: [
      { name: t.nav.dashboard, href: '/dashboard', icon: LayoutDashboard },
      { name: t.nav.patients, href: '/patients', icon: Users },
      { name: t.nav.rendezVous, href: '/rendez-vous', icon: CalendarCheck },
      { name: t.nav.factures, href: '/factures', icon: Receipt },
      { name: t.nav.chat, href: '/chat', icon: MessageCircle },
    ],
  };
  return nav[role as keyof typeof nav] || nav.ADMIN;
};

const DashboardLayout: React.FC = () => {
  const { user, logout } = useAuthStore();
  const { notifications, nonLuCount, fetchNotifications, marquerLu, marquerToutLu } = useNotificationStore();
  const { messagesNonLus, fetchMessagesNonLus } = useChatStore();
  const { theme, toggleTheme } = useTheme();
  const { t, language, toggleLanguage } = useTranslation();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showNotifPanel, setShowNotifPanel] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchNotifications();
    fetchMessagesNonLus();
    const interval = setInterval(fetchNotifications, 30000);
    const msgInterval = setInterval(fetchMessagesNonLus, 5000);

    // Connexion WebSocket temps réel
    const token = localStorage.getItem('medicare_token');
    if (token) {
      websocketService.connect(token);
    }

    // Réagir aux nouveaux messages en temps réel
    const unsubMsg = websocketService.onMessage(() => {
      fetchMessagesNonLus();
    });

    // Réagir aux nouvelles notifications en temps réel
    const unsubNotif = websocketService.onNotification(() => {
      fetchNotifications();
    });

    return () => {
      clearInterval(interval);
      clearInterval(msgInterval);
      unsubMsg();
      unsubNotif();
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (!user) return null;

  const navItems = getNavigation(t, user.role);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const roleLabel = t.roles[user.role as keyof typeof t.roles] || user.role;

  return (
    <div className="min-h-screen bg-surface-50">
      <Helmet>
        <title>{t.app.name} - {roleLabel}</title>
      </Helmet>

      {/* Sidebar mobile */}
      {sidebarOpen && (
        <div className="fixed inset-0 z-40 lg:hidden">
          <div className="fixed inset-0 bg-black/50" onClick={() => setSidebarOpen(false)} />
          <div className="fixed inset-y-0 left-0 w-64 bg-white shadow-xl animate-slide-in-right">
            <SidebarContent navItems={navItems} user={user} t={t} onClose={() => setSidebarOpen(false)} />
          </div>
        </div>
      )}

      {/* Sidebar desktop */}
      <div className="hidden lg:fixed lg:inset-y-0 lg:flex lg:w-64 lg:flex-col">
        <div className="flex flex-col flex-1 bg-white border-r border-surface-200">
          <SidebarContent navItems={navItems} user={user} t={t} onClose={() => {}} />
        </div>
      </div>

      {/* Main content */}
      <div className="lg:pl-64">
        {/* Navbar */}
        <nav className="sticky top-0 z-30 bg-white/80 backdrop-blur-lg border-b border-surface-200">
          <div className="flex items-center justify-between h-16 px-4 sm:px-6">
            <button className="lg:hidden p-2 rounded-lg hover:bg-surface-100" onClick={() => setSidebarOpen(true)}>
              <Menu className="w-6 h-6 text-surface-600" />
            </button>

            <div className="flex-1 flex items-center justify-end space-x-2">
              {/* Language Toggle */}
              <button
                onClick={toggleLanguage}
                className="px-2.5 py-2 rounded-lg hover:bg-surface-100 text-surface-500 hover:text-primary-600 transition-colors flex items-center space-x-1"
                title={t.language.toggle}
              >
                <Languages className="w-5 h-5" />
                <span className="text-xs font-semibold uppercase tracking-wider">{language}</span>
              </button>

              {/* Dark Mode Toggle */}
              <button
                onClick={toggleTheme}
                className="p-2 rounded-lg hover:bg-surface-100 text-surface-500 hover:text-yellow-600 transition-colors"
                title={theme === 'dark' ? t.theme.light : t.theme.dark}
              >
                {theme === 'dark' ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
              </button>

              {/* Notifications */}
              <div className="relative">
                <button
                  className="relative p-2 rounded-lg hover:bg-surface-100 transition-colors"
                  onClick={() => setShowNotifPanel(!showNotifPanel)}
                >
                  <Bell className="w-5 h-5 text-surface-600" />
                  {nonLuCount > 0 && (
                    <span className="absolute -top-0.5 -right-0.5 inline-flex items-center justify-center w-5 h-5 text-xs font-bold text-white bg-danger-500 rounded-full">
                      {nonLuCount > 99 ? '99+' : nonLuCount}
                    </span>
                  )}
                </button>

                {/* Notification dropdown panel */}
                {showNotifPanel && (
                  <>
                    <div className="fixed inset-0 z-10" onClick={() => setShowNotifPanel(false)} />
                    <div className="absolute right-0 mt-2 w-80 sm:w-96 bg-white dark:bg-surface-100 rounded-xl shadow-xl border border-surface-200 dark:border-surface-300/30 z-20 animate-scale-in overflow-hidden">
                      {/* Header */}
                      <div className="flex items-center justify-between px-4 py-3 border-b border-surface-200 dark:border-surface-300/20">
                        <h3 className="text-sm font-semibold text-surface-900">{t.notif.title}</h3>
                        {nonLuCount > 0 && (
                          <button
                            onClick={marquerToutLu}
                            className="text-xs font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400 transition-colors"
                          >
                            {t.notif.marquerToutLu}
                          </button>
                        )}
                      </div>

                      {/* Notification list */}
                      <div className="max-h-80 overflow-y-auto">
                        {notifications.length === 0 ? (
                          <div className="flex flex-col items-center py-10 text-surface-400">
                            <Bell className="w-10 h-10 mb-2 opacity-50" />
                            <p className="text-sm">{t.notif.aucune}</p>
                          </div>
                        ) : (
                          notifications.map((notif) => {
                            const typeIcons: Record<string, React.ReactNode> = {
                              NOUVEAU_RDV: <CalendarCheck className="w-4 h-4 text-primary-500" />,
                              ANNULATION_RDV: <X className="w-4 h-4 text-danger-500" />,
                              RAPPEL_RDV: <Calendar className="w-4 h-4 text-accent-500" />,
                              FACTURE: <DollarSign className="w-4 h-4 text-health-500" />,
                            };
                            return (
                              <button
                                key={notif.id}
                                className={`w-full flex items-start space-x-3 px-4 py-3 text-left transition-colors hover:bg-primary-50/50 dark:hover:bg-primary-900/10 ${
                                  !notif.lu ? 'bg-primary-50/30 dark:bg-primary-900/5' : ''
                                }`}
                                onClick={() => {
                                  if (notif.lien) {
                                    navigate(notif.lien);
                                  }
                                  if (!notif.lu) {
                                    marquerLu(notif.id);
                                  }
                                  setShowNotifPanel(false);
                                }}
                              >
                                {/* Icon */}
                                <div className="w-8 h-8 rounded-full bg-surface-100 dark:bg-surface-200 flex items-center justify-center shrink-0">
                                  {typeIcons[notif.type] || <Info className="w-4 h-4 text-primary-500" />}
                                </div>

                                {/* Content */}
                                <div className="flex-1 min-w-0">
                                  <p className="text-sm font-medium text-surface-900 truncate">
                                    {notif.titre}
                                  </p>
                                  {notif.contenu && (
                                    <p className="text-xs text-surface-500 mt-0.5 line-clamp-2">
                                      {notif.contenu}
                                    </p>
                                  )}
                                  <p className="text-xs text-surface-400 mt-1">
                                    {formatDistanceToNow(new Date(notif.createdAt), {
                                      addSuffix: true,
                                      locale: language === 'fr' ? fr : undefined,
                                    })}
                                  </p>
                                </div>

                                {/* Unread dot */}
                                {!notif.lu && (
                                  <div className="w-2 h-2 rounded-full bg-primary-500 mt-1.5 shrink-0" />
                                )}
                              </button>
                            );
                          })
                        )}
                      </div>
                    </div>
                  </>
                )}
              </div>

              {/* User menu */}
              <div className="relative">
                <button
                  className="flex items-center space-x-2 p-2 rounded-lg hover:bg-surface-100 transition-colors"
                  onClick={() => setShowUserMenu(!showUserMenu)}
                >
                  <div className="w-8 h-8 bg-primary-100 dark:bg-primary-900/40 rounded-full flex items-center justify-center">
                    <span className="text-sm font-semibold text-primary-700 dark:text-primary-300">
                      {user.prenom[0]}{user.nom[0]}
                    </span>
                  </div>
                  <div className="hidden sm:block text-left">
                    <p className="text-sm font-medium text-surface-900">{user.prenom} {user.nom}</p>
                    <p className="text-xs text-surface-500">{roleLabel}</p>
                  </div>
                  <ChevronDown className="w-4 h-4 text-surface-400" />
                </button>

                {showUserMenu && (
                  <>
                    <div className="fixed inset-0 z-10" onClick={() => setShowUserMenu(false)} />
                    <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-surface-200 py-1 z-20 animate-scale-in">
                      <div className="px-4 py-3 border-b border-surface-100">
                        <p className="text-sm font-medium text-surface-900">{user.prenom} {user.nom}</p>
                        <p className="text-xs text-surface-500">{user.email}</p>
                      </div>
                      <button
                        onClick={handleLogout}
                        className="w-full flex items-center px-4 py-2.5 text-sm text-danger-600 hover:bg-danger-50 transition-colors"
                      >
                        <LogOut className="w-4 h-4 mr-2" />
                        {t.auth.logout}
                      </button>
                    </div>
                  </>
                )}
              </div>
            </div>
          </div>
        </nav>

        {/* Page content */}
        <main className="p-4 sm:p-6 lg:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

const SidebarContent: React.FC<{
  navItems: Array<{ name: string; href: string; icon: React.FC<{ className?: string }> }>;
  user: { prenom: string; nom: string; role: string };
  t: ReturnType<typeof useTranslation>['t'];
  onClose: () => void;
}> = ({ navItems, user, t, onClose }) => {
  const { messagesNonLus } = useChatStore();
  return (
  <div className="flex flex-col h-full">
    <div className="flex items-center justify-between px-6 py-5 border-b border-surface-200">
      <div className="flex items-center space-x-3">
        <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center shadow-md shadow-primary-600/30">
          <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M12 4v16M4 12h16" strokeWidth="2.5" strokeLinecap="round"/>
            <circle cx="12" cy="12" r="9" strokeWidth="1.5"/>
          </svg>
        </div>
        <div>
          <h2 className="text-lg font-display font-bold text-surface-900">{t.app.name}</h2>
          <p className="text-xs text-surface-500">{t.app.subtitle}</p>
        </div>
      </div>
      <button className="lg:hidden p-1 rounded-lg hover:bg-surface-100" onClick={onClose}>
        <X className="w-5 h-5 text-surface-500" />
      </button>
    </div>

    <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
      {navItems.map((item) => (
        <NavLink
          key={item.href}
          to={item.href}
          onClick={onClose}
          className={({ isActive }) =>
            `flex items-center px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 ${
              isActive
                ? 'bg-primary-50 text-primary-700 border-r-2 border-primary-500 dark:bg-primary-900/20 dark:text-primary-300 dark:border-primary-400'
                : 'text-surface-600 hover:bg-surface-50 hover:text-surface-900 dark:hover:bg-surface-100/50'
            }`
          }
        >
          <item.icon className="w-5 h-5 mr-3 flex-shrink-0" />
          <span className="flex-1">{item.name}</span>
          {item.href === '/chat' && messagesNonLus > 0 && (
            <span className="ml-2 bg-primary-500 text-white text-[11px] font-bold min-w-[20px] h-5 px-1.5 rounded-full flex items-center justify-center">
              {messagesNonLus > 99 ? '99+' : messagesNonLus}
            </span>
          )}
        </NavLink>
      ))}
    </nav>

    <div className="px-3 py-4 border-t border-surface-200">
      <div className="flex items-center px-3 py-2 text-xs text-surface-400">
        <span className="inline-block w-2 h-2 rounded-full bg-green-400 mr-2" />
        {t.common.connected}
      </div>
    </div>    </div>
  );
};

export default DashboardLayout;
