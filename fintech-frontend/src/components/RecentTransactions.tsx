import { useTransactions } from '../hooks/useTransactions';
import { useAccounts } from '../hooks/useAccounts';
import { Receipt, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { Link } from 'react-router-dom';

interface RecentTransactionsProps {
  limit?: number;
}

export const RecentTransactions = ({ limit = 5 }: RecentTransactionsProps) => {
  const { data: accounts } = useAccounts();
  const { data: transactions, isLoading } = useTransactions(
    accounts?.[0]?.id || '',
    { size: limit }
  );

  if (isLoading) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <div className="animate-pulse">
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
          <div className="space-y-3">
            {[1, 2, 3, 4, 5].map((i) => (
              <div key={i} className="h-12 bg-gray-200 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (!transactions || !transactions.content || transactions.content.length === 0) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Transactions</h3>
        <div className="text-center py-6">
          <Receipt className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">No transactions</h3>
          <p className="mt-1 text-sm text-gray-500">
            Get started by adding your first transaction.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white shadow rounded-lg p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-medium text-gray-900">Recent Transactions</h3>
        <Link
          to="/transactions"
          className="text-sm font-medium text-indigo-600 hover:text-indigo-500"
        >
          View all
        </Link>
      </div>
      <div className="space-y-3">
        {transactions.content.map((transaction) => {
          const isIncome = transaction.amount > 0;
          const account = accounts?.find(acc => acc.id === transaction.accountId);
          
          return (
            <div
              key={transaction.id}
              className="flex items-center justify-between p-3 border border-gray-200 rounded-lg hover:bg-gray-50"
            >
              <div className="flex items-center space-x-3">
                <div className={`p-2 rounded-full ${
                  isIncome ? 'bg-green-100' : 'bg-red-100'
                }`}>
                  {isIncome ? (
                    <ArrowUpRight className="h-4 w-4 text-green-600" />
                  ) : (
                    <ArrowDownRight className="h-4 w-4 text-red-600" />
                  )}
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-900">
                    {transaction.description}
                  </p>
                  <p className="text-xs text-gray-500">
                    {account?.name} • {new Date(transaction.postedAt).toLocaleDateString()}
                  </p>
                </div>
              </div>
              <div className="text-right">
                <p className={`text-sm font-medium ${
                  isIncome ? 'text-green-600' : 'text-red-600'
                }`}>
                  {isIncome ? '+' : ''}${transaction.amount.toLocaleString('en-US', { 
                    minimumFractionDigits: 2 
                  })}
                </p>
                <p className="text-xs text-gray-500">
                  {transaction.status}
                </p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
