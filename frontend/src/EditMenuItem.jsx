import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './EditMenuItem.module.css';

export default function EditMenuItem() {
  const { rid, mid } = useParams();
  const nav = useNavigate();
  const token = localStorage.getItem('token');

  const [form, setForm] = useState({
    name: '',
    description: '',
    price: 0,
    restaurant: { id: rid },
    category: null
  });
  const [categories, setCategories] = useState([]);
  const [selectedCat, setSelectedCat] = useState('');
  const [newCatName, setNewCatName] = useState('');

  // 1) fetch categories first
  useEffect(() => {
    fetch('http://localhost:8080/api/food-categories', {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(setCategories)
      .catch(console.error);
  }, [token]);

  // 2) fetch menu‐item only when editing an existing one
  useEffect(() => {
    // mid will be undefined for “new” route (or equal "new")
    if (!mid || mid === 'new') return;

    fetch(`http://localhost:8080/api/menu-items/${mid}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => {
        if (!res.ok) throw new Error('Nie znaleziono pozycji');
        return res.json();
      })
      .then(mi => {
        setForm(mi);
        setSelectedCat(mi.category?.id?.toString() ?? '');
      })
      .catch(err => {
        console.error(err);
        alert('Błąd ładowania danych: ' + err.message);
      });
  }, [mid, token]);

  const save = async e => {
    e.preventDefault();
    if (form.price <= 0) return alert('Cena musi być większa od zera');

    let categoryId = selectedCat;
    if (selectedCat === 'new') {
      const cRes = await fetch('http://localhost:8080/api/food-categories', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({ name: newCatName })
      });
      if (!cRes.ok) {
        const err = await cRes.text();
        return alert('Błąd dodawania kategorii: ' + err);
      }
      const created = await cRes.json();
      categoryId = created.id;
    }

    const payload = {
      ...form,
      restaurant: { id: rid },
      category: { id: categoryId }
    };
    const url =
      mid === 'new'
        ? 'http://localhost:8080/api/menu-items'
        : `http://localhost:8080/api/menu-items/${mid}`;
    const method = mid === 'new' ? 'POST' : 'PUT';

    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });
    if (res.ok) nav(`/owner/restaurants/${rid}/menu`);
    else alert(await res.text());
  };

  return (
    <>
      <Navbar />
      <div className={styles.editor}>
        <h2>{mid === 'new' ? 'Nowa' : 'Edytuj'} pozycję</h2>
        <form onSubmit={save}>
          <label>
            Nazwa
            <input
              value={form.name}
              onChange={e => setForm({ ...form, name: e.target.value })}
              required
            />
          </label>

          <label>
            Opis
            <textarea
              value={form.description}
              onChange={e =>
                setForm({ ...form, description: e.target.value })
              }
            />
          </label>

          <label>
            Cena
            <input
              type="number"
              min="0.01"
              step="0.01"
              value={form.price}
              onChange={e =>
                setForm({ ...form, price: +e.target.value })
              }
              required
            />
          </label>

          <label>
            Kategoria
            <select
              value={selectedCat}
              onChange={e => setSelectedCat(e.target.value)}
              required
            >
              {/* zawsze pokazujemy placeholder – ale on nie zostanie wybrany, gdy selectedCat ≠ '' */}
              <option value="" disabled>
                Wybierz kategorię
              </option>
              {categories.map(c => (
                <option key={c.id} value={c.id.toString()}>
                  {c.name}
                </option>
              ))}
              <option value="new">+ Nowa kategoria</option>
            </select>
          </label>

          {selectedCat === 'new' && (
            <label>
              Nowa kategoria
              <input
                type="text"
                value={newCatName}
                onChange={e => setNewCatName(e.target.value)}
                required
              />
            </label>
          )}

          <button type="submit">Zapisz</button>
        </form>
      </div>
    </>
  );
}