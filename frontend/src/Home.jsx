import { useEffect, useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'
import { FiRefreshCw } from 'react-icons/fi'
import UserDetails from './UserDetails'
import './App.css'

document.title = 'Users list ∙ QuickPlate'

function Home() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [newUser, setNewUser] = useState({
      firstName: '',
      lastName: '',
      email: '',
      passwordHash: '',
      phone: ''
  })

  const fetchUsers = () => {
    setLoading(true)
    fetch('http://localhost:8080/api/users')
        .then(res => res.json())
        .then(data => {
            console.log('Fetched users:', data)
            setUsers(data)
            setLoading(false)
        })
        .catch(err => {
            console.error('Error fetching users:', err)
            setLoading(false)
        })
  }

  useEffect(() => {
      fetchUsers()
  }, [])

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (name === "phone") {
      let numericValue = value.replace(/\D/g, "");
      numericValue = numericValue.substring(0, 9);
      const formatted = numericValue.match(/.{1,3}/g)?.join(" ") || "";
      
      setNewUser(prevState => ({
        ...prevState,
        phone: formatted
      }));
    } else {
      setNewUser(prevState => ({
        ...prevState,
        [name]: value
      }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    fetch('http://localhost:8080/api/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(newUser)
    })
    .then((res) => {
        if (!res.ok) {
            throw new Error('Network response was not ok');
        }
        return res.json();
    })
    .then((data) => {
        console.log('User added:', data);
        setNewUser({
            firstName: '',
            lastName: '',
            email: '',
            passwordHash: '',
            phone: ''
        });
        setShowForm(false);
        fetchUsers();
    })
    .catch((err) => {
        console.error('Error while adding user', err);
    });
  };

  return (
      <div className="wrapper">
          <header>
              <h1>Users list</h1>
              <button className="refresh-icon" onClick={fetchUsers}>
                  <FiRefreshCw size={20} />
              </button>
              <button className="add-user" onClick={() => setShowForm(prev => !prev)}>
                  {showForm ? 'Cancel' : 'Add User'}
              </button>
          </header>
          {showForm && (
              <form className="user-form" onSubmit={handleSubmit}>
                  <input
                      type="text"
                      name="firstName"
                      placeholder="First Name"
                      value={newUser.firstName}
                      onChange={handleInputChange}
                      required
                  />
                  <input
                      type="text"
                      name="lastName"
                      placeholder="Last Name"
                      value={newUser.lastName}
                      onChange={handleInputChange}
                      required
                  />
                  <input
                      type="email"
                      name="email"
                      placeholder="Email"
                      value={newUser.email}
                      onChange={handleInputChange}
                      required
                  />
                  <input
                      type="password"
                      name="passwordHash"
                      placeholder="Password"
                      value={newUser.passwordHash}
                      onChange={handleInputChange}
                      required
                  />
                  <input
                      type="tel"
                      name="phone"
                      placeholder="Phone number"
                      pattern="[0-9]{3}-[0-9]{3}-[0-9]{3}"
                      value={newUser.phone}
                      onChange={handleInputChange}
                  />
                  <button type="submit">Add user</button>
              </form>
          )}
          {loading ? (
              <p className="loading">Loading users...</p>
          ) : (
              <div className="user-grid">
                  {users.map(user => (
                      <Link to={`/users/${user.id}`} key={user.id} className="user-card">
                          <h2>{`${user.firstName} ${user.lastName}`}</h2>
                      </Link>
                  ))}
              </div>
          )}
      </div>
  )
}

export default Home