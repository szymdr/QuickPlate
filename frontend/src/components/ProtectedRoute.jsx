import React from 'react';
import { Navigate } from 'react-router-dom';

export default function ProtectedRoute({ children, role, redirectTo = '/login' }) {
  const user = JSON.parse(localStorage.getItem('user')||'null');
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (role) {
    if (Array.isArray(user.roles)) {
      if (!user.roles.includes(role)) return <Navigate to={redirectTo} replace />;
    }
    else if (user.role) {
      if (user.role !== role) return <Navigate to={redirectTo} replace />;
    }
    else if (user.accountTypeId != null) {
      if (user.accountTypeId !== role) return <Navigate to={redirectTo} replace />;
    }
  }
  return children;
}