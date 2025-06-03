import { useState, useEffect } from 'react';
import { useCashflowReport, useSpendByCategoryReport, useTrendReport, useExportReport } from '../hooks/useReports';
import { PieChart, Pie, Cell, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';
import { Download, Calendar, TrendingUp, DollarSign, PieChart as PieChartIcon } from 'lucide-react';

export const ReportsPage = () => {
  const [dateRange, setDateRange] = useState({
    from: '',
    to: '',
  });
  const [trendMonths, setTrendMonths] = useState(6);

  const { data: cashflowReport, isLoading: cashflowLoading } = useCashflowReport(dateRange.from, dateRange.to);
  const { data: spendByCategoryReport, isLoading: spendByCategoryLoading } = useSpendByCategoryReport(dateRange.from, dateRange.to);
  const { data: trendReport, isLoading: trendLoading } = useTrendReport(trendMonths);
  const exportReport = useExportReport();

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

  const handleExport = async (reportType: 'cashflow' | 'spend-by-category' | 'trend') => {
    try {
      if (reportType === 'trend') {
        await exportReport(reportType, { months: trendMonths });
      } else {
        await exportReport(reportType, dateRange);
      }
    } catch (error) {
      console.error('Failed to export report:', error);
    }
  };

  // Colors for charts
  const COLORS = ['#3B82F6', '#EF4444', '#10B981', '#F59E0B', '#8B5CF6', '#EC4899', '#06B6D4', '#84CC16'];

  const formatCurrency = (value: number) => `$${value.toLocaleString('en-US', { minimumFractionDigits: 2 })}`;

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Reports</h1>
          <p className="mt-1 text-sm text-gray-500">
            Visualize your financial data with interactive charts and reports.
          </p>
        </div>
      </div>

      {/* Date Range Selector */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center space-x-4">
          <Calendar className="h-5 w-5 text-gray-400" />
          <div className="flex items-center space-x-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">From</label>
              <input
                type="date"
                value={dateRange.from}
                onChange={(e) => setDateRange(prev => ({ ...prev, from: e.target.value }))}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">To</label>
              <input
                type="date"
                value={dateRange.to}
                onChange={(e) => setDateRange(prev => ({ ...prev, to: e.target.value }))}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Trend Months</label>
              <select
                value={trendMonths}
                onChange={(e) => setTrendMonths(parseInt(e.target.value))}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              >
                <option value={3}>3 months</option>
                <option value={6}>6 months</option>
                <option value={12}>12 months</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      {/* Cashflow Report */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <TrendingUp className="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <h2 className="text-lg font-semibold text-gray-900">Cashflow Report</h2>
              <p className="text-sm text-gray-500">Income vs expenses over time</p>
            </div>
          </div>
          <button
            onClick={() => handleExport('cashflow')}
            className="inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            <Download className="h-4 w-4 mr-2" />
            Export CSV
          </button>
        </div>

        {cashflowLoading ? (
          <div className="h-64 flex items-center justify-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
          </div>
        ) : cashflowReport ? (
          <div className="space-y-4">
            {/* Summary Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="text-center p-4 bg-green-50 rounded-lg">
                <p className="text-sm text-green-600">Total Income</p>
                <p className="text-2xl font-semibold text-green-900">
                  {formatCurrency(cashflowReport.totalIncome)}
                </p>
              </div>
              <div className="text-center p-4 bg-red-50 rounded-lg">
                <p className="text-sm text-red-600">Total Expenses</p>
                <p className="text-2xl font-semibold text-red-900">
                  {formatCurrency(cashflowReport.totalExpenses)}
                </p>
              </div>
              <div className={`text-center p-4 rounded-lg ${
                cashflowReport.netCashflow >= 0 ? 'bg-green-50' : 'bg-red-50'
              }`}>
                <p className={`text-sm ${cashflowReport.netCashflow >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                  Net Cashflow
                </p>
                <p className={`text-2xl font-semibold ${
                  cashflowReport.netCashflow >= 0 ? 'text-green-900' : 'text-red-900'
                }`}>
                  {formatCurrency(cashflowReport.netCashflow)}
                </p>
              </div>
            </div>

            {/* Area Chart */}
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={cashflowReport.dataPoints}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="date" 
                    tickFormatter={formatDate}
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis tickFormatter={formatCurrency} tick={{ fontSize: 12 }} />
                  <Tooltip 
                    formatter={(value: number) => [formatCurrency(value), '']}
                    labelFormatter={(label) => `Date: ${formatDate(label)}`}
                  />
                  <Area
                    type="monotone"
                    dataKey="income"
                    stackId="1"
                    stroke="#10B981"
                    fill="#10B981"
                    fillOpacity={0.6}
                    name="Income"
                  />
                  <Area
                    type="monotone"
                    dataKey="expenses"
                    stackId="2"
                    stroke="#EF4444"
                    fill="#EF4444"
                    fillOpacity={0.6}
                    name="Expenses"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>
        ) : (
          <div className="text-center py-12">
            <TrendingUp className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No cashflow data</h3>
            <p className="mt-1 text-sm text-gray-500">Select a date range to view cashflow data.</p>
          </div>
        )}
      </div>

      {/* Spend by Category Report */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <PieChartIcon className="h-6 w-6 text-purple-600" />
            </div>
            <div>
              <h2 className="text-lg font-semibold text-gray-900">Spend by Category</h2>
              <p className="text-sm text-gray-500">Breakdown of expenses by category</p>
            </div>
          </div>
          <button
            onClick={() => handleExport('spend-by-category')}
            className="inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            <Download className="h-4 w-4 mr-2" />
            Export CSV
          </button>
        </div>

        {spendByCategoryLoading ? (
          <div className="h-64 flex items-center justify-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
          </div>
        ) : spendByCategoryReport ? (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Pie Chart */}
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={spendByCategoryReport.categoryData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="amount"
                  >
                    {spendByCategoryReport.categoryData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value: number) => formatCurrency(value)} />
                </PieChart>
              </ResponsiveContainer>
            </div>

            {/* Category List */}
            <div className="space-y-3">
              <div className="text-center p-3 bg-gray-50 rounded-lg">
                <p className="text-sm text-gray-600">Total Spent</p>
                <p className="text-2xl font-semibold text-gray-900">
                  {formatCurrency(spendByCategoryReport.totalSpent)}
                </p>
              </div>
              <div className="space-y-2 max-h-48 overflow-y-auto">
                {spendByCategoryReport.categoryData.map((category, index) => (
                  <div key={category.categoryId} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div className="flex items-center space-x-3">
                      <div
                        className="w-4 h-4 rounded-full"
                        style={{ backgroundColor: COLORS[index % COLORS.length] }}
                      />
                      <div>
                        <p className="text-sm font-medium text-gray-900">{category.categoryName}</p>
                        <p className="text-xs text-gray-500">{category.transactionCount} transactions</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-sm font-semibold text-gray-900">
                        {formatCurrency(category.amount)}
                      </p>
                      <p className="text-xs text-gray-500">
                        {((category.amount / spendByCategoryReport.totalSpent) * 100).toFixed(1)}%
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        ) : (
          <div className="text-center py-12">
            <PieChartIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No spending data</h3>
            <p className="mt-1 text-sm text-gray-500">Select a date range to view spending by category.</p>
          </div>
        )}
      </div>

      {/* Trend Report */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-orange-100 rounded-lg">
              <DollarSign className="h-6 w-6 text-orange-600" />
            </div>
            <div>
              <h2 className="text-lg font-semibold text-gray-900">Monthly Trends</h2>
              <p className="text-sm text-gray-500">Income and expenses over time</p>
            </div>
          </div>
          <button
            onClick={() => handleExport('trend')}
            className="inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            <Download className="h-4 w-4 mr-2" />
            Export CSV
          </button>
        </div>

        {trendLoading ? (
          <div className="h-64 flex items-center justify-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
          </div>
        ) : trendReport ? (
          <div className="space-y-4">
            {/* Summary Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="text-center p-4 bg-green-50 rounded-lg">
                <p className="text-sm text-green-600">Avg Monthly Income</p>
                <p className="text-2xl font-semibold text-green-900">
                  {formatCurrency(trendReport.totalIncome / trendReport.monthlyData.length)}
                </p>
              </div>
              <div className="text-center p-4 bg-red-50 rounded-lg">
                <p className="text-sm text-red-600">Avg Monthly Expenses</p>
                <p className="text-2xl font-semibold text-red-900">
                  {formatCurrency(trendReport.totalExpenses / trendReport.monthlyData.length)}
                </p>
              </div>
              <div className={`text-center p-4 rounded-lg ${
                trendReport.netCashflow >= 0 ? 'bg-green-50' : 'bg-red-50'
              }`}>
                <p className={`text-sm ${trendReport.netCashflow >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                  Avg Net Cashflow
                </p>
                <p className={`text-2xl font-semibold ${
                  trendReport.netCashflow >= 0 ? 'text-green-900' : 'text-red-900'
                }`}>
                  {formatCurrency(trendReport.netCashflow / trendReport.monthlyData.length)}
                </p>
              </div>
            </div>

            {/* Line Chart */}
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={trendReport.monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="month" 
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis tickFormatter={formatCurrency} tick={{ fontSize: 12 }} />
                  <Tooltip 
                    formatter={(value: number) => [formatCurrency(value), '']}
                    labelFormatter={(label) => `Month: ${label}`}
                  />
                  <Line
                    type="monotone"
                    dataKey="totalIncome"
                    stroke="#10B981"
                    strokeWidth={2}
                    name="Income"
                  />
                  <Line
                    type="monotone"
                    dataKey="totalExpenses"
                    stroke="#EF4444"
                    strokeWidth={2}
                    name="Expenses"
                  />
                  <Line
                    type="monotone"
                    dataKey="netCashflow"
                    stroke="#3B82F6"
                    strokeWidth={2}
                    name="Net Cashflow"
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        ) : (
          <div className="text-center py-12">
            <DollarSign className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No trend data</h3>
            <p className="mt-1 text-sm text-gray-500">Trend data will appear as you accumulate more transactions.</p>
          </div>
        )}
      </div>
    </div>
  );
};