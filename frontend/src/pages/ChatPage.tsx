import React, { useEffect, useState, useRef } from 'react';
import { useChatStore } from '../store/chatStore';
import { useAuthStore } from '../store/authStore';
import Avatar from '../components/ui/Avatar';
import BackButton from '../components/ui/BackButton';
import Spinner from '../components/ui/Spinner';
import { Send, MessageCircle, Plus, X, Search, UserPlus, Check, CheckCheck } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { useTranslation } from '../contexts/LanguageContext';
import { messageApi } from '../services/api';

const ChatPage: React.FC = () => {
  const { contacts, selectedContactId, conversations, isLoading, fetchContacts, selectContact, sendMessage, setCurrentUser, startPolling, stopPolling, onlineUserIds } = useChatStore();
  const { user } = useAuthStore();
  const { t } = useTranslation();
  const [newMessage, setNewMessage] = useState('');
  const [showNewChat, setShowNewChat] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Array<{ id: number; nom: string; prenom: string; email: string; role: string; photoUrl: string }>>([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const searchInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => { if (user?.id) setCurrentUser(user.id); }, [user, setCurrentUser]);

  useEffect(() => {
    fetchContacts();
    startPolling();
    return () => stopPolling();
  }, [fetchContacts, startPolling, stopPolling]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [conversations, selectedContactId]);

  useEffect(() => {
    if (showNewChat && searchInputRef.current) {
      searchInputRef.current.focus();
    }
  }, [showNewChat]);

  useEffect(() => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }
    const timer = setTimeout(async () => {
      setSearchLoading(true);
      try {
        const res = await messageApi.searchUsers(searchQuery);
        const contactIds = new Set(contacts.map(c => c.id));
        setSearchResults(res.data.filter((u: any) => !contactIds.has(u.id)));
      } catch (err) {
        console.error('Erreur recherche:', err);
      } finally {
        setSearchLoading(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery, contacts]);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !selectedContactId) return;
    await sendMessage({ destinataireId: selectedContactId, contenu: newMessage });
    setNewMessage('');
  };

  const handleStartConversation = async (contactId: number) => {
    setShowNewChat(false);
    setSearchQuery('');
    setSearchResults([]);
    await fetchContacts();
    await selectContact(contactId);
  };

  const currentMessages = selectedContactId ? conversations[selectedContactId] || [] : [];
  const selectedContact = contacts.find(c => c.id === selectedContactId);

  const isOnline = (contactId: number) => onlineUserIds.includes(contactId);

  return (
    <div className="animate-fade-in">
      <BackButton to="/dashboard" label={t.buttons.retourDashboard} />
      <div className="flex h-[calc(100vh-13.5rem)] relative">
      {/* Liste des contacts */}
      <div className="w-72 lg:w-80 bg-white rounded-l-xl border border-surface-200 flex flex-col">
        <div className="p-4 border-b border-surface-200 flex items-center justify-between">
          <h2 className="font-semibold text-surface-900">{t.chat.conversations}</h2>
          <button
            onClick={() => setShowNewChat(true)}
            className="p-2 rounded-lg bg-primary-50 text-primary-600 hover:bg-primary-100 transition-colors"
            title={t.chat.nouvelleConversation || 'Nouvelle conversation'}
          >
            <UserPlus className="w-4 h-4" />
          </button>
        </div>
        <div className="flex-1 overflow-y-auto">
          {contacts.length === 0 ? (
            <div className="text-center py-8 text-surface-400 text-sm">{t.empty.aucuneConversation}</div>
          ) : contacts.map(c => (
            <div key={c.id}
              className={`flex items-center space-x-3 p-3 cursor-pointer transition-colors relative ${selectedContactId === c.id ? 'bg-primary-50 border-l-4 border-primary-600' : 'hover:bg-surface-50'}`}
              onClick={() => selectContact(c.id)}>
              <div className="relative shrink-0">
                <Avatar nom={c.nom} prenom={c.prenom} size="sm" />
                {isOnline(c.id) ? (
                  <span className="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-green-500 border-2 border-white dark:border-surface-50 rounded-full shadow-sm" />
                ) : (
                  <span className="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-surface-300 border-2 border-white dark:border-surface-50 rounded-full" />
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center space-x-2">
                  <p className="text-sm font-medium text-surface-900 truncate">{c.prenom} {c.nom}</p>
                  {isOnline(c.id) && (
                    <span className="text-[10px] font-medium text-green-600 shrink-0">●</span>
                  )}
                </div>
                <p className="text-xs text-surface-500">{t.roles[c.role as keyof typeof t.roles] || c.role}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Zone de chat */}
      <div className="flex-1 bg-white rounded-r-xl border-t border-b border-r border-surface-200 flex flex-col">
        {!selectedContactId ? (
          <div className="flex-1 flex items-center justify-center text-surface-400">
            <div className="text-center">
              <MessageCircle className="w-16 h-16 mx-auto mb-4 opacity-50" />
              <p>{t.chat.selectionnezConversation}</p>
              <button
                onClick={() => setShowNewChat(true)}
                className="mt-4 inline-flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors text-sm"
              >
                <Plus className="w-4 h-4 mr-1.5" />
                {t.chat.nouvelleConversation || 'Nouvelle conversation'}
              </button>
            </div>
          </div>
        ) : isLoading ? (
          <div className="flex-1 flex items-center justify-center"><Spinner /></div>
        ) : (
          <>
            {/* Header du chat */}
            <div className="p-4 border-b border-surface-200 bg-surface-50">
              {selectedContact && (
                <div className="flex items-center space-x-3">
                  <div className="relative">
                    <Avatar nom={selectedContact.nom} prenom={selectedContact.prenom} />
                    {isOnline(selectedContact.id) && (
                      <span className="absolute -bottom-0.5 -right-0.5 w-4 h-4 bg-green-500 border-2 border-white dark:border-surface-50 rounded-full shadow-sm" />
                    )}
                  </div>
                  <div>
                    <p className="font-medium text-surface-900">{selectedContact.prenom} {selectedContact.nom}</p>
                    <p className={`text-xs flex items-center ${isOnline(selectedContact.id) ? 'text-green-600' : 'text-surface-400'}`}>
                      <span className={`inline-block w-2 h-2 rounded-full mr-1.5 ${isOnline(selectedContact.id) ? 'bg-green-500' : 'bg-surface-300'}`} />
                      {isOnline(selectedContact.id) ? t.chat.enLigne : t.chat.horsLigne || 'Hors ligne'}
                    </p>
                  </div>
                </div>
              )}
            </div>

            {/* Messages */}
            <div className="flex-1 overflow-y-auto p-4 space-y-3">
              {currentMessages.length === 0 ? (
                <p className="text-center text-surface-400 py-8">{t.chat.aucunMessage}</p>
              ) : (
                currentMessages.map(msg => {
                  const isMoi = msg.expediteurId === user?.id;
                  return (
                    <div key={msg.id} className={`flex ${isMoi ? 'justify-end' : 'justify-start'}`}>
                      <div className={`max-w-[70%] p-3 rounded-2xl ${isMoi ? 'bg-primary-600 text-white rounded-br-sm' : 'bg-surface-100 text-surface-900 rounded-bl-sm'}`}>
                        <p className="text-sm whitespace-pre-wrap break-words">{msg.contenu}</p>
                        {/* Indicateurs de statut : SEULEMENT pour les messages envoyés (isMoi) */}
                        {isMoi ? (
                          <div className="flex items-center justify-end space-x-1 mt-1 text-primary-200">
                            <span className="text-[11px]">{format(new Date(msg.createdAt), 'HH:mm')}</span>
                            {/* ✓ Envoyé (non délivré) */}
                            {!msg.delivered && (
                              <Check className="w-3 h-3 opacity-60" />
                            )}
                            {/* ✓✓ gris = Délivré (non lu) */}
                            {msg.delivered && !msg.lu && (
                              <CheckCheck className="w-3.5 h-3.5 text-primary-200/60" />
                            )}
                            {/* ✓✓ bleu = Lu */}
                            {msg.lu && (
                              <CheckCheck className="w-3.5 h-3.5 text-blue-300" />
                            )}
                          </div>
                        ) : (
                          /* Pour les messages reçus : juste l'heure, PAS de voyants */
                          <div className="flex items-center justify-start mt-1">
                            <span className="text-[11px] text-surface-400">{format(new Date(msg.createdAt), 'HH:mm')}</span>
                          </div>
                        )}
                      </div>
                    </div>
                  );
                })
              )}
              <div ref={messagesEndRef} />
            </div>

            {/* Input */}
            <form onSubmit={handleSend} className="p-4 border-t border-surface-200">
              <div className="flex items-center space-x-2">
                <input type="text" value={newMessage} onChange={e => setNewMessage(e.target.value)}
                  placeholder={t.chat.placeholder}
                  className="flex-1 px-4 py-2.5 border border-surface-300 rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
                <button type="submit" disabled={!newMessage.trim()}
                  className="p-2.5 bg-primary-600 text-white rounded-full hover:bg-primary-700 disabled:opacity-50 transition-colors">
                  <Send className="w-4 h-4" />
                </button>
              </div>
            </form>
          </>
        )}
      </div>

      {/* Modal Nouvelle conversation */}
      {showNewChat && (
        <div className="fixed inset-0 z-50 flex items-start justify-center pt-20">
          <div className="fixed inset-0 bg-black/40" onClick={() => { setShowNewChat(false); setSearchQuery(''); setSearchResults([]); }} />
          <div className="relative bg-white rounded-xl shadow-2xl border border-surface-200 w-full max-w-md mx-4 animate-scale-in">
            <div className="p-4 border-b border-surface-200 flex items-center justify-between">
              <h3 className="font-semibold text-surface-900">{t.chat.nouvelleConversation || 'Nouvelle conversation'}</h3>
              <button onClick={() => { setShowNewChat(false); setSearchQuery(''); setSearchResults([]); }}
                className="p-1 rounded-lg hover:bg-surface-100 text-surface-400">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="p-4">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-surface-400" />
                <input
                  ref={searchInputRef}
                  type="text"
                  value={searchQuery}
                  onChange={e => setSearchQuery(e.target.value)}
                  placeholder={t.common.search || 'Rechercher un utilisateur...'}
                  className="w-full pl-10 pr-4 py-2.5 border border-surface-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"
                />
              </div>
            </div>

            <div className="max-h-64 overflow-y-auto px-4 pb-4">
              {searchLoading ? (
                <div className="flex justify-center py-4"><Spinner /></div>
              ) : searchQuery.trim() && searchResults.length === 0 ? (
                <p className="text-center text-surface-400 text-sm py-4">{t.common.noResults}</p>
              ) : searchResults.length > 0 ? (
                <div className="space-y-1">
                  {searchResults.map((u: any) => (
                    <button
                      key={u.id}
                      onClick={() => handleStartConversation(u.id)}
                      className="w-full flex items-center space-x-3 p-3 rounded-lg hover:bg-primary-50 transition-colors text-left"
                    >
                      <div className="relative shrink-0">
                        <Avatar nom={u.nom} prenom={u.prenom} size="sm" />
                        {isOnline(u.id) && (
                          <span className="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-green-500 border-2 border-white dark:border-surface-50 rounded-full" />
                        )}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-surface-900">{u.prenom} {u.nom}</p>
                        <p className="text-xs text-surface-500">{t.roles[u.role as keyof typeof t.roles] || u.role} · {u.email}</p>
                      </div>
                    </button>
                  ))}
                </div>
              ) : searchQuery.trim() === '' ? (
                <p className="text-center text-surface-400 text-sm py-4">{t.common.search}</p>
              ) : null}
            </div>
          </div>
        </div>
      )}
      </div>
    </div>
  );
};

export default ChatPage;
