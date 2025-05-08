import { Routes, Route } from 'react-router-dom';
import Users from './Users';
import UserDetails from './UserDetails';
import Login from './Login';
import Logout from './Logout';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/login" element={<Login />} />
      <Route path="/logout" element={<Logout />} />
      <Route path="/users" element={<Users />} />
      <Route path="/users/:id" element={<UserDetails />} />
    </Routes>
  );
}

export default App;