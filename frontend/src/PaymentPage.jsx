import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './PaymentPage.module.css';

export default function PaymentPage() {
  useEffect(() => {
    document.title = 'Payment ∙ QuickPlate'
  }, [])
  const navigate = useNavigate();
  const { cart, dateTime, guests, orderId } = useLocation().state || {};
  const [method, setMethod] = useState('card');
  const [cardNumber, setCardNumber] = useState('');

  const total = cart?.reduce((sum, ci) => sum + ci.quantity * ci.item.price, 0) || 0;

  const handlePay = async e => {
    e.preventDefault();

    const token = localStorage.getItem('token');
    if (!token) {
      return navigate('/login', { state: { redirectTo: '/payment', cart, dateTime, guests, orderId } });
    }

    const res = await fetch(`http://localhost:8080/api/orders/${orderId}/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ status: 'paid' })
    });

    if (!res.ok) {
      const err = await res.text();
      return alert('Błąd zmiany statusu: ' + err);
    }

    alert(`Płatność ${method} za ${total.toFixed(2)} zł`);
    navigate('/', { replace: true });
  };

  return (
    <>
      <Navbar />
      <div className={styles.paymentPage}>
        <div className={styles.paymentWrapper}>

          <section className={styles.orderSummary}>
            <h3>Podsumowanie zamówienia</h3>
            {cart?.map(ci => (
              <div key={ci.item.id} className={styles.lineItem}>
                <span>{ci.item.name} ×{ci.quantity}</span>
                <span>{(ci.quantity * ci.item.price).toFixed(2)} zł</span>
              </div>
            ))}
            <div className={styles.lineTotal}>
              <span>Razem:</span>
              <span>{total.toFixed(2)} zł</span>
            </div>
          </section>

          <section className={styles.reservationInfo}>
            <h3>Rezerwacja</h3>
            <p><strong>Data i godzina:</strong> {dateTime.replace('T', ' ')}</p>
            <p><strong>Liczba gości:</strong> {guests}</p>
          </section>

          <form className={styles.paymentForm} onSubmit={handlePay}>
            <h3>Wybierz formę płatności</h3>
            <div className={styles.inputGroup}>
              <label>
                <input
                  type="radio"
                  name="method"
                  value="card"
                  checked={method === 'card'}
                  onChange={e => setMethod(e.target.value)}
                /> Karta płatnicza
              </label>
              <label>
                <input
                  type="radio"
                  name="method"
                  value="cash"
                  checked={method === 'cash'}
                  onChange={e => setMethod(e.target.value)}
                /> Gotówka
              </label>
            </div>
            {method === 'card' && (
              <div className={styles.inputGroup}>
                <label htmlFor="card">Numer karty</label>
                <input
                  id="card"
                  type="text"
                  placeholder="XXXX XXXX XXXX XXXX"
                  value={cardNumber}
                  onChange={e => setCardNumber(e.target.value)}
                  required
                />
              </div>
            )}
            <button type="submit" className={styles.paymentBtn}>
              Zapłać {total.toFixed(2)} zł
            </button>
          </form>

        </div>
      </div>
    </>
  );
}