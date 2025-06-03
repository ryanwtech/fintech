import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { Budget, BudgetItem, CreateBudgetRequest, UpdateBudgetItemRequest } from '../types';

export const useBudgets = (month?: string) => {
  return useQuery({
    queryKey: ['budgets', month],
    queryFn: async () => {
      const response = await api.get('/budgets', {
        params: month ? { month } : undefined,
      });
      return response.data;
    },
  });
};

export const useBudget = (id: string) => {
  return useQuery({
    queryKey: ['budget', id],
    queryFn: async () => {
      const response = await api.get(`/budgets/${id}`);
      return response.data;
    },
  });
};

export const useCreateBudget = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Budget, Error, CreateBudgetRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/budgets', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
    },
  });
};

export const useUpdateBudgetItem = () => {
  const queryClient = useQueryClient();
  
  return useMutation<BudgetItem, Error, { budgetId: string; categoryId: string; data: UpdateBudgetItemRequest }>({
    mutationFn: async ({ budgetId, categoryId, data }) => {
      const response = await api.patch(`/budgets/${budgetId}/items/${categoryId}`, data);
      return response.data;
    },
    onSuccess: (_, { budgetId }) => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      queryClient.invalidateQueries({ queryKey: ['budget', budgetId] });
    },
  });
};

export const useDeleteBudget = () => {
  const queryClient = useQueryClient();
  
  return useMutation<void, Error, string>({
    mutationFn: async (id) => {
      await api.delete(`/budgets/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
    },
  });
};