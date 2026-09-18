import React from 'react';
import { clsx } from 'clsx';

interface AvatarProps {
  nom: string;
  prenom: string;
  photoUrl?: string;
  size?: 'sm' | 'md' | 'lg' | 'xl';
  className?: string;
}

const Avatar: React.FC<AvatarProps> = ({ nom, prenom, photoUrl, size = 'md', className }) => {
  const sizes = { sm: 'w-8 h-8 text-xs', md: 'w-10 h-10 text-sm', lg: 'w-14 h-14 text-lg', xl: 'w-20 h-20 text-2xl' };
  const initials = `${prenom?.[0] || ''}${nom?.[0] || ''}`.toUpperCase();

  if (photoUrl) {
    return <img src={photoUrl} alt={`${prenom} ${nom}`} className={clsx('rounded-full object-cover', sizes[size], className)} />;
  }

  return (
    <div className={clsx(
      'rounded-full bg-primary-100 text-primary-700 font-semibold flex items-center justify-center',
      sizes[size], className
    )}>
      {initials}
    </div>
  );
};

export default Avatar;
