import React, { useEffect, useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from './components/Navbar';
import styles from './RestaurantPage.module.css';

function RestaurantPage() {
  const { id } = useParams();
  const storageKey = `cart_${id}`;

  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [cart, setCart] = useState(() => {
    const saved = localStorage.getItem(storageKey);
    return saved ? JSON.parse(saved) : [];
  });

  useEffect(() => {
    fetch(`http://localhost:8080/api/restaurants/${id}`)
      .then(res => res.json())
      .then(data => setRestaurant(data));
  }, [id]);

  useEffect(() => {
    fetch(`http://localhost:8080/api/restaurants/${id}/menu`)
      .then(res => res.json())
      .then(data => {
        const withCategory = data.map(item => ({
          ...item,
          category: item.foodCategory?.name || 'Uncategorized'
        }));
        setMenu(withCategory);
      });
  }, [id]);

  useEffect(() => {
    localStorage.setItem(storageKey, JSON.stringify(cart));
  }, [cart, storageKey]);

  const addToCart = (item) => {
    setCart(prev => {
      const exist = prev.find(ci => ci.item.id === item.id);
      if (exist) {
        return prev.map(ci =>
          ci.item.id === item.id
            ? { ...ci, quantity: ci.quantity + 1 }
            : ci
        );
      }
      return [...prev, { item, quantity: 1 }];
    });
  };

  const sectionRefs = useRef({});

  const grouped = menu.reduce((acc, item) => {
    acc[item.category] = acc[item.category] || [];
    acc[item.category].push(item);
    return acc;
  }, {});

  // zmienia ilość lub usuwa pozycję jeśli qty ≤ 0
  const updateCartItem = (itemId, delta) => {
    setCart(prev =>
      prev.flatMap(ci => {
        if (ci.item.id !== itemId) return ci;
        const newQty = ci.quantity + delta;
        return newQty > 0 ? { ...ci, quantity: newQty } : [];
      })
    );
  };

  const removeFromCart = itemId => {
    setCart(prev => prev.filter(ci => ci.item.id !== itemId));
  };

  return (
    <>
      <Navbar />
      <div className={styles.restaurantPage}>
        <div className={styles.left}>
          <div className={styles.hero}>
            <img src="/restaurant_placeholder.jpg" alt="restaurant" />
            <div className={styles.heroOverlay}>
              <div className={styles.heroDetails}>
                <h2>{restaurant?.name || 'Loading...'}</h2>
                <div className={styles.stats}>
                  <span>⭐ {restaurant?.rating ?? 'No rating'}</span>
                  <span>🪑 Free</span>
                  <span>💲 Expensive</span>
                </div>
                <p>{restaurant?.description}</p>
              </div>
            </div>
          </div>

          {/* TABS */}
          <div className={styles.tabs}>
            {Object.keys(grouped).map(cat => (
              <button
                key={cat}
                onClick={() => {
                  const el = sectionRefs.current[cat];
                  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }}
              >
                {cat}
              </button>
            ))}
          </div>

          {/* MENU */}
          <div className={styles.menuSection}>
            {Object.entries(grouped).map(([cat, items]) => (
              <div key={cat} ref={el => (sectionRefs.current[cat] = el)} className={styles.categoryBlock}>
                <h3>{cat} ({items.length})</h3>
                <div className={styles.menuList}>
                  {items.map(item => (
                    <div key={item.id} className={styles.menuItem}>
                      <img src="/food_placeholder.jpg" alt={item.name} />
                      <div>
                        <h4>{item.name}</h4>
                        <p>{item.description}</p>
                        <span>{Number(item.price).toFixed(2)} zł</span>
                      </div>
                      <button className={styles.addBtn} onClick={() => addToCart(item)}>＋</button>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className={styles.right}>
          <h3>Cart</h3>
          {cart.length === 0 ? (
            <div className={styles.emptyCart}>
              <span className={styles.cartIcon}>👜</span>
              <p>Your cart is empty :(</p>
            </div>
          ) : (
            <div className={styles.cartList}>
              {cart.map(ci => (
                <div key={ci.item.id} className={styles.cartItem}>
                  <span className={styles.cartName}>{ci.item.name}</span>
                  <span className={styles.cartPrice}>{Number(ci.item.price).toFixed(2)} zł</span>
                  <div className={styles.cartControls}>
                    <button
                      onClick={() => updateCartItem(ci.item.id, -1)}
                      className={styles.qtyBtn}
                    >
                      –
                    </button>
                    <span className={styles.qty}>{ci.quantity}</span>
                    <button
                      onClick={() => updateCartItem(ci.item.id, +1)}
                      className={styles.qtyBtn}
                    >
                      +
                    </button>
                  </div>
                  <button
                    onClick={() => removeFromCart(ci.item.id)}
                    className={styles.removeBtn}
                  >
                    Remove
                  </button>
                </div>
              ))}
            </div>
          )}

          {cart.length > 0 && (
            <>
              <div className={styles.cartSummary}>
                <span>Total:</span>
                <span>{cart
                  .reduce((sum, ci) => sum + ci.quantity * ci.item.price, 0)
                  .toFixed(2)} zł</span>
              </div>
              <button
                className={styles.checkoutBtn}
                onClick={() => alert('Checkout!')}
              >
                Checkout
              </button>
            </>
          )}
        </div>
      </div>
    </>
  );
}

export default RestaurantPage;
