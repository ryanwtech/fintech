import { useParams } from 'react-router-dom';
import { useAccount } from '../hooks/useAccounts';

export const AccountDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const { data: account, isLoading } = useAccount(id!);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (!account) {
    return (
      <div className="text-center py-12">
        <h3 className="mt-2 text-sm font-medium text-gray-900">Account not found</h3>
        <p className="mt-1 text-sm text-gray-500">
          The account you're looking for doesn't exist.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">{account.name}</h1>
        <p className="mt-1 text-sm text-gray-500">
          {account.accountType} • {account.currency}
        </p>
      </div>

      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Account Details
          </h3>
          <div className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-2">
            <div>
              <dt className="text-sm font-medium text-gray-500">Balance</dt>
              <dd className="mt-1 text-2xl font-semibold text-gray-900">
                ${account.balance.toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Status</dt>
              <dd className="mt-1">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  account.isActive
                    ? 'bg-green-100 text-green-800'
                    : 'bg-red-100 text-red-800'
                }`}>
                  {account.isActive ? 'Active' : 'Inactive'}
                </span>
              </dd>
            </div>
          </div>
        </div>
      </div>

      {/* Transactions will be implemented here */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Recent Transactions
          </h3>
          <div className="mt-5">
            <p className="text-sm text-gray-500">
              Transaction history will be displayed here.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
