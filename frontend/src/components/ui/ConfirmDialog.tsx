import React from 'react';
import { AlertTriangle } from 'lucide-react';
import Modal from './Modal';
import Button from './Button';

interface ConfirmDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title?: string;
  message?: string;
  confirmText?: string;
  cancelText?: string;
  variant?: 'danger' | 'primary';
  isLoading?: boolean;
}

const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  isOpen, onClose, onConfirm, title = 'Confirmer', message = 'Êtes-vous sûr de vouloir continuer ?',
  confirmText = 'Confirmer', cancelText = 'Annuler', variant = 'danger', isLoading = false,
}) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} size="sm">
      <div className="text-center py-4">
        <div className={`mx-auto w-12 h-12 rounded-full flex items-center justify-center mb-4 ${
          variant === 'danger' ? 'bg-red-100' : 'bg-primary-100'
        }`}>
          <AlertTriangle className={`w-6 h-6 ${variant === 'danger' ? 'text-red-600' : 'text-primary-600'}`} />
        </div>
        <h3 className="text-lg font-semibold text-surface-900">{title}</h3>
        <p className="mt-2 text-sm text-surface-500">{message}</p>
      </div>
      <div className="flex items-center justify-center space-x-3 mt-4">
        <Button variant="ghost" onClick={onClose} disabled={isLoading}>{cancelText}</Button>
        <Button variant={variant} onClick={onConfirm} isLoading={isLoading}>{confirmText}</Button>
      </div>
    </Modal>
  );
};

export default ConfirmDialog;
