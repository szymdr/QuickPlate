import { useEffect, useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'
import UserDetails from './UserDetails'
import './App.css'

function Home() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [newUser, setNewUser] = useState({
      firstName: '',
      lastName: '',
      email: '',
      phone: ''
  })

  const fetchUsers = () => {
    setLoading(true)
    fetch('http://localhost:8080/api/users')
        .then(res => res.json())
        .then(data => {
            console.log('Pobrani użytkownicy:', data)
            setUsers(data)
            setLoading(false)
        })
        .catch(err => {
            console.error('Błąd pobierania danych:', err)
            setLoading(false)
        })
  }

  useEffect(() => {
      fetchUsers()
  }, [])

  const handleInputChange = (e) => {
      const { name, value } = e.target
      setNewUser(prevState => ({
          ...prevState,
          [name]: value
      }))
  }

  const handleSubmit = (e) => {
      e.preventDefault()
      // Tutaj należy wysłać newUser do backendu (np. przy użyciu fetch/axios)
      console.log('Dodawany użytkownik:', newUser)
      setNewUser({
          firstName: '',
          lastName: '',
          email: '',
          phone: ''
      })
      setShowForm(false)
      // Po udanym zapisie możesz odświeżyć listę użytkowników:
      fetchUsers()
  }

  return (
      <div className="wrapper">
          <header>
              <h1>Lista użytkowników</h1>
              <button onClick={fetchUsers}>Odśwież dane</button>
              <button onClick={() => setShowForm(prev => !prev)}>
                  {showForm ? 'Anuluj' : 'Dodaj nowego użytkownika'}
              </button>
          </header>
          {showForm && (
              <form className="user-form" onSubmit={handleSubmit}>
                  <input
                      type="text"
                      name="firstName"
                      placeholder="Imię"
                      value={newUser.firstName}
                      onChange={handleInputChange}
                      required
                  />
                  <input
                      type="text"
                      name="lastName"
                      placeholder="Nazwisko"
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
                      type="text"
                      name="phone"
                      placeholder="Telefon"
                      value={newUser.phone}
                      onChange={handleInputChange}
                  />
                  <button type="submit">Dodaj użytkownika</button>
              </form>
          )}
          {loading ? (
              <p className="loading">Ładowanie użytkowników...</p>
          ) : (
              <div className="user-grid">
                  {users.map(user => (
                      <Link to={`/users/${user.id}`} key={user.id} className="user-card">
                          <h2>{`${user.firstName} ${user.lastName}`}</h2>
                          <p><strong>Email:</strong> {user.email}</p>
                          <p><strong>Telefon:</strong> {user.phone || 'Brak danych'}</p>
                          <p>
                              <strong>Data rejestracji:</strong>{' '}
                              {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'Brak daty'}
                          </p>
                      </Link>
                  ))}
              </div>
          )}
      </div>
  )
}

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/users/:id" element={<UserDetails />} />
            </Routes>
        </Router>
    )
}

export default App