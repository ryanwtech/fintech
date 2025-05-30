import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAccount } from '../hooks/useAccounts';
import { useTransactions } from '../hooks/useTransactions';
import { useCategories } from '../hooks/useCategories';
import { CsvImportModal } from '../components/CsvImportModal';
import { 
  ArrowLeft, 
  Upload, 
  Filter, 
  Search, 
  Calendar,
  ChevronLeft,
  ChevronRight,
  Plus,
  Edit,
  Trash2
} from 'lucide-react';

export const AccountDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const { data: account, isLoading: accountLoading } = useAccount(id || '');
  const { data: categories } = useCategories();
  
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
  
  const [showImportModal, setShowImportModal] = useState(false);
  const [showFilters, setShowFilters] = useState(false);

  const { data: transactions, isLoading: transactionsLoading } = useTransactions(
    id || '',
    {
      from: filters.from || undefined,
      to: filters.to || undefined,
      q: filters.q || undefined,
      categoryId: filters.categoryId || undefined,
      page: filters.page,
      size: filters.size,
      sortBy: filters.sortBy,
      sortDir: filters.sortDir,
    }
  );

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

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
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

  if (accountLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (!account) {
    return (
      <div className="text-center py-12">
        <h3 className="text-lg font-medium text-gray-900">Account not found</h3>
        <p className="mt-1 text-sm text-gray-500">
          The account you're looking for doesn't exist.
        </p>
        <Link
          to="/accounts"
          className="mt-4 inline-flex items-center text-sm font-medium text-indigo-600 hover:text-indigo-500"
        >
          <ArrowLeft className="h-4 w-4 mr-1" />
          Back to Accounts
        </Link>
      </div>
    );
  }

  const totalPages = transactions ? Math.ceil(transactions.totalElements / filters.size) : 0;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Link
            to="/accounts"
            className="text-gray-400 hover:text-gray-600"
          >
            <ArrowLeft className="h-6 w-6" />
          </Link>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{account.name}</h1>
            <p className="text-sm text-gray-500">
              {account.accountType} • {account.currency} • Balance: ${account.balance.toLocaleString('en-US', { minimumFractionDigits: 2 })}
            </p>
          </div>
        </div>
        <div className="flex space-x-3">
          <button
            onClick={() => setShowImportModal(true)}
            className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50"
          >
            <Upload className="h-4 w-4 mr-2" />
            Import CSV
          </button>
          <button className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700">
            <Plus className="h-4 w-4 mr-2" />
            Add Transaction
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-900">Transactions</h3>
            <button
              onClick={() => setShowFilters(!showFilters)}
              className="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
            >
              <Filter className="h-4 w-4 mr-2" />
              Filters
            </button>
          </div>

          {showFilters && (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4 mb-4">
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
          )}

          {/* Clear Filters */}
          {(filters.from || filters.to || filters.q || filters.categoryId) && (
            <div className="mb-4">
              <button
                onClick={clearFilters}
                className="text-sm text-indigo-600 hover:text-indigo-500"
              >
                Clear all filters
              </button>
            </div>
          )}

          {/* Transactions Table */}
          {transactionsLoading ? (
            <div className="animate-pulse">
              <div className="space-y-3">
                {[1, 2, 3, 4, 5].map((i) => (
                  <div key={i} className="h-16 bg-gray-200 rounded"></div>
                ))}
              </div>
            </div>
          ) : !transactions || transactions.content.length === 0 ? (
            <div className="text-center py-12">
              <h3 className="text-lg font-medium text-gray-900">No transactions found</h3>
              <p className="mt-1 text-sm text-gray-500">
                {filters.from || filters.to || filters.q || filters.categoryId
                  ? 'Try adjusting your filters or clear them to see all transactions.'
                  : 'Get started by adding your first transaction or importing from CSV.'
                }
              </p>
            </div>
          ) : (
            <>
              <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
                <table className="min-w-full divide-y divide-gray-300">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Date
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Description
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Category
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Amount
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {transactions.content.map((transaction) => {
                      const isIncome = transaction.amount > 0;
                      const category = categories?.find(cat => cat.id === transaction.categoryId);
                      
                      return (
                        <tr key={transaction.id} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {new Date(transaction.postedAt).toLocaleDateString()}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <div>
                              <div className="text-sm font-medium text-gray-900">
                                {transaction.description}
                              </div>
                              {transaction.merchant && (
                                <div className="text-sm text-gray-500">
                                  {transaction.merchant}
                                </div>
                              )}
                            </div>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {category ? (
                              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                                {category.name}
                              </span>
                            ) : (
                              <span className="text-gray-400">Uncategorized</span>
                            )}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                              transaction.status === 'CLEARED' 
                                ? 'bg-green-100 text-green-800'
                                : transaction.status === 'PENDING'
                                ? 'bg-yellow-100 text-yellow-800'
                                : 'bg-red-100 text-red-800'
                            }`}>
                              {transaction.status}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            <span className={isIncome ? 'text-green-600' : 'text-red-600'}>
                              {isIncome ? '+' : ''}${transaction.amount.toLocaleString('en-US', { 
                                minimumFractionDigits: 2 
                              })}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            <div className="flex items-center justify-end space-x-2">
                              <button className="text-gray-400 hover:text-gray-600">
                                <Edit className="h-4 w-4" />
                              </button>
                              <button className="text-gray-400 hover:text-red-600">
                                <Trash2 className="h-4 w-4" />
                              </button>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
                  <div className="flex-1 flex justify-between sm:hidden">
                    <button
                      onClick={() => handlePageChange(filters.page - 1)}
                      disabled={filters.page === 0}
                      className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Previous
                    </button>
                    <button
                      onClick={() => handlePageChange(filters.page + 1)}
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
                          onClick={() => handlePageChange(filters.page - 1)}
                          disabled={filters.page === 0}
                          className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          <ChevronLeft className="h-5 w-5" />
                        </button>
                        {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                          const page = i;
                          return (
                            <button
                              key={page}
                              onClick={() => handlePageChange(page)}
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
                          onClick={() => handlePageChange(filters.page + 1)}
                          disabled={filters.page >= totalPages - 1}
                          className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          <ChevronRight className="h-5 w-5" />
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

      {/* CSV Import Modal */}
      <CsvImportModal
        isOpen={showImportModal}
        onClose={() => setShowImportModal(false)}
        accountId={id}
      />
    </div>
  );
};