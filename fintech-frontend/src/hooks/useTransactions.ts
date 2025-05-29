import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { Transaction, CreateTransactionRequest, UpdateTransactionRequest } from '../types';

export const useTransactions = (
  accountId: string,
  params?: {
    from?: string;
    to?: string;
    q?: string;
    categoryId?: string;
    page?: number;
    size?: number;
  }
) => {
  return useQuery({
    queryKey: ['transactions', accountId, params],
    queryFn: async () => {
      const response = await api.get(`/accounts/${accountId}/transactions`, {
        params,
      });
      return response.data;
    },
    enabled: !!accountId,
  });
};

export const useCreateTransaction = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Transaction, Error, { accountId: string; data: CreateTransactionRequest }>({
    mutationFn: async ({ accountId, data }) => {
      const response = await api.post(`/accounts/${accountId}/transactions`, data);
      return response.data;
    },
    onSuccess: (_, { accountId }) => {
      queryClient.invalidateQueries({ queryKey: ['transactions', accountId] });
      queryClient.invalidateQueries({ queryKey: ['accounts'] });
    },
  });
};

export const useUpdateTransaction = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Transaction, Error, { id: string; data: UpdateTransactionRequest }>({
    mutationFn: async ({ id, data }) => {
      const response = await api.patch(`/transactions/${id}`, data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transactions'] });
      queryClient.invalidateQueries({ queryKey: ['accounts'] });
    },
  });
};

export const useImportTransactions = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ accountId, file }: { accountId: string; file: File }) => {
      const formData = new FormData();
      formData.append('file', file);
      const response = await api.post(`/transactions/import`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        params: { accountId },
      });
      return response.data;
    },
    onSuccess: (_, { accountId }) => {
      queryClient.invalidateQueries({ queryKey: ['transactions', accountId] });
      queryClient.invalidateQueries({ queryKey: ['accounts'] });
    },
  });
};
