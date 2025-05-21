import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import styles from './UserDetails.module.css'

function UserDetails() {
  const { id } = useParams()
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    fetch(`http://localhost:8080/api/users/${id}`)
      .then(res => res.json())
      .then(data => {
        setUser(data)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [id])

  if (loading) return <p>Loading user details...</p>
  if (!user)   return <p>User not found.</p>

  return (
    <div className={styles.userDetails}>
      <h2>{user.name}</h2>
      <p><strong>Email:</strong> {user.email}</p>
      <p><strong>Phone number:</strong> {user.phone.match(/.{1,3}/g)?.join(' ')}</p>
      <p><strong>Registration date:</strong> {new Date(user.createdAt).toLocaleDateString()}</p>
      <br/>
      <Link to="/users" className={styles.return}>Return to users list</Link>
    </div>
  )
}

export default UserDetails