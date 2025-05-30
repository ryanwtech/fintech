import { useAccounts } from '../hooks/useAccounts';
import { CashflowSummary } from '../components/CashflowSummary';
import { RecentTransactions } from '../components/RecentTransactions';
import { BudgetProgress } from '../components/BudgetProgress';
import { CreditCard, DollarSign, TrendingUp, TrendingDown } from 'lucide-react';
import { useState, useEffect } from 'react';

export const DashboardPage = () => {
  const { data: accounts, isLoading } = useAccounts();
  const [dateRange, setDateRange] = useState<{ from: string; to: string }>({
    from: '',
    to: '',
  });

  // Set default date range to current month
  useEffect(() => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    
    setDateRange({
      from: firstDay.toISOString().split('T')[0],
      to: lastDay.toISOString().split('T')[0],
    });
  }, []);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  const totalBalance = accounts?.reduce((sum, account) => sum + account.balance, 0) || 0;
  const activeAccounts = accounts?.filter(account => account.isActive).length || 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-1 text-sm text-gray-500">
          Welcome back! Here's an overview of your finances.
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <DollarSign className="h-6 w-6 text-gray-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    Total Balance
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    ${totalBalance.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <CreditCard className="h-6 w-6 text-gray-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    Active Accounts
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    {activeAccounts}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <TrendingUp className="h-6 w-6 text-green-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    This Month Income
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    $0.00
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <TrendingDown className="h-6 w-6 text-red-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    This Month Expenses
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    $0.00
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Cashflow Summary */}
      {dateRange.from && dateRange.to && (
        <CashflowSummary from={dateRange.from} to={dateRange.to} />
      )}

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Transactions */}
        <RecentTransactions limit={5} />
        
        {/* Budget Progress */}
        <BudgetProgress month={dateRange.from} />
      </div>

      {/* Recent Accounts */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Your Accounts
          </h3>
          <div className="mt-5">
            {accounts && accounts.length > 0 ? (
              <div className="space-y-3">
                {accounts.slice(0, 5).map((account) => (
                  <div
                    key={account.id}
                    className="flex items-center justify-between p-3 border border-gray-200 rounded-md hover:bg-gray-50"
                  >
                    <div className="flex items-center">
                      <CreditCard className="h-5 w-5 text-gray-400 mr-3" />
                      <div>
                        <p className="text-sm font-medium text-gray-900">
                          {account.name}
                        </p>
                        <p className="text-sm text-gray-500">
                          {account.accountType} • {account.currency}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-sm font-medium text-gray-900">
                        ${account.balance.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                      </p>
                      <p className={`text-xs ${
                        account.isActive ? 'text-green-600' : 'text-red-600'
                      }`}>
                        {account.isActive ? 'Active' : 'Inactive'}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-6">
                <CreditCard className="mx-auto h-12 w-12 text-gray-400" />
                <h3 className="mt-2 text-sm font-medium text-gray-900">No accounts</h3>
                <p className="mt-1 text-sm text-gray-500">
                  Get started by creating your first account.
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
