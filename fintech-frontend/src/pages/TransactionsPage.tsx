import { useState, useEffect } from 'react';
import { useAccounts } from '../hooks/useAccounts';
import { useAllTransactions } from '../hooks/useTransactions';
import { useCategories } from '../hooks/useCategories';
import { useDeleteTransaction } from '../hooks/useTransactions';
import { EditableTransactionRow } from '../components/EditableTransactionRow';
import { BulkOperations } from '../components/BulkOperations';
import { TableSkeletonLoader } from '../components/SkeletonLoader';
import { Plus, Search, Filter, Download, ChevronUp, ChevronDown } from 'lucide-react';

export const TransactionsPage = () => {
  const [selectedAccount, setSelectedAccount] = useState<string>('');
  const [filters, setFilters] = useState({
    from: '',
    to: '',
    q: '',
    categoryId: '',
    page: 0,
    size: 20,
    sortBy: 'postedAt',
    sortDir: 'desc' as 'asc' | 'desc',
  });
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [showFilters, setShowFilters] = useState(false);

  const { data: accounts } = useAccounts();
  const { data: categories } = useCategories();
  const { data: transactions, isLoading } = useAllTransactions({
    from: filters.from || undefined,
    to: filters.to || undefined,
    q: filters.q || undefined,
    categoryId: filters.categoryId || undefined,
    page: filters.page,
    size: filters.size,
    sortBy: filters.sortBy,
    sortDir: filters.sortDir,
  });
  const deleteTransaction = useDeleteTransaction();

  // Set default date range to current month
  useEffect(() => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    
    setFilters(prev => ({
      ...prev,
      from: firstDay.toISOString().split('T')[0],
      to: lastDay.toISOString().split('T')[0],
    }));
  }, []);

  const handleFilterChange = (key: string, value: string | number) => {
    setFilters(prev => ({
      ...prev,
      [key]: value,
      page: key !== 'page' ? 0 : value, // Reset to first page when changing other filters
    }));
  };

  const handleSort = (column: string) => {
    setFilters(prev => ({
      ...prev,
      sortBy: column,
      sortDir: prev.sortBy === column && prev.sortDir === 'desc' ? 'asc' : 'desc',
    }));
  };

  const handleSelectAll = (checked: boolean) => {
    if (checked && transactions) {
      setSelectedIds(transactions.content.map(t => t.id));
    } else {
      setSelectedIds([]);
    }
  };

  const handleSelectTransaction = (id: string, selected: boolean) => {
    if (selected) {
      setSelectedIds(prev => [...prev, id]);
    } else {
      setSelectedIds(prev => prev.filter(transactionId => transactionId !== id));
    }
  };

  const handleBulkUpdate = async (ids: string[], updates: any) => {
    // TODO: Implement bulk update API call
    console.log('Bulk update:', ids, updates);
  };

  const handleBulkDelete = async (ids: string[]) => {
    // TODO: Implement bulk delete API call
    console.log('Bulk delete:', ids);
  };

  const clearFilters = () => {
    setFilters(prev => ({
      ...prev,
      from: '',
      to: '',
      q: '',
      categoryId: '',
      page: 0,
    }));
  };

  const totalPages = transactions ? Math.ceil(transactions.totalElements / filters.size) : 0;
  const allSelected = transactions && selectedIds.length === transactions.content.length;
  const someSelected = selectedIds.length > 0 && selectedIds.length < (transactions?.content.length || 0);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Transactions</h1>
          <p className="mt-1 text-sm text-gray-500">
            View and manage all your transactions.
          </p>
        </div>
        <div className="flex space-x-3">
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50"
          >
            <Filter className="h-4 w-4 mr-2" />
            Filters
          </button>
          <button className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50">
            <Download className="h-4 w-4 mr-2" />
            Export
          </button>
          <button className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700">
            <Plus className="h-4 w-4 mr-2" />
            Add Transaction
          </button>
        </div>
      </div>

      {/* Filters */}
      {showFilters && (
        <div className="bg-white shadow rounded-lg p-6">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
            <div>
              <label className="block text-sm font-medium text-gray-700">Account</label>
              <select
                value={selectedAccount}
                onChange={(e) => setSelectedAccount(e.target.value)}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              >
                <option value="">All Accounts</option>
                {accounts?.map((account) => (
                  <option key={account.id} value={account.id}>
                    {account.name}
                  </option>
                ))}
              </select>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700">From Date</label>
              <input
                type="date"
                value={filters.from}
                onChange={(e) => handleFilterChange('from', e.target.value)}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700">To Date</label>
              <input
                type="date"
                value={filters.to}
                onChange={(e) => handleFilterChange('to', e.target.value)}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700">Search</label>
              <div className="mt-1 relative">
                <input
                  type="text"
                  value={filters.q}
                  onChange={(e) => handleFilterChange('q', e.target.value)}
                  placeholder="Search transactions..."
                  className="block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm pl-10"
                />
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              </div>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700">Category</label>
              <select
                value={filters.categoryId}
                onChange={(e) => handleFilterChange('categoryId', e.target.value)}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              >
                <option value="">All Categories</option>
                {categories?.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Clear Filters */}
          {(filters.from || filters.to || filters.q || filters.categoryId) && (
            <div className="mt-4">
              <button
                onClick={clearFilters}
                className="text-sm text-indigo-600 hover:text-indigo-500"
              >
                Clear all filters
              </button>
            </div>
          )}
        </div>
      )}

      {/* Bulk Operations */}
      <BulkOperations
        selectedIds={selectedIds}
        onClearSelection={() => setSelectedIds([])}
        onBulkUpdate={handleBulkUpdate}
        onBulkDelete={handleBulkDelete}
      />

      {/* Transactions Table */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg leading-6 font-medium text-gray-900">
              Transactions
              {transactions && (
                <span className="ml-2 text-sm font-normal text-gray-500">
                  ({transactions.totalElements} total)
                </span>
              )}
            </h3>
          </div>

          {isLoading ? (
            <TableSkeletonLoader rows={5} columns={7} />
          ) : !transactions || transactions.content.length === 0 ? (
            <div className="text-center py-12">
              <h3 className="text-lg font-medium text-gray-900">No transactions found</h3>
              <p className="mt-1 text-sm text-gray-500">
                {filters.from || filters.to || filters.q || filters.categoryId
                  ? 'Try adjusting your filters or clear them to see all transactions.'
                  : 'Get started by adding your first transaction.'
                }
              </p>
            </div>
          ) : (
            <>
              <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
                <table className="min-w-full divide-y divide-gray-300">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left">
                        <input
                          type="checkbox"
                          checked={allSelected}
                          ref={(input) => {
                            if (input) input.indeterminate = someSelected;
                          }}
                          onChange={(e) => handleSelectAll(e.target.checked)}
                          className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                        />
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        <button
                          onClick={() => handleSort('postedAt')}
                          className="flex items-center space-x-1 hover:text-gray-700"
                        >
                          <span>Date</span>
                          {filters.sortBy === 'postedAt' ? (
                            filters.sortDir === 'desc' ? (
                              <ChevronDown className="h-4 w-4" />
                            ) : (
                              <ChevronUp className="h-4 w-4" />
                            )
                          ) : null}
                        </button>
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        <button
                          onClick={() => handleSort('description')}
                          className="flex items-center space-x-1 hover:text-gray-700"
                        >
                          <span>Description</span>
                          {filters.sortBy === 'description' ? (
                            filters.sortDir === 'desc' ? (
                              <ChevronDown className="h-4 w-4" />
                            ) : (
                              <ChevronUp className="h-4 w-4" />
                            )
                          ) : null}
                        </button>
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Category
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        <button
                          onClick={() => handleSort('amount')}
                          className="flex items-center space-x-1 hover:text-gray-700"
                        >
                          <span>Amount</span>
                          {filters.sortBy === 'amount' ? (
                            filters.sortDir === 'desc' ? (
                              <ChevronDown className="h-4 w-4" />
                            ) : (
                              <ChevronUp className="h-4 w-4" />
                            )
                          ) : null}
                        </button>
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {transactions.content.map((transaction) => (
                      <EditableTransactionRow
                        key={transaction.id}
                        transaction={transaction}
                        isSelected={selectedIds.includes(transaction.id)}
                        onSelect={handleSelectTransaction}
                        onDelete={(id) => {
                          if (window.confirm('Are you sure you want to delete this transaction?')) {
                            deleteTransaction.mutate(id);
                          }
                        }}
                      />
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
                  <div className="flex-1 flex justify-between sm:hidden">
                    <button
                      onClick={() => handleFilterChange('page', filters.page - 1)}
                      disabled={filters.page === 0}
                      className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Previous
                    </button>
                    <button
                      onClick={() => handleFilterChange('page', filters.page + 1)}
                      disabled={filters.page >= totalPages - 1}
                      className="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Next
                    </button>
                  </div>
                  <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                    <div>
                      <p className="text-sm text-gray-700">
                        Showing{' '}
                        <span className="font-medium">{filters.page * filters.size + 1}</span>
                        {' '}to{' '}
                        <span className="font-medium">
                          {Math.min((filters.page + 1) * filters.size, transactions.totalElements)}
                        </span>
                        {' '}of{' '}
                        <span className="font-medium">{transactions.totalElements}</span>
                        {' '}results
                      </p>
                    </div>
                    <div>
                      <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
                        <button
                          onClick={() => handleFilterChange('page', filters.page - 1)}
                          disabled={filters.page === 0}
                          className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          <ChevronUp className="h-5 w-5" />
                        </button>
                        {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                          const page = i;
                          return (
                            <button
                              key={page}
                              onClick={() => handleFilterChange('page', page)}
                              className={`relative inline-flex items-center px-4 py-2 border text-sm font-medium ${
                                page === filters.page
                                  ? 'z-10 bg-indigo-50 border-indigo-500 text-indigo-600'
                                  : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'
                              }`}
                            >
                              {page + 1}
                            </button>
                          );
                        })}
                        <button
                          onClick={() => handleFilterChange('page', filters.page + 1)}
                          disabled={filters.page >= totalPages - 1}
                          className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          <ChevronDown className="h-5 w-5" />
                        </button>
                      </nav>
                    </div>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};