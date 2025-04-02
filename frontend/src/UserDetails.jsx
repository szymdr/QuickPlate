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
                console.error('Błąd pobierania szczegółów użytkownika:', err)
                setLoading(false)
            })
    }, [id])

    if (loading) {
        return <p>Ładowanie szczegółów użytkownika...</p>
    }

    if (!user) {
        return <p>Użytkownik nie znaleziony.</p>
    }

    return (
        <div className="user-details">
            {/* Przyjmujemy, że serializacja obejmuje pole "name" (getName()) */}
            <h2>{user.name}</h2>
            <p><strong>Email:</strong> {user.email}</p>
            {/* Inne pola możesz dostosować do danych z bazy */}
            <Link to="/">Powrót do listy użytkowników</Link>
        </div>
    )
}

export default UserDetails