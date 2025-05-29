import { z } from 'zod';

export const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
});

export const registerSchema = z.object({
  username: z.string().min(3, 'Username must be at least 3 characters'),
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
});

export const createAccountSchema = z.object({
  name: z.string().min(1, 'Account name is required'),
  accountType: z.enum(['CHECKING', 'SAVINGS', 'CREDIT', 'INVESTMENT']),
  currency: z.string().min(3, 'Currency must be at least 3 characters'),
});

export const createTransactionSchema = z.object({
  amount: z.number().min(0.01, 'Amount must be greater than 0'),
  description: z.string().min(1, 'Description is required'),
  merchant: z.string().optional(),
  postedAt: z.string().min(1, 'Posted date is required'),
  categoryId: z.string().optional(),
});

export const updateTransactionSchema = z.object({
  description: z.string().optional(),
  merchant: z.string().optional(),
  postedAt: z.string().optional(),
  categoryId: z.string().optional(),
});

export const createCategorySchema = z.object({
  name: z.string().min(1, 'Category name is required'),
  description: z.string().optional(),
  color: z.string().optional(),
  icon: z.string().optional(),
  isIncome: z.boolean(),
});

export const updateCategorySchema = z.object({
  name: z.string().optional(),
  description: z.string().optional(),
  color: z.string().optional(),
  icon: z.string().optional(),
  isIncome: z.boolean().optional(),
});

export const createBudgetSchema = z.object({
  name: z.string().min(1, 'Budget name is required'),
  description: z.string().optional(),
  startDate: z.string().min(1, 'Start date is required'),
  endDate: z.string().min(1, 'End date is required'),
  totalAmount: z.number().min(0.01, 'Total amount must be greater than 0'),
  items: z.array(z.object({
    categoryId: z.string().min(1, 'Category is required'),
    plannedAmount: z.number().min(0, 'Planned amount must be non-negative'),
  })).min(1, 'At least one budget item is required'),
});

export const updateBudgetItemSchema = z.object({
  plannedAmount: z.number().min(0, 'Planned amount must be non-negative'),
});

export const createRuleSchema = z.object({
  name: z.string().min(1, 'Rule name is required'),
  description: z.string().optional(),
  conditions: z.string().min(1, 'Conditions are required'),
  actions: z.string().min(1, 'Actions are required'),
  priority: z.number().min(1, 'Priority must be at least 1'),
  enabled: z.boolean(),
});

export const updateRuleSchema = z.object({
  name: z.string().optional(),
  description: z.string().optional(),
  conditions: z.string().optional(),
  actions: z.string().optional(),
  priority: z.number().min(1).optional(),
  enabled: z.boolean().optional(),
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;
export type CreateAccountFormData = z.infer<typeof createAccountSchema>;
export type CreateTransactionFormData = z.infer<typeof createTransactionSchema>;
export type UpdateTransactionFormData = z.infer<typeof updateTransactionSchema>;
export type CreateCategoryFormData = z.infer<typeof createCategorySchema>;
export type UpdateCategoryFormData = z.infer<typeof updateCategorySchema>;
export type CreateBudgetFormData = z.infer<typeof createBudgetSchema>;
export type UpdateBudgetItemFormData = z.infer<typeof updateBudgetItemSchema>;
export type CreateRuleFormData = z.infer<typeof createRuleSchema>;
export type UpdateRuleFormData = z.infer<typeof updateRuleSchema>;
