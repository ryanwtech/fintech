import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { Account, CreateAccountRequest } from '../types';

export const useAccounts = () => {
  return useQuery<Account[]>({
    queryKey: ['accounts'],
    queryFn: async () => {
      const response = await api.get('/accounts');
      return response.data;
    },
  });
};

export const useAccount = (id: string) => {
  return useQuery<Account>({
    queryKey: ['accounts', id],
    queryFn: async () => {
      const response = await api.get(`/accounts/${id}`);
      return response.data;
    },
    enabled: !!id,
  });
};

export const useCreateAccount = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Account, Error, CreateAccountRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/accounts', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['accounts'] });
    },
  });
};
