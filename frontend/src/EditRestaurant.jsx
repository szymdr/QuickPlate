import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './EditRestaurant.module.css';

export default function EditRestaurant() {
  const { id } = useParams();
  const nav = useNavigate();
  const token = localStorage.getItem('token');
  const [form, setForm] = useState({
    name: '',
    address: '',
    phone: '',
    email: '',
    openingHours: ''
  });

  useEffect(() => {
    if (id !== 'new') {
      fetch(`http://localhost:8080/api/restaurants/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(r => r.json())
        .then(data => setForm(data));
    }
  }, [id, token]);

  const save = async e => {
    e.preventDefault();
    const method = id === 'new' ? 'POST' : 'PUT';
    const url = id === 'new'
      ? 'http://localhost:8080/api/restaurants'
      : `http://localhost:8080/api/restaurants/${id}`;
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(form)
    });
    if (res.ok) nav('/owner');
    else alert(await res.text());
  };

  return (
    <>
      <Navbar/>
      <div className={styles.editor}>
        <h2>{id === 'new' ? 'Nowa' : 'Edytuj'} restaurację</h2>
        <form onSubmit={save}>
          {['Name','Address','Phone','Email','Opening hours'].map(f => (
            <label key={f}>
              {f.charAt(0).toUpperCase() + f.slice(1)}
              <input
                name={f}
                value={form[f] || ''}
                onChange={e => setForm({ ...form, [f]: e.target.value })}
                required
              />
            </label>
          ))}
          <button type="submit">Zapisz</button>
        </form>
      </div>
    </>
  );
}