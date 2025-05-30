import toast from 'react-hot-toast';
import { AxiosError } from 'axios';

export const handleApiError = (error: unknown, defaultMessage = 'An error occurred') => {
  console.error('API Error:', error);

  if (error instanceof AxiosError) {
    const message = error.response?.data?.message || error.message || defaultMessage;
    
    // Handle specific error cases
    if (error.response?.status === 401) {
      toast.error('Session expired. Please log in again.');
      return;
    }
    
    if (error.response?.status === 403) {
      toast.error('You do not have permission to perform this action.');
      return;
    }
    
    if (error.response?.status === 404) {
      toast.error('Resource not found.');
      return;
    }
    
    if (error.response?.status === 422) {
      // Validation errors
      const validationErrors = error.response?.data?.errors;
      if (validationErrors && Array.isArray(validationErrors)) {
        validationErrors.forEach((err: any) => {
          toast.error(err.message || 'Validation error');
        });
        return;
      }
    }
    
    if (error.response && error.response.status >= 500) {
      toast.error('Server error. Please try again later.');
      return;
    }
    
    toast.error(message);
  } else if (error instanceof Error) {
    toast.error(error.message);
  } else {
    toast.error(defaultMessage);
  }
};

export const showSuccessToast = (message: string) => {
  toast.success(message);
};

export const showErrorToast = (message: string) => {
  toast.error(message);
};

export const showLoadingToast = (message: string) => {
  return toast.loading(message);
};
