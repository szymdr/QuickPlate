import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './OwnerDashboard.module.css';

export default function OwnerDashboard() {
  const [rests, setRests] = useState([]);
  const [expanded, setExpanded] = useState(null);
  const [resByRest, setResByRest] = useState({});
  const [statusSeq, setStatusSeq] = useState([]);
  const [expandedId, setExpandedId] = useState(null);
  const [openStatus, setOpenStatus] = useState({});
  const token   = localStorage.getItem('token');
  const ownerId = JSON.parse(localStorage.getItem('user')).id;
  const nav     = useNavigate();

  useEffect(() => {
    if (!token) return nav('/login', { replace: true });
    fetch(`http://localhost:8080/api/restaurants/owner/${ownerId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(r => r.ok ? r.json() : [])
      .then(data => setRests(data))
      .catch(() => setRests([]));
  }, [ownerId, token, nav]);

  useEffect(() => {
    fetch('http://localhost:8080/api/orders/statuses', {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(r => r.ok ? r.json() : [])
      .then(setStatusSeq)
      .catch(() => setStatusSeq([]));
  }, [token]);

  const getNextStatus = current => {
    const i = statusSeq.indexOf(current);
    return i >= 0 && i < statusSeq.length - 1
      ? statusSeq[i + 1]
      : null;
  };

  const handleStatusChange = async (orderId, restId, status) => {
    const res = await fetch(
      `http://localhost:8080/api/orders/${orderId}/status`,
      {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({ status })
      }
    );
    if (!res.ok) {
      const txt = await res.text();
      return alert('Błąd zmiany statusu: ' + txt);
    }
    const updated = await res.json();  // { id, status }
    setResByRest(prev => {
      const updatedList = prev[restId].map(item =>
        item.orderId === orderId
          ? { ...item, status: updated.status }
          : item
      );
      return { ...prev, [restId]: updatedList };
    });
  };

  const toggle = id => {
    const opening = expanded !== id;
    setExpanded(opening ? id : null);
    if (opening && !resByRest[id]) {
      fetch(`http://localhost:8080/api/reservations/restaurant/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(r => r.ok ? r.json() : [])
        .then(async list => {
          const withStatus = await Promise.all(
            list.map(async r => {
              const or = await fetch(
                `http://localhost:8080/api/orders/reservation/${r.id}`,
                { headers: { Authorization: `Bearer ${token}` } }
              );
              if (!or.ok) {
                return { ...r, orderId: null, status: null, total: null, orderItems: [] };
              }
              const order = await or.json();
              return {
                ...r,
                orderId: order.id,
                status: order.status,
                total: order.totalPrice,
                orderItems: order.items || []
              };
            })
          );
          setResByRest(prev => ({ ...prev, [id]: withStatus }));
        })
        .catch(err => {
          console.error('Could not load reservations:', err);
          setResByRest(prev => ({ ...prev, [id]: [] }));
        });
    }
  };

  const toggleRes = reservationId => {
    setExpandedId(prev => (prev === reservationId ? null : reservationId));
  };

  const toggleStatusGroup = (restId, status) => {
    setOpenStatus(prev => {
      const curr = new Set(prev[restId] || []);
      if (curr.has(status)) curr.delete(status);
      else curr.add(status);
      return { ...prev, [restId]: Array.from(curr) };
    });
  };

  return (
    <>
      <Navbar/>
      <div className={styles.dashboard}>
        <h2>Moje restauracje</h2>
        <Link to="/owner/restaurants/new" className={styles.addLink}>
          Dodaj restaurację
        </Link>
        <ul className={styles.resList}>
          {rests.map(r => {
            const items = resByRest[r.id] || [];
            const grouped = items.reduce((acc, v) => {
              acc[v.status] = acc[v.status] || [];
              acc[v.status].push(v);
              return acc;
            }, {});
            return (
              <li key={r.id}>
                <div
                  className={styles.restHeader}
                  onClick={() => toggle(r.id)}
                >
                  <span>{r.name}</span>
                  <button>
                    {expanded === r.id ? 'Ukryj' : 'Zobacz rezerwacje'}
                  </button>
                </div>

                {expanded === r.id && (
                  <div className={styles.resSection}>
                    {statusSeq.map(st => {
                      const arr = grouped[st] || [];
                      if (!arr.length) return null;
                      const isOpenGroup = (openStatus[r.id] || []).includes(st);
                      return (
                        <div key={st} className={styles.statusGroup}>
                          <div
                            className={styles.statusHeader}
                            onClick={() => toggleStatusGroup(r.id,st)}
                          >
                            <h4>Status: {st.charAt(0).toUpperCase()+st.slice(1)}</h4>
                            <button className={styles.statusToggleBtn}>
                              {isOpenGroup ? 'Ukryj' : `Pokaż (${arr.length})`}
                            </button>
                          </div>
                          {isOpenGroup && (
                            <ul>
                              {arr.map(rx => {
                                const isOpen = expandedId === rx.id;
                                const dt = new Date(rx.reservationTime);
                                const dateStr = dt.toLocaleDateString();
                                const timeStr = dt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
                                return (
                                  <li key={rx.id} className={styles.reservationItem}>
                                    <div className={styles.reservationInfo}>
                                      <span>Nr rezerwacji: {rx.id}</span>
                                      <span>Rezerwujący: {rx.user.firstName} {rx.user.lastName}</span>
                                    </div>
                                    <div className={styles.resDetails}>
                                      <div className={styles.resField}>
                                        <label>Data</label>
                                        <span>{dateStr}</span>
                                      </div>
                                      <div className={styles.resField}>
                                        <label>Godzina</label>
                                        <span>{timeStr}</span>
                                      </div>
                                      <div className={styles.resField}>
                                        <label>Stolik</label>
                                        <span>{rx.tableNumber}</span>
                                      </div>
                                      <div className={styles.resField}>
                                        <label>Cena</label>
                                        <span>
                                          {rx.total != null ? `${rx.total.toFixed(2)} zł` : '–'}
                                        </span>
                                      </div>
                                    </div>
                                    <div className={styles.resActions}>
                                      {rx.orderId ? (
                                        <select
                                          className={styles.statusSelect}
                                          value={rx.status}
                                          onChange={e =>
                                            handleStatusChange(rx.orderId, r.id, e.target.value)
                                          }
                                        >
                                          {statusSeq.map(s => (
                                            <option key={s} value={s}>
                                              {s.charAt(0).toUpperCase() + s.slice(1)}
                                            </option>
                                          ))}
                                        </select>
                                      ) : (
                                        <p className={styles.noOrderNotice}>
                                          Brak zamówienia do zmiany statusu
                                        </p>
                                      )}
                                      <button
                                        className={styles.toggleBtn}
                                        onClick={() => toggleRes(rx.id)}
                                      >
                                        {isOpen ? 'Ukryj dania' : 'Pokaż dania'}
                                      </button>
                                    </div>
                                    {isOpen && rx.orderItems.length > 0 && (
                                      <div className={styles.orderDetails}>
                                        <h4>Pozycje zamówienia</h4>
                                        <ul>
                                          {rx.orderItems.map(oi => (
                                            <li key={oi.id}>
                                              {oi.menuItem.name} ×{oi.quantity} ={' '}
                                              {(oi.price * oi.quantity).toFixed(2)} zł
                                            </li>
                                          ))}
                                        </ul>
                                      </div>
                                    )}
                                  </li>
                                );
                              })}
                            </ul>
                          )}
                        </div>
                      );
                    })}
                    {items.length === 0 && <p>Brak rezerwacji</p>}
                  </div>
                )}
              </li>
            );
          })}
        </ul>
      </div>
    </>
  );
}