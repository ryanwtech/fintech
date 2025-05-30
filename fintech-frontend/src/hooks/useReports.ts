import { useQuery } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { CashflowReport, SpendByCategoryReport, TrendReport } from '../types';

export const useCashflowReport = (from?: string, to?: string) => {
  return useQuery<CashflowReport>({
    queryKey: ['reports', 'cashflow', from, to],
    queryFn: async () => {
      const response = await api.get('/reports/cashflow', {
        params: { from, to },
      });
      return response.data;
    },
    enabled: !!from && !!to,
  });
};

export const useSpendByCategoryReport = (from?: string, to?: string) => {
  return useQuery<SpendByCategoryReport>({
    queryKey: ['reports', 'spend-by-category', from, to],
    queryFn: async () => {
      const response = await api.get('/reports/spend-by-category', {
        params: { from, to },
      });
      return response.data;
    },
    enabled: !!from && !!to,
  });
};

export const useTrendReport = (months: number = 6) => {
  return useQuery<TrendReport>({
    queryKey: ['reports', 'trend', months],
    queryFn: async () => {
      const response = await api.get('/reports/trend', {
        params: { months },
      });
      return response.data;
    },
  });
};
