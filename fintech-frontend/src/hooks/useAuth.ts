import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { User, AuthResponse, LoginRequest, RegisterRequest } from '../types';

export const useMe = () => {
  return useQuery<User>({
    queryKey: ['me'],
    queryFn: async () => {
      const response = await api.get('/auth/profile');
      return response.data;
    },
    retry: false,
  });
};

export const useLogin = () => {
  const queryClient = useQueryClient();
  
  return useMutation<AuthResponse, Error, LoginRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/auth/login', data);
      return response.data;
    },
    onSuccess: (data) => {
      localStorage.setItem('token', data.token);
      queryClient.setQueryData(['me'], data.user);
    },
  });
};

export const useRegister = () => {
  const queryClient = useQueryClient();
  
  return useMutation<AuthResponse, Error, RegisterRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/auth/register', data);
      return response.data;
    },
    onSuccess: (data) => {
      localStorage.setItem('token', data.token);
      queryClient.setQueryData(['me'], data.user);
    },
  });
};

export const useLogout = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async () => {
      localStorage.removeItem('token');
    },
    onSuccess: () => {
      queryClient.clear();
    },
  });
};
