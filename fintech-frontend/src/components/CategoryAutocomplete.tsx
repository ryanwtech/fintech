import { useState, useRef, useEffect } from 'react';
import { useCategories } from '../hooks/useCategories';
import { useDebounce } from '../hooks/useDebounce';
import { Check, ChevronDown, Search, X } from 'lucide-react';

interface CategoryAutocompleteProps {
  value?: string;
  onChange: (categoryId: string | null) => void;
  placeholder?: string;
  disabled?: boolean;
  className?: string;
}

export const CategoryAutocomplete = ({
  value,
  onChange,
  placeholder = "Select category...",
  disabled = false,
  className = "",
}: CategoryAutocompleteProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<any>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const { data: categories, isLoading } = useCategories();
  const debouncedSearchTerm = useDebounce(searchTerm, 300);

  // Filter categories based on search term
  const filteredCategories = categories?.filter(category =>
    category.name.toLowerCase().includes(debouncedSearchTerm.toLowerCase()) ||
    category.description?.toLowerCase().includes(debouncedSearchTerm.toLowerCase())
  ) || [];

  // Find selected category
  useEffect(() => {
    if (value && categories) {
      const category = categories.find(cat => cat.id === value);
      setSelectedCategory(category || null);
    } else {
      setSelectedCategory(null);
    }
  }, [value, categories]);

  // Handle outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
        setSearchTerm('');
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = (category: any) => {
    setSelectedCategory(category);
    onChange(category.id);
    setIsOpen(false);
    setSearchTerm('');
  };

  const handleClear = () => {
    setSelectedCategory(null);
    onChange(null);
    setIsOpen(false);
    setSearchTerm('');
  };

  const handleInputFocus = () => {
    if (!disabled) {
      setIsOpen(true);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Escape') {
      setIsOpen(false);
      setSearchTerm('');
    }
  };

  return (
    <div className={`relative ${className}`} ref={dropdownRef}>
      <div className="relative">
        <input
          ref={inputRef}
          type="text"
          value={isOpen ? searchTerm : (selectedCategory?.name || '')}
          onChange={(e) => setSearchTerm(e.target.value)}
          onFocus={handleInputFocus}
          onKeyDown={handleKeyDown}
          placeholder={placeholder}
          disabled={disabled}
          className={`w-full px-3 py-2 pr-20 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm ${
            disabled ? 'bg-gray-100 cursor-not-allowed' : 'bg-white'
          }`}
        />
        
        <div className="absolute inset-y-0 right-0 flex items-center">
          {selectedCategory && !isOpen && (
            <button
              type="button"
              onClick={handleClear}
              className="p-1 text-gray-400 hover:text-gray-600"
            >
              <X className="h-4 w-4" />
            </button>
          )}
          <div className="h-6 w-px bg-gray-300 mx-1" />
          <button
            type="button"
            onClick={() => setIsOpen(!isOpen)}
            disabled={disabled}
            className="p-1 text-gray-400 hover:text-gray-600 disabled:cursor-not-allowed"
          >
            <ChevronDown className={`h-4 w-4 transition-transform ${isOpen ? 'rotate-180' : ''}`} />
          </button>
        </div>
      </div>

      {isOpen && (
        <div className="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-auto">
          {isLoading ? (
            <div className="px-3 py-2 text-sm text-gray-500 flex items-center">
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-indigo-600 mr-2"></div>
              Loading categories...
            </div>
          ) : filteredCategories.length === 0 ? (
            <div className="px-3 py-2 text-sm text-gray-500">
              {debouncedSearchTerm ? 'No categories found' : 'No categories available'}
            </div>
          ) : (
            <>
              {!debouncedSearchTerm && (
                <button
                  type="button"
                  onClick={() => handleSelect(null)}
                  className={`w-full px-3 py-2 text-left text-sm hover:bg-gray-100 flex items-center ${
                    !selectedCategory ? 'bg-indigo-50 text-indigo-700' : 'text-gray-700'
                  }`}
                >
                  <div className="w-4 h-4 mr-2 flex items-center justify-center">
                    {!selectedCategory && <Check className="h-3 w-3" />}
                  </div>
                  No category
                </button>
              )}
              {filteredCategories.map((category) => (
                <button
                  key={category.id}
                  type="button"
                  onClick={() => handleSelect(category)}
                  className={`w-full px-3 py-2 text-left text-sm hover:bg-gray-100 flex items-center ${
                    selectedCategory?.id === category.id ? 'bg-indigo-50 text-indigo-700' : 'text-gray-700'
                  }`}
                >
                  <div className="w-4 h-4 mr-2 flex items-center justify-center">
                    {selectedCategory?.id === category.id && <Check className="h-3 w-3" />}
                  </div>
                  <div className="flex-1">
                    <div className="font-medium">{category.name}</div>
                    {category.description && (
                      <div className="text-xs text-gray-500">{category.description}</div>
                    )}
                  </div>
                  {category.color && (
                    <div
                      className="w-3 h-3 rounded-full ml-2"
                      style={{ backgroundColor: category.color }}
                    />
                  )}
                </button>
              ))}
            </>
          )}
        </div>
      )}
    </div>
  );
};
