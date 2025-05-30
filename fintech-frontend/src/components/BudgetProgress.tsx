import { useBudgets } from '../hooks/useBudgets';
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';
import { Target, AlertCircle } from 'lucide-react';

interface BudgetProgressProps {
  month?: string;
}

export const BudgetProgress = ({ month }: BudgetProgressProps) => {
  const { data: budgets, isLoading, error } = useBudgets(month);

  if (isLoading) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <div className="animate-pulse">
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
          <div className="h-64 bg-gray-200 rounded"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Budget Progress</h3>
        <div className="text-center text-gray-500">
          <AlertCircle className="mx-auto h-12 w-12 text-gray-400" />
          <p className="mt-2">Unable to load budget data</p>
        </div>
      </div>
    );
  }

  if (!budgets || budgets.length === 0) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Budget Progress</h3>
        <div className="text-center py-6">
          <Target className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">No budgets</h3>
          <p className="mt-1 text-sm text-gray-500">
            Create your first budget to track your spending.
          </p>
        </div>
      </div>
    );
  }

  // Calculate overall budget progress
  const totalPlanned = budgets.reduce((sum, budget) => sum + (budget.totalAmount || 0), 0);
  const totalSpent = budgets.reduce((sum, budget) => {
    // This would need to be calculated from actual spending data
    // For now, we'll use a placeholder
    return sum + (budget.totalAmount * 0.6); // 60% spent as example
  }, 0);
  
  const progressPercentage = totalPlanned > 0 ? (totalSpent / totalPlanned) * 100 : 0;
  const isOverBudget = progressPercentage > 100;

  // Prepare data for charts
  const budgetData = budgets.map(budget => ({
    name: budget.name,
    planned: budget.totalAmount || 0,
    spent: (budget.totalAmount || 0) * 0.6, // Placeholder
    remaining: (budget.totalAmount || 0) * 0.4, // Placeholder
  }));

  const pieData = [
    { name: 'Spent', value: totalSpent, color: isOverBudget ? '#EF4444' : '#10B981' },
    { name: 'Remaining', value: Math.max(0, totalPlanned - totalSpent), color: '#E5E7EB' },
  ];

  return (
    <div className="bg-white shadow rounded-lg p-6">
      <h3 className="text-lg font-medium text-gray-900 mb-4">Budget Progress</h3>
      
      {/* Overall Progress */}
      <div className="mb-6">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm font-medium text-gray-700">Overall Progress</span>
          <span className={`text-sm font-medium ${
            isOverBudget ? 'text-red-600' : 'text-gray-900'
          }`}>
            {progressPercentage.toFixed(1)}%
          </span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div
            className={`h-2 rounded-full transition-all duration-300 ${
              isOverBudget ? 'bg-red-500' : 'bg-green-500'
            }`}
            style={{ width: `${Math.min(progressPercentage, 100)}%` }}
          />
        </div>
        <div className="flex justify-between text-xs text-gray-500 mt-1">
          <span>${totalSpent.toLocaleString('en-US', { minimumFractionDigits: 2 })} spent</span>
          <span>${totalPlanned.toLocaleString('en-US', { minimumFractionDigits: 2 })} budgeted</span>
        </div>
      </div>

      {/* Budget Breakdown Chart */}
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={budgetData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip 
              formatter={(value, name) => [
                `$${Number(value).toLocaleString('en-US', { minimumFractionDigits: 2 })}`,
                name === 'planned' ? 'Planned' : name === 'spent' ? 'Spent' : 'Remaining'
              ]}
            />
            <Bar dataKey="planned" fill="#E5E7EB" name="planned" />
            <Bar dataKey="spent" fill="#10B981" name="spent" />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Budget List */}
      <div className="mt-4 space-y-2">
        {budgets.slice(0, 3).map((budget) => {
          const budgetProgress = budget.totalAmount > 0 ? 
            ((budget.totalAmount * 0.6) / budget.totalAmount) * 100 : 0;
          
          return (
            <div key={budget.id} className="flex items-center justify-between text-sm">
              <span className="text-gray-700">{budget.name}</span>
              <div className="flex items-center space-x-2">
                <div className="w-16 bg-gray-200 rounded-full h-1">
                  <div
                    className="bg-green-500 h-1 rounded-full"
                    style={{ width: `${Math.min(budgetProgress, 100)}%` }}
                  />
                </div>
                <span className="text-gray-500 w-12 text-right">
                  {budgetProgress.toFixed(0)}%
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
