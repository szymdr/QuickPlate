import { Routes, Route } from 'react-router-dom';
import Home from './Home';
import UserDetails from './UserDetails';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/users/:id" element={<UserDetails />} />
    </Routes>
  );
}

export default App;