import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { BankConnection, LinkBankRequest } from '../types';

export const useBankConnections = () => {
  return useQuery({
    queryKey: ['bank-connections'],
    queryFn: async () => {
      const response = await api.get('/integrations/mockbank/connections');
      return response.data;
    },
  });
};

export const useLinkBank = () => {
  const queryClient = useQueryClient();
  
  return useMutation<BankConnection, Error, LinkBankRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/integrations/mockbank/link', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bank-connections'] });
    },
  });
};

export const useUnlinkBank = () => {
  const queryClient = useQueryClient();
  
  return useMutation<void, Error, string>({
    mutationFn: async (connectionId) => {
      await api.delete(`/integrations/mockbank/connections/${connectionId}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bank-connections'] });
    },
  });
};
