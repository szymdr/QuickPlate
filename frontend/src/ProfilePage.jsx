import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './ProfilePage.module.css';

export default function ProfilePage() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const storedUser = localStorage.getItem('user');
  const initialUser = storedUser ? JSON.parse(storedUser) : null;

  const [user, setUser] = useState(initialUser);
  const [reservations, setReservations] = useState([]);
  const [form, setForm] = useState({
    firstName: user?.firstName || '',
    lastName:  user?.lastName  || '',
    email:     user?.email     || '',
    phone:     user?.phone     || ''
  });
  const [expandedId, setExpandedId] = useState(null);

  useEffect(() => {
    if (!token || !user?.id) {
      navigate('/login', { state: { redirectTo: '/profile' } });
      return;
    }

    fetch(`http://localhost:8080/api/reservations/user/${user.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => {
        if (!res.ok) throw new Error('Could not load reservations');
        return res.json();
      })
      .then(async data => {
        const withOrders = await Promise.all(
          data.map(async r => {
            const orRes = await fetch(
              `http://localhost:8080/api/orders/reservation/${r.id}`,
              { headers: { Authorization: `Bearer ${token}` } }
            );
            const order = orRes.ok ? await orRes.json() : null;
            return {
              ...r,
              status: order?.status ?? r.status,
              totalPrice: order?.totalPrice,
              orderItems: order?.items
            };
          })
        );
        withOrders.sort(
          (a, b) => new Date(b.reservationTime) - new Date(a.reservationTime)
        );
        setReservations(withOrders);
      })
      .catch(err => {
        console.error(err);
        setReservations([]);
      });
  }, [navigate, token, user]);

  const toggle = id => {
    setExpandedId(prev => (prev === id ? null : id));
  };

  const handleChange = e => {
    if (e.target.name === "phone") {
      let numericValue = e.target.value.replace(/\D/g, "");
      numericValue = numericValue.substring(0, 9);
      
      setForm({ ...form, [e.target.name]: numericValue });
    } else {
      setForm({ ...form, [e.target.name]: e.target.value });
    }
  };

  const formatPhoneNumber = (phone) => {
    return phone.match(/.{1,3}/g)?.join(" ") || "";
  }

  const handleSave = async e => {
    e.preventDefault();
    const res = await fetch(
      `http://localhost:8080/api/users/${user.id}`,
      {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(form)
      }
    );
    if (!res.ok) {
      const err = await res.text();
      return alert('Błąd aktualizacji profilu: ' + err);
    }
    const updated = await res.json();
    localStorage.setItem('user', JSON.stringify(updated));
    setUser(updated);
    alert('Dane zaktualizowane');
  };

  return (
    <>
      <Navbar />
      <div className={styles.profilePage}>
        <h2>Mój profil</h2>
        <form className={styles.profileForm} onSubmit={handleSave}>
          <label>
            Imię
            <input
              name="firstName"
              value={form.firstName}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            Nazwisko
            <input
              name="lastName"
              value={form.lastName}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            Email
            <input
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            Telefon
            <input
              name="phone"
              type="tel"
              value={formatPhoneNumber(form.phone)}
              onChange={handleChange}
            />
          </label>
          <button type="submit">Zapisz zmiany</button>
        </form>

        <h3>Moje rezerwacje ({reservations.length})</h3>
        <ul className={styles.resList}>
          {reservations.map(r => (
            <li key={r.id} onClick={() => toggle(r.id)} className={styles.resItem}>
              <strong>{r.restaurant?.name || '–'}</strong><br/>
              Data: {r.reservationTime.replace('T',' ')}<br/>
              Stolik: {r.tableNumber}<br/>
              Status: {r.status}<br/>
              Cena: {r.totalPrice != null
                ? `${r.totalPrice.toFixed(2)} zł`
                : '–'}<br/>

              {expandedId === r.id && r.orderItems && (
                <div className={styles.orderDetails}>
                  <h4>Pozycje zamówienia</h4>
                  <ul>
                    {r.orderItems.map(oi => (
                      <li key={oi.id}>
                        {oi.menuItem.name} ×{oi.quantity} = {(
                          oi.price * oi.quantity
                        ).toFixed(2)} zł
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </li>
          ))}
          {reservations.length === 0 && <p>Brak rezerwacji</p>}
        </ul>
      </div>
    </>
  );
}