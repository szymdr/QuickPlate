import { Routes, Route } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import Users from './Users';
import UserDetails from './UserDetails';
import Login from './Login';
import Logout from './Logout';
import HomePage from './HomePage';
import RestaurantPage from './RestaurantPage';
import RegisterPage from './Register';
import ReservationPage from './ReservationPage';
import PaymentPage from './PaymentPage';
import ProfilePage from './ProfilePage';
import OwnerDashboard from './OwnerDashboard';
import EditRestaurant from './EditRestaurant';
import MenuManagement from './MenuManagement';
import EditMenuItem from './EditMenuItem';
import AdminDashboard from './AdminDashboard';

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/restaurants/:id" element={<RestaurantPage />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/logout" element={<Logout />} />
      <Route path="/users" element={<Users />} />
      <Route path="/users/:id" element={<UserDetails />} />
      <Route path="/reservation" element={<ReservationPage />} />
      <Route path="/reservation/:id" element={<ReservationPage />} />
      <Route path="/payment" element={<PaymentPage />} />
      <Route path="/profile" element={<ProfilePage />} />

      <Route path="/owner" element={
        <ProtectedRoute role="RESTAURANT_OWNER">
          <OwnerDashboard/>
        </ProtectedRoute>
      }/>
      <Route path="/owner/restaurants/:id/edit" element={
        <ProtectedRoute role="RESTAURANT_OWNER">
          <EditRestaurant/>
        </ProtectedRoute>
      }/>
      <Route path="/owner/restaurants/new" element={
        <ProtectedRoute role="RESTAURANT_OWNER">
          <EditRestaurant/>
        </ProtectedRoute>
      }/>
      <Route path="/owner/restaurants/:id/menu" element={
        <ProtectedRoute role="RESTAURANT_OWNER">
          <MenuManagement/>
        </ProtectedRoute>
      }/>
      <Route path="/owner/restaurants/:rid/menu/:mid/edit" element={
        <ProtectedRoute role="RESTAURANT_OWNER">
          <EditMenuItem/>
        </ProtectedRoute>
      }/>
      <Route path="/owner/restaurants/:rid/menu/new" element={
        <ProtectedRoute role="RESTAURANT_OWNER">
          <EditMenuItem/>
        </ProtectedRoute>
      }/>
      <Route path="/admin" element={
        <ProtectedRoute role="ADMIN" navigateTo="/">
          <AdminDashboard />
        </ProtectedRoute>
      }/>
    </Routes>
  );
}

export default App;