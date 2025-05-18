import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './theme';
import LandingPage from './pages/LandingPage';
import Login from './pages/Login';
import Register from './pages/Register';
import AdminLogin from './pages/AdminLogin';
import Dashboard from './pages/Dashboard';
import Beneficiaries from './pages/Beneficiaries';
import Transfer from './pages/Transfer';
import CustomerDetails from './pages/CustomerDetails';
import AdminDashboard from './pages/AdminDashboard';
import ProtectedAdminRoute from './components/ProtectedAdminRoute';
import UserManagement from './pages/UserManagement';
import ForgotPassword from './pages/ForgotPassword';
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={
            <>
              <Navbar />
              <Login />
            </>
          } />
          <Route path="/register" element={
            <>
              <Navbar />
              <Register />
            </>
          } />
          <Route path="/admin/login" element={
            <>
              <Navbar />
              <AdminLogin />
            </>
          } />
          <Route path="/forgot-password" element={
            <>
              <Navbar />
              <ForgotPassword />
            </>
          } />
          <Route path="/dashboard" element={
            <PrivateRoute>
              <>
                <Navbar />
                <Dashboard />
              </>
            </PrivateRoute>
          } />
          <Route path="/beneficiaries" element={
            <PrivateRoute>
              <>
                <Navbar />
                <Beneficiaries />
              </>
            </PrivateRoute>
          } />
          <Route path="/transfer/:beneficiaryId" element={
            <PrivateRoute>
              <>
                <Navbar />
                <Transfer />
              </>
            </PrivateRoute>
          } />
          <Route path="/customer-details" element={
            <PrivateRoute>
              <>
                <Navbar />
                <CustomerDetails />
              </>
            </PrivateRoute>
          } />
          <Route path="/admin" element={<ProtectedAdminRoute />}>
            <Route index element={<AdminDashboard />} />
            <Route path="users" element={<UserManagement />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;
