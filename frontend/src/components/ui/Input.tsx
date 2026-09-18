import React, { forwardRef } from 'react';
import { clsx } from 'clsx';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, helperText, leftIcon, className, ...props }, ref) => {
    return (
      <div className="w-full">
        {label && (
          <label className="block text-sm font-medium text-surface-700 mb-1.5">
            {label}
            {props.required && <span className="text-danger-500 ml-1">*</span>}
          </label>
        )}
        <div className="relative">
          {leftIcon && (
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <span className="text-surface-400">{leftIcon}</span>
            </div>
          )}
          <input
            ref={ref}
            className={clsx(
              'block w-full rounded-lg border bg-white px-3 py-2.5 text-sm text-surface-900',
              'placeholder:text-surface-400',
              'transition-colors duration-200',
              'focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500',
              'disabled:bg-surface-50 disabled:text-surface-500 disabled:cursor-not-allowed',
              error
                ? 'border-danger-300 focus:ring-danger-500/20 focus:border-danger-500'
                : 'border-surface-300',
              leftIcon && 'pl-10',
              className
            )}
            {...props}
          />
        </div>
        {error && <p className="mt-1.5 text-xs text-danger-500">{error}</p>}
        {helperText && !error && (
          <p className="mt-1.5 text-xs text-surface-400">{helperText}</p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';
export default Input;
