import { useState, useEffect } from 'react';
import { CategoryAutocomplete } from './CategoryAutocomplete';
import { RuleTestModal } from './RuleTestModal';
import { Play, X } from 'lucide-react';
import type { Rule, CreateRuleRequest, UpdateRuleRequest } from '../types';

interface RuleFormProps {
  rule?: Rule | null;
  onSubmit: (data: CreateRuleRequest | UpdateRuleRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
}

export const RuleForm = ({ rule, onSubmit, onCancel, isLoading = false }: RuleFormProps) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    pattern: '',
    targetCategoryId: '',
    priority: 0,
    enabled: true,
  });
  const [showTestModal, setShowTestModal] = useState(false);
  const [isValidPattern, setIsValidPattern] = useState(true);
  const [patternError, setPatternError] = useState('');

  useEffect(() => {
    if (rule) {
      setFormData({
        name: rule.name || '',
        description: rule.description || '',
        pattern: rule.pattern || '',
        targetCategoryId: rule.targetCategoryId || '',
        priority: rule.priority || 0,
        enabled: rule.enabled ?? true,
      });
    }
  }, [rule]);

  const validatePattern = (pattern: string) => {
    if (!pattern.trim()) {
      setIsValidPattern(true);
      setPatternError('');
      return true;
    }

    try {
      new RegExp(pattern);
      setIsValidPattern(true);
      setPatternError('');
      return true;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      setIsValidPattern(false);
      setPatternError('Invalid regular expression pattern');
      return false;
    }
  };

  const handlePatternChange = (value: string) => {
    setFormData(prev => ({ ...prev, pattern: value }));
    validatePattern(value);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!isValidPattern) {
      return;
    }

    const submitData = {
      ...formData,
      targetCategoryId: formData.targetCategoryId || undefined,
    };

    onSubmit(submitData);
  };

  const handleTestPattern = () => {
    if (formData.pattern.trim()) {
      setShowTestModal(true);
    }
  };

  const isFormValid = formData.name.trim() && formData.pattern.trim() && isValidPattern;

  return (
    <>
      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
          {/* Rule Name */}
          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-700">
              Rule Name *
            </label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
              placeholder="e.g., Coffee Shops"
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              required
            />
          </div>

          {/* Description */}
          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-700">
              Description
            </label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
              placeholder="Optional description of what this rule does"
              rows={3}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
          </div>

          {/* Pattern */}
          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-700">
              Pattern *
            </label>
            <div className="mt-1 flex rounded-md shadow-sm">
              <input
                type="text"
                value={formData.pattern}
                onChange={(e) => handlePatternChange(e.target.value)}
                placeholder="e.g., ^starbucks|coffee|espresso$"
                className={`flex-1 border rounded-l-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm ${
                  !isValidPattern ? 'border-red-300' : 'border-gray-300'
                }`}
                required
              />
              <button
                type="button"
                onClick={handleTestPattern}
                disabled={!formData.pattern.trim() || !isValidPattern}
                className="inline-flex items-center px-3 py-2 border border-l-0 border-gray-300 rounded-r-md bg-gray-50 text-gray-500 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Play className="h-4 w-4" />
              </button>
            </div>
            {patternError && (
              <p className="mt-1 text-sm text-red-600">{patternError}</p>
            )}
            <p className="mt-1 text-xs text-gray-500">
              Regular expression to match transaction descriptions or merchants
            </p>
          </div>

          {/* Target Category */}
          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-700">
              Target Category *
            </label>
            <CategoryAutocomplete
              value={formData.targetCategoryId}
              onChange={(categoryId) => setFormData(prev => ({ ...prev, targetCategoryId: categoryId || '' }))}
              placeholder="Select category to assign matched transactions"
              className="mt-1"
            />
          </div>

          {/* Priority */}
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Priority
            </label>
            <input
              type="number"
              value={formData.priority}
              onChange={(e) => setFormData(prev => ({ ...prev, priority: parseInt(e.target.value) || 0 }))}
              min="0"
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
            <p className="mt-1 text-xs text-gray-500">
              Lower numbers = higher priority (rules are processed in order)
            </p>
          </div>

          {/* Enabled */}
          <div className="flex items-center">
            <input
              type="checkbox"
              id="enabled"
              checked={formData.enabled}
              onChange={(e) => setFormData(prev => ({ ...prev, enabled: e.target.checked }))}
              className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
            />
            <label htmlFor="enabled" className="ml-2 block text-sm text-gray-700">
              Enable this rule
            </label>
          </div>
        </div>

        {/* Form Actions */}
        <div className="flex justify-end space-x-3">
          <button
            type="button"
            onClick={onCancel}
            className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            <X className="h-4 w-4 mr-2" />
            Cancel
          </button>
          <button
            type="submit"
            disabled={!isFormValid || isLoading}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                {rule ? 'Updating...' : 'Creating...'}
              </>
            ) : (
              rule ? 'Update Rule' : 'Create Rule'
            )}
          </button>
        </div>
      </form>

      {/* Test Modal */}
      <RuleTestModal
        isOpen={showTestModal}
        onClose={() => setShowTestModal(false)}
        initialPattern={formData.pattern}
      />
    </>
  );
};
