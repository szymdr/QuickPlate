import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import Navbar from './components/Navbar'
import styles from './HomePage.module.css'

function HomePage() {
    useEffect(() => {
        document.title = 'Home ∙ QuickPlate'
    }, [])

    const [search, setSearch] = useState('')
    const [restaurants, setRestaurants] = useState([])

    useEffect(() => {
        fetch('http://localhost:8080/api/restaurants')
            .then(res => res.json())
            .then(data => setRestaurants(data))
    }, [])

    const filtered = restaurants.filter(r =>
        r.name.toLowerCase().includes(search.toLowerCase())
    )

    return (
        <>
          <Navbar />
          <div className={styles.homePage}>
            <div className={styles.pageContent}>
              <h1>Wyszukaj restaurację</h1>
              <input
                  className={styles.searchInput}
                  type="text"
                  placeholder="Wpisz nazwę restauracji..."
                  value={search}
                  onChange={e => setSearch(e.target.value)}
              />
              <div className={styles.grid}>
                  {filtered.map(r => (
                      <Link key={r.id} to={`/restaurants/${r.id}`} className={styles.card}>
                          <h2>{r.name}</h2>
                          <p>{r.address}</p>
                          <p>{r.openingHours}</p>
                      </Link>
                  ))}
              </div>
            </div>
          </div>
        </>
    )
}

export default HomePage
