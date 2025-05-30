import { useCashflowReport } from '../hooks/useReports';
import { DollarSign, TrendingUp, TrendingDown, ArrowUpRight, ArrowDownRight } from 'lucide-react';

interface CashflowSummaryProps {
  from: string;
  to: string;
}

export const CashflowSummary = ({ from, to }: CashflowSummaryProps) => {
  const { data: cashflow, isLoading, error } = useCashflowReport(from, to);

  if (isLoading) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <div className="animate-pulse">
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-20 bg-gray-200 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Cashflow Summary</h3>
        <div className="text-center text-gray-500">
          <p>Unable to load cashflow data</p>
        </div>
      </div>
    );
  }

  if (!cashflow) {
    return null;
  }

  const { totalIncome, totalExpenses, netCashflow } = cashflow;
  const isPositive = netCashflow >= 0;

  return (
    <div className="bg-white shadow rounded-lg p-6">
      <h3 className="text-lg font-medium text-gray-900 mb-4">Cashflow Summary</h3>
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        {/* Total Income */}
        <div className="bg-green-50 rounded-lg p-4">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <TrendingUp className="h-6 w-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-green-600">Total Income</p>
              <p className="text-2xl font-semibold text-green-900">
                ${totalIncome.toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </p>
            </div>
          </div>
        </div>

        {/* Total Expenses */}
        <div className="bg-red-50 rounded-lg p-4">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <TrendingDown className="h-6 w-6 text-red-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-red-600">Total Expenses</p>
              <p className="text-2xl font-semibold text-red-900">
                ${Math.abs(totalExpenses).toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </p>
            </div>
          </div>
        </div>

        {/* Net Cashflow */}
        <div className={`rounded-lg p-4 ${isPositive ? 'bg-green-50' : 'bg-red-50'}`}>
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <DollarSign className={`h-6 w-6 ${isPositive ? 'text-green-600' : 'text-red-600'}`} />
            </div>
            <div className="ml-4">
              <p className={`text-sm font-medium ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
                Net Cashflow
              </p>
              <div className="flex items-center">
                <p className={`text-2xl font-semibold ${isPositive ? 'text-green-900' : 'text-red-900'}`}>
                  ${Math.abs(netCashflow).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                </p>
                {isPositive ? (
                  <ArrowUpRight className="h-4 w-4 text-green-600 ml-1" />
                ) : (
                  <ArrowDownRight className="h-4 w-4 text-red-600 ml-1" />
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
