export const Input = ({ 
  type = 'text', 
  placeholder, 
  value, 
  onChange, 
  className = '',
  error = false,
  ...props 
}) => {
  const baseClasses = 'block w-full px-3 py-2 border rounded-md text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-offset-0 transition-colors';
  const normalClasses = 'border-gray-300 focus:border-blue-500 focus:ring-blue-500';
  const errorClasses = 'border-red-300 focus:border-red-500 focus:ring-red-500';
  
  const combinedClasses = `${baseClasses} ${error ? errorClasses : normalClasses} ${className}`;

  return (
    <input
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      className={combinedClasses}
      {...props}
    />
  );
};

export const Label = ({ children, htmlFor, className = '' }) => {
  return (
    <label 
      htmlFor={htmlFor}
      className={`block text-sm font-medium text-gray-700 mb-1 ${className}`}
    >
      {children}
    </label>
  );
};