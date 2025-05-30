import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { Category, CreateCategoryRequest, UpdateCategoryRequest } from '../types';

export const useCategories = () => {
  return useQuery<Category[]>({
    queryKey: ['categories'],
    queryFn: async () => {
      const response = await api.get('/categories');
      return response.data;
    },
  });
};

export const useCategory = (id: string) => {
  return useQuery<Category>({
    queryKey: ['categories', id],
    queryFn: async () => {
      const response = await api.get(`/categories/${id}`);
      return response.data;
    },
    enabled: !!id,
  });
};

export const useCreateCategory = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Category, Error, CreateCategoryRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/categories', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categories'] });
    },
  });
};

export const useUpdateCategory = () => {
  const queryClient = useQueryClient();
  
  return useMutation<Category, Error, { id: string; data: UpdateCategoryRequest }>({
    mutationFn: async ({ id, data }) => {
      const response = await api.patch(`/categories/${id}`, data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categories'] });
    },
  });
};

export const useDeleteCategory = () => {
  const queryClient = useQueryClient();
  
  return useMutation<void, Error, string>({
    mutationFn: async (id) => {
      await api.delete(`/categories/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categories'] });
    },
  });
};
