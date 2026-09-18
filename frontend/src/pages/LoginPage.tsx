import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { useTranslation } from '../contexts/LanguageContext';
import { Eye, EyeOff, LogIn, Mail, Lock, AlertCircle } from 'lucide-react';
import toast from 'react-hot-toast';

const demoAccounts = [
  { email: 'jkaremamana@gmail.com', password: 'Bonheur0407@', role: 'ADMIN', label: 'Administrateur', color: 'from-primary-600 to-primary-700', bg: 'bg-primary-50 dark:bg-primary-950/30' },
];

const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [selectedDemo, setSelectedDemo] = useState<number | null>(null);
  const [fieldErrors, setFieldErrors] = useState<{ email?: string; password?: string }>({});
  const { login, isLoading } = useAuthStore();
  const { t } = useTranslation();
  const navigate = useNavigate();

  const validate = (): boolean => {
    const errors: { email?: string; password?: string } = {};
    if (!email.trim()) {
      errors.email = t.auth.fillFields;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      errors.email = t.auth.loginError;
    }
    if (!password) {
      errors.password = t.auth.fillFields;
    } else if (password.length < 3) {
      errors.password = t.auth.loginError;
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    try {
      await login(email, password);
      toast.success(t.auth.loginSuccess);
      navigate('/dashboard');
    } catch (err: any) {
      const message = err instanceof Error ? err.message : t.auth.loginError;
      toast.error(message);
    }
  };

  const handleDemoClick = (account: typeof demoAccounts[0], index: number) => {
    setSelectedDemo(index);
    setEmail(account.email);
    setPassword(account.password);
    setFieldErrors({});
  };

  return (
    <div className="w-full max-w-md animate-fade-in">
      {/* Title */}
      <div className="mb-8">
        <div className="lg:hidden flex items-center justify-center w-14 h-14 bg-primary-600 rounded-2xl mb-4 mx-auto shadow-lg">
          <svg className="w-7 h-7 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M12 4v16M4 12h16" strokeWidth="2.5" strokeLinecap="round"/>
            <circle cx="12" cy="12" r="9" strokeWidth="1.5"/>
          </svg>
        </div>
        <h2 className="text-2xl lg:text-3xl font-display font-bold text-surface-900 text-center lg:text-left">{t.auth.welcome}</h2>
        <p className="text-surface-500 mt-2 text-center lg:text-left lg:text-lg">{t.auth.loginTitle}</p>
      </div>

      {/* Login Form */}
      <form onSubmit={handleSubmit} className="space-y-5">
        {/* Email Field */}
        <div>
          <label className="block text-sm font-medium text-surface-700 dark:text-surface-300 mb-1.5">
            <Mail className="w-4 h-4 inline mr-1.5 -mt-0.5" />
            {t.auth.email}
          </label>
          <div className="relative">
            <input
              type="email"
              value={email}
              onChange={(e) => { setEmail(e.target.value); setFieldErrors(f => ({ ...f, email: undefined })); }}
              placeholder={t.auth.emailPlaceholder}
              className={`w-full px-4 py-3 rounded-xl border text-sm transition-all
                ${fieldErrors.email
                  ? 'border-danger-300 focus:ring-danger-500/20 focus:border-danger-500'
                  : 'border-surface-300 focus:ring-primary-500/20 focus:border-primary-500'
                }
                bg-white dark:bg-surface-100 text-surface-900 placeholder-surface-400
                focus:outline-none focus:ring-2`}
              autoFocus
            />
          </div>
          {fieldErrors.email && (
            <p className="mt-1.5 text-xs text-danger-500 flex items-center">
              <AlertCircle className="w-3 h-3 mr-1" />
              {fieldErrors.email}
            </p>
          )}
        </div>

        {/* Password Field */}
        <div>
          <label className="block text-sm font-medium text-surface-700 dark:text-surface-300 mb-1.5">
            <Lock className="w-4 h-4 inline mr-1.5 -mt-0.5" />
            {t.auth.password}
          </label>
          <div className="relative">
            <input
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => { setPassword(e.target.value); setFieldErrors(f => ({ ...f, password: undefined })); }}
              placeholder={t.auth.passwordPlaceholder}
              className={`w-full px-4 py-3 pr-12 rounded-xl border text-sm transition-all
                ${fieldErrors.password
                  ? 'border-danger-300 focus:ring-danger-500/20 focus:border-danger-500'
                  : 'border-surface-300 focus:ring-primary-500/20 focus:border-primary-500'
                }
                bg-white dark:bg-surface-100 text-surface-900 placeholder-surface-400
                focus:outline-none focus:ring-2`}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-surface-400 hover:text-surface-600 dark:hover:text-surface-300 transition-colors p-1"
            >
              {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>
          {fieldErrors.password && (
            <p className="mt-1.5 text-xs text-danger-500 flex items-center">
              <AlertCircle className="w-3 h-3 mr-1" />
              {fieldErrors.password}
            </p>
          )}
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          disabled={isLoading}
          className="w-full flex items-center justify-center py-3.5 px-4 bg-gradient-to-r from-primary-600 to-primary-700 text-white rounded-xl font-medium
            hover:from-primary-700 hover:to-primary-800 focus:ring-4 focus:ring-primary-300/50 transition-all
            disabled:opacity-50 disabled:cursor-not-allowed shadow-lg shadow-primary-500/25
            active:scale-[0.98]"
        >
          {isLoading ? (
            <>
              <svg className="animate-spin h-5 w-5 mr-2" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
              </svg>
              {t.common.loading}
            </>
          ) : (
            <>
              <LogIn className="w-5 h-5 mr-2" />
              {t.auth.login}
            </>
          )}
        </button>
      </form>

      {/* Divider */}
      <div className="relative my-8">
        <div className="absolute inset-0 flex items-center">
          <div className="w-full border-t border-surface-200 dark:border-surface-300/30" />
        </div>
        <div className="relative flex justify-center text-xs uppercase">
          <span className="bg-white dark:bg-surface-50 px-4 text-surface-400 font-semibold tracking-wider">
            {t.auth.demoAccounts}
          </span>
        </div>
      </div>

      {/* Demo Accounts */}
      <div className="space-y-3">
        {demoAccounts.map((account, index) => (
          <button
            key={account.email}
            type="button"
            onClick={() => handleDemoClick(account, index)}
            className={`w-full flex items-center justify-between p-3.5 rounded-xl border-2 transition-all text-left
              ${selectedDemo === index
                ? 'border-primary-500 bg-primary-50/50 dark:bg-primary-950/30 dark:border-primary-600'
                : 'border-surface-200 dark:border-surface-300/20 hover:border-surface-300 dark:hover:border-surface-300/50 hover:bg-surface-50 dark:hover:bg-surface-100/50'
              }`}
          >
            <div className="flex items-center space-x-3">
              <div className={`w-9 h-9 rounded-lg bg-gradient-to-br ${account.color} flex items-center justify-center shadow-sm`}>
                <span className="text-xs font-bold text-white">
                  {account.role === 'ADMIN' ? 'A' : account.role === 'MEDECIN' ? 'M' : 'S'}
                </span>
              </div>
              <div>
                <p className="text-sm font-medium text-surface-900">{account.label}</p>
                <p className="text-xs text-surface-400 mt-0.5 font-mono">{account.email}</p>
              </div>
            </div>
            <div className="flex-shrink-0">
              <div className={`w-6 h-6 rounded-full border-2 flex items-center justify-center transition-all
                ${selectedDemo === index
                  ? 'border-primary-500 bg-primary-500'
                  : 'border-surface-300'
                }`}
              >
                {selectedDemo === index && (
                  <svg className="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
                  </svg>
                )}
              </div>
            </div>
          </button>
        ))}
      </div>

      {/* Footer Note */}
      <p className="mt-8 text-center text-xs text-surface-400">
        {t.app.name} &copy; {new Date().getFullYear()} &mdash; <span className="text-surface-300">{t.app.subtitle}</span>
      </p>
    </div>
  );
};

export default LoginPage;
