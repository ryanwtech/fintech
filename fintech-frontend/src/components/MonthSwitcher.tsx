import { ChevronLeft, ChevronRight } from 'lucide-react';

interface MonthSwitcherProps {
  currentMonth: string; // Format: YYYY-MM
  onMonthChange: (month: string) => void;
  className?: string;
}

export const MonthSwitcher = ({ currentMonth, onMonthChange, className = "" }: MonthSwitcherProps) => {
  const formatMonth = (month: string) => {
    const [year, monthNum] = month.split('-');
    const date = new Date(parseInt(year), parseInt(monthNum) - 1);
    return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  };

  const goToPreviousMonth = () => {
    const [year, month] = currentMonth.split('-').map(Number);
    const date = new Date(year, month - 1);
    date.setMonth(date.getMonth() - 1);
    const newMonth = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
    onMonthChange(newMonth);
  };

  const goToNextMonth = () => {
    const [year, month] = currentMonth.split('-').map(Number);
    const date = new Date(year, month - 1);
    date.setMonth(date.getMonth() + 1);
    const newMonth = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
    onMonthChange(newMonth);
  };

  const goToCurrentMonth = () => {
    const now = new Date();
    const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    onMonthChange(currentMonth);
  };

  const isCurrentMonth = () => {
    const now = new Date();
    const current = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    return currentMonth === current;
  };

  return (
    <div className={`flex items-center space-x-4 ${className}`}>
      <button
        onClick={goToPreviousMonth}
        className="p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100"
        title="Previous month"
      >
        <ChevronLeft className="h-5 w-5" />
      </button>
      
      <div className="flex items-center space-x-2">
        <h2 className="text-lg font-semibold text-gray-900">
          {formatMonth(currentMonth)}
        </h2>
        {!isCurrentMonth() && (
          <button
            onClick={goToCurrentMonth}
            className="text-sm text-indigo-600 hover:text-indigo-500"
          >
            Go to current month
          </button>
        )}
      </div>
      
      <button
        onClick={goToNextMonth}
        className="p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100"
        title="Next month"
      >
        <ChevronRight className="h-5 w-5" />
      </button>
    </div>
  );
};
