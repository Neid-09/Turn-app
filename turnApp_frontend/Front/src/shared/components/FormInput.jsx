export default function FormInput({
  label,
  name,
  type = 'text',
  value,
  onChange,
  placeholder,
  required = false,
  icon: Icon,
  error,
  className = '',
  inputClassName = '',
  disabled = false,
  helpText,
  ...props
}) {
  return (
    <div className={`space-y-2 ${className}`}>
      {label && (
        <label className="block text-sm font-semibold text-gray-700">
          {label} {required && <span className="text-red-500">*</span>}
        </label>
      )}
      <div>
        <div className="relative">
          {Icon && (
            <Icon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5 pointer-events-none" />
          )}
          {type === 'select' ? (
            <select
              name={name}
              value={value}
              onChange={onChange}
              disabled={disabled}
              className={`w-full ${Icon ? 'pl-11' : 'pl-4'} pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 transition-all appearance-none bg-white cursor-pointer ${
                error
                  ? 'border-red-500 focus:ring-red-500'
                  : 'border-gray-300 focus:ring-blue-500 focus:border-transparent'
              } ${disabled ? 'bg-gray-100 cursor-not-allowed' : ''} ${inputClassName}`}
              {...props}
            >
              {props.children}
            </select>
          ) : (
            <input
              type={type}
              name={name}
              value={value}
              onChange={onChange}
              placeholder={placeholder}
              disabled={disabled}
              className={`w-full ${Icon ? 'pl-11' : 'pl-4'} pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 transition-all ${
                error
                  ? 'border-red-500 focus:ring-red-500'
                  : 'border-gray-300 focus:ring-blue-500 focus:border-transparent'
              } ${disabled ? 'bg-gray-100 cursor-not-allowed' : ''} ${inputClassName}`}
              {...props}
            />
          )}
        </div>
        {error && (
          <p className="text-red-500 text-xs mt-1 ml-1">{error}</p>
        )}
        {helpText && !error && (
          <p className="text-xs text-gray-500 ml-1">{helpText}</p>
        )}
      </div>
    </div>
  );
}
