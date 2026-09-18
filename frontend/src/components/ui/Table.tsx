import React from 'react';
import { clsx } from 'clsx';
import { ChevronUp, ChevronDown, ChevronsUpDown } from 'lucide-react';
import { useTranslation } from '../../contexts/LanguageContext';

interface Column<T> {
  key: string;
  header: string;
  render?: (item: T) => React.ReactNode;
  sortable?: boolean;
  className?: string;
}

interface TableProps<T> {
  columns: Column<T>[];
  data: T[];
  isLoading?: boolean;
  onRowClick?: (item: T) => void;
  emptyMessage?: string;
  sortKey?: string;
  sortDirection?: 'asc' | 'desc';
  onSort?: (key: string) => void;
}

function Table<T extends { id?: number }>({
  columns, data, isLoading, onRowClick, emptyMessage, sortKey, sortDirection, onSort,
}: TableProps<T>) {
  const { t } = useTranslation();
  const displayEmpty = emptyMessage || t.empty.aucuneDonnee;

  if (isLoading) {
    return (
      <div className="medical-card">
        <div className="p-12 text-center">
          <div className="animate-spin w-10 h-10 border-2 border-primary-500 border-t-transparent rounded-full mx-auto" />
          <p className="mt-4 text-sm text-surface-400">{t.common.loading}</p>
        </div>
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="medical-card">
        <div className="p-12 text-center">
          <p className="text-sm text-surface-400">{displayEmpty}</p>
        </div>
      </div>
    );
  }

  return (
    <>
    {/* Vue mobile : cartes empilées */}
    <div className="sm:hidden space-y-3">
      {data.map((item, idx) => (
        <div
          key={(item as any).id || idx}
          className={`medical-card p-4 ${onRowClick ? 'cursor-pointer active:bg-surface-50' : ''}`}
          onClick={() => onRowClick?.(item)}
        >
          {columns.map((col) => (
            <div key={col.key} className="flex items-start justify-between gap-4 py-1 text-sm border-b border-surface-100 dark:border-surface-300/10 last:border-0">
              <span className="text-surface-500 shrink-0 font-medium">{col.header}</span>
              <span className="text-surface-700 dark:text-surface-300 flex-1 min-w-0 text-right">{col.render ? col.render(item) : (item as any)[col.key] ?? '-'}</span>
            </div>
          ))}
        </div>
      ))}
    </div>

    {/* Vue desktop / tablette : tableau avec défilement horizontal */}
    <div className="hidden sm:block medical-card p-0 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-surface-200 dark:divide-surface-300/20">
          <thead>
            <tr className="bg-gradient-to-r from-primary-50/80 to-surface-50 dark:from-primary-900/10 dark:to-surface-100">
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={clsx(
                    'px-4 py-3.5 text-left text-xs font-semibold text-surface-600 dark:text-surface-400 uppercase tracking-wider',
                    col.sortable && 'cursor-pointer select-none hover:text-primary-600 dark:hover:text-primary-400',
                    col.className
                  )}
                  onClick={() => col.sortable && onSort?.(col.key)}
                >
                  <div className="flex items-center space-x-1">
                    <span>{col.header}</span>
                    {col.sortable && (
                      <span className="text-surface-300 dark:text-surface-500">
                        {sortKey === col.key ? (
                          sortDirection === 'asc' ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />
                        ) : (
                          <ChevronsUpDown className="w-3.5 h-3.5" />
                        )}
                      </span>
                    )}
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-surface-100 dark:divide-surface-300/10">
            {data.map((item, idx) => (
              <tr
                key={(item as any).id || idx}
                className={clsx(
                  'transition-all duration-150',
                  onRowClick
                    ? 'cursor-pointer hover:bg-primary-50/60 dark:hover:bg-primary-900/10 hover:shadow-sm'
                    : 'hover:bg-surface-50 dark:hover:bg-surface-100/30'
                )}
                onClick={() => onRowClick?.(item)}
              >
                {columns.map((col) => (
                  <td key={col.key} className={clsx('px-4 py-3.5 text-sm text-surface-700 dark:text-surface-300', col.className)}>
                    {col.render ? col.render(item) : (item as any)[col.key] ?? '-'}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
    </>
  );
}

export default Table;
