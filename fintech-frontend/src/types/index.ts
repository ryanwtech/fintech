export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'USER' | 'ADMIN';
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Account {
  id: string;
  userId: string;
  name: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'CREDIT' | 'INVESTMENT';
  balance: number;
  currency: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Category {
  id: string;
  userId?: string;
  name: string;
  description?: string;
  color?: string;
  icon?: string;
  isIncome: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Transaction {
  id: string;
  accountId: string;
  categoryId?: string;
  amount: number;
  description: string;
  merchant?: string;
  postedAt: string;
  transactionType: 'CREDIT' | 'DEBIT';
  status: 'PENDING' | 'CLEARED' | 'FAILED';
  externalId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Budget {
  id: string;
  userId: string;
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  totalAmount: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface BudgetItem {
  id: string;
  budgetId: string;
  categoryId: string;
  plannedAmount: number;
  actualAmount: number;
  createdAt: string;
  updatedAt: string;
}

export interface Rule {
  id: string;
  userId: string;
  name: string;
  description?: string;
  pattern: string;
  targetCategoryId: string;
  targetCategoryName?: string;
  priority: number;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRuleRequest {
  name: string;
  description?: string;
  pattern: string;
  targetCategoryId: string;
  priority?: number;
  enabled?: boolean;
}

export interface UpdateRuleRequest {
  name?: string;
  description?: string;
  pattern?: string;
  targetCategoryId?: string;
  priority?: number;
  enabled?: boolean;
}

export interface BankConnection {
  id: string;
  userId: string;
  bankName: string;
  externalConnectionId: string;
  accountNumberMasked: string;
  connectionStatus: 'ACTIVE' | 'INACTIVE' | 'ERROR';
  lastSyncAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface CreateAccountRequest {
  name: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'CREDIT' | 'INVESTMENT';
  currency: string;
}

export interface CreateTransactionRequest {
  amount: number;
  description: string;
  merchant?: string;
  postedAt: string;
  categoryId?: string;
}

export interface UpdateTransactionRequest {
  description?: string;
  merchant?: string;
  postedAt?: string;
  categoryId?: string;
}

export interface CreateCategoryRequest {
  name: string;
  description?: string;
  color?: string;
  icon?: string;
  isIncome: boolean;
}

export interface UpdateCategoryRequest {
  name?: string;
  description?: string;
  color?: string;
  icon?: string;
  isIncome?: boolean;
}

export interface CreateBudgetRequest {
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  totalAmount: number;
  items: BudgetItemRequest[];
}

export interface BudgetItemRequest {
  categoryId: string;
  plannedAmount: number;
}

export interface UpdateBudgetItemRequest {
  plannedAmount: number;
}

export interface CashflowReport {
  totalIncome: number;
  totalExpenses: number;
  netCashflow: number;
  dataPoints: CashflowDataPoint[];
}

export interface CashflowDataPoint {
  date: string;
  income: number;
  expenses: number;
  netCashflow: number;
}

export interface SpendByCategoryReport {
  totalSpent: number;
  categoryData: CategorySpendData[];
}

export interface CategorySpendData {
  categoryId: string;
  categoryName: string;
  categoryColor?: string;
  amount: number;
  transactionCount: number;
}

export interface TrendReport {
  totalIncome: number;
  totalExpenses: number;
  netCashflow: number;
  monthlyData: MonthlyTrendData[];
}

export interface MonthlyTrendData {
  month: string;
  monthStart: string;
  monthEnd: string;
  totalIncome: number;
  totalExpenses: number;
  netCashflow: number;
  transactionCount: number;
}
