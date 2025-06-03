import { useQuery } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { CashflowReport, SpendByCategoryReport, TrendReport } from '../types';

export const useCashflowReport = (from?: string, to?: string) => {
  return useQuery({
    queryKey: ['reports', 'cashflow', from, to],
    queryFn: async () => {
      const response = await api.get('/reports/cashflow', {
        params: { from, to },
      });
      return response.data as CashflowReport;
    },
    enabled: !!(from && to),
  });
};

export const useSpendByCategoryReport = (from?: string, to?: string) => {
  return useQuery({
    queryKey: ['reports', 'spend-by-category', from, to],
    queryFn: async () => {
      const response = await api.get('/reports/spend-by-category', {
        params: { from, to },
      });
      return response.data as SpendByCategoryReport;
    },
    enabled: !!(from && to),
  });
};

export const useTrendReport = (months: number = 6) => {
  return useQuery({
    queryKey: ['reports', 'trend', months],
    queryFn: async () => {
      const response = await api.get('/reports/trend', {
        params: { months },
      });
      return response.data as TrendReport;
    },
  });
};

export const useExportReport = () => {
  return async (reportType: 'cashflow' | 'spend-by-category' | 'trend', params: any) => {
    const response = await api.get(`/reports/${reportType}/export`, {
      params,
      responseType: 'blob',
    });
    
    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `${reportType}-report-${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  };
};