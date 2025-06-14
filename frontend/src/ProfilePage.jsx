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
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    const token = localStorage.getItem('token');
    fetch(`http://localhost:8080/api/users/${user.id}`, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
      .then(res => {
        if (!res.ok) throw new Error('Network response was not ok');
        return res.json();
      })
      .then(data => {
        setUser(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));

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
              orderId: order?.id,
              status:    order?.status    ?? r.status,
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

    const payload = {
      firstName: form.firstName,
      lastName:  form.lastName,
      email:     form.email,
      phone:     form.phone,
      accountType: { id: user.accountType.id }
    };

    try {
      const res = await fetch(
        `http://localhost:8080/api/users/${user.id}`,
        {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            'Accept':       'application/json',
            Authorization:  `Bearer ${token}`
          },
          body: JSON.stringify(payload)
        }
      );

      if (!res.ok) {
        const errText = await res.text();
        console.error('Profile update failed:', errText);
        return alert('Błąd aktualizacji profilu: ' + errText);
      }

      const updated = await res.json();
      setUser(updated);
      setForm({
        firstName: updated.firstName || '',
        lastName:  updated.lastName  || '',
        email:     updated.email     || '',
        phone:     updated.phone     || ''
      });
      localStorage.setItem('user', JSON.stringify(updated));
      alert('Dane zaktualizowane');

    } catch (err) {
      console.error('Unexpected error updating profile:', err);
      alert('Nieoczekiwany błąd podczas aktualizacji profilu');
    }
  };

  const cancelReservation = async r => {
    if (!window.confirm('Anulować rezerwację?')) return;
    if (!r.orderId) return alert('Brak zamówienia do anulowania');
    const res = await fetch(
      `http://localhost:8080/api/orders/${r.orderId}`,
      {
        method: 'PATCH',
        headers: { 'Content-Type':'application/json', Authorization:`Bearer ${token}` },
        body: JSON.stringify({ status: 'CANCELLED' })
      }
    );
    if (res.ok) {
      setReservations(prev =>
        prev.map(x => x.id === r.id ? { ...x, status: 'CANCELLED' } : x)
      );
    } else {
      alert('Nie udało się anulować');
    }
  };

  if (loading) return <div>Loading...</div>;

  return (
    <>
      <Navbar/>
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
            <li
              key={r.id}
              className={styles.resItem}
              onClick={() => toggle(r.id)}
            >
              <strong>{r.restaurant?.name}</strong><br/>
              Data: {r.reservationTime.replace('T',' ')}<br/>
              Stolik: {r.tableNumber}<br/>
              Status: {r.status}<br/>
              Cena: {r.totalPrice != null
                ? `${r.totalPrice.toFixed(2)} zł` : '–'}<br/>

              {(r.status==='pending' || r.status==='paid' || r.status==='accepted') && (
                <button
                  className={styles.cancelBtn}
                  onClick={e => {
                    e.stopPropagation();
                    cancelReservation(r);
                  }}
                >
                  Anuluj rezerwację
                </button>
              )}

              {expandedId === r.id && r.orderItems && (
                <div className={styles.orderDetails}>
                  <h4>Pozycje zamówienia</h4>
                  <ul>
                    {r.orderItems.map(oi => (
                      <li key={oi.id}>
                        {oi.menuItem.name} ×{oi.quantity} ={' '}
                        {(oi.price * oi.quantity).toFixed(2)} zł
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