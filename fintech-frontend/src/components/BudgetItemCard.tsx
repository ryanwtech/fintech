import { useState } from 'react';
import { useUpdateBudgetItem } from '../hooks/useBudgets';
import { Edit2, Save, X, TrendingUp, TrendingDown } from 'lucide-react';
import type { BudgetItem, Category } from '../types';

interface BudgetItemCardProps {
  budgetItem: BudgetItem;
  category: Category;
  onUpdate: (budgetItem: BudgetItem) => void;
}

export const BudgetItemCard = ({ budgetItem, category, onUpdate }: BudgetItemCardProps) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editAmount, setEditAmount] = useState(budgetItem.plannedAmount.toString());
  
  const updateBudgetItem = useUpdateBudgetItem();

  const actualAmount = budgetItem.actualAmount || 0;
  const plannedAmount = budgetItem.plannedAmount || 0;
  const progressPercentage = plannedAmount > 0 ? Math.min((actualAmount / plannedAmount) * 100, 100) : 0;
  const isOverBudget = actualAmount > plannedAmount;
  const remainingAmount = plannedAmount - actualAmount;

  const handleSave = async () => {
    try {
      const updatedItem = await updateBudgetItem.mutateAsync({
        budgetId: budgetItem.budgetId,
        categoryId: budgetItem.categoryId,
        data: { plannedAmount: parseFloat(editAmount) || 0 },
      });
      onUpdate(updatedItem);
      setIsEditing(false);
    } catch (error) {
      console.error('Failed to update budget item:', error);
    }
  };

  const handleCancel = () => {
    setEditAmount(budgetItem.plannedAmount.toString());
    setIsEditing(false);
  };

  const getProgressColor = () => {
    if (progressPercentage >= 100) return 'bg-red-500';
    if (progressPercentage >= 80) return 'bg-yellow-500';
    return 'bg-green-500';
  };

  const getStatusBadge = () => {
    if (isOverBudget) {
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
          <TrendingUp className="h-3 w-3 mr-1" />
          Over Budget
        </span>
      );
    }
    
    if (progressPercentage >= 80) {
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
          <TrendingUp className="h-3 w-3 mr-1" />
          Near Limit
        </span>
      );
    }
    
    return (
      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
        <TrendingDown className="h-3 w-3 mr-1" />
        On Track
      </span>
    );
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-3">
          {category.color && (
            <div
              className="w-4 h-4 rounded-full"
              style={{ backgroundColor: category.color }}
            />
          )}
          <div>
            <h3 className="text-lg font-medium text-gray-900">{category.name}</h3>
            {category.description && (
              <p className="text-sm text-gray-500">{category.description}</p>
            )}
          </div>
        </div>
        {getStatusBadge()}
      </div>

      <div className="space-y-4">
        {/* Progress Bar */}
        <div>
          <div className="flex justify-between text-sm text-gray-600 mb-1">
            <span>Spent: ${actualAmount.toLocaleString('en-US', { minimumFractionDigits: 2 })}</span>
            <span>Budget: ${plannedAmount.toLocaleString('en-US', { minimumFractionDigits: 2 })}</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div
              className={`h-2 rounded-full transition-all duration-300 ${getProgressColor()}`}
              style={{ width: `${Math.min(progressPercentage, 100)}%` }}
            />
          </div>
          <div className="flex justify-between text-xs text-gray-500 mt-1">
            <span>{progressPercentage.toFixed(1)}% used</span>
            <span className={isOverBudget ? 'text-red-600' : 'text-gray-500'}>
              {isOverBudget ? `Over by $${Math.abs(remainingAmount).toLocaleString('en-US', { minimumFractionDigits: 2 })}` : 
               `$${remainingAmount.toLocaleString('en-US', { minimumFractionDigits: 2 })} remaining`}
            </span>
          </div>
        </div>

        {/* Amount Editing */}
        <div className="flex items-center justify-between">
          <div className="flex-1">
            {isEditing ? (
              <div className="flex items-center space-x-2">
                <span className="text-sm text-gray-600">Budget:</span>
                <input
                  type="number"
                  value={editAmount}
                  onChange={(e) => setEditAmount(e.target.value)}
                  className="w-24 px-2 py-1 text-sm border border-gray-300 rounded focus:ring-indigo-500 focus:border-indigo-500"
                  step="0.01"
                  min="0"
                />
                <div className="flex space-x-1">
                  <button
                    onClick={handleSave}
                    disabled={updateBudgetItem.isPending}
                    className="p-1 text-green-600 hover:text-green-800 disabled:opacity-50"
                  >
                    <Save className="h-4 w-4" />
                  </button>
                  <button
                    onClick={handleCancel}
                    className="p-1 text-gray-400 hover:text-gray-600"
                  >
                    <X className="h-4 w-4" />
                  </button>
                </div>
              </div>
            ) : (
              <div className="flex items-center space-x-2">
                <span className="text-sm text-gray-600">Budget:</span>
                <span className="text-lg font-semibold text-gray-900">
                  ${plannedAmount.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                </span>
                <button
                  onClick={() => setIsEditing(true)}
                  className="p-1 text-gray-400 hover:text-indigo-600"
                >
                  <Edit2 className="h-4 w-4" />
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
