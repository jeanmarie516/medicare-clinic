import React, { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { type Translations, type SupportedLanguage, translations, supportedLanguages, languageLabels } from '../i18n';

interface LanguageContextType {
  language: SupportedLanguage;
  t: Translations;
  setLanguage: (lang: SupportedLanguage) => void;
  toggleLanguage: () => void;
  supportedLanguages: SupportedLanguage[];
  languageLabels: Record<SupportedLanguage, string>;
}

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

export const LanguageProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [language, setLanguageState] = useState<SupportedLanguage>(() => {
    const stored = localStorage.getItem('medicare_language');
    if (stored === 'fr' || stored === 'en') return stored;
    // Default to French if browser preference is French
    const browserLang = navigator.language;
    if (browserLang.startsWith('fr')) return 'fr';
    return 'en';
  });

  const t = translations[language];

  useEffect(() => {
    localStorage.setItem('medicare_language', language);
    document.documentElement.lang = language === 'fr' ? 'fr' : 'en';
  }, [language]);

  const setLanguage = useCallback((lang: SupportedLanguage) => {
    setLanguageState(lang);
  }, []);

  const toggleLanguage = useCallback(() => {
    setLanguageState(prev => prev === 'fr' ? 'en' : 'fr');
  }, []);

  return (
    <LanguageContext.Provider value={{
      language,
      t,
      setLanguage,
      toggleLanguage,
      supportedLanguages,
      languageLabels,
    }}>
      {children}
    </LanguageContext.Provider>
  );
};

export const useTranslation = (): LanguageContextType => {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useTranslation must be used within a LanguageProvider');
  }
  return context;
};

export { type Translations, type SupportedLanguage };
