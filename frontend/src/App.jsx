import { Routes, Route } from 'react-router-dom';
import Users from './Users';
import UserDetails from './UserDetails';
import Login from './Login';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/users" element={<Users />} />
      <Route path="/users/:id" element={<UserDetails />} />
      <Route path="/login" element={<Login />} />
    </Routes>
  );
}

export default App;