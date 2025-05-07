import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'

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
            .catch(err => {
                console.error('Error while fetching user:', err)
                setLoading(false)
            })
    }, [id])
  
    const formatPhoneNumber = phone => {
        return phone.match(/.{1,3}/g)?.join(' ') || phone;
    }

    if (loading) {
        return <p>Loading user details...</p>
    }

    if (!user) {
        return <p>User not found.</p>
    }

    return (
        <div className="user-details">
            <h2>{user.name}</h2>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Phone number:</strong> {formatPhoneNumber(user.phone)}</p>
            <p><strong>Registration date:</strong> {new Date(user.createdAt).toLocaleDateString()}</p>
            <br />
            <Link to="/users" className='return'>Return to users list</Link>
        </div>
    )
}

export default UserDetails