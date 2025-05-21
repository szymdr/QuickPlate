import { Routes, Route } from 'react-router-dom';
import Users from './Users';
import UserDetails from './UserDetails';
import Login from './Login';
import Logout from './Logout';
import HomePage from './HomePage';
import RestaurantPage from './RestaurantPage';
import RegisterPage from './Register';

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
    </Routes>
  );
}

export default App;