import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './AdminDashboard.module.css';

export default function AdminDashboard() {
  const [restaurants, setRestaurants]   = useState([]);
  const [reservations, setReservations] = useState([]);
  const [users, setUsers]               = useState([]);
  const [editingCell, setEditingCell]   = useState({ type:'', id:null, field:'' });
  const [editValue, setEditValue]       = useState('');
  const token   = localStorage.getItem('token');
  const navigate= useNavigate();
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  };

  useEffect(() => {
    if (!token) return navigate('/login', { replace: true });
    fetchAll();
  }, [token]);

  const fetchAll = () => {
    fetch('http://localhost:8080/api/admin/restaurants', { headers })
      .then(r=>r.ok? r.json():[]).then(setRestaurants);
    fetch('http://localhost:8080/api/admin/reservations', { headers })
      .then(r=>r.ok? r.json():[]).then(setReservations);
    fetch('http://localhost:8080/api/admin/users', { headers })
      .then(r=>r.ok? r.json():[]).then(setUsers);
  };

  const startEdit = (type, id, field, value) => {
    setEditingCell({ type, id, field });
    setEditValue(value);
  };

  const commitEdit = async () => {
    const { type, id, field } = editingCell;
    let url, body = { [field]: editValue };
    if (type==='restaurant')    url = `http://localhost:8080/api/restaurants/${id}`;
    else if (type==='reservation') url = `http://localhost:8080/api/reservations/${id}`;
    else if (type==='user')       url = `http://localhost:8080/api/users/${id}`;
    await fetch(url, { method:'PATCH', headers, body: JSON.stringify(body) });
    setEditingCell({ type:'', id:null, field:'' });
    fetchAll();
  };

  const onKey = e => { if (e.key==='Enter') commitEdit(); };

  return (
    <>
      <Navbar />
      <div className={styles.dashboard}>
        <h2>Admin Panel</h2>

        {/* Restaurants */}
        <section className={styles.adminSection}>
          <h3>Restaurants ({restaurants.length})</h3>
          <table className={styles.dataTable}>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Address</th>
                <th>Owner ID</th>
              </tr>
            </thead>
            <tbody>
              {restaurants.map(r => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td onClick={()=>startEdit('restaurant',r.id,'name',r.name)}>
                    {editingCell.type==='restaurant' && editingCell.id===r.id && editingCell.field==='name'
                      ? <input
                          autoFocus
                          value={editValue}
                          onChange={e=>setEditValue(e.target.value)}
                          onBlur={commitEdit}
                          onKeyDown={onKey}
                        />
                      : r.name
                    }
                  </td>
                  <td onClick={()=>startEdit('restaurant',r.id,'address',r.address)}>
                    {editingCell.type==='restaurant' && editingCell.id===r.id && editingCell.field==='address'
                      ? <input
                          autoFocus
                          value={editValue}
                          onChange={e=>setEditValue(e.target.value)}
                          onBlur={commitEdit}
                          onKeyDown={onKey}
                        />
                      : r.address
                    }
                  </td>
                  <td>{r.owner?.id}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* Reservations */}
        <section className={styles.adminSection}>
          <h3>Reservations ({reservations.length})</h3>
          <table className={styles.dataTable}>
            <thead>
              <tr>
                <th>ID</th>
                <th>User</th>
                <th>Restaurant</th>
                <th>Table</th>
                <th>Time</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {reservations.map(r => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.user?.firstName} {r.user?.lastName}</td>
                  <td>{r.restaurant?.name}</td>
                  <td onClick={()=>startEdit('reservation',r.id,'tableNumber',r.tableNumber)}>
                    {editingCell.type==='reservation' && editingCell.id===r.id && editingCell.field==='tableNumber'
                      ? <input
                          type="number"
                          autoFocus
                          value={editValue}
                          onChange={e=>setEditValue(e.target.value)}
                          onBlur={commitEdit}
                          onKeyDown={onKey}
                        />
                      : r.tableNumber
                    }
                  </td>
                  <td onClick={()=>startEdit('reservation',r.id,'reservationTime',r.reservationTime)}>
                    {editingCell.type==='reservation' && editingCell.id===r.id && editingCell.field==='reservationTime'
                      ? <input
                          autoFocus
                          value={editValue}
                          onChange={e=>setEditValue(e.target.value)}
                          onBlur={commitEdit}
                          onKeyDown={onKey}
                        />
                      : new Date(r.reservationTime).toLocaleString()
                    }
                  </td>
                  <td>{r.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* Users */}
        <section className={styles.adminSection}>
          <h3>Users ({users.length})</h3>
          <table className={styles.dataTable}>
            <thead>
              <tr>
                <th>ID</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Email</th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td onClick={() => startEdit('user', u.id, 'firstName', u.firstName)}>
                    {editingCell.type==='user' && editingCell.id===u.id && editingCell.field==='firstName'
                      ? <input autoFocus value={editValue} onChange={e=>setEditValue(e.target.value)} onBlur={commitEdit} onKeyDown={onKey}/>
                      : u.firstName
                    }
                  </td>
                  <td onClick={() => startEdit('user', u.id, 'lastName', u.lastName)}>
                    {editingCell.type==='user' && editingCell.id===u.id && editingCell.field==='lastName'
                      ? <input autoFocus value={editValue} onChange={e=>setEditValue(e.target.value)} onBlur={commitEdit} onKeyDown={onKey}/>
                      : u.lastName
                    }
                  </td>
                  <td onClick={() => startEdit('user', u.id, 'email', u.email)}>
                    {editingCell.type==='user' && editingCell.id===u.id && editingCell.field==='email'
                      ? <input type="email" autoFocus value={editValue} onChange={e=>setEditValue(e.target.value)} onBlur={commitEdit} onKeyDown={onKey}/>
                      : u.email
                    }
                  </td>
                  <td onClick={() => startEdit('user', u.id, 'role', u.accountType?.name || u.role)}>
                    {editingCell.type==='user' && editingCell.id===u.id && editingCell.field==='role'
                      ? (
                        <select autoFocus value={editValue} onChange={e=>setEditValue(e.target.value)} onBlur={commitEdit} onKeyDown={onKey}>
                          <option value="admin">Admin</option>
                          <option value="user">User</option>
                          <option value="restaurant_owner">Restaurant Owner</option>
                        </select>
                      )
                      : u.accountType?.name || u.role
                    }
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </>
  );
}