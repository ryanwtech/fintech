import { useState } from 'react';
import { useLinkBank, useUnlinkBank } from '../hooks/useBankConnections';
import { Link, Unlink, CheckCircle, XCircle, Clock, AlertCircle } from 'lucide-react';
import type { BankConnection, LinkBankRequest } from '../types';

interface BankConnectionCardProps {
  connection?: BankConnection;
  onConnectionChange: () => void;
}

export const BankConnectionCard = ({ connection, onConnectionChange }: BankConnectionCardProps) => {
  const [showLinkForm, setShowLinkForm] = useState(false);
  const [formData, setFormData] = useState<LinkBankRequest>({
    bankName: 'Mock Bank',
    accountNumber: '',
    routingNumber: '123456789',
    accountName: '',
    currency: 'USD',
  });

  const linkBank = useLinkBank();
  const unlinkBank = useUnlinkBank();

  const handleLink = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await linkBank.mutateAsync(formData);
      setShowLinkForm(false);
      onConnectionChange();
    } catch (error) {
      console.error('Failed to link bank:', error);
    }
  };

  const handleUnlink = async () => {
    if (!connection) return;
    if (window.confirm('Are you sure you want to unlink this bank account?')) {
      try {
        await unlinkBank.mutateAsync(connection.id);
        onConnectionChange();
      } catch (error) {
        console.error('Failed to unlink bank:', error);
      }
    }
  };

  const getStatusIcon = () => {
    switch (connection?.connectionStatus) {
      case 'ACTIVE':
        return <CheckCircle className="h-5 w-5 text-green-500" />;
      case 'INACTIVE':
        return <XCircle className="h-5 w-5 text-gray-400" />;
      case 'ERROR':
        return <AlertCircle className="h-5 w-5 text-red-500" />;
      default:
        return <Clock className="h-5 w-5 text-yellow-500" />;
    }
  };

  const getStatusColor = () => {
    switch (connection?.connectionStatus) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'INACTIVE':
        return 'bg-gray-100 text-gray-800';
      case 'ERROR':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-yellow-100 text-yellow-800';
    }
  };

  if (!connection) {
    return (
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="text-center">
          <div className="mx-auto h-12 w-12 text-gray-400 mb-4">
            <Link className="h-12 w-12" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No Bank Connected</h3>
          <p className="text-sm text-gray-500 mb-4">
            Connect your bank account to automatically import transactions.
          </p>
          <button
            onClick={() => setShowLinkForm(true)}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700"
          >
            <Link className="h-4 w-4 mr-2" />
            Connect Bank
          </button>
        </div>

        {/* Link Form Modal */}
        {showLinkForm && (
          <div className="fixed inset-0 z-50 overflow-y-auto">
            <div className="flex min-h-screen items-end justify-center px-4 pt-4 pb-20 text-center sm:block sm:p-0">
              <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" onClick={() => setShowLinkForm(false)} />

              <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">
                &#8203;
              </span>

              <div className="relative inline-block transform overflow-hidden rounded-lg bg-white text-left align-bottom shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-md sm:align-middle">
                <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Connect Mock Bank</h3>
                  
                  <form onSubmit={handleLink} className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Bank Name</label>
                      <input
                        type="text"
                        value={formData.bankName}
                        onChange={(e) => setFormData(prev => ({ ...prev, bankName: e.target.value }))}
                        className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700">Account Number</label>
                      <input
                        type="text"
                        value={formData.accountNumber}
                        onChange={(e) => setFormData(prev => ({ ...prev, accountNumber: e.target.value }))}
                        placeholder="Enter account number"
                        className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700">Account Name (Optional)</label>
                      <input
                        type="text"
                        value={formData.accountName}
                        onChange={(e) => setFormData(prev => ({ ...prev, accountName: e.target.value }))}
                        placeholder="Enter account holder name"
                        className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                      />
                    </div>

                    <div className="flex justify-end space-x-3">
                      <button
                        type="button"
                        onClick={() => setShowLinkForm(false)}
                        className="px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
                      >
                        Cancel
                      </button>
                      <button
                        type="submit"
                        disabled={linkBank.isPending}
                        className="px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
                      >
                        {linkBank.isPending ? 'Connecting...' : 'Connect'}
                      </button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-3">
          {getStatusIcon()}
          <div>
            <h3 className="text-lg font-medium text-gray-900">{connection.bankName}</h3>
            <p className="text-sm text-gray-500">Account ending in {connection.accountNumberMasked}</p>
          </div>
        </div>
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor()}`}>
          {connection.connectionStatus}
        </span>
      </div>

      <div className="space-y-2 text-sm text-gray-600">
        <div className="flex justify-between">
          <span>Connection ID:</span>
          <span className="font-mono">{connection.externalConnectionId}</span>
        </div>
        <div className="flex justify-between">
          <span>Last Sync:</span>
          <span>{connection.lastSyncAt ? new Date(connection.lastSyncAt).toLocaleString() : 'Never'}</span>
        </div>
        <div className="flex justify-between">
          <span>Created:</span>
          <span>{new Date(connection.createdAt).toLocaleDateString()}</span>
        </div>
      </div>

      <div className="mt-4 flex justify-end">
        <button
          onClick={handleUnlink}
          disabled={unlinkBank.isPending}
          className="inline-flex items-center px-3 py-2 border border-red-300 text-sm font-medium rounded-md text-red-700 bg-white hover:bg-red-50 disabled:opacity-50"
        >
          <Unlink className="h-4 w-4 mr-2" />
          {unlinkBank.isPending ? 'Unlinking...' : 'Unlink'}
        </button>
      </div>
    </div>
  );
};
