import React from 'react';
import { Outlet } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';
import { useTheme } from '../contexts/ThemeContext';
import { useTranslation } from '../contexts/LanguageContext';
import { Sun, Moon, Languages, Heart, Shield, Clock, CalendarCheck, FileText, Receipt, MessageCircle } from 'lucide-react';

const AuthLayout: React.FC = () => {
  const { theme, toggleTheme } = useTheme();
  const { t, language, toggleLanguage } = useTranslation();

  return (
    <div className="min-h-screen flex bg-surface-50">
      <Helmet>
        <title>{t.auth.login} - {t.app.name}</title>
      </Helmet>

      {/* Left Side - Branding */}
      <div className="hidden lg:flex lg:w-1/2 relative overflow-hidden medical-gradient">
        {/* Animated background pattern - caduceus/medical glow */}
        <div className="absolute inset-0">
          <div className="absolute top-0 -left-40 w-96 h-96 bg-white/10 rounded-full blur-3xl animate-pulse-slow" />
          <div className="absolute bottom-0 -right-20 w-80 h-80 bg-teal-200/10 rounded-full blur-3xl animate-pulse-slow" style={{ animationDelay: '1s' }} />
          <div className="absolute top-1/3 right-1/4 w-48 h-48 bg-emerald-300/10 rounded-full blur-3xl animate-pulse-slow" style={{ animationDelay: '2s' }} />
        </div>

        {/* Medical + healing symbols SVG pattern */}
        <div className="absolute inset-0 opacity-8">
          <svg className="w-full h-full" viewBox="0 0 500 500" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <pattern id="medical-cross-pattern" x="0" y="0" width="80" height="80" patternUnits="userSpaceOnUse">
                {/* Medical cross */}
                <rect x="36" y="24" width="8" height="32" rx="2" fill="white" opacity="0.15"/>
                <rect x="24" y="36" width="32" height="8" rx="2" fill="white" opacity="0.15"/>
                {/* Small circle dots */}
                <circle cx="10" cy="10" r="1.5" fill="white" opacity="0.1"/>
                <circle cx="70" cy="70" r="1.5" fill="white" opacity="0.1"/>
                {/* DNA helix subtle */}
                <path d="M72 10 Q76 15 72 20 Q68 25 72 30" stroke="white" strokeWidth="0.5" fill="none" opacity="0.08"/>
                <path d="M8 50 Q12 55 8 60 Q4 65 8 70" stroke="white" strokeWidth="0.5" fill="none" opacity="0.08"/>
              </pattern>
            </defs>
            <rect width="500" height="500" fill="url(#medical-cross-pattern)" />
          </svg>
        </div>

        {/* Content */}
        <div className="relative z-10 flex flex-col justify-between w-full p-16">
          {/* Logo Area */}
          <div>
            <div className="flex items-center space-x-4 mb-16">
              <div className="w-14 h-14 bg-white rounded-2xl flex items-center justify-center shadow-lg shadow-teal-900/30">
                <svg className="w-8 h-8 text-primary-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M12 4v16M4 12h16" strokeWidth="2.5" strokeLinecap="round"/>
                  <circle cx="12" cy="12" r="9" strokeWidth="1.5"/>
                </svg>
              </div>
              <div>
                <h1 className="text-2xl font-bold text-white font-display tracking-tight">{t.app.name}</h1>
                <p className="text-primary-200 text-sm">{t.app.subtitle}</p>
              </div>
            </div>

            {/* Hero Text */}
            <div className="space-y-6 max-w-lg">
              <h2 className="text-4xl font-display font-bold text-white leading-tight">
                {t.branding.hero}<br />
                <span className="text-primary-200">{t.branding.heroHighlight}</span>
              </h2>
              <p className="text-primary-100/80 text-lg leading-relaxed">
                {t.branding.description}
              </p>
            </div>

            {/* Features Grid - Medical themed */}
            <div className="mt-16 grid grid-cols-2 gap-6 max-w-lg">
              {(
                [
                  { Icon: CalendarCheck, key: 'rdv' as const },
                  { Icon: FileText, key: 'dossiers' as const },
                  { Icon: Receipt, key: 'facturation' as const },
                  { Icon: MessageCircle, key: 'messagerie' as const },
              ]).map(({ Icon, key }) => (
                <div key={key} className="flex items-start space-x-3 text-primary-100/90 group cursor-default">
                  <div className="w-10 h-10 rounded-xl bg-white/10 flex items-center justify-center backdrop-blur-sm group-hover:bg-white/20 transition-all">
                    <Icon className="w-5 h-5" />
                  </div>
                  <div>
                    <span className="text-sm font-semibold block">{t.branding.features[key].title}</span>
                    <span className="text-xs text-primary-200/60">{t.branding.features[key].desc}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Footer */}
          <div className="flex items-center space-x-6 text-primary-200/60 text-sm">
            <div className="flex items-center space-x-2">
              <Shield className="w-4 h-4" />
              <span>{t.branding.footer.securite}</span>
            </div>
            <div className="flex items-center space-x-2">
              <Clock className="w-4 h-4" />
              <span>{t.branding.footer.disponibilite}</span>
            </div>
            <div className="flex items-center space-x-2">
              <Heart className="w-4 h-4" />
              <span>{t.branding.footer.qualite}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Right Side - Login Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-12 relative">
        {/* Theme & Language Toggles */}
        <div className="absolute top-6 right-6 flex items-center space-x-2 z-10">
          <button
            onClick={toggleLanguage}
            className="px-3 py-2 rounded-xl bg-white dark:bg-surface-100 border border-surface-200 text-surface-500 hover:text-primary-600 hover:border-primary-300 transition-all shadow-sm flex items-center space-x-1"
            title={t.language.toggle}
          >
            <Languages className="w-4 h-4" />
            <span className="text-xs font-semibold uppercase tracking-wider">{language}</span>
          </button>
          <button
            onClick={toggleTheme}
            className="p-2 rounded-xl bg-white dark:bg-surface-100 border border-surface-200 text-surface-500 hover:text-yellow-500 hover:border-yellow-300 transition-all shadow-sm"
            title={theme === 'dark' ? t.theme.light : t.theme.dark}
          >
            {theme === 'dark' ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
          </button>
        </div>

        {/* Mobile Logo */}
        <div className="lg:hidden absolute top-6 left-6 flex items-center space-x-3">
          <div className="w-10 h-10 bg-primary-600 rounded-xl flex items-center justify-center shadow-md">
            <Heart className="w-5 h-5 text-white" fill="currentColor" />
          </div>
          <div>
            <span className="text-lg font-bold text-surface-900 font-display">{t.app.name}</span>
          </div>
        </div>

        <Outlet />
      </div>
    </div>
  );
};

export default AuthLayout;
