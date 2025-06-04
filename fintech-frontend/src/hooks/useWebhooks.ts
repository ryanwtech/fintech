import { useQuery, useMutation } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { WebhookEvent, WebhookPayload } from '../types';

export const useWebhookEvents = () => {
  return useQuery({
    queryKey: ['webhook-events'],
    queryFn: async () => {
      const response = await api.get('/webhooks/events');
      return response.data;
    },
    refetchInterval: 5000, // Refetch every 5 seconds to show real-time updates
  });
};

export const useSimulateWebhook = () => {
  return useMutation<any, Error, WebhookPayload>({
    mutationFn: async (payload) => {
      const response = await api.post('/webhooks/test/simulate', payload);
      return response.data;
    },
  });
};

export const useSendWebhook = () => {
  return useMutation<any, Error, string>({
    mutationFn: async (payload) => {
      const response = await api.post('/webhooks/mockbank', payload, {
        headers: {
          'Content-Type': 'application/json',
        },
      });
      return response.data;
    },
  });
};
