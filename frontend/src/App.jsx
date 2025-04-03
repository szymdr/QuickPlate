import { Routes, Route } from 'react-router-dom';
import Home from './Home';
import UserDetails from './UserDetails';
import Login from './Login';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/users/:id" element={<UserDetails />} />
      <Route path="/login" element={<Login />} />
    </Routes>
  );
}

export default App;