import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { Rule, CreateRuleRequest, UpdateRuleRequest } from '../types';

export const useRules = () => {
  return useQuery({
    queryKey: ['rules'],
    queryFn: async () => {
      const response = await api.get('/rules');
      return response.data;
    },
  });
};

export const useCreateRule = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Rule, Error, CreateRuleRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/rules', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rules'] });
    },
  });
};

export const useUpdateRule = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Rule, Error, { id: string; data: UpdateRuleRequest }>({
    mutationFn: async ({ id, data }) => {
      const response = await api.patch(`/rules/${id}`, data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rules'] });
    },
  });
};

export const useDeleteRule = () => {
  const queryClient = useQueryClient();
  
  return useMutation<void, Error, string>({
    mutationFn: async (id) => {
      await api.delete(`/rules/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rules'] });
    },
  });
};

export const useToggleRule = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Rule, Error, { id: string; enabled: boolean }>({
    mutationFn: async ({ id, enabled }) => {
      const response = await api.patch(`/rules/${id}`, { enabled });
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rules'] });
    },
  });
};

export const useReorderRules = () => {
  const queryClient = useQueryClient();
  
  return useMutation<void, Error, { ruleIds: string[] }>({
    mutationFn: async ({ ruleIds }) => {
      await api.patch('/rules/reorder', { ruleIds });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rules'] });
    },
  });
};

export const useTestRule = () => {
  return useMutation<{ matches: boolean; matchedText?: string }, Error, { pattern: string; testText: string }>({
    mutationFn: async ({ pattern, testText }) => {
      const response = await api.post('/rules/test', { pattern, testText });
      return response.data;
    },
  });
};
