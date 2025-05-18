import axios from 'axios';

// export const API_URL = 'http://localhost:8080/api';
export const API_URL = 'https://banking-app-backend-2wdi.onrender.com/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor to add the auth token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials) => api.post('/auth/login/customer', credentials),
  register: (userData) => api.post('/auth/register/customer', userData),
  getCustomerDetails: () => api.get('/customer-details'),
  addCustomerDetails: (details) => api.post('/customer-details/add', details),
  getAccountBalance: () => api.get('/accounts/balance'),
  getBeneficiaries: () => api.get('/accounts/beneficiaries'),
  addBeneficiary: (beneficiary) => api.post('/accounts/beneficiaries', beneficiary),
  transferToBeneficiary: (transferData) => api.post('/transfers/beneficiary', transferData),
  getTransactionHistory: (page = 0, size = 10) => api.get(`/transfers/history?page=${page}&size=${size}`),
  getCurrentUser: () => api.get('/auth/me'),
};

export default api; 