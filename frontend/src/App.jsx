import { Routes, Route } from 'react-router-dom';
import Users from './Users';
import UserDetails from './UserDetails';
import Login from './Login';
import Logout from './Logout';
import HomePage from './HomePage';
import RestaurantPage from './RestaurantPage';
import RegisterPage from './Register';
import ReservationPage from './ReservationPage';

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
    </Routes>
  );
}

export default App;