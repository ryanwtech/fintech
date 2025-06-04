import { useState } from 'react';
import { useBankConnections } from '../hooks/useBankConnections';
import { BankConnectionCard } from '../components/BankConnectionCard';
import { WebhookSimulator } from '../components/WebhookSimulator';
import { CardSkeletonLoader } from '../components/SkeletonLoader';
import { CreditCard, Webhook, Settings as SettingsIcon, RefreshCw } from 'lucide-react';

export const SettingsPage = () => {
  const [activeTab, setActiveTab] = useState<'bank' | 'webhooks'>('bank');
  const { data: bankConnections, isLoading, refetch } = useBankConnections();

  const tabs = [
    { id: 'bank', name: 'Bank Connections', icon: CreditCard },
    { id: 'webhooks', name: 'Webhook Simulator', icon: Webhook },
  ];

  const handleConnectionChange = () => {
    refetch();
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Settings</h1>
          <p className="mt-1 text-sm text-gray-500">
            Manage your bank connections and webhook settings.
          </p>
        </div>
        <CardSkeletonLoader count={2} />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Settings</h1>
        <p className="mt-1 text-sm text-gray-500">
          Manage your bank connections and webhook settings.
        </p>
      </div>

      {/* Tab Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as 'bank' | 'webhooks')}
                className={`${
                  activeTab === tab.id
                    ? 'border-indigo-500 text-indigo-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                } whitespace-nowrap py-2 px-1 border-b-2 font-medium text-sm flex items-center space-x-2`}
              >
                <Icon className="h-4 w-4" />
                <span>{tab.name}</span>
              </button>
            );
          })}
        </nav>
      </div>

      {/* Tab Content */}
      <div className="mt-6">
        {activeTab === 'bank' && (
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-lg font-medium text-gray-900">Bank Connections</h2>
                <p className="text-sm text-gray-500">
                  Connect your bank accounts to automatically import transactions.
                </p>
              </div>
              <button
                onClick={() => refetch()}
                className="inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
              >
                <RefreshCw className="h-4 w-4 mr-2" />
                Refresh
              </button>
            </div>

            <BankConnectionCard
              connection={bankConnections?.[0]}
              onConnectionChange={handleConnectionChange}
            />

            {/* Connection Info */}
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <h3 className="text-sm font-medium text-blue-900 mb-2">About Mock Bank Integration</h3>
              <div className="text-sm text-blue-800 space-y-1">
                <p>This is a demonstration integration with a mock bank service.</p>
                <p>• Transactions are simulated and not real financial data</p>
                <p>• Use the webhook simulator to test transaction imports</p>
                <p>• All data is stored locally and can be deleted at any time</p>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'webhooks' && (
          <div className="space-y-6">
            <div>
              <h2 className="text-lg font-medium text-gray-900">Webhook Simulator</h2>
              <p className="text-sm text-gray-500">
                Test webhook integrations by sending simulated transaction data.
              </p>
            </div>

            <WebhookSimulator />

            {/* Webhook Info */}
            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <h3 className="text-sm font-medium text-green-900 mb-2">Webhook Endpoints</h3>
              <div className="text-sm text-green-800 space-y-1">
                <p><strong>Mock Bank Webhook:</strong> <code className="bg-green-100 px-1 rounded">POST /webhooks/mockbank</code></p>
                <p><strong>Test Simulator:</strong> <code className="bg-green-100 px-1 rounded">POST /webhooks/test/simulate</code></p>
                <p><strong>Event List:</strong> <code className="bg-green-100 px-1 rounded">GET /webhooks/events</code></p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};