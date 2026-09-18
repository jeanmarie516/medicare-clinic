import fr, { type Translations } from './fr';
import en from './en';

export const translations = { fr, en } as const;
export type { Translations };
export type SupportedLanguage = keyof typeof translations;
export const supportedLanguages: SupportedLanguage[] = ['fr', 'en'];
export const languageLabels: Record<SupportedLanguage, string> = {
  fr: 'Français',
  en: 'English',
};

export { fr, en };
