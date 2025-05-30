import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import { useAuthStore } from '../store/authStore';
import { handleApiError, showSuccessToast } from '../lib/errorHandler';
import type { User, AuthResponse, LoginRequest, RegisterRequest } from '../types';

export const useMe = () => {
  const { user, isAuthenticated } = useAuthStore();
  
  return useQuery<User>({
    queryKey: ['me'],
    queryFn: async () => {
      const response = await api.get('/auth/profile');
      return response.data;
    },
    retry: false,
    enabled: isAuthenticated && !!user,
    initialData: user || undefined,
  });
};

export const useLogin = () => {
  const queryClient = useQueryClient();
  const setAuth = useAuthStore((state) => state.setAuth);
  
  return useMutation<AuthResponse, Error, LoginRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/auth/login', data);
      return response.data;
    },
    onSuccess: (data) => {
      setAuth(data.user, data.token);
      queryClient.setQueryData(['me'], data.user);
      showSuccessToast(`Welcome back, ${data.user.firstName}!`);
    },
    onError: (error) => {
      handleApiError(error, 'Login failed. Please check your credentials.');
    },
  });
};

export const useRegister = () => {
  const queryClient = useQueryClient();
  const setAuth = useAuthStore((state) => state.setAuth);
  
  return useMutation<AuthResponse, Error, RegisterRequest>({
    mutationFn: async (data) => {
      const response = await api.post('/auth/register', data);
      return response.data;
    },
    onSuccess: (data) => {
      setAuth(data.user, data.token);
      queryClient.setQueryData(['me'], data.user);
      showSuccessToast(`Welcome, ${data.user.firstName}! Your account has been created.`);
    },
    onError: (error) => {
      handleApiError(error, 'Registration failed. Please try again.');
    },
  });
};

export const useLogout = () => {
  const queryClient = useQueryClient();
  const clearAuth = useAuthStore((state) => state.clearAuth);
  
  return useMutation({
    mutationFn: async () => {
      // Optionally call logout endpoint
      try {
        await api.post('/auth/logout');
      } catch (error) {
        // Ignore logout endpoint errors
        console.warn('Logout endpoint failed:', error);
      }
    },
    onSuccess: () => {
      clearAuth();
      queryClient.clear();
      showSuccessToast('You have been logged out successfully.');
    },
    onError: (error) => {
      // Even if logout endpoint fails, clear local state
      clearAuth();
      queryClient.clear();
      handleApiError(error, 'Logout failed, but you have been logged out locally.');
    },
  });
};
