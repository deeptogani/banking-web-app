import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';

const ProtectedAdminRoute = () => {
  const { token, role } = useAuth();

  // If not logged in or not an admin, redirect to admin login
  if (!token || role !== 'ADMIN') {
    return <Navigate to="/admin/login" replace />;
  }

  // If logged in as admin, render the child routes
  return <Outlet />;
};

export default ProtectedAdminRoute; 