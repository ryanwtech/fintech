import { useState, useEffect } from 'react';
import { useUpdateTransaction } from '../hooks/useTransactions';
import { useCategories } from '../hooks/useCategories';
import { CategoryAutocomplete } from './CategoryAutocomplete';
import { Edit, Save, X, Trash2 } from 'lucide-react';
import { Transaction } from '../types';

interface EditableTransactionRowProps {
  transaction: Transaction;
  onDelete?: (id: string) => void;
  onUpdate?: (transaction: Transaction) => void;
  isSelected?: boolean;
  onSelect?: (id: string, selected: boolean) => void;
}

export const EditableTransactionRow = ({
  transaction,
  onDelete,
  onUpdate,
  isSelected = false,
  onSelect,
}: EditableTransactionRowProps) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState({
    description: transaction.description,
    merchant: transaction.merchant || '',
    amount: transaction.amount,
    categoryId: transaction.categoryId || '',
  });

  const { data: categories } = useCategories();
  const updateTransaction = useUpdateTransaction();

  const category = categories?.find(cat => cat.id === transaction.categoryId);
  const isIncome = transaction.amount > 0;

  useEffect(() => {
    setEditData({
      description: transaction.description,
      merchant: transaction.merchant || '',
      amount: transaction.amount,
      categoryId: transaction.categoryId || '',
    });
  }, [transaction]);

  const handleSave = async () => {
    try {
      const updatedTransaction = await updateTransaction.mutateAsync({
        id: transaction.id,
        data: {
          description: editData.description,
          merchant: editData.merchant,
          amount: editData.amount,
          categoryId: editData.categoryId || undefined,
        },
      });
      
      onUpdate?.(updatedTransaction);
      setIsEditing(false);
    } catch (error) {
      console.error('Failed to update transaction:', error);
    }
  };

  const handleCancel = () => {
    setEditData({
      description: transaction.description,
      merchant: transaction.merchant || '',
      amount: transaction.amount,
      categoryId: transaction.categoryId || '',
    });
    setIsEditing(false);
  };

  const handleDelete = () => {
    if (window.confirm('Are you sure you want to delete this transaction?')) {
      onDelete?.(transaction.id);
    }
  };

  const formatAmount = (amount: number) => {
    return `${isIncome ? '+' : ''}$${amount.toLocaleString('en-US', { 
      minimumFractionDigits: 2 
    })}`;
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  return (
    <tr className={`hover:bg-gray-50 ${isSelected ? 'bg-indigo-50' : ''}`}>
      <td className="px-6 py-4 whitespace-nowrap">
        <input
          type="checkbox"
          checked={isSelected}
          onChange={(e) => onSelect?.(transaction.id, e.target.checked)}
          className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
        />
      </td>
      
      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
        {formatDate(transaction.postedAt)}
      </td>
      
      <td className="px-6 py-4 whitespace-nowrap">
        {isEditing ? (
          <div className="space-y-1">
            <input
              type="text"
              value={editData.description}
              onChange={(e) => setEditData(prev => ({ ...prev, description: e.target.value }))}
              className="w-full px-2 py-1 text-sm border border-gray-300 rounded focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="Description"
            />
            <input
              type="text"
              value={editData.merchant}
              onChange={(e) => setEditData(prev => ({ ...prev, merchant: e.target.value }))}
              className="w-full px-2 py-1 text-sm border border-gray-300 rounded focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="Merchant"
            />
          </div>
        ) : (
          <div>
            <div className="text-sm font-medium text-gray-900">
              {transaction.description}
            </div>
            {transaction.merchant && (
              <div className="text-sm text-gray-500">
                {transaction.merchant}
              </div>
            )}
          </div>
        )}
      </td>
      
      <td className="px-6 py-4 whitespace-nowrap">
        {isEditing ? (
          <CategoryAutocomplete
            value={editData.categoryId}
            onChange={(categoryId) => setEditData(prev => ({ ...prev, categoryId: categoryId || '' }))}
            placeholder="Select category"
            className="w-full"
          />
        ) : (
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
            {category?.name || 'Uncategorized'}
          </span>
        )}
      </td>
      
      <td className="px-6 py-4 whitespace-nowrap">
        {isEditing ? (
          <input
            type="number"
            step="0.01"
            value={editData.amount}
            onChange={(e) => setEditData(prev => ({ ...prev, amount: parseFloat(e.target.value) || 0 }))}
            className="w-full px-2 py-1 text-sm border border-gray-300 rounded focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="Amount"
          />
        ) : (
          <span className={`text-sm font-medium ${isIncome ? 'text-green-600' : 'text-red-600'}`}>
            {formatAmount(transaction.amount)}
          </span>
        )}
      </td>
      
      <td className="px-6 py-4 whitespace-nowrap">
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
          transaction.status === 'CLEARED' 
            ? 'bg-green-100 text-green-800'
            : transaction.status === 'PENDING'
            ? 'bg-yellow-100 text-yellow-800'
            : 'bg-red-100 text-red-800'
        }`}>
          {transaction.status}
        </span>
      </td>
      
      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
        {isEditing ? (
          <div className="flex items-center space-x-2">
            <button
              onClick={handleSave}
              disabled={updateTransaction.isPending}
              className="text-green-600 hover:text-green-900 disabled:opacity-50"
            >
              <Save className="h-4 w-4" />
            </button>
            <button
              onClick={handleCancel}
              className="text-gray-400 hover:text-gray-600"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        ) : (
          <div className="flex items-center space-x-2">
            <button
              onClick={() => setIsEditing(true)}
              className="text-indigo-600 hover:text-indigo-900"
            >
              <Edit className="h-4 w-4" />
            </button>
            {onDelete && (
              <button
                onClick={handleDelete}
                className="text-red-600 hover:text-red-900"
              >
                <Trash2 className="h-4 w-4" />
              </button>
            )}
          </div>
        )}
      </td>
    </tr>
  );
};
