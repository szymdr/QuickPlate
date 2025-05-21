import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import Navbar from './components/Navbar'
import styles from './RestaurantPage.module.css'

function RestaurantPage() {
  const { id } = useParams()
  const storageKey = `cart_${id}`

  const [menu, setMenu] = useState([])
  const [cart, setCart] = useState(() => {
    const saved = localStorage.getItem(storageKey)
    return saved ? JSON.parse(saved) : []
  })

  useEffect(() => {
    fetch(`http://localhost:8080/api/restaurants/${id}/menu`)
      .then(res => res.json())
      .then(data => {
        const withCategory = data.map(item => ({
          ...item,
          category: item.foodCategory?.name || 'Uncategorized'
        }))
        setMenu(withCategory)
      })
  }, [id])

  // persist cart on changes
  useEffect(() => {
    localStorage.setItem(storageKey, JSON.stringify(cart))
  }, [cart, storageKey])

  // add or increment
  const addToCart = (item) => {
    setCart(prev => {
      const exist = prev.find(ci => ci.item.id === item.id)
      if (exist) {
        return prev.map(ci =>
          ci.item.id === item.id
            ? { ...ci, quantity: ci.quantity + 1 }
            : ci
        )
      }
      return [...prev, { item, quantity: 1 }]
    })
  }

  // change quantity by ±1, or remove if <=0
  const updateQuantity = (itemId, delta) => {
    setCart(prev => prev.flatMap(ci => {
      if (ci.item.id === itemId) {
        const q = ci.quantity + delta
        return q > 0 ? [{ ...ci, quantity: q }] : []
      }
      return [ci]
    }))
  }

  const removeFromCart = itemId => {
    setCart(prev => prev.filter(ci => ci.item.id !== itemId))
  }

  const grouped = menu.reduce((acc, item) => {
    acc[item.category] = acc[item.category] || []
    acc[item.category].push(item)
    return acc
  }, {})

  return (
    <>
      <Navbar />
      <div className={styles.restaurantPage}>
        <div className={styles.pageContent}>
          <div className={styles.layout}>
            <div className={styles.menu}>
              {Object.keys(grouped).map(cat => (
                <div key={cat} className={styles.category}>
                  <h3>{cat}</h3>
                  {grouped[cat].map(item => (
                    <div key={item.id} className={styles.item}>
                      <div>
                        <strong>{item.name}</strong>
                        <p>{item.description}</p>
                      </div>
                      <div>
                        <span>{Number(item.price).toFixed(2)} zł</span>
                        <button onClick={() => addToCart(item)}>Dodaj</button>
                      </div>
                    </div>
                  ))}
                </div>
              ))}
            </div>

            <div className={styles.cart}>
              <h2>Koszyk</h2>
              {cart.map(ci => (
                <div key={ci.item.id} className={styles.cartItem}>
                  <span>{ci.item.name}</span>
                  <div className={styles.qtyControls}>
                    <button onClick={() => updateQuantity(ci.item.id, -1)}>-</button>
                    <span>{ci.quantity}</span>
                    <button onClick={() => updateQuantity(ci.item.id,  1)}>+</button>
                  </div>
                  <span>{(ci.item.price * ci.quantity).toFixed(2)} zł</span>
                  <button className={styles.remove} onClick={() => removeFromCart(ci.item.id)}>
                    Usuń
                  </button>
                </div>
              ))}

              <hr />
              <p>
                Suma:{' '}
                {cart
                  .reduce((sum, ci) => sum + ci.item.price * ci.quantity, 0)
                  .toFixed(2)}{' '}
                zł
              </p>
              <button className={styles.checkout}>Przejdź do rezerwacji</button>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

export default RestaurantPage
