import { useState, useEffect } from 'react';
import { useBudgets, useCreateBudget } from '../hooks/useBudgets';
import { useCategories } from '../hooks/useCategories';
import { MonthSwitcher } from '../components/MonthSwitcher';
import { BudgetItemCard } from '../components/BudgetItemCard';
import { CardSkeletonLoader } from '../components/SkeletonLoader';
import { Plus, DollarSign, TrendingUp, TrendingDown } from 'lucide-react';

export const BudgetsPage = () => {
  const [currentMonth, setCurrentMonth] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);

  const { data: budgets, isLoading } = useBudgets(currentMonth);
  const { data: categories } = useCategories();
  const createBudget = useCreateBudget();

  // Set current month on mount
  useEffect(() => {
    const now = new Date();
    const month = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    setCurrentMonth(month);
  }, []);

  const handleCreateBudget = async () => {
    if (!currentMonth) return;

    const [year, month] = currentMonth.split('-').map(Number);
    const startDate = new Date(year, month - 1, 1);
    const endDate = new Date(year, month, 0);

    try {
      await createBudget.mutateAsync({
        name: `Budget for ${startDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}`,
        startDate: startDate.toISOString().split('T')[0],
        endDate: endDate.toISOString().split('T')[0],
        totalAmount: 0,
        items: categories?.map(cat => ({
          categoryId: cat.id,
          plannedAmount: 0,
        })) || [],
      });
      setShowCreateForm(false);
    } catch (error) {
      console.error('Failed to create budget:', error);
    }
  };

  const currentBudget = budgets?.[0]; // Assuming one budget per month
  const totalPlanned = currentBudget?.items?.reduce((sum, item) => sum + (item.plannedAmount || 0), 0) || 0;
  const totalSpent = currentBudget?.items?.reduce((sum, item) => sum + (item.actualAmount || 0), 0) || 0;
  const totalRemaining = totalPlanned - totalSpent;
  const isOverBudget = totalSpent > totalPlanned;

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Budgets</h1>
            <p className="mt-1 text-sm text-gray-500">
              Track your spending against planned budgets.
            </p>
          </div>
        </div>
        <CardSkeletonLoader count={3} />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Budgets</h1>
          <p className="mt-1 text-sm text-gray-500">
            Track your spending against planned budgets.
          </p>
        </div>
        <div className="flex items-center space-x-3">
          <MonthSwitcher
            currentMonth={currentMonth}
            onMonthChange={setCurrentMonth}
          />
          {!currentBudget && (
            <button
              onClick={handleCreateBudget}
              disabled={createBudget.isPending}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
            >
              <Plus className="h-4 w-4 mr-2" />
              Create Budget
            </button>
          )}
        </div>
      </div>

      {/* Budget Summary */}
      {currentBudget && (
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">{currentBudget.name}</h2>
            <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${
              isOverBudget 
                ? 'bg-red-100 text-red-800' 
                : 'bg-green-100 text-green-800'
            }`}>
              {isOverBudget ? (
                <>
                  <TrendingUp className="h-4 w-4 mr-1" />
                  Over Budget
                </>
              ) : (
                <>
                  <TrendingDown className="h-4 w-4 mr-1" />
                  On Track
                </>
              )}
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="text-center">
              <div className="flex items-center justify-center w-12 h-12 bg-blue-100 rounded-lg mx-auto mb-2">
                <DollarSign className="h-6 w-6 text-blue-600" />
              </div>
              <p className="text-sm text-gray-600">Total Budget</p>
              <p className="text-2xl font-semibold text-gray-900">
                ${totalPlanned.toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </p>
            </div>

            <div className="text-center">
              <div className="flex items-center justify-center w-12 h-12 bg-red-100 rounded-lg mx-auto mb-2">
                <TrendingUp className="h-6 w-6 text-red-600" />
              </div>
              <p className="text-sm text-gray-600">Total Spent</p>
              <p className="text-2xl font-semibold text-gray-900">
                ${totalSpent.toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </p>
            </div>

            <div className="text-center">
              <div className={`flex items-center justify-center w-12 h-12 rounded-lg mx-auto mb-2 ${
                isOverBudget ? 'bg-red-100' : 'bg-green-100'
              }`}>
                <TrendingDown className={`h-6 w-6 ${isOverBudget ? 'text-red-600' : 'text-green-600'}`} />
              </div>
              <p className="text-sm text-gray-600">
                {isOverBudget ? 'Over Budget' : 'Remaining'}
              </p>
              <p className={`text-2xl font-semibold ${
                isOverBudget ? 'text-red-600' : 'text-gray-900'
              }`}>
                ${Math.abs(totalRemaining).toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </p>
            </div>
          </div>

          {/* Overall Progress Bar */}
          <div className="mt-6">
            <div className="flex justify-between text-sm text-gray-600 mb-2">
              <span>Overall Progress</span>
              <span>{totalPlanned > 0 ? ((totalSpent / totalPlanned) * 100).toFixed(1) : 0}% used</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-3">
              <div
                className={`h-3 rounded-full transition-all duration-300 ${
                  isOverBudget ? 'bg-red-500' : 'bg-blue-500'
                }`}
                style={{ 
                  width: `${totalPlanned > 0 ? Math.min((totalSpent / totalPlanned) * 100, 100) : 0}%` 
                }}
              />
            </div>
          </div>
        </div>
      )}

      {/* Budget Items */}
      {currentBudget ? (
        <div className="space-y-4">
          <h3 className="text-lg font-medium text-gray-900">Categories</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {currentBudget.items?.map((item) => {
              const category = categories?.find(cat => cat.id === item.categoryId);
              if (!category) return null;

              return (
                <BudgetItemCard
                  key={item.id}
                  budgetItem={item}
                  category={category}
                  onUpdate={(updatedItem) => {
                    // Update the budget item in the local state
                    // This would typically be handled by the query cache
                  }}
                />
              );
            })}
          </div>
        </div>
      ) : (
        <div className="text-center py-12">
          <div className="mx-auto h-12 w-12 text-gray-400">
            <DollarSign className="h-12 w-12" />
          </div>
          <h3 className="mt-2 text-sm font-medium text-gray-900">No budget for this month</h3>
          <p className="mt-1 text-sm text-gray-500">
            Create a budget to start tracking your spending against planned amounts.
          </p>
          <div className="mt-6">
            <button
              onClick={handleCreateBudget}
              disabled={createBudget.isPending}
              className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
            >
              <Plus className="h-4 w-4 mr-2" />
              Create Budget
            </button>
          </div>
        </div>
      )}
    </div>
  );
};