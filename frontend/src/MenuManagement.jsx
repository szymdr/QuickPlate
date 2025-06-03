import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './MenuManagement.module.css';

export default function MenuManagement() {
  const { id } = useParams(); // restaurant id
  const token = localStorage.getItem('token');
  const [menu, setMenu] = useState([]);

  useEffect(() => {
    fetch(`http://localhost:8080/api/menu-items/restaurant/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(r => r.json())
      .then(setMenu);
  }, [id, token]);

  // group items by category name
  const grouped = menu.reduce((acc, mi) => {
    const cat = mi.category?.name || 'Bez kategorii';
    acc[cat] = acc[cat] || [];
    acc[cat].push(mi);
    return acc;
  }, {});

  return (
    <>
      <Navbar/>
      <div className={styles.menuPage}>
        <h2>Menu restauracji</h2>
        <Link to={`/owner/restaurants/${id}/menu/new`} className={styles.addLink}>
          Dodaj pozycję
        </Link>

        {Object.entries(grouped).map(([catName, items]) => (
          <section key={catName} className={styles.categorySection}>
            <h3 className={styles.categoryHeader}>{catName}</h3>
            <ul className={styles.menuList}>
              {items.map(mi => (
                <li key={mi.id} className={styles.menuItem}>
                  <span>{mi.name} – {mi.price.toFixed(2)} zł</span>
                  <Link to={`/owner/restaurants/${id}/menu/${mi.id}/edit`}>
                    Edytuj
                  </Link>
                </li>
              ))}
            </ul>
          </section>
        ))}
      </div>
    </>
  );
}