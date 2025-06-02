import { useState } from 'react';
import { GripVertical, Edit, Trash2, Play, Eye, EyeOff } from 'lucide-react';
import { RuleTestModal } from './RuleTestModal';
import type { Rule } from '../types';

interface DraggableRuleRowProps {
  rule: Rule;
  onEdit: (rule: Rule) => void;
  onDelete: (id: string) => void;
  onToggle: (id: string, enabled: boolean) => void;
  onTest: (rule: Rule) => void;
  isDragging?: boolean;
  dragHandleProps?: any;
}

export const DraggableRuleRow = ({
  rule,
  onEdit,
  onDelete,
  onToggle,
  onTest,
  isDragging = false,
  dragHandleProps,
}: DraggableRuleRowProps) => {
  const [showTestModal, setShowTestModal] = useState(false);

  const handleToggle = () => {
    onToggle(rule.id, !rule.enabled);
  };

  const handleDelete = () => {
    if (window.confirm(`Are you sure you want to delete the rule "${rule.name}"?`)) {
      onDelete(rule.id);
    }
  };

  const handleTest = () => {
    setShowTestModal(true);
  };

  const formatPattern = (pattern: string) => {
    if (pattern.length > 50) {
      return pattern.substring(0, 50) + '...';
    }
    return pattern;
  };

  return (
    <>
      <tr className={`hover:bg-gray-50 ${isDragging ? 'opacity-50' : ''}`}>
        {/* Drag Handle */}
        <td className="px-6 py-4 whitespace-nowrap">
          <div
            {...dragHandleProps}
            className="cursor-grab active:cursor-grabbing text-gray-400 hover:text-gray-600"
          >
            <GripVertical className="h-4 w-4" />
          </div>
        </td>

        {/* Priority */}
        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
          {rule.priority || 0}
        </td>

        {/* Name */}
        <td className="px-6 py-4 whitespace-nowrap">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              {rule.enabled ? (
                <Eye className="h-4 w-4 text-green-500" />
              ) : (
                <EyeOff className="h-4 w-4 text-gray-400" />
              )}
            </div>
            <div className="ml-3">
              <div className="text-sm font-medium text-gray-900">
                {rule.name}
              </div>
              {rule.description && (
                <div className="text-sm text-gray-500">
                  {rule.description}
                </div>
              )}
            </div>
          </div>
        </td>

        {/* Pattern */}
        <td className="px-6 py-4 whitespace-nowrap">
          <div className="text-sm text-gray-900 font-mono">
            {formatPattern(rule.pattern)}
          </div>
        </td>

        {/* Target Category */}
        <td className="px-6 py-4 whitespace-nowrap">
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800">
            {rule.targetCategoryName || 'Unknown Category'}
          </span>
        </td>

        {/* Status */}
        <td className="px-6 py-4 whitespace-nowrap">
          <button
            onClick={handleToggle}
            className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
              rule.enabled
                ? 'bg-green-100 text-green-800 hover:bg-green-200'
                : 'bg-gray-100 text-gray-800 hover:bg-gray-200'
            }`}
          >
            {rule.enabled ? 'Enabled' : 'Disabled'}
          </button>
        </td>

        {/* Actions */}
        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
          <div className="flex items-center justify-end space-x-2">
            <button
              onClick={handleTest}
              className="text-indigo-600 hover:text-indigo-900"
              title="Test pattern"
            >
              <Play className="h-4 w-4" />
            </button>
            <button
              onClick={() => onEdit(rule)}
              className="text-indigo-600 hover:text-indigo-900"
              title="Edit rule"
            >
              <Edit className="h-4 w-4" />
            </button>
            <button
              onClick={handleDelete}
              className="text-red-600 hover:text-red-900"
              title="Delete rule"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          </div>
        </td>
      </tr>

      {/* Test Modal */}
      <RuleTestModal
        isOpen={showTestModal}
        onClose={() => setShowTestModal(false)}
        initialPattern={rule.pattern}
      />
    </>
  );
};
