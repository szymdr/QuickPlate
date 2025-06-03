import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './OwnerDashboard.module.css';

export default function OwnerDashboard() {
  const [rests, setRests] = useState([]);
  const token = localStorage.getItem('token');
  const ownerId = JSON.parse(localStorage.getItem('user')).id;
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) {
      // not logged in → force login
      return navigate('/login', { replace: true });
    }

    fetch(`http://localhost:8080/api/restaurants/owner/${ownerId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`    // ← must be present
      }
    })
      .then(res => {
        if (!res.ok) {
          console.error('Could not load restaurants:', res.status, res.statusText);
          return [];   // ensure returning an array
        }
        return res.json();
      })
      .then(data => setRests(Array.isArray(data) ? data : []))
      .catch(err => {
        console.error(err);
        setRests([]);
      });
  }, [ownerId, token, navigate]);

  return (
    <>
      <Navbar/>
      <div className={styles.dashboard}>
        <h2>Moje restauracje</h2>
        <Link to="/owner/restaurants/new" className={styles.addLink}>
          Dodaj restaurację
        </Link>
        <ul className={styles.resList}>
          {rests.map(r => (
            <li key={r.id}>
              <span>{r.name}</span>
              <div>
                <Link to={`/owner/restaurants/${r.id}/edit`}>Edytuj</Link>
                <Link to={`/owner/restaurants/${r.id}/menu`}>Menu</Link>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}