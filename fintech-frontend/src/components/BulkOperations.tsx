import { useState } from 'react';
import { useCategories } from '../hooks/useCategories';
import { useUpdateTransaction } from '../hooks/useTransactions';
import { CategoryAutocomplete } from './CategoryAutocomplete';
import { X, Tag, Trash2, Download } from 'lucide-react';

interface BulkOperationsProps {
  selectedIds: string[];
  onClearSelection: () => void;
  onBulkUpdate: (ids: string[], updates: any) => Promise<void>;
  onBulkDelete: (ids: string[]) => Promise<void>;
}

export const BulkOperations = ({
  selectedIds,
  onClearSelection,
  onBulkUpdate,
  onBulkDelete,
}: BulkOperationsProps) => {
  const [showRecategorize, setShowRecategorize] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [selectedCategoryId, setSelectedCategoryId] = useState<string | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);

  const { data: categories } = useCategories();
  const updateTransaction = useUpdateTransaction();

  const selectedCount = selectedIds.length;

  const handleRecategorize = async () => {
    if (!selectedCategoryId) return;

    setIsProcessing(true);
    try {
      await onBulkUpdate(selectedIds, { categoryId: selectedCategoryId });
      setShowRecategorize(false);
      setSelectedCategoryId(null);
      onClearSelection();
    } catch (error) {
      console.error('Failed to recategorize transactions:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleBulkDelete = async () => {
    setIsProcessing(true);
    try {
      await onBulkDelete(selectedIds);
      setShowDeleteConfirm(false);
      onClearSelection();
    } catch (error) {
      console.error('Failed to delete transactions:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleExport = () => {
    // TODO: Implement CSV export functionality
    console.log('Export selected transactions:', selectedIds);
  };

  if (selectedCount === 0) return null;

  return (
    <div className="bg-indigo-50 border border-indigo-200 rounded-lg p-4 mb-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <span className="text-sm font-medium text-indigo-900">
            {selectedCount} transaction{selectedCount !== 1 ? 's' : ''} selected
          </span>
          
          <div className="flex items-center space-x-2">
            <button
              onClick={() => setShowRecategorize(true)}
              disabled={isProcessing}
              className="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded text-indigo-700 bg-indigo-100 hover:bg-indigo-200 disabled:opacity-50"
            >
              <Tag className="h-3 w-3 mr-1" />
              Recategorize
            </button>
            
            <button
              onClick={() => setShowDeleteConfirm(true)}
              disabled={isProcessing}
              className="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded text-red-700 bg-red-100 hover:bg-red-200 disabled:opacity-50"
            >
              <Trash2 className="h-3 w-3 mr-1" />
              Delete
            </button>
            
            <button
              onClick={handleExport}
              disabled={isProcessing}
              className="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded text-gray-700 bg-gray-100 hover:bg-gray-200 disabled:opacity-50"
            >
              <Download className="h-3 w-3 mr-1" />
              Export
            </button>
          </div>
        </div>
        
        <button
          onClick={onClearSelection}
          className="text-indigo-400 hover:text-indigo-600"
        >
          <X className="h-4 w-4" />
        </button>
      </div>

      {/* Recategorize Modal */}
      {showRecategorize && (
        <div className="mt-4 p-4 bg-white border border-gray-200 rounded-lg">
          <h3 className="text-sm font-medium text-gray-900 mb-3">
            Recategorize {selectedCount} transaction{selectedCount !== 1 ? 's' : ''}
          </h3>
          <div className="flex items-center space-x-3">
            <div className="flex-1">
              <CategoryAutocomplete
                value={selectedCategoryId || ''}
                onChange={setSelectedCategoryId}
                placeholder="Select new category"
              />
            </div>
            <button
              onClick={handleRecategorize}
              disabled={!selectedCategoryId || isProcessing}
              className="px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-md hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isProcessing ? 'Updating...' : 'Update'}
            </button>
            <button
              onClick={() => setShowRecategorize(false)}
              className="px-4 py-2 bg-gray-300 text-gray-700 text-sm font-medium rounded-md hover:bg-gray-400"
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteConfirm && (
        <div className="mt-4 p-4 bg-white border border-red-200 rounded-lg">
          <h3 className="text-sm font-medium text-red-900 mb-2">
            Delete {selectedCount} transaction{selectedCount !== 1 ? 's' : ''}?
          </h3>
          <p className="text-sm text-red-700 mb-4">
            This action cannot be undone. Are you sure you want to delete these transactions?
          </p>
          <div className="flex items-center space-x-3">
            <button
              onClick={handleBulkDelete}
              disabled={isProcessing}
              className="px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-md hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isProcessing ? 'Deleting...' : 'Delete'}
            </button>
            <button
              onClick={() => setShowDeleteConfirm(false)}
              className="px-4 py-2 bg-gray-300 text-gray-700 text-sm font-medium rounded-md hover:bg-gray-400"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
