import React from 'react';
import { Navigate, replace } from 'react-router-dom';

const ProtectedRoute = ({ role, navigateTo = '/login', children }) => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (!user) {
    return <Navigate to={navigateTo} replace />;
  }
  if (role && user.accountType?.name !== role) {
    return <Navigate to={navigateTo} replace />;
  }
  return children;
};

export default ProtectedRoute;