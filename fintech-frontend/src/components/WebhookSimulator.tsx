import { useState } from 'react';
import { useSimulateWebhook, useSendWebhook, useWebhookEvents } from '../hooks/useWebhooks';
import { Play, Send, Clock, CheckCircle, XCircle, AlertCircle, Copy } from 'lucide-react';
import { toast } from 'react-hot-toast';
import type { WebhookPayload, WebhookEvent } from '../types';

export const WebhookSimulator = () => {
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState<WebhookPayload>({
    eventType: 'transactions.new',
    accountId: 'mock-account-123',
    transactions: [
      {
        transactionId: 'txn-' + Date.now(),
        amount: -25.50,
        description: 'Coffee Purchase',
        merchant: 'Starbucks',
        postedAt: new Date().toISOString(),
        currency: 'USD',
        category: 'Food & Dining',
        status: 'CLEARED',
      },
    ],
  });

  const simulateWebhook = useSimulateWebhook();
  const sendWebhook = useSendWebhook();
  const { data: webhookEvents, isLoading } = useWebhookEvents();

  const handleSimulate = async () => {
    try {
      await simulateWebhook.mutateAsync(formData);
      toast.success('Webhook simulated successfully!');
    } catch (error) {
      toast.error('Failed to simulate webhook');
      console.error('Webhook simulation error:', error);
    }
  };

  const handleSend = async () => {
    try {
      const payload = JSON.stringify(formData);
      await sendWebhook.mutateAsync(payload);
      toast.success('Webhook sent successfully!');
    } catch (error) {
      toast.error('Failed to send webhook');
      console.error('Webhook send error:', error);
    }
  };

  const addTransaction = () => {
    setFormData(prev => ({
      ...prev,
      transactions: [
        ...prev.transactions,
        {
          transactionId: 'txn-' + Date.now(),
          amount: Math.random() > 0.5 ? -Math.random() * 100 : Math.random() * 100,
          description: 'Sample Transaction',
          merchant: 'Sample Merchant',
          postedAt: new Date().toISOString(),
          currency: 'USD',
          category: 'General',
          status: 'CLEARED',
        },
      ],
    }));
  };

  const removeTransaction = (index: number) => {
    setFormData(prev => ({
      ...prev,
      transactions: prev.transactions.filter((_, i) => i !== index),
    }));
  };

  const updateTransaction = (index: number, field: string, value: any) => {
    setFormData(prev => ({
      ...prev,
      transactions: prev.transactions.map((txn, i) => 
        i === index ? { ...txn, [field]: value } : txn
      ),
    }));
  };

  const copyPayload = () => {
    navigator.clipboard.writeText(JSON.stringify(formData, null, 2));
    toast.success('Payload copied to clipboard!');
  };

  const getStatusIcon = (status: WebhookEvent['status']) => {
    switch (status) {
      case 'PROCESSED':
        return <CheckCircle className="h-4 w-4 text-green-500" />;
      case 'FAILED':
        return <XCircle className="h-4 w-4 text-red-500" />;
      case 'PENDING':
        return <Clock className="h-4 w-4 text-yellow-500" />;
      default:
        return <AlertCircle className="h-4 w-4 text-gray-500" />;
    }
  };

  const getStatusColor = (status: WebhookEvent['status']) => {
    switch (status) {
      case 'PROCESSED':
        return 'bg-green-100 text-green-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="space-y-6">
      {/* Webhook Simulator Form */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-medium text-gray-900">Webhook Simulator</h3>
          <div className="flex space-x-2">
            <button
              onClick={() => setShowForm(!showForm)}
              className="inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
            >
              {showForm ? 'Hide Form' : 'Show Form'}
            </button>
            <button
              onClick={copyPayload}
              className="inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
            >
              <Copy className="h-4 w-4 mr-2" />
              Copy Payload
            </button>
          </div>
        </div>

        {showForm && (
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Event Type</label>
                <select
                  value={formData.eventType}
                  onChange={(e) => setFormData(prev => ({ ...prev, eventType: e.target.value }))}
                  className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                >
                  <option value="transactions.new">New Transactions</option>
                  <option value="transactions.updated">Updated Transactions</option>
                  <option value="account.updated">Account Updated</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Account ID</label>
                <input
                  type="text"
                  value={formData.accountId}
                  onChange={(e) => setFormData(prev => ({ ...prev, accountId: e.target.value }))}
                  className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
            </div>

            {/* Transactions */}
            <div>
              <div className="flex items-center justify-between mb-2">
                <label className="block text-sm font-medium text-gray-700">Transactions</label>
                <button
                  onClick={addTransaction}
                  className="text-sm text-indigo-600 hover:text-indigo-500"
                >
                  + Add Transaction
                </button>
              </div>
              <div className="space-y-3">
                {formData.transactions.map((transaction, index) => (
                  <div key={index} className="border border-gray-200 rounded-lg p-4">
                    <div className="flex items-center justify-between mb-3">
                      <h4 className="text-sm font-medium text-gray-900">Transaction {index + 1}</h4>
                      <button
                        onClick={() => removeTransaction(index)}
                        className="text-red-600 hover:text-red-800"
                      >
                        Remove
                      </button>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                      <div>
                        <label className="block text-xs font-medium text-gray-700">Amount</label>
                        <input
                          type="number"
                          step="0.01"
                          value={transaction.amount}
                          onChange={(e) => updateTransaction(index, 'amount', parseFloat(e.target.value) || 0)}
                          className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div>
                        <label className="block text-xs font-medium text-gray-700">Description</label>
                        <input
                          type="text"
                          value={transaction.description}
                          onChange={(e) => updateTransaction(index, 'description', e.target.value)}
                          className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div>
                        <label className="block text-xs font-medium text-gray-700">Merchant</label>
                        <input
                          type="text"
                          value={transaction.merchant || ''}
                          onChange={(e) => updateTransaction(index, 'merchant', e.target.value)}
                          className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div>
                        <label className="block text-xs font-medium text-gray-700">Category</label>
                        <input
                          type="text"
                          value={transaction.category || ''}
                          onChange={(e) => updateTransaction(index, 'category', e.target.value)}
                          className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div>
                        <label className="block text-xs font-medium text-gray-700">Status</label>
                        <select
                          value={transaction.status}
                          onChange={(e) => updateTransaction(index, 'status', e.target.value)}
                          className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        >
                          <option value="PENDING">Pending</option>
                          <option value="CLEARED">Cleared</option>
                          <option value="FAILED">Failed</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-xs font-medium text-gray-700">Currency</label>
                        <select
                          value={transaction.currency}
                          onChange={(e) => updateTransaction(index, 'currency', e.target.value)}
                          className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        >
                          <option value="USD">USD</option>
                          <option value="EUR">EUR</option>
                          <option value="GBP">GBP</option>
                        </select>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                onClick={handleSimulate}
                disabled={simulateWebhook.isPending}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
              >
                <Play className="h-4 w-4 mr-2" />
                {simulateWebhook.isPending ? 'Simulating...' : 'Simulate'}
              </button>
              <button
                onClick={handleSend}
                disabled={sendWebhook.isPending}
                className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
              >
                <Send className="h-4 w-4 mr-2" />
                {sendWebhook.isPending ? 'Sending...' : 'Send Webhook'}
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Webhook Events List */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Webhook Events</h3>
        
        {isLoading ? (
          <div className="text-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600 mx-auto"></div>
            <p className="mt-2 text-sm text-gray-500">Loading events...</p>
          </div>
        ) : webhookEvents && webhookEvents.length > 0 ? (
          <div className="space-y-3">
            {webhookEvents.slice(0, 10).map((event) => (
              <div key={event.id} className="border border-gray-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center space-x-3">
                    {getStatusIcon(event.status)}
                    <div>
                      <h4 className="text-sm font-medium text-gray-900">{event.eventType}</h4>
                      <p className="text-xs text-gray-500">Source: {event.source}</p>
                    </div>
                  </div>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(event.status)}`}>
                    {event.status}
                  </span>
                </div>
                
                <div className="text-xs text-gray-500 space-y-1">
                  <div>Created: {new Date(event.createdAt).toLocaleString()}</div>
                  {event.processedAt && (
                    <div>Processed: {new Date(event.processedAt).toLocaleString()}</div>
                  )}
                  {event.errorMessage && (
                    <div className="text-red-600">Error: {event.errorMessage}</div>
                  )}
                  {event.retryCount > 0 && (
                    <div>Retries: {event.retryCount}</div>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-8">
            <AlertCircle className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No webhook events</h3>
            <p className="mt-1 text-sm text-gray-500">Send a webhook to see events here.</p>
          </div>
        )}
      </div>
    </div>
  );
};
