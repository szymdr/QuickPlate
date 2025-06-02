import React, { useState, useRef, useEffect } from 'react';
import { useLocation, useParams, useNavigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './ReservationPage.module.css';

export default function ReservationPage() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const reservedCart = location.state?.cart || [];

  const today = new Date();
  const todayStr = today.toISOString().slice(0, 10);

  const getNextSlot = () => {
    const dt = new Date();
    dt.setSeconds(0, 0);
    const m = dt.getMinutes();
    const rem = m % 15;
    if (rem) dt.setMinutes(m + (15 - rem));
    return dt.toTimeString().slice(0, 5); // "HH:MM"
  };

  const [date, setDate] = useState(todayStr);
  const [time, setTime] = useState(getNextSlot());
  const [guests, setGuests] = useState(1);
  const [success, setSuccess] = useState(false);
  const [timeOpen, setTimeOpen] = useState(false);
  const listRef = useRef();

  // zamykanie dropdownu click poza
  useEffect(() => {
    const onClick = e => {
      if (timeOpen && listRef.current && !listRef.current.contains(e.target)) {
        setTimeOpen(false);
      }
    };
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, [timeOpen]);

  const timeSlots = [];
  for (let h = 0; h < 24; h++) {
    for (let m = 0; m < 60; m += 15) {
      const hh = String(h).padStart(2, '0');
      const mm = String(m).padStart(2, '0');
      timeSlots.push(`${hh}:${mm}`);
    }
  }

  const handleSubmit = e => {
    e.preventDefault();
    const dateTime = `${date}T${time}`;
    const cartState = { cart: reservedCart, dateTime, guests };

    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login', { state: { redirectTo: '/payment', ...cartState } });
      return;
    }

    fetch('/api/reservations', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({ time: dateTime, guests })
    })
      .then(res => {
        if (!res.ok) throw new Error('Reservation failed');
        return res.json();
      })
      .then(() => {
        navigate('/payment', { state: cartState });
      })
      .catch(err => {
        console.error(err);
        alert('Błąd rezerwacji');
      });
  };

  return (
    <>
      <Navbar />
      <div className={styles.reservationPage}>
        <div className={styles.reservationWrapper}>
          {reservedCart.length > 0 && (
            <aside className={styles.cartSidebar}>
              <h3>Twoje zamówienie</h3>
              {reservedCart.map(ci => (
                <div key={ci.item.id} className={styles.cartItem}>
                  <span>{ci.item.name} ×{ci.quantity}</span>
                  <span>{(ci.quantity * ci.item.price).toFixed(2)} zł</span>
                </div>
              ))}
              <div className={styles.cartSummary}>
                <span>Łącznie:</span>
                <span>
                  {reservedCart
                    .reduce((sum, ci) => sum + ci.quantity * ci.item.price, 0)
                    .toFixed(2)} zł
                </span>
              </div>
            </aside>
          )}
          <div className={styles.formContainer}>
            <div className={styles.formBox}>
              <h2>Rezerwacja stolika</h2>
              <form onSubmit={handleSubmit}>
                <div className={styles.inputGroup}>
                  <label htmlFor="date">Data</label>
                  <input
                    id="date"
                    type="date"
                    min={todayStr}
                    value={date}
                    onChange={e => setDate(e.target.value)}
                    required
                  />
                </div>
                <div className={styles.inputGroup}>
                  <label htmlFor="time">Godzina</label>
                  <div className={styles.timePickerContainer} ref={listRef}>
                    <input
                      id="time"
                      readOnly
                      className={styles.timeInput}
                      value={time}
                      onClick={() => setTimeOpen(o => !o)}
                    />
                    {timeOpen && (
                      <ul className={styles.timeList}>
                        {timeSlots
                          .filter(slot => date !== todayStr || slot >= getNextSlot())
                          .map(slot => (
                            <li
                              key={slot}
                              className={styles.timeOption}
                              onClick={() => { setTime(slot); setTimeOpen(false); }}
                            >
                              {slot}
                            </li>
                          ))}
                      </ul>
                    )}
                  </div>
                </div>
                <div className={styles.inputGroup}>
                  <label htmlFor="guests">Liczba gości</label>
                  <input
                    id="guests"
                    type="number"
                    min="1"
                    value={guests}
                    onChange={e => setGuests(e.target.value)}
                    required
                  />
                </div>
                <button type="submit" className={styles.submitBtn}>
                  Zarezerwuj i zapłać
                </button>
              </form>
              {success && <p className={styles.success}>Rezerwacja pomyślna!</p>}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
